package org.mdpnp.apps.testapp.oximetry;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.rtiapi.data.EventLoop;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.subscription.Subscriber;

import ice.OximetryAveragingObjectiveDataWriter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
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
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class OximetryTestApplication {
	
	private DeviceListModel dlm;
	private NumericFxList numeric;
	private OximetryAveragingObjectiveDataWriter writer;
	
	@FXML protected LineChart<Number, Number> lineChart;
	final ObservableList<XYChart.Series<Number, Number>> series = FXCollections.observableArrayList();
    private final List<ValueSeriesListener> values = new ArrayList<ValueSeriesListener>();
	@FXML private ComboBox<Device> oximeters;
	@FXML private TextField requestedAverage;
	@FXML private TextField currentAverage;
	@FXML private TextField graphDelay;
	@FXML private TextField simpleAlarmThreshold;
	@FXML private Button setButton;
	@FXML private Button chooseFile;
	@FXML private BorderPane main;
	
	private Background nonAlarmBackground;
	private Background alarmBackground=new Background(new BackgroundFill(Color.RED, null, null));
	
	private boolean listenerPresent;
	
	
	private final String SOFT_CAN_GET_AVE=ice.SP02_SOFT_CAN_GET_AVERAGING_RATE.VALUE;
	private final String SOFT_CAN_SET_AVE=ice.SP02_SOFT_CAN_SET_AVERAGING_RATE.VALUE;
	private final String OPER_CAN_GET_AVE=ice.SP02_OPER_CAN_GET_AVERAGING_RATE.VALUE;
	private final String OPER_CAN_SET_AVE=ice.SP02_OPER_CAN_SET_AVERAGING_RATE.VALUE;
	private final String AVE_RATE=ice.SP02_AVERAGING_RATE.VALUE;
	/**
	 * The max average rate that is "OK" to use in this app.  Should be configurable elsewhere?
	 */
	private final int MAX_AVE_RATE=4;
	
	public void set(DeviceListModel dlm, NumericFxList numeric, OximetryAveragingObjectiveDataWriter writer) {
		this.dlm=dlm;
		this.numeric=numeric;
		this.writer=writer;
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
			try {
				chooseCSVFile();
			} catch (IOException ioe) {
				Alert exceptionAlert=new Alert(AlertType.ERROR, "Could not open the selected file", ButtonType.OK);
			}
		});
	}
	
	private void chooseCSVFile() throws IOException {
		FileChooser chooser=new FileChooser();
		ExtensionFilter ef=new ExtensionFilter("CSV Files", "*.csv");
		chooser.getExtensionFilters().add(ef);
		chooser.setSelectedExtensionFilter(ef);
		File selected=chooser.showOpenDialog(oximeters.getParent().getScene().getWindow());
		setInputFile(selected);
	}
	
	private void setInputFile(File input) throws IOException {
		ArrayList<Integer> times=new ArrayList<>();
		ArrayList<Float> values=new ArrayList<>();
		String s;
		try(BufferedReader br=Files.newBufferedReader(input.toPath())) {
			while( (s=br.readLine()) != null) {
				String[] split=s.split(",");
				try {
					times.add(Integer.parseInt(split[0]));
					values.add(Float.parseFloat(split[1]));
				} catch (NumberFormatException nfe) {
					System.err.println("Skipping line "+s+" as not a number");
				}
			}
		}
		createAndRunChart(times, values);
	}
	
	private void createAndRunChart(ArrayList<Integer> x, ArrayList<Float> y) {
		//Mostly copied, to start with, from org.mdpnp.apps.testapps.chart.Chart
		NumberAxis xAxis = new NumberAxis();
		NumberAxis yAxis = new NumberAxis();
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setMinHeight(250.0);
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);
        
        lineChart.setTitle("Respiratory depression detection");
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
        yAxis.setUpperBound(yMax);
        yAxis.setLowerBound(yMin);
        xAxis.setUpperBound(x.get(x.size()-1));
        
        //Create a series that represents the alarm threshold.
        String thresholdString=simpleAlarmThreshold.getText();
        int simpleThreshold=Integer.parseInt(thresholdString);
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
        
        int delay;
        try {
        	delay=Integer.parseInt(graphDelay.getText());
        } catch (NumberFormatException nfe) {
        	delay=1000;
        }
        
        Timeline tl = new Timeline();
//        ThreadLocal<Integer> i=new ThreadLocal<Integer>();
        AtomicInteger i=new AtomicInteger(0);
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
                	spO2.getData().add(new XYChart.Data<Number, Number>(xx, yy));
                	//TODO:  - Should we do "alarm condition detection" here or in a separate thread?
                	if(yy<simpleThreshold) {
                		//Simple alarm threshold.
                		setSimpleAlarmCondition();
                	} else {
                		unsetSimpleAlarmCondition();
                	}
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
        System.err.println("Series s has "+spO2.getData().size()+" elements");
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
	
	private void setSimpleAlarmCondition() {
		main.setBackground(alarmBackground);
	}

	private void unsetSimpleAlarmCondition() {
		main.setBackground(nonAlarmBackground);
	}
	
	private void handleDeviceChange(Device newDevice) {
		boolean[] iceCanGetAve=new boolean[1];
		boolean[] iceCanSetAve=new boolean[1];
		boolean[] opCanGetAve=new boolean[1];
		boolean[] opCanSetAve=new boolean[1];
		int[] ave=new int[1];
		numeric.forEach( n -> {
			System.err.println("handleDeviceChange numeric dev ident is "+n.getUnique_device_identifier()+" "+n.getMetric_id());
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
						System.err.println("OTA.changed vals are "+arg1+" "+arg2);
						currentAverage.setText(Integer.toString(arg2.intValue()));
						Thread.dumpStack();
						ave[0]=arg2.intValue();
					}
					
				});
			}
		});
		System.err.println("After forEach numerics, canGetAve is "+iceCanGetAve[0]);
		System.err.println("After forEach numerics, outsideAve is "+ave[0]);
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
			requestedAverage.setText("");
			requestedAverage.setDisable(true);
			setButton.setDisable(true);
		}
		currentAverage.setText(currentAve);
		Thread.dumpStack();
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
		System.err.println("Published an objective for average time "+desiredAverage);
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
		System.err.println("OTA.stop called");
		if(listenerPresent) {
			oximeters.getSelectionModel().selectedItemProperty().removeListener(deviceChangeListener);
			listenerPresent=false;
		}
	}
	
	public void activate() {
		System.err.println("OTA.activate called");
		if(!listenerPresent) {
			oximeters.getSelectionModel().selectedItemProperty().addListener(deviceChangeListener);
			listenerPresent=true;
		}
	}
	
	public OximetryTestApplication() {
		System.err.println("OTA constructor...");
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

}
