package org.mdpnp.apps.testapp.closedloopcontrol;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.chart.Chart;
import org.mdpnp.apps.testapp.chart.DateAxis;
import org.mdpnp.apps.testapp.patient.EMRFacade;
import org.mdpnp.apps.testapp.patient.PatientInfo;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelImpl;
import org.mdpnp.apps.testapp.vital.VitalSign;
import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.devices.DeviceIdentityBuilder;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.devices.MDSHandler.Connectivity.MDSEvent;
import org.mdpnp.devices.MDSHandler.Connectivity.MDSListener;
import org.mdpnp.devices.MDSHandler.Patient.PatientEvent;
import org.mdpnp.devices.MDSHandler.Patient.PatientListener;
import org.mdpnp.devices.PartitionAssignmentController;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.sql.SQLLogging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;

import ice.FlowRateObjective;
import ice.FlowRateObjectiveDataWriter;
import ice.MDSConnectivity;
import ice.Patient;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

public class ClosedLoopControlTestApplication implements EventHandler<ActionEvent> {
	
	private ApplicationContext parentContext;
	private Subscriber assignedSubscriber;	//Use a slightly different name here to avoid poss conflict with any other subscriber variable.
	private DeviceListModel dlm;
	private NumericFxList numeric;
	private SampleArrayFxList samples;
	private FlowRateObjectiveDataWriter writer;
	private MDSHandler mdsHandler;
	private VitalModel vitalModel;
	private EMRFacade emr;
	
	@FXML VBox bpVBox;
	@FXML VBox bpGraphBox;	//TODO: Something simpler than a VBox?
	private DateAxis dateAxis;
		
	@FXML private ComboBox<Device> bpsources;
	@FXML private ComboBox<Device> pumps;
	@FXML private ComboBox<String> algos;
	@FXML private TextField currentDiastolic;
	@FXML private TextField currentSystolic;
	@FXML private TextField currentMean;
	@FXML private Spinner targetSystolic;
	@FXML private Spinner targetDiastolic;
	@FXML private Spinner systolicAlarm;
	@FXML private Spinner diastolicAlarm;
	@FXML private Label errorLabel;
	@FXML private ToggleGroup operatingMode;
	@FXML private Toggle openRadio;
	@FXML private Spinner infusionRate;
	@FXML private BorderPane main;
	@FXML private Label lastPumpUpdate;
	@FXML private Label lastBPUpdate;
	@FXML private Button startButton;
	@FXML private Label patientNameLabel;
	@FXML private Label currentPumpSpeed;
	
	
	private final String FLOW_RATE=rosetta.MDC_FLOW_FLUID_PUMP.VALUE;
	private final String ARTERIAL=rosetta.MDC_PRESS_BLD_ART_ABP.VALUE;
	
	private static final float LOWER_INFUSION_LIMIT=100f;
	private static final float UPPER_INFUSION_LIMIT=2000f;
	private static final int LOWER_SYSTOLIC_LIMIT=40;
	private static final int UPPER_SYSTOLIC_LIMIT=200;
	private static final int LOWER_DIASTOLIC_LIMIT=10;
	private static final int UPPER_DIASTOLIC_LIMIT=150;
	private static final int SYSTOLIC_ALARM_LOWER=0;
	private static final int SYSTOLIC_ALARM_HIGHER=150;
	private static final int DIASTOLIC_ALARM_LOWER=0;
	private static final int DIASTOLIC_ALARM_HIGHER=120;
	private static final int MIN_FLOW_RATE=100;
	private static final int MAX_FLOW_RATE=2000;
	private static final int INITIAL_SYSTOLIC=120;
	private static final int INITIAL_DIASTOLIC=80;
	private static final int INITIAL_SYSTOLIC_ALARM=100;
	private static final int INITIAL_DIASTOLIC_ALARM=60;
	
	private static final long interval= 5 * 60 * 1000L; 
	
	private static final Logger log = LoggerFactory.getLogger(ClosedLoopControlTestApplication.class);
	
	private boolean listenerPresent;
	
	private String[] SYS_PARAMS=new String[] { rosetta.MDC_PRESS_BLD_SYS.VALUE, rosetta.MDC_PRESS_BLD_ART_SYS.VALUE, rosetta.MDC_PRESS_INTRA_CRAN_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_AORT_SYS.VALUE, rosetta.MDC_PRESS_BLD_ART_ABP_SYS.VALUE, rosetta.MDC_PRESS_BLD_ART_FEMORAL_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_ART_PULM_SYS.VALUE, rosetta.MDC_PRESS_BLD_ART_UMB_SYS.VALUE, rosetta.MDC_PRESS_BLD_ATR_LEFT_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_ATR_RIGHT_SYS.VALUE
    };
	
	private String[] DIA_PARAMS=new String[] {
			rosetta.MDC_PRESS_BLD_DIA.VALUE, rosetta.MDC_PRESS_BLD_ART_DIA.VALUE, rosetta.MDC_PRESS_INTRA_CRAN_DIA.VALUE,
            rosetta.MDC_PRESS_BLD_AORT_DIA.VALUE, rosetta.MDC_PRESS_BLD_ART_ABP_DIA.VALUE, rosetta.MDC_PRESS_BLD_ART_FEMORAL_DIA.VALUE,
            rosetta.MDC_PRESS_BLD_ART_PULM_DIA.VALUE, rosetta.MDC_PRESS_BLD_ART_UMB_DIA.VALUE, rosetta.MDC_PRESS_BLD_ATR_LEFT_DIA.VALUE,
            rosetta.MDC_PRESS_BLD_ATR_RIGHT_DIA.VALUE
	};
	
	private String[] MEAN_PARAMS=new String[] {
			rosetta.MDC_PRESS_BLD_MEAN.VALUE, rosetta.MDC_PRESS_BLD_ART_MEAN.VALUE, rosetta.MDC_PRESS_INTRA_CRAN_MEAN.VALUE,
            rosetta.MDC_PRESS_BLD_AORT_MEAN.VALUE, rosetta.MDC_PRESS_BLD_ART_ABP_MEAN.VALUE, rosetta.MDC_PRESS_BLD_ART_FEMORAL_MEAN.VALUE,
            rosetta.MDC_PRESS_BLD_ART_PULM_MEAN.VALUE, rosetta.MDC_PRESS_BLD_ART_UMB_MEAN.VALUE, rosetta.MDC_PRESS_BLD_ATR_LEFT_MEAN.VALUE,
            rosetta.MDC_PRESS_BLD_ATR_RIGHT_MEAN.VALUE
	};
	
	private HashMap<String, Parent> udiToPump=new HashMap<>();
	
	/**
	 * The "current" patient, used to determine if the patient has changed
	 */
	private Patient currentPatient;
	
