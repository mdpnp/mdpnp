package org.mdpnp.apps.testapp.oximetry;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.prefs.Preferences;

import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.PartitionChooserModel;
import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.devices.PartitionAssignmentController;
import org.mdpnp.devices.MDSHandler.Connectivity.MDSEvent;
import org.mdpnp.devices.MDSHandler.Connectivity.MDSListener;
import org.mdpnp.devices.MDSHandler.Patient.PatientEvent;
import org.mdpnp.devices.MDSHandler.Patient.PatientListener;
import org.mdpnp.guis.waveform.javafx.JavaFXWaveformPane;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.subscription.Subscriber;

import ice.MDSConnectivity;
import ice.OximetryAveragingObjectiveDataWriter;
import ice.Patient;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class OximetryTestApplication {
	
	private DeviceListModel dlm;
	private NumericFxList numeric;
	private OximetryAveragingObjectiveDataWriter writer;
	private MDSHandler mdsHandler;
	
	@FXML protected LineChart<Number, Number> lineChart;
	final ObservableList<XYChart.Series<Number, Number>> series = FXCollections.observableArrayList();
    private final List<ValueSeriesListener> values = new ArrayList<ValueSeriesListener>();
	@FXML private ComboBox<Device> oximeters;
	@FXML private TextField requestedAverage;
	@FXML private TextField currentAverage;
	@FXML private TextField graphDelay;
	@FXML private TextField simpleAlarmThreshold;
	@FXML private TextField smartAlarmCount;
	@FXML private Button setButton;
	@FXML private Button chooseFile;
	@FXML private Button runSequence;
	@FXML private Button clearGraph;
	@FXML private TextField selectedFile;
	@FXML private BorderPane main;
	
	private Background nonAlarmBackground;
	private Background alarmBackground=new Background(new BackgroundFill(Color.RED, null, null));
	
	private boolean listenerPresent;
	
	private boolean inAlarm;
	
	private final String SOFT_CAN_GET_AVE=ice.SP02_SOFT_CAN_GET_AVERAGING_RATE.VALUE;
	private final String SOFT_CAN_SET_AVE=ice.SP02_SOFT_CAN_SET_AVERAGING_RATE.VALUE;
	private final String OPER_CAN_GET_AVE=ice.SP02_OPER_CAN_GET_AVERAGING_RATE.VALUE;
	private final String OPER_CAN_SET_AVE=ice.SP02_OPER_CAN_SET_AVERAGING_RATE.VALUE;
	private final String AVE_RATE=ice.SP02_AVERAGING_RATE.VALUE;

	private static final String SAMPLE_FILE_DIR="sample_file_dir";
	/**
	 * The max average rate that is "OK" to use in this app.  Should be configurable elsewhere?
	 */
	private final int MAX_AVE_RATE=4;
	
	/**
	 * The "current" patient, used to determine if the patient has changed
	 */
	private Patient currentPatient;
	
	private static final Logger log = LoggerFactory.getLogger(JavaFXWaveformPane.class);
	
	
	public void set(DeviceListModel dlm, NumericFxList numeric, OximetryAveragingObjectiveDataWriter writer, MDSHandler mdsHandler) {
		this.dlm=dlm;
		this.numeric=numeric;
		this.writer=writer;
		this.mdsHandler=mdsHandler;
	}
	
	class DeviceChangeListener implements ChangeListener<Device> {

		@Override
		public void changed(ObservableValue<? extends Device> observable, Device oldValue, Device newValue) {
			handleDeviceChange(newValue);
		}
	}
	
	DeviceChangeListener deviceChangeListener=new DeviceChangeListener();
	
	public void start(EventLoop eventLoop, Subscriber subscriber) {
		dlm.getContents().forEach( d-> {
			//How can we get "all oximetry devices"?  For now, just add all devices.
			oximeters.getItems().add(d);
		});
		
		dlm.getContents().addListener(new ListChangeListener<Device>() {
			@Override
			public void onChanged(Change<? extends Device> change) {
				while(change.next()) {
					change.getAddedSubList().forEach( d -> {
						oximeters.getItems().add(d);
					});
					change.getRemoved().forEach( d-> {
						oximeters.getItems().remove(d);
					});
				}
			}
		});
		
		oximeters.getSelectionModel().selectedItemProperty().addListener(deviceChangeListener);
		listenerPresent=true;
		
		oximeters.setCellFactory(new Callback<ListView<Device>,ListCell<Device>>() {

			@Override
			public ListCell<Device> call(ListView<Device> device) {
				return new DeviceListCell();
			}
			
		});
		
		oximeters.setConverter(new StringConverter<Device>() {

			@Override
			public Device fromString(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String toString(Device arg0) {
				// TODO Auto-generated method stub
				return arg0.getModel();
			}
			
		});
		
		setButton.setOnAction(e-> {
			setTheAverageTime(Integer.parseInt(requestedAverage.getText()));
		});
		
		chooseFile.setOnAction(e-> {
			chooseCSVFile();
		});
		
		runSequence.setOnAction(e-> {
			try {
				startSequence();
			} catch (IOException ioe) {
				Alert exceptionAlert=new Alert(AlertType.ERROR, "Could not open the selected file", ButtonType.OK);
			}
		});
		
		clearGraph.setOnAction(e-> {
			clearGraph();
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
		            	clearGraph();
		            	return;	//Nothing else to do.
		            }
		            if( ! currentPatient.mrn.equals(p.mrn) ) {
		            	//Patient has changed
		            	currentPatient=p;
		            	clearGraph();
		            }
		            
		            //deviceUdiToPatientMRN.put(c.unique_device_identifier, p);
		        }
		    }
			
		});
		
	}
	
	/**
	 * Clear the current graph.  Refactored to allow invocation from a different event handler thread,
	 * using the standard check.  Which is possibly overkill (as we could likely just always call runLater
	 * even if we were on the GUI thread, but it does the job.
	 */
	private void clearGraph() {
		if(Platform.isFxApplicationThread()) {
			reallyClearGraph();
		} else {
			Platform.runLater(new Runnable() {
				public void run() {
					reallyClearGraph();
				}
			});
		}
	}
	
	private void reallyClearGraph() {
		if(lineChart==null || lineChart.getData()==null) {
			//No chart yet - possibly/probably from an MDSEvent invocation.  Just return to avoid any exceptions.
			return;
		}
		lineChart.getData().remove(0, lineChart.getData().size());
		//Also reset the background
		unsetAlarmCondition();
	}
	
	private void chooseCSVFile() {

		Preferences prefs=Preferences.userNodeForPackage(OximetryTestApplication.class);
		String baseDir=prefs.get(SAMPLE_FILE_DIR, System.getProperty("user.home"));

		FileChooser chooser=new FileChooser();
		ExtensionFilter ef=new ExtensionFilter("CSV Files", "*.csv");
		chooser.getExtensionFilters().add(ef);
		chooser.setSelectedExtensionFilter(ef);
		chooser.setInitialDirectory(new File(baseDir));
		File selected=chooser.showOpenDialog(oximeters.getParent().getScene().getWindow());
		if(selected!=null) {
			prefs.put(SAMPLE_FILE_DIR, selected.getParent());
			selectedFile.setText(selected.getAbsolutePath());
			//setInputFile(selected);
		} //else, cancelled...
	}
	
	private void startSequence() throws IOException {
		ArrayList<Integer> times=new ArrayList<>();
		ArrayList<Float> values=new ArrayList<>();
		String s;
		try(BufferedReader br=Files.newBufferedReader(new File(selectedFile.getText()).toPath())) {
			while( (s=br.readLine()) != null) {
				String[] split=s.split(",");
				try {
					times.add(Integer.parseInt(split[0]));
					values.add(Float.parseFloat(split[1]));
				} catch (NumberFormatException nfe) {
					log.info("Skipping line "+s+" as not a number");
				}
			}
		}
		createAndRunChart(times, values);
	}
	
	private void createAndRunChart(ArrayList<Integer> x, ArrayList<Float> y) {
		//Mostly copied, to start with, from org.mdpnp.apps.testapps.chart.Chart
		//Moved validation to the top, so we can avoid creating series etc. that we then don't use
        int delay;
        try {
        	delay=Integer.parseInt(graphDelay.getText());
        } catch (NumberFormatException nfe) {
        	delay=1000;
        }
        if(delay<1) {
        	Alert alert=new Alert(AlertType.ERROR, "Run speed must be a positive value",ButtonType.OK);
        	alert.showAndWait();
        	return;
        }
        
        //Validate the alarm threshold
        String thresholdString=simpleAlarmThreshold.getText();
        int simpleThreshold;
        try {
        	simpleThreshold=Integer.parseInt(thresholdString);
        } catch (NumberFormatException nfe) {
        	Alert alert=new Alert(AlertType.ERROR,"Alarm Threshold must be a valid number", ButtonType.OK);
        	alert.showAndWait();
        	return;
        }
        if(simpleThreshold<1 || simpleThreshold>100) {
        	Alert alert=new Alert(AlertType.ERROR,"Alarm Threshold must be between 1 and 100", ButtonType.OK);
        	alert.showAndWait();
        	return;
        }
        
        //Validate the smart alarm count
        String smartString=smartAlarmCount.getText();
        //Slightly different validation here, as this is not required.
        int smartCount;
        if(smartString.trim().isEmpty()) {
        	smartCount=0;	//We will treat empty string as 0 and smartCount=0 as disabled.
        } else {
        	try {
        		smartCount=Integer.parseInt(smartString);
        	} catch (NumberFormatException nfe) {
        		Alert alert=new Alert(AlertType.ERROR,"Smart Alarm Count must be a valid number", ButtonType.OK);
            	alert.showAndWait();
            	return;
        	}
        	//Any more validation?
        }
        
        //At start of run, reset and alarm condition from end of previous run
        unsetAlarmCondition();
		NumberAxis xAxis = new NumberAxis();
		NumberAxis yAxis = new NumberAxis();
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setMinHeight(250.0);
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);
        
        lineChart.setTitle("Pulse Oximeter Analysis");
        lineChart.setData(series);
        BorderPane.setAlignment(lineChart, Pos.CENTER);
//        v.addListener(valueListener);
//        v.forEach((t)->add(t));
        xAxis.setAutoRanging(false);
        yAxis.setForceZeroInRange(false);
        yAxis.setAutoRanging(false);
        xAxis.setAnimated(false);
        yAxis.setAnimated(false);
        //Scan through y for upper and lower;
        float yMin=y.get(0);
        float yMax=yMin;
        for(int i=0;i<y.size();i++) {
        	float f=y.get(i);
        	if(f<yMin) yMin=f;
        	if(f>yMax) yMax=f;
        }
        //Set y axis max to 100
        yAxis.setUpperBound(100);
        //Slightly pad the lower limit
        //Use round on the lower bound to force an integer value on the axis label
        yAxis.setLowerBound(Math.round(yMin-2));
        yAxis.setTickUnit(5);
        xAxis.setUpperBound(x.get(x.size()-1));
        xAxis.setMinorTickVisible(false);
        xAxis.tickUnitProperty().set(50);
        
        //Create a series that represents the alarm threshold.
        Series<Number,Number> thresholdSeries=new Series<>();
        thresholdSeries.setName("Alarm Threshold");
        series.add(thresholdSeries);
        for(int i=0;i<x.size();i++) {
        	thresholdSeries.getData().add(new XYChart.Data<Number, Number>(x.get(i), simpleThreshold));
        }
        
        //If we loop through our x and y values here, we can sleep or schedule an executor to do it later
        Series<Number, Number> spO2=new Series<>();
        spO2.setName("SpO2");
        series.add(spO2);
        
        main.setCenter(lineChart);
        
        Timeline tl = new Timeline();