	private Connection dbconn;
	
	/**
	 * Graphing timeline used to animate the axis
	 */
	private Timeline timeline;
	
	private IntegerProperty systolicProperty=new SimpleIntegerProperty();
	private IntegerProperty diastolicProperty=new SimpleIntegerProperty();
	
	private boolean pleaseStopAlgo;
	private Thread algoThread;
	/**
	 * Thread that monitors the last update time from the BP monitor
	 */
	private Thread bpUpdateAlarmThread;
	private Thread pumpUpdateAlarmThread;

	
	SampleArrayFx[] sampleFromSelectedMonitor;
	
	private static final int oneMinute= 1000 * 60;
	private static final int fiveMinutes= 1000 * 60 * 5;
	
	private boolean running;
	
	//TODO: Make all algos implement an interface so we can dynamically find them.
	/**
	 * Map of name for a control algorithm to the method that implements it.  Populated in configureFields
	 */
	Map<String, Method> allAlgos=new HashMap<>();
	/**
	 * The current session id.
	 */
	private String sessionid;
	
	/**
	 * A prepared statement used to record the end time of the session in the database.
	 */
	private PreparedStatement endStatement;
	
	private PreparedStatement flowStatement;
	
	private PreparedStatement sampleStatement;
	
	//Need a context here...
	public void set(ApplicationContext parentContext, DeviceListModel dlm, NumericFxList numeric, SampleArrayFxList samples,
			FlowRateObjectiveDataWriter writer, MDSHandler mdsHandler, VitalModel vitalModel, Subscriber subscriber, EMRFacade emr) {
		this.parentContext=parentContext;
		this.dlm=dlm;
		this.numeric=numeric;
		this.samples=samples;
		this.writer=writer;
		this.mdsHandler=mdsHandler;
		this.vitalModel=vitalModel;
		this.assignedSubscriber=subscriber;
		this.emr=emr;
		configureFields();
	}
	
	private final class NumericBPDevice extends AbstractDevice {

		private InstanceHolder<ice.Numeric> systolic;
		private InstanceHolder<ice.Numeric> diastolic;

		public NumericBPDevice(Subscriber subscriber, Publisher publisher, EventLoop eventLoop) {
			super(subscriber, publisher, eventLoop);
            deviceIdentity.manufacturer = "";
            deviceIdentity.model = "BP Numeric Provider";
            deviceIdentity.serial_number = "1234";
            AbstractSimulatedDevice.randomUDI(deviceIdentity);		//TODO: clone the device id, or does that mess everything up?
            writeDeviceIdentity();
//            System.err.println("NumericBPDeviceConstructor subscriber is "+subscriber);
		}

		public void writeNumerics() {
            DeviceClock.Reading sampleTime = clock.instant();
            // TODO clearly a synchronization issue here.
            // enforce a singular calling thread or synchronize accesses
            systolic = numericSample(systolic, (int) Math.round(systolicProperty.floatValue()), rosetta.MDC_PRESS_BLD_ART_ABP_SYS.VALUE, 
                    rosetta.MDC_PRESS_BLD_ART_ABP_SYS.VALUE, 0, rosetta.MDC_DIM_MMHG.VALUE, sampleTime);
            diastolic = numericSample(diastolic, (int) Math.round(diastolicProperty.floatValue()), rosetta.MDC_PRESS_BLD_ART_ABP_DIA.VALUE, 
                    rosetta.MDC_PRESS_BLD_ART_ABP_DIA.VALUE, 0, rosetta.MDC_DIM_MMHG.VALUE, sampleTime);
		}
	}

    protected final DeviceClock clock = new DeviceClock.WallClock();
    private DeviceDriverProvider.DeviceAdapter numericBPDeviceAdapter;
    private NumericBPDevice numericBPDevice;