//        ThreadLocal<Integer> i=new ThreadLocal<Integer>();
        AtomicInteger i=new AtomicInteger(0);
        AtomicInteger timesBelowThreshold=new AtomicInteger(0);
        AtomicBoolean dipped=new AtomicBoolean(false);
//        int i=0;
        
        //Before possibly hitting an alarm condition, preserve the current background
        nonAlarmBackground=oximeters.getBackground();
        
        
        tl.getKeyFrames().add(new KeyFrame(
        	Duration.millis(delay), 
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                	int index=i.getAndIncrement();
                	int xx=x.get(index);
                	float yy=y.get(index);
                	XYChart.Data<Number,Number> point=new XYChart.Data<>(xx, yy);
                	
                	//TODO:  - Should we do "alarm condition detection" here or in a separate thread?
                	
                	//Alarm handling
                	if(smartCount==0) {
                		//Smart alarms are disabled - simple alarm condition only
                		if(yy<simpleThreshold) {
                			if(!inAlarm) {
                				//This is the transition point
                				log.info("Need to add an alarm node for xx="+xx);
                				Circle circle=new Circle();
                				circle.setRadius(5f);
                				circle.fillProperty().set(Color.RED);
                				point.nodeProperty().set(circle);
                			}
                			setAlarmCondition();
                		} else {
                			unsetAlarmCondition();
                		}
                	} else {
                		//Smart alarms are enabled
                		if(yy<simpleThreshold) {
	                		if(!dipped.get()) {
	                			dipped.set(true);
	                			int currentSmart=timesBelowThreshold.incrementAndGet();
	                			if(currentSmart==smartCount) {
	                				timesBelowThreshold.set(0);		//Ditch this later when we want alarm to stay on
	                				if(!inAlarm) {
		                				Circle circle=new Circle();
		                				circle.setRadius(5f);
		                				circle.fillProperty().set(Color.RED);
		                				point.nodeProperty().set(circle);
	                				}
	                				setAlarmCondition();
	                			}
	                		}
                		} else {
//                			System.err.println("Calling unsetAlarmCondition with smart alarms enabled at point "+xx);
                			//UNCOMMENT THE NEXT LINE IF WE WANT SMART ALARM RESETS
//                			unsetAlarmCondition();
                			if(dipped.get()) {
                				dipped.set(false);
                			}
                		}
                	}
                	spO2.getData().add(point);
                	
                	
                	
//                	if(yy<simpleThreshold) {
//                		//Simple alarm threshold - IF smart alarm is disabled
//                		if(smartCount==0) {
//                			//Smart alarms are disabled - just set the simple alarm condition
//                			if(inAlarm) {
//                				//Already alarm - remove nodes
//                				point.nodeProperty().set(null);
//                			}
//                			setAlarmCondition();
//                		} else {
//                			if(!dipped.get()) {
//                				//We were not previously below the threshold, now we are...
//                				dipped.set(true);
//	                			int currentSmart=timesBelowThreshold.incrementAndGet();
////	                			System.err.println("currentSmart is "+currentSmart+" at index "+index);
//	                			if(currentSmart==smartCount) {
//	                				setAlarmCondition();
//	                				timesBelowThreshold.set(0);	//Reset the count
//	                			}
//                			}
//                		}
//                	} else {
//                		point.nodeProperty().set(null);
//                		if(inAlarm) {
//	                		unsetAlarmCondition();
//                		} else {
//                			
//                		}
//                		if(dipped.get()) {
//                			//We were in a possible alarm state, but now aren't
//                			dipped.set(false);
////                			System.err.println("set dipped to false at index at index "+index);
//                		}
//                	}
                }
        }));
        
        
        tl.setCycleCount(x.size());
        tl.play();
        
//        for(int i=0;i<x.size();i++) {
//        	series.get(0).getData().add(new XYChart.Data<Number, Number>(x.get(i), y.get(i)));
//        	try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        	s.getData().add(new XYChart.Data<Number, Number>(x.get(i), y.get(i)));
//        }
        log.info("Series s has "+spO2.getData().size()+" elements");
//        lineChart.getData().addAll(series);
//        lineChart.getData().add(s);
        /*
        Series<Number, Number> fourSecRepeated=new Series<>();
        fourSecRepeated.setName("4 sec avg");
        int overFour=s.getData().size()/4;
        for(int i=0;i<overFour;i++) {
        	float nextAve= ( y.get((i*4)+0) + y.get((i*4)+1) + y.get((i*4)+2) + y.get((i*4)+3) ) / 4;
        	for(int j=0;j<4;j++) {
        		fourSecRepeated.getData().add(new XYChart.Data<Number, Number>( (i*4)+j, nextAve));
        	}
        }
        series.add(fourSecRepeated);
        
        Series<Number, Number> eightSecRepeated=new Series<>();
        eightSecRepeated.setName("8 sec avg");
        int overEight=s.getData().size()/8;
        for(int i=0;i<overEight;i++) {
        	float nextAve= ( y.get((i*8)+0) + y.get((i*8)+1) + y.get((i*8)+2) + y.get((i*8)+3) +
        			y.get((i*8)+4) + y.get((i*8)+5) + y.get((i*8)+6) + y.get((i*8)+7)) / 8;
        	for(int j=0;j<8;j++) {
        		eightSecRepeated.getData().add(new XYChart.Data<Number, Number>( (i*8)+j, nextAve));
        	}
        }
        series.add(eightSecRepeated);

        Series<Number, Number> sixteenSecRepeated=new Series<>();
        sixteenSecRepeated.setName("16 sec avg");
        int overSixteen=s.getData().size()/16;
        for(int i=0;i<overSixteen;i++) {
        	float nextAve= ( y.get((i*16)+0) + y.get((i*16)+1) + y.get((i*16)+2) + y.get((i*16)+3) +
        			y.get((i*16)+4) + y.get((i*16)+5) + y.get((i*16)+6) + y.get((i*16)+7) +
        			y.get((i*16)+8) + y.get((i*16)+9) + y.get((i*16)+10) + y.get((i*16)+11) +
        			y.get((i*16)+12) + y.get((i*16)+13) + y.get((i*16)+14) + y.get((i*16)+15)) / 16;
        	for(int j=0;j<16;j++) {
        		sixteenSecRepeated.getData().add(new XYChart.Data<Number, Number>( (i*16)+j, nextAve));
        	}
        }
        series.add(sixteenSecRepeated);
        */
        
        
	}
	
	private void setAlarmCondition() {
		if(!inAlarm) {
//		System.err.println("Alarm condition ON");
			main.setBackground(alarmBackground);
			inAlarm=true;
		}
	}

	private void unsetAlarmCondition() {
//		System.err.println("Alarm condition OFF");
		if(inAlarm) {
			main.setBackground(nonAlarmBackground);
			inAlarm=false;
		}
	}
	
	private void handleDeviceChange(Device newDevice) {
		boolean[] iceCanGetAve=new boolean[1];
		boolean[] iceCanSetAve=new boolean[1];
		boolean[] opCanGetAve=new boolean[1];
		boolean[] opCanSetAve=new boolean[1];
		int[] ave=new int[1];
		log.info("OTA.handleDeviceChange newDevice is "+newDevice);
		if(null==newDevice) return;	//No device selected and/or available - can happen when patient is changed and no devices for that patient
		numeric.forEach( n -> {
			log.info("handleDeviceChange numeric dev ident is "+n.getUnique_device_identifier()+" "+n.getMetric_id());
			if( ! n.getUnique_device_identifier().equals(newDevice.getUDI())) return;	//Some other device
			//When we get here, we are looking at a property for the currently selected device
			if(n.getMetric_id().equals(SOFT_CAN_GET_AVE) && n.getValue()>0f) iceCanGetAve[0]=true;
			if(n.getMetric_id().equals(SOFT_CAN_SET_AVE) && n.getValue()>0f) iceCanSetAve[0]=true;
			if(n.getMetric_id().equals(OPER_CAN_GET_AVE) && n.getValue()>0f) opCanGetAve[0]=true;
			if(n.getMetric_id().equals(OPER_CAN_SET_AVE) && n.getValue()>0f) opCanSetAve[0]=true;
			if(n.getMetric_id().equals(AVE_RATE) && n.getValue()>0f) ave[0]=Math.round(n.getValue());
			if(n.getMetric_id().equals(AVE_RATE)) {
				n.valueProperty().addListener(new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
						log.info("OTA.changed vals are "+arg1+" "+arg2);
						currentAverage.setText(Integer.toString(arg2.intValue()));
//						Thread.dumpStack();
						ave[0]=arg2.intValue();
					}
					
				});
			}
		});
		log.info("After forEach numerics, canGetAve is "+iceCanGetAve[0]);
		log.info("After forEach numerics, outsideAve is "+ave[0]);
		if(!iceCanGetAve[0]) {
			//Case 2
			Alert alert=new Alert(Alert.AlertType.CONFIRMATION,"OpenICE cannot automatically determine the averaging time for this device\n"+
					"Can you confirm that it is less than 4 seconds?",
					new ButtonType[] {ButtonType.YES, ButtonType.NO});
			Optional<ButtonType> result=alert.showAndWait();
			if(result.isPresent() && result.get()==ButtonType.YES) {
				//Great - this is OK.  Store it somewhere maybe?
				//4 and 5 combined - should "can you get the average" and "is it 4 seconds or less" be separate questions?
				showGoodAlert();
				//IN THIS LOGIC, WE DON'T FIND OUT IF IT'S SETTABLE OR NOT.
				updateControls(iceCanSetAve[0], "<=4");
			} else {
				//19th July...
				//Can the operator set the average.
				if(opCanSetAve[0]) {
					Optional<ButtonType> okOrCancel=promptToSetAverage();
					if(okOrCancel.isPresent() && okOrCancel.get()==ButtonType.OK) {
						/*
						 * Operator says they have set the average to 4.  In this branch of the code,
						 * ice cannot get the average, so we must just assume they have
						 */
						showGoodAlert();
						return;
					} else {
						showRejectedAlert();
						return;
					}
				}
				//Case 3
				showRejectedAlert();
				disableAllControls();
			}
			//Alert alert=new Alert(Alert.AlertType.ERROR,"The average rate for this device is not known",ButtonType.OK);
			//alert.showAndWait();
			return;
		}
		//This is case 1 - ICE can get the averaging rate.
		if(ave[0]<=MAX_AVE_RATE) {
			//Case 5
			showGoodAlert();
			updateControls(iceCanSetAve[0], Integer.toString(ave[0]));
			return;
		}
		//Here, we are at case 6 - average is over the limit
		//Can we set it?
		if(iceCanSetAve[0]) {
			//Case 10
			setTheAverageTime(4);
			showGoodAlert();
			updateControls(iceCanSetAve[0],Integer.toString(ave[0]));
			return;
		}
		//Case 7 - ICE cannot set.
		if(opCanSetAve[0]) {
			StringBuilder message=new StringBuilder();
//			if()
			//Case 9 - Operator can set it.
			Alert alert=new Alert(Alert.AlertType.CONFIRMATION,"The average for this device is "+ave[0]+".  Please confirm you have set the average to "+MAX_AVE_RATE+" or less");
			alert.showAndWait();
			showGoodAlert();
			updateControls(iceCanSetAve[0],"<=4");
			return;
		}
		//Case 8 - Average can't be set.
		showRejectedAlert();
		disableAllControls();
		return;
	}
	
	private Optional<ButtonType> promptToSetAverage() {
		Alert alert=new Alert(Alert.AlertType.CONFIRMATION,"Please set the device averaging time to 4 seconds",
				new ButtonType[] {ButtonType.OK, ButtonType.CANCEL});
		return alert.showAndWait();
	}
	
	private void updateControls(boolean iceCanSetAve, String currentAve) {
		if(iceCanSetAve) {
//			requestedAverage.setText("");
			requestedAverage.setDisable(false);
			setButton.setDisable(false);
		} else {
			//We can't set the average, so disable the controls
			requestedAverage.setText("Not Available");
			requestedAverage.setDisable(true);
			setButton.setDisable(true);
		}
		currentAverage.setText(currentAve);
		//currentAverage should not be editable
		currentAverage.setEditable(false);
//		Thread.dumpStack();
	}
	
	private void disableAllControls() {
		requestedAverage.setText("");
		requestedAverage.setDisable(true);
		setButton.setDisable(true);
	}
	
	private void setTheAverageTime(int desiredAverage) {
		requestedAverage.setText(Integer.toString(desiredAverage));
		ice.OximetryAveragingObjective objective=new ice.OximetryAveragingObjective();
		objective.newAverageTime=desiredAverage;
		objective.unique_device_identifier=oximeters.getValue().getUDI();
		writer.write(objective, InstanceHandle_t.HANDLE_NIL);
		log.info("Published an objective for average time "+desiredAverage);
	}
	
	private void showGoodAlert() {
		Alert goodAlert=new Alert(Alert.AlertType.INFORMATION,"This Oximeter can be used with this app",ButtonType.OK);
		goodAlert.showAndWait();
	}
	
	private void showRejectedAlert() {
		Alert rejectedAlert=new Alert(Alert.AlertType.ERROR,"This Oximeter cannot be used with this app",ButtonType.OK);
		rejectedAlert.showAndWait();
	}
	
	public void stop() {
		log.info("OTA.stop called");
		if(listenerPresent) {
			oximeters.getSelectionModel().selectedItemProperty().removeListener(deviceChangeListener);
			listenerPresent=false;
		}
	}
	
	public void activate() {
		log.info("OTA.activate called");
		if(!listenerPresent) {
			oximeters.getSelectionModel().selectedItemProperty().addListener(deviceChangeListener);
			listenerPresent=true;
		}
	}
	
	public OximetryTestApplication() {
		
	}
	
	class DeviceListCell extends ListCell<Device> {
        @Override protected void updateItem(Device device, boolean empty) {
            super.updateItem(device, empty);
            if (!empty && device != null) {
                setText(device.getModel());
            } else {
                setText(null);
            }
        }
    }
	
	private static class ValueSeriesListener { 
        public XYChart.Series<Number, Number> s;
        public Value v;
        public ChangeListener<Number> l;
    }
	
	@Subscribe
    public void onPartitionChooserChangeEvent(PartitionChooserModel.PartitionChooserChangeEvent evt) {
		log.info("Partition change in OTA...");
	}

}