    private void createNumericDevice() {
        if (numericBPDeviceAdapter == null) {

            DeviceDriverProvider.SpringLoadedDriver df = new DeviceDriverProvider.SpringLoadedDriver() {
                @Override
                public DeviceType getDeviceType() {
                    return new DeviceType(ice.ConnectionType.Simulated, "Simulated", "NumericBP", "NumericBP", 1);
                }

                @Override
                public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
                    //TODO: This is a clone of rapid respiratory device.  Do we need a new subscriber here?
                    //Can't it share the passed in subscriber?  That already comes from the parentContext
                    //anyway, so is the same bean if you look back to the factory.
                    EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
                    Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
                    Publisher publisher = context.getBean("publisher", Publisher.class);
                    return new NumericBPDevice(subscriber, publisher, eventLoop);
                }
            };

            try {
                numericBPDeviceAdapter = df.create((AbstractApplicationContext) parentContext);

                // TODO Make this more elegant
                List<String> strings = new ArrayList<String>();
                SubscriberQos qos = new SubscriberQos();
//                System.err.println("assignedSubscriber is "+assignedSubscriber);
                assignedSubscriber.get_qos(qos);

                for (int i = 0; i < qos.partition.name.size(); i++) {
                    strings.add((String) qos.partition.name.get(i));
                }

                //numericBPDeviceAdapter.setPartition(strings.toArray(new String[0]));
                

            }
            catch(Exception ex) {
                throw new RuntimeException("Failed to create a driver", ex);
            }

        }
    	numericBPDeviceAdapter.setPartition(new String[] {PartitionAssignmentController.toPartition(currentPatient.mrn)});
        numericBPDevice=(NumericBPDevice)numericBPDeviceAdapter.getDevice();
        //We have the device - we must associate it with the correct partition...
        //mdsHandler.
        //mdsHandler.publish(new MDSConnectivity());
        String partitionToAssociate=PartitionAssignmentController.toPartition(currentPatient.mrn);
        MDSConnectivity connectivity=new MDSConnectivity();
        connectivity.partition=partitionToAssociate;
        connectivity.unique_device_identifier=numericBPDevice.getUniqueDeviceIdentifier();
        mdsHandler.publish(connectivity);
//        System.err.println(connectivity.toString("Published device connectivity ",2));
    }

	Pattern p=Pattern.compile("[0-9]?");
	
	private void configureFields() {
		//main.setPrefSize(400, 400);
		//new TextFormatter<?>(UnaryOperator<TextFormatter.Change> change
		targetDiastolic.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(LOWER_DIASTOLIC_LIMIT,UPPER_DIASTOLIC_LIMIT,INITIAL_DIASTOLIC));
		targetDiastolic.setEditable(true);
		targetSystolic.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(LOWER_SYSTOLIC_LIMIT,UPPER_SYSTOLIC_LIMIT,INITIAL_SYSTOLIC));
		targetSystolic.setEditable(true);
		diastolicAlarm.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(DIASTOLIC_ALARM_LOWER, DIASTOLIC_ALARM_HIGHER,INITIAL_DIASTOLIC_ALARM));
		diastolicAlarm.setEditable(true);
		systolicAlarm.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(SYSTOLIC_ALARM_LOWER, SYSTOLIC_ALARM_HIGHER,INITIAL_SYSTOLIC_ALARM));
		systolicAlarm.setEditable(true);
		SpinnerValueFactory.DoubleSpinnerValueFactory rateFactory=new SpinnerValueFactory.DoubleSpinnerValueFactory(MIN_FLOW_RATE, MAX_FLOW_RATE);
		rateFactory.setAmountToStepBy(0.1);
		infusionRate.setValueFactory(rateFactory);
		
		/*
		 * We use bindBidirectional here because systolicProperty and diastolicProperty and numbers,
		 * but the text field is a String, and bindBidrectional allows us to specify a converter
		 * to handle the change between the two.
		 */
		currentSystolic.textProperty().bindBidirectional(systolicProperty, new NumberStringConverter());
		currentDiastolic.textProperty().bindBidirectional(diastolicProperty, new NumberStringConverter());
		
		currentSystolic.setEditable(false);
		currentDiastolic.setEditable(false);
		

		operatingMode.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> ov, Toggle oldToggle, Toggle newToggle) {
				if(newToggle.equals(openRadio)) {
					bpVBox.setVisible(false);
					bpVBox.setManaged(false);
				} else {
					bpVBox.setManaged(true);
					bpVBox.setVisible(true);
				}
				
			}
			
		});
		
		try {
			allAlgos.put("10% change", this.getClass().getMethod("simonsSimpleAlgo", null));
			allAlgos.put("Linear", this.getClass().getMethod("linearAlgo", null));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		algos.getItems().addAll(allAlgos.keySet());
		
	}
	
	private void isInt(String s) throws NumberFormatException {
		if(s.length()==0) return;
		Integer.parseInt(s);
	}
	
	public void stop() {

	}
	
	public void destroy() {
		if(algoThread!=null) {
			pleaseStopAlgo=true;
			algoThread.interrupt();
		}
		try {
			dbconn.close();
		} catch (SQLException e) {
			log.error("Could not cleanly close sql connection",e);
		}
	}
	
	public void activate() {
		log.info("CLC.activate does nothing at the moment");
	}
	
	class BPDeviceChangeListener implements ChangeListener<Device> {

		@Override
		public void changed(ObservableValue<? extends Device> observable, Device oldValue, Device newValue) {
			handleBPDeviceChange(newValue);
		}
	}

	BPDeviceChangeListener bpDeviceChangeListener=new BPDeviceChangeListener();
	
	public void start(EventLoop eventLoop, Subscriber subscriber) {
		
		//Rely on addition of metrics to add devices...
		numeric.addListener(new ListChangeListener<NumericFx>() {
			@Override
			public void onChanged(Change<? extends NumericFx> change) {
				while(change.next()) {
					change.getAddedSubList().forEach( n -> {
						if(n.getMetric_id().equals(FLOW_RATE)) {
							pumps.getItems().add(dlm.getByUniqueDeviceIdentifier(n.getUnique_device_identifier()));
						}

					});
				}
			}
		});
		
		//...and removal of devices to remove devices.
		dlm.getContents().addListener(new ListChangeListener<Device>() {
			@Override
			public void onChanged(Change<? extends Device> change) {
				while(change.next()) {
					change.getRemoved().forEach( d-> {
						bpsources.getItems().remove(d);
						pumps.getItems().remove(d);
					});
				}
			}
		});
		
		//Similarly, rely on metrics to add BP devices.
		samples.addListener(new ListChangeListener<SampleArrayFx>() {
			@Override
			public void onChanged(Change<? extends SampleArrayFx> change) {
				while(change.next()) {
					change.getAddedSubList().forEach( n -> {
						if(n.getMetric_id().equals(ARTERIAL)) {
							bpsources.getItems().add(dlm.getByUniqueDeviceIdentifier(n.getUnique_device_identifier()));
						}
					});
				}
				
			}
		});
		
		bpsources.getSelectionModel().selectedItemProperty().addListener(bpDeviceChangeListener);
		listenerPresent=true;
		
		bpsources.setCellFactory(new Callback<ListView<Device>,ListCell<Device>>() {

			@Override
			public ListCell<Device> call(ListView<Device> device) {
				return new DeviceListCell();
			}
			
		});
		
		bpsources.setConverter(new StringConverter<Device>() {

			@Override
			public Device fromString(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String toString(Device device) {
				// TODO Auto-generated method stub
				return device.getModel()+"("+device.getComPort()+")";
			}
			
		});
		
		pumps.setCellFactory(new Callback<ListView<Device>,ListCell<Device>>() {

			@Override
			public ListCell<Device> call(ListView<Device> device) {
				return new DeviceListCell();
			}
			
		});
		
		pumps.setConverter(new StringConverter<Device>() {

			@Override
			public Device fromString(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String toString(Device device) {
				// TODO Auto-generated method stub
				return device.getModel()+"("+device.getComPort()+")";
			}
			
		});
		
		mdsHandler.addPatientListener(new PatientListener() {

			@Override
			public void handlePatientChange(PatientEvent evt) {

			}
			
		});
		
		mdsHandler.addConnectivityListener(new MDSListener() {

			@Override
			public void handleConnectivityChange(MDSEvent evt) {
		        ice.MDSConnectivity c = (MDSConnectivity) evt.getSource();
//		        System.err.println("CLC.handleConnectivity Partition is "+c.partition);

		        String mrnPartition = PartitionAssignmentController.findMRNPartition(c.partition);

		        if(mrnPartition != null) {
		            //log.info("udi " + c.unique_device_identifier + " is MRN=" + mrnPartition);

		            Patient p = new Patient();
		            p.mrn = PartitionAssignmentController.toMRN(mrnPartition);
		            
		            if(currentPatient==null) {
		            	/*
		            	 * The patient has definitely changed - even if the selected patient is "Unassigned",
		            	 * then that "Patient" has an ID
		            	 */
		            	currentPatient=p;
                        setPatientNameLabel();
		            	return;	//Nothing else to do.
		            }
		            if( ! currentPatient.mrn.equals(p.mrn) ) {
		            	//Patient has changed
		            	currentPatient=p;
                        setPatientNameLabel();
		            }
		            
		            //deviceUdiToPatientMRN.put(c.unique_device_identifier, p);
		        }
		    }
			
		});
		
       	dbconn = SQLLogging.getConnection();

	}
	
	private void setPatientNameLabel() {
		try {
            ObservableList<PatientInfo> patientInfos=emr.getPatients();
            FilteredList<PatientInfo> onlyPatient=patientInfos.filtered(new Predicate<PatientInfo>() {

				@Override
				public boolean test(PatientInfo t) {
					if(t.getMrn().equals(currentPatient.mrn)) {
						return true;
					}
					return false;
				}

            });
            PatientInfo pi=onlyPatient.get(0);
            patientNameLabel.setText("Current Patient: "+pi.getFirstName()+" "+pi.getLastName());
            patientNameLabel.setFont(Font.font(24));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

	/**
	 * Use this to allow access to the numeric sample that has a listener attached.
	 * Then if the pump is changed, the listener can be detached from the previous numeric
	 */
	private NumericFx currentPumpNumeric;
	
	private float[] getMinAndMax(Number[] numbers) {
		float[] minAndMax=new float[] {numbers[0].floatValue(),numbers[0].floatValue()};
		for(int i=1;i<numbers.length;i++) {
			if(numbers[i].floatValue()<minAndMax[0]) minAndMax[0]=numbers[i].floatValue();
			if(numbers[i].floatValue()>minAndMax[1]) minAndMax[1]=numbers[i].floatValue();
		}
		diastolicProperty.set((int)minAndMax[0]);
		systolicProperty.set((int)minAndMax[1]);
		//This getMinAndMax method is called before the start button is pressed, in which case the numericBPDevice
		//won't have been created yet, hence the null check.
		if(numericBPDevice!=null) {
			numericBPDevice.writeNumerics();
		}
		return minAndMax;
	}
	
	class SampleValuesChangeListener implements ChangeListener<Number[]> {

		@Override
		public void changed(ObservableValue<? extends Number[]> observable, Number[] oldValue, Number[] newValue) {
			//Ignore the old values.  Just get new ones.
			float[] minMax=getMinAndMax(newValue);
			//System.err.println("got minMax as "+minMax[0]+ " and "+minMax[1]);
			//currentDiastolic.setText(Integer.toString((int)minMax[0]));
			//currentSystolic.setText(Integer.toString((int)minMax[1]));
			/*
			 * https://nursingcenter.com/ncblog/december-2011/calculating-the-map
			 */
			float meanCalc=(minMax[1]+(2*minMax[0]))/3;
			currentMean.setText(Integer.toString((int)meanCalc));
			buffer.put(currentBPSample); 	//Store it in case we want it later.
			if(recording) {
				recorded++;
			}
			if(recording && recorded==3) {
				SampleArrayFx[] samplesToStore=buffer.get(3);
				try {
					if(sampleStatement==null) {
						sampleStatement=dbconn.prepareStatement("INSERT INTO samples_for_export(t_sec, t_nanosec, udi, metric_id, floats) VALUES (?,?,?,?,?)");
					}
					/*
					 * It's theoretically possible for the call to get to return elements that are null, if not enough puts have been done to fill the
					 * number of elements requested in the get.  So check for nulls in the loop.
					 */
					for(SampleArrayFx sample : samplesToStore) {
						if(sample==null) {
							continue;
						}
						//presentation time is set like this
						//setPresentation_time(new Date(v.presentation_time.sec * 1000L + v.presentation_time.nanosec / 1000000L));
						Instant i=sample.getPresentation_time().toInstant();
						sampleStatement.setLong(1, i.getEpochSecond());
						sampleStatement.setLong(2, i.getNano());
						sampleStatement.setString(3, sample.getUnique_device_identifier());
						sampleStatement.setString(4, sample.getMetric_id());
						
						Number[] floatsForDb=sample.getValues();
						JsonArrayBuilder builder=Json.createArrayBuilder();
						for(int j=0;j<floatsForDb.length;j++) {
							builder.add(floatsForDb[j].floatValue());
						}
						JsonArray jsonArray=builder.build();
						sampleStatement.setString(5, jsonArray.toString());
						sampleStatement.execute();
					}
				} catch (SQLException sqle) {
					log.error("Failed to store samples for export",sqle);
				}
				recording=false;
				recorded=0;
			}
		}
	}
	
	SampleValuesChangeListener bpArrayListener=new SampleValuesChangeListener();
	
	/**
	 * Use this to allow access to the array sample that has a listener attached.
	 * Then if the BP monitor is changed, the listener can be detached from the previous sample
	 */
	private SampleArrayFx currentBPSample;
	
	/**
	 * A rolling array of samples.
	 */
	private CircularBuffer buffer=new CircularBuffer(5);
	
	/**
	 * Are we recording samples for entry into the export table of the database?
	 */
	private boolean recording=false;
	
	/**
	 * How many samples have we recorded?
	 */
	private int recorded=0;
	
	private void handleBPDeviceChange(Device newDevice) {
		log.info("QCT.handleDeviceChange newDevice is "+newDevice);
		if(currentBPSample!=null) {
			currentBPSample.valuesProperty().removeListener(bpArrayListener);
		}
		if(null==newDevice) return;	//No device selected and/or available - can happen when patient is changed and no devices for that patient
		samples.forEach( s-> {
			if (! s.getUnique_device_identifier().contentEquals(newDevice.getUDI())) return;	//Some other device.
			//This sample is from the current device.
			if(s.getMetric_id().equals(ARTERIAL)) {
				s.valuesProperty().addListener(bpArrayListener);
				currentBPSample=s;
			}
		});
	}
	
	class DeviceListCell extends ListCell<Device> {
        @Override protected void updateItem(Device device, boolean empty) {
            super.updateItem(device, empty);
            if (!empty && device != null) {
                setText(device.getModel()+"("+device.getComPort()+")");
            } else {
                setText(null);
            }
        }
    }

	public void startProcess() {
		if(running) {
			orderlyStop();
			running=false;
			return;
		}
		if(checkValid()) {
			runForMode();
		}
	}
	
	//TODO: is it better to have multiple alerts, one for each parameter - or a combined one?
	
	/*
	 * Using multiple seperate alerts, each included in its own validity checked method, allows for nicer
	 * and more modular code, but could be annoying for the user in the case of multiple invalid options.
	 * They won't see more than one alert at a time, because we will do
	 * 
	 * if(aValid() && bValid() && cValid().....)
	 * 
	 * but they might want all errors to be reported at once.  Maybe we extend the use of an error label to
	 * give a "live" status.  In theory, the values should be constrained by the factory used for the spinners,
	 * but...  
	 * 
	 * Anyway, in the meantime...
	 */
	
	
	private boolean checkValid() {
		if(openRadio.isSelected()) {
			//Open loop mode.
			return checkInfusionRate() && checkPumpSelected() && checkMonitorSelected();
		} else {
			//Closed loop mode.
			//Check patient selected
			//Check QCore selected
			//Check BP monitor selected
			//Check target BP range
			//Check infusion rate.
			return checkTargetBPRange() && checkInfusionRate() && checkPumpSelected() && checkMonitorSelected() && checkAlgoSelected() && checkPatientSelected();
		}
	}
	
	private boolean checkTargetBPRange() {
		int systolicValue=(int)targetSystolic.getValue();
		if(systolicValue<LOWER_SYSTOLIC_LIMIT || systolicValue>UPPER_SYSTOLIC_LIMIT) {
			Alert alert=new Alert(AlertType.ERROR,"Target systolic must be between "+LOWER_SYSTOLIC_LIMIT+" and "+UPPER_SYSTOLIC_LIMIT,ButtonType.OK);
			alert.showAndWait();
			return false;
		}
		int diastolicValue=(int)targetDiastolic.getValue();
		if(diastolicValue<LOWER_DIASTOLIC_LIMIT || diastolicValue>UPPER_DIASTOLIC_LIMIT) {
			Alert alert=new Alert(AlertType.ERROR,"Target diastolic must be between "+LOWER_DIASTOLIC_LIMIT+" and "+UPPER_DIASTOLIC_LIMIT,ButtonType.OK);
			alert.showAndWait();
			return false;
		}
		return true;
	}
	
	private boolean checkInfusionRate() {
		double infusionRateValue=(double)infusionRate.getValue();
		if(infusionRateValue>=LOWER_INFUSION_LIMIT && infusionRateValue<=UPPER_INFUSION_LIMIT) {
			return true;
		} else {
			//Simpler to do the alert here as we know what mode we are in.
			Alert alert=new Alert(AlertType.ERROR,"In open-loop mode, infusion rate must be between "+LOWER_INFUSION_LIMIT+" and "+UPPER_INFUSION_LIMIT,ButtonType.OK);
			alert.showAndWait();
			return false;
		}
	}
	
	private boolean checkPumpSelected() {
		Device d=pumps.getSelectionModel().getSelectedItem();
		if(d==null) {
			Alert alert=new Alert(AlertType.ERROR,"A pump must be selected",ButtonType.OK);
			alert.showAndWait();
			return false;
		}
		return true;
	}
	
	private boolean checkMonitorSelected() {
		Device d=bpsources.getSelectionModel().getSelectedItem();
		if(d==null) {
			Alert alert=new Alert(AlertType.ERROR,"A BP monitor must be selected",ButtonType.OK);
			alert.showAndWait();
			return false;
		}
		return true;
	}
	
	private boolean checkAlgoSelected() {
		String s=algos.getSelectionModel().getSelectedItem();
		if(s==null) {
			Alert alert=new Alert(AlertType.ERROR,"A control algorithm must be selected",ButtonType.OK);
			alert.showAndWait();
			return false;
		}
		return true;
	}
	
	private boolean checkPatientSelected() {
		if(currentPatient==null) {
			Alert alert=new Alert(AlertType.ERROR,"A patient must be selected",ButtonType.OK);
			alert.showAndWait();
			return false;
		}
		return true;
	}
	
	NumericFx[] flowRateFromSelectedPump=new NumericFx[1];

	private void runForMode() {
		//Get a whole graph thing...
		FXMLLoader loader = new FXMLLoader(Chart.class.getResource("Chart.fxml"));
        try {
        	
        	VitalModel myPrivateModel=null;
        	myPrivateModel=new VitalModelImpl(dlm, numeric);
        	VitalSign bothBP=VitalSign.BothBP;
        	Vital vitalForChart=bothBP.addToModel(myPrivateModel);
        	
        	
        	Parent node = loader.load();
            Chart chart = loader.getController();

            long now = System.currentTimeMillis();
            now -= now % 1000;
            dateAxis=new DateAxis(new Date(now - interval), new Date(now));
            dateAxis.setAutoRanging(false);
            dateAxis.setAnimated(false);
            
            timeline = new Timeline(new KeyFrame(new Duration(1000.0), this));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();

            chart.setModel(vitalForChart, dateAxis);
            //TODO: What should happen during a stop/start cycle?  Just stop the timeline, or actually kill a previous instance and add a new one?
            if(bpGraphBox.getChildren().size()>1) {
            	//Remove any existing instance
            	bpGraphBox.getChildren().remove(0);
            }
            bpGraphBox.getChildren().add(0,node);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        Device pump=pumps.getSelectionModel().getSelectedItem();
        
        numeric.forEach( n -> {
        	if( n.getUnique_device_identifier().equals(pump.getUDI()) && n.getMetric_id().equals(FLOW_RATE)) {
        		//This is the flow rate from the pump we want
//        		System.err.println("Found numeric for matching pump");
        		flowRateFromSelectedPump[0]=n;
        	}
        });
        
        Device monitor=bpsources.getSelectionModel().getSelectedItem();
        sampleFromSelectedMonitor=new SampleArrayFx[1];
        samples.forEach( s -> {
        	if(s.getUnique_device_identifier().equals(monitor.getUDI()) && s.getMetric_id().equals(ARTERIAL)) {
        		sampleFromSelectedMonitor[0]=s;
        	}
        });
        
        lastPumpUpdate.textProperty().bind(Bindings.format("Last pump update %s", flowRateFromSelectedPump[0].presentation_timeProperty()));
        lastBPUpdate.textProperty().bind(Bindings.format("Last BP update %s", sampleFromSelectedMonitor[0].presentation_timeProperty()));
        currentPumpSpeed.textProperty().bind(Bindings.format("Current flow rate (ml/hour) %.2f", flowRateFromSelectedPump[0].valueProperty()));
        currentPumpSpeed.setFont(Font.font(24));
		if(openRadio.isSelected()) {
			
		} else {
			closedLoopAlgo();
		}
		
		createNumericDevice();
		
		//Maybe we can use a different session identifier later, but this is handy for now.
		String hostname=null;
		try {
			hostname=InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			log.warn("Could not get local hostname - using localhost instead");
		}
		DateFormat df=SimpleDateFormat.getDateTimeInstance();
		sessionid="FROA_"+currentPatient.mrn+"_"+hostname+"_"+df.format(new Date());
		String mrnString=currentPatient.mrn;
		AppConfig appConfig=new AppConfig();
		appConfig.mode=openRadio.isSelected() ? 0 : 1;
		appConfig.target_sys=(int)targetSystolic.getValue();
		appConfig.target_dia=(int)targetDiastolic.getValue();
		appConfig.sys_alarm=(int)systolicAlarm.getValue();
		appConfig.dia_alarm=(int)diastolicAlarm.getValue();
		appConfig.bp_udi=monitor.getUDI();
		appConfig.bp_numeric=numericBPDevice.getUniqueDeviceIdentifier();
		appConfig.pump_udi=pump.getUDI();
		double rate=(double)infusionRate.getValue();
		appConfig.inf_rate=(float)rate;
		appConfig.sessionid=sessionid;
		try {
			appConfig.patientid=Integer.parseInt(mrnString);
		} catch (NumberFormatException nfe) {
			log.warn("Non-numeric mrn "+mrnString);
			appConfig.patientid=0;
		}
		appConfig.writeToDb();
		
		startBPUpdateAlarmThread();
		startBPValueMonitor();
		
		running=true;
		startButton.setText("Stop");
	}
	
	private void closedLoopAlgo() {
		if(algoThread!=null) {
			pleaseStopAlgo=true;
			algoThread.interrupt();
			pleaseStopAlgo=false;
		}
		String algoName=algos.getValue();
		Method algoMethod=allAlgos.get(algoName);
		try {
			//Every algo should populate algoThread.
			algoMethod.invoke(this, null);
			algoThread.start();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This is only public to allow simple reflection access.  Later on when it implements an interface
	 * or we do other dynamic discovery, it can go back to being private
	 */
	public void simonsSimpleAlgo() {
		algoThread=new Thread() {
			@Override
			public void run() {
				try {
					double infusionRateValue=(double)infusionRate.getValue();
					setFlowRate((float)infusionRateValue);
//					System.err.println("Set initial speed in Simons Simple Algo");
					//Now, we check the BP.
					//Continually check the BP against the target;
					while(true) {
						//Check the BP against the target
						//TODO: Why doesn't this just use the property?
						int currentSys=Integer.parseInt(currentSystolic.getText());
						int compare=((int)targetSystolic.getValue()-5);
						if( currentSys < compare ) {
//							System.err.println("Need to increase the pump speed");
							//Need to increase the pump speed.
							float delta=(float)(infusionRateValue*0.1);
							setFlowRate((float)(infusionRateValue+delta));
							infusionRateValue=infusionRateValue+delta;
							//TODO: How should we record this - as a new instance of AppConfig, or not?
						} else {
//							System.err.println("Setting the pump speed back to default");
							//Current BP is OK.  Set the speed to default.
							//!!!!GET A CLASS CAST HERE DOUBLE CANNOT BE CAST TO FLOAT!
							infusionRateValue=(double)infusionRate.getValue();
							if(flowRateFromSelectedPump[0].getValue()!=infusionRateValue) {
								setFlowRate((float)infusionRateValue);
							}
//							objective.newFlowRate=(float)infusionRateValue;
//							objective.unique_device_identifier=pumpUDI;
//							writer.write(objective, InstanceHandle_t.HANDLE_NIL);
//							infusionRateValue=objective.newFlowRate;
							//TODO: How should we record this - as a new instance of AppConfig, or not?
						}
						//Sleep after deciding what to do.
						sleep(60000);
					}
				} catch (InterruptedException ie) {
					if( ! pleaseStopAlgo) {
						System.err.println("Unexpected interruption...");
					}
					return;
				}
			} 
		};
	}
	
	/**
	 * A control alogrithm that sets the pump speed to be
	 * 
	 * 100 + 2 * (target systolic - current systolic)
	 * 
	 * For example, if target is 120 and current is 90, the difference is 30 so the target is 160.
	 * 
	 * If target is 120 and current is 75, the difference is 45 and so the target is 190.
	 */
	public void linearAlgo() {
		
		algoThread=new Thread() {
			public void run() {
				try {
					//TODO: factor out this initial step to something that can be shared?
					double infusionRateValue=(double)infusionRate.getValue();
					setFlowRate((float)infusionRateValue);
					while(true) {
						int currentSys=Integer.parseInt(currentSystolic.getText());
						int compare=((int)targetSystolic.getValue()-5);
						if( currentSys < compare ) {
							float newFlowRate=(float)(100 + 2 * ( (int) targetSystolic.getValue() - currentSys));
							setFlowRate(newFlowRate);
							infusionRateValue=newFlowRate;
						} else {
//							System.err.println("Setting the pump speed back to default");
							//Current BP is OK.  Set the speed to default.
							//!!!!GET A CLASS CAST HERE DOUBLE CANNOT BE CAST TO FLOAT!
							infusionRateValue=(double)infusionRate.getValue();
							setFlowRate((float)infusionRateValue);
//							objective.newFlowRate=(float)infusionRateValue;
//							objective.unique_device_identifier=pumpUDI;
//							writer.write(objective, InstanceHandle_t.HANDLE_NIL);
//							infusionRateValue=objective.newFlowRate;
						}
						Thread.sleep(60000);
					}
				} catch (InterruptedException ie) {
					if( ! pleaseStopAlgo) {
						System.err.println("Unexpected interruption...");
					}
					return;
				}
			}	//End of run()
		};	//End of new Thread()
		
	}
	
	private void setFlowRate(float newRate) {
		Device selectedPump=pumps.getSelectionModel().getSelectedItem();
		String pumpUDI=selectedPump.getUDI();
		FlowRateObjective objective=new FlowRateObjective();
		objective.newFlowRate=newRate;
		objective.unique_device_identifier=pumpUDI;
		try {
			if(flowStatement==null) {
				flowStatement=dbconn.prepareStatement("INSERT INTO flowrequest(t_millis, target_udi, target_type, requestedRate, source_id, source_type) VALUES (?,?,?,?,?,?)");
			}
			flowStatement.setLong(1, System.currentTimeMillis()/1000);
			flowStatement.setString(2, objective.unique_device_identifier);
			flowStatement.setString(3, "D");
			flowStatement.setFloat(4, objective.newFlowRate);
			flowStatement.setString(5, getClass().getSimpleName());
			flowStatement.setString(6, "A");
			flowStatement.execute();
			recording=true;	//If we set the flow rate, remember to record samples.
			recorded++;
		} catch (SQLException e) {
			log.error("Failed to create flowrequest statement",e);
		}
		
		writer.write(objective, InstanceHandle_t.HANDLE_NIL);
	}
	
	/**
	 * Starts a thread that monitors the last time data was received from the BP monitor
	 */
	public void startBPUpdateAlarmThread() {
		bpUpdateAlarmThread=new Thread() {
			Alert oneMinuteAlert=new Alert(AlertType.WARNING,"Data was not received from the BP monitor for at least one minute",ButtonType.OK);
			boolean showing;
			public void run() {
				try {
					while(true) {
						Date lastUpdate=sampleFromSelectedMonitor[0].getPresentation_time();
						Date now=new Date();
						long delta=now.getTime()-lastUpdate.getTime();
//						System.err.println("BP update delta is "+delta);
						if(  delta > fiveMinutes) {
							stopEverything();	//Calling stop everything will interrupt this thread.
							return;	//But return anyway.
						}
						if( delta > oneMinute && ! showing) {
//							System.err.println("More than one minute since last update - showing oneMinuteAlert");
							javafx.application.Platform.runLater(()-> {
								oneMinuteAlert.show();
							});
							showing=true;
							oneMinuteAlert.resultProperty().addListener(new ChangeListener<ButtonType>() {
							
								@Override
								public void changed(ObservableValue<? extends ButtonType> observable, ButtonType oldValue,
										ButtonType newValue) {
									if(newValue.equals(ButtonType.OK)) {
										showing=false;
									}
									
								}
								
							});
						}
						sleep(5000);
					}
				} catch (InterruptedException ie) {
//					System.err.println("bpUpdateAlarmThread was interrupted.  Calling return...");
					return;
				}
			}
		};
		bpUpdateAlarmThread.start();
	}
	
	ChangeListener<Number> systolicListener=new ChangeListener<Number>() {
		boolean sysShowing[]=new boolean[] {false};
		Alert sysAlert=new Alert(AlertType.WARNING,"Systolic value is below the alarm threshold");
		
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			//TODO: Do we really need the extra boolean, or do the alarm popups prevent further alarms being shown anyway because of being modal?
			if(newValue.intValue()<(int)systolicAlarm.getValue() && !sysShowing[0] ) {
//				System.err.println("systolic alarm condition...");
				javafx.application.Platform.runLater(()-> {
					sysShowing[0]=true;
					sysAlert.show();
				});
				BPAlarm alarm=new BPAlarm(BP_ALARM_SYS, newValue.intValue());
			} else {
				sysAlert.hide();
				sysShowing[0]=false;
			}
		}
	};
	
	ChangeListener<Number> diastolicListener=new ChangeListener<Number>() {
		boolean diaShowing[]=new boolean[] {false};
		Alert diaAlert=new Alert(AlertType.WARNING,"Diastolic value is below the alarm threshold");
		
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if(newValue.intValue()<(int)diastolicAlarm.getValue() && !diaShowing[0]) {
//				System.err.println("diastolic alarm condition...");
				javafx.application.Platform.runLater(()-> {
					diaShowing[0]=true;
					diaAlert.show();
				});
				BPAlarm alarm=new BPAlarm(BP_ALARM_DIA, newValue.intValue());
			} else {
				diaAlert.hide();
				diaShowing[0]=false;
			}
		}
	};
	
	/**
	 * Method that monitors the BP values (systolic/diastolic).  We don't need a thread
	 * to do this, because we are using properties for those two variables, and so we can
	 * just add listeners to them.  The listeners are assigned to instance variables above,
	 * so that the instances can be removed later.
	 */
	public void startBPValueMonitor() {
		//https://bugs.openjdk.java.net/browse/JDK-8125218
		systolicProperty.addListener(systolicListener);
		diastolicProperty.addListener(diastolicListener);
	}
	
	public void stopBPValueMonitor() {
		systolicProperty.removeListener(systolicListener);
		diastolicProperty.removeListener(diastolicListener);
	}

	private void stopEverything() {
		javafx.application.Platform.runLater(()-> {
			Alert alert=new Alert(AlertType.ERROR,"Data was not received from devices for more than 5 minutes");
			alert.show();
		});
		orderlyStop();
	}

	private void orderlyStop() {
		pleaseStopAlgo=true;
		if(algoThread!=null) {
			algoThread.interrupt();
		}
		if(bpUpdateAlarmThread!=null) {
			bpUpdateAlarmThread.interrupt();
		}
		if(pumpUpdateAlarmThread!=null) {
			pumpUpdateAlarmThread.interrupt();
		}
		stopBPValueMonitor();
		recordSessionEnd();
		//Move the closure of the numeric device to after the recordSessionEnd() call,
		//as recordSessionEnd() makes use of the UDI for numericBPDevice.
		if(numericBPDevice!=null) {
			numericBPDevice.shutdown();
			numericBPDevice=null;	//Prevent any further writes from writeNumerics.
			numericBPDeviceAdapter.stop();
			numericBPDeviceAdapter=null;
		}
		startButton.setText("Start");
	}

	private void recordSessionEnd() {
		try {
			Device monitor=bpsources.getSelectionModel().getSelectedItem();
			Device pump=pumps.getSelectionModel().getSelectedItem();
			String mrnString=currentPatient.mrn;
			AppConfig appConfig=new AppConfig();
			appConfig.mode=openRadio.isSelected() ? 0 : 1;
			appConfig.target_sys=(int)targetSystolic.getValue();
			appConfig.target_dia=(int)targetDiastolic.getValue();
			appConfig.sys_alarm=(int)systolicAlarm.getValue();
			appConfig.dia_alarm=(int)diastolicAlarm.getValue();
			appConfig.bp_udi=monitor.getUDI();
			appConfig.bp_numeric=numericBPDevice.getUniqueDeviceIdentifier();
			appConfig.pump_udi=pump.getUDI();
			double rate=(double)infusionRate.getValue();
			appConfig.inf_rate=(float)rate;
			appConfig.sessionid=sessionid;
			try {
				appConfig.patientid=Integer.parseInt(mrnString);
			} catch (NumberFormatException nfe) {
				log.warn("Non-numeric mrn "+mrnString);
				appConfig.patientid=0;
			}
			appConfig.endOfSession=true;
			appConfig.writeToDb();
		} catch (Exception sqle) {
			log.error("Failed to record session end time in database",sqle);
		}
	}

	@Override
	public void handle(ActionEvent arg0) {
		long now = System.currentTimeMillis();
        now -= now % 1000;
        Date lowerBound=new Date(now - interval);
        Date upperBound=new Date(now);
       	dateAxis.setLowerBound(lowerBound);
        dateAxis.setUpperBound(upperBound);	
		
	}
	
	private static final int BP_ALARM_SYS=1;
	private static final int BP_ALARM_DIA=2;

	/**
	 * A small class to contain a BP alarm, and a way to store it in the database
	 * @author simon
	 *
	 */
	private class BPAlarm {
		//TODO: Inconsitent style with the AppConfig class
		//TODO: Device time as well?  Prob important.
		int type;
		int value;

		/**
		 * Creates a new instance of the Blood Pressure alarm, which is immediately written
		 * to the database.
		 *
		 * @param type Should be one of BP_ALARM_SYS or BP_ALARM_DIA - but no check here
		 * @param value The value for the BP.
		 */
		BPAlarm(int type, int value) {
			this.type=type;
			this.value=value;
			writeToDb();
		}

		/**
		 * Alarm statement for froa_bp_alarm
		 */
		private PreparedStatement stmt;
		
		/**
		 * alarms_for_export statement
		 */
		private PreparedStatement exportStmt;

		private void writeToDb() {
			int t=(int)(System.currentTimeMillis()/1000);	//Make sure we use the same one for both froa_bp_alarm and alarms_for_export.
			try {
				if(stmt==null) {
					stmt=dbconn.prepareStatement("INSERT INTO froa_bp_alarm(alarmtype,value,t_sec) VALUES (?,?,?)");
				}
				stmt.setInt(1, type);
				stmt.setInt(2, value);
				stmt.setInt(3, t);
				stmt.execute();
			} catch (SQLException sqle) {
				log.warn("Could not create db record of alarm", sqle);
			}
			try {
				if(exportStmt==null) {
					exportStmt=dbconn.prepareStatement("INSERT INTO alarms_for_export(t_sec, mrn, source, sourcetype, alarmtype, alarmvalue, local_time) VALUES (?,?,?,?,?,?,?)");
				}
				exportStmt.setInt(1, t);
				exportStmt.setString(2,currentPatient.mrn);
				exportStmt.setString(3, getClass().getSimpleName());
				exportStmt.setString(4, "A");
				exportStmt.setString(5, type==BP_ALARM_SYS ? "Systolic" : "Diastolic");
				exportStmt.setString(6, String.valueOf(value));
				exportStmt.setString(7, DateFormat.getDateTimeInstance().format( new Date(t*1000L) ) );
				exportStmt.execute();
			} catch (SQLException sqle) {
				log.warn("Could not create export record of alarm", sqle);
			}
		}

	}
	
	/**
	 * Create a simple narrative representation of the alarm condition.
	 * @param type
	 * @param value
	 * @param time
	 * @return
	 */
	private String createAlarmDescription(int type, int value, int time) {
		Date narrativeDate=new Date(time*1000L);
		System.err.println("narrativeDate is "+narrativeDate);
		String narrativeString=DateFormat.getDateTimeInstance().format(narrativeDate);
		return (type==BP_ALARM_SYS ? "Systolic " : "Diastolic ") + "reached value "+value+" at "+narrativeString+" locally";
	}

	/**
	 * A small class to contain the app configuration when the app is started
	 * or changed, and a way to store it in the database in accordance with the
	 * logging requirement.
	 * @author simon
	 *
	 */
	private class AppConfig {
		/**
		 * 0 for open, 1 for closed
		 */
		int mode;
		/**
		 * Target systolic rate
		 */
		int target_sys;
		/**
		 * Target diastolic rate
		 */
		int target_dia;
		/**
		 * Systolic alarm level
		 */
		int sys_alarm;
		/**
		 * Diastolic alarm level
		 */
		int dia_alarm;
		/**
		 * UDI for the pump
		 */
		String pump_udi;
		/**
		 * UDI for the BP monitor
		 */
		String bp_udi;
		/**
		 * The UDI for the numeric device that produces BP numerics from waveforms
		 */
		String bp_numeric;
		/**
		 * Infusion rate
		 */
		float inf_rate;
		/**
		 * The ID of the patient.
		 */
		int patientid;
		/**
		 * The id of the session
		 */
		String sessionid;
		
		/**
		 * True if the session is ending.
		 */
		boolean endOfSession;

		private PreparedStatement stmt;

		void writeToDb() {
			try {
				if(endOfSession) {
					//Write an object with end time
					stmt=dbconn.prepareStatement("INSERT INTO froa_config(mode,target_sys,target_dia,sys_alarm,dia_alarm,pump_udi,bp_udi,bp_numeric,inf_rate,endtime,patient_id,session) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
				} else {
					//Write an object with start time
					stmt=dbconn.prepareStatement("INSERT INTO froa_config(mode,target_sys,target_dia,sys_alarm,dia_alarm,pump_udi,bp_udi,bp_numeric,inf_rate,starttime,patient_id,session) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
				}
				stmt.setInt(1,mode);
				stmt.setInt(2,target_sys);
				stmt.setInt(3,target_dia);
				stmt.setInt(4,sys_alarm);
				stmt.setInt(5,dia_alarm);
				stmt.setString(6, pump_udi);
				stmt.setString(7, bp_udi);
				stmt.setString(8, bp_numeric);
				stmt.setFloat(9, inf_rate);
				stmt.setInt(10, (int)(System.currentTimeMillis()/1000));
				stmt.setInt(11, patientid);
				stmt.setString(12, sessionid);
				stmt.execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}	//end of AppConfig

	public void deviceOnly() {
		if(numericBPDevice==null) {
			createNumericDevice();
		} else {
			numericBPDeviceAdapter.stop();
			numericBPDeviceAdapter=null;
			numericBPDevice=null;
		}
	}
	
	class CircularBuffer {
		int size;
		int index;
		SampleArrayFx[] samples;
		
		public CircularBuffer(int size) {
			this.size=size;
			samples=new SampleArrayFx[size];
		}
		
		public void put(SampleArrayFx sample) {
			//System.err.println("put called with sample time of "+sample.getPresentation_time().toInstant().toEpochMilli());
			samples[index++ % size]=copy(sample);
		}
		
		public SampleArrayFx[] get(int howMany) {
			System.err.println("get called for "+howMany+" elements");
			dump();
			SampleArrayFx[] ret=new SampleArrayFx[howMany];
			int start= (index % size) - 1 ;
			if(start<0) {
				start=size-1;	//Back to the end.
			}
			for(int i=0;i<howMany;i++) {
				ret[i]=samples[start--];
				if(ret[i]!=null) {
					System.err.println("Added sample with time "+ret[i].getPresentation_time().toInstant().toEpochMilli()+" to ret["+i+"] from samples["+(start+1)+"]");
				} else {
					System.err.println("WARNING - returning a null SampleArrayFx in CircularBuffer.get - current index is "+index);
				}
				if(start<0) {
					start=size-1;	//Back to the end.
				}
			}
			return ret;
		}
		
		/**
		 * A copy routine for a SampleArrayFx that only populates the properties we need later.
		 * This is heinous...
		 * @param in
		 * @return
		 */
		private SampleArrayFx copy(SampleArrayFx in) {
			SampleArrayFx clone=new SampleArrayFx();
			clone.setPresentation_time(in.getPresentation_time());
			clone.setMetric_id(in.getMetric_id());
			clone.setUnique_device_identifier(in.getUnique_device_identifier());
			clone.valuesProperty().set(in.getValues());
			return clone;
		}
		
		private void dump() {
			for(int i=0;i<size;i++) {
				if(samples[i]==null) {
					break;
				}
				System.out.println("["+i+"] has sample with time "+samples[i].getPresentation_time().toInstant().toEpochMilli());
			}
		}
	}
	
	

}
