package org.mdpnp.apps.testapp.export;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import org.mdpnp.apps.testapp.DialogUtils;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.export.DataCollectorAppFactory.PersisterUIController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

public class DataCollectorApp implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(DataCollectorApp.class);

    @FXML protected TreeView<Object> tree;
    @FXML protected TableView<Row> table;
    @FXML protected SplitPane masterPanel;
    @FXML protected BorderPane persisterContainer;
    @FXML protected Button startControl;
    @FXML protected MenuButton addDataSampleControl;
    @FXML protected VBox btns;
    @FXML protected CheckBox rawTimes;
    
    protected ObservableList<Row> tblModel = FXCollections.observableArrayList();

    protected static class Row {
        private final String uniqueDeviceIdentifier, instanceId, metricId, devTime;
        private final float value;
        
        public Row(final String uniqueDeviceIdentifier, final String instanceId, 
                   final String metricId, final String devTime, final float value) {
            this.uniqueDeviceIdentifier = uniqueDeviceIdentifier;
            this.instanceId = instanceId;
            this.metricId = metricId;
            this.devTime = devTime;
            this.value = value;
        }
        
        public float getValue() {
            return value;
        }
        
        public String getDevTime() {
            return devTime;
        }
        public String getInstanceId() {
            return instanceId;
        }
        public String getMetricId() {
            return metricId;
        }
        public String getUniqueDeviceIdentifier() {
            return uniqueDeviceIdentifier;
        }
    }
    
    private DataCollector[]   dataCollectors;
    private DataFilter        dataFilter;
    private final DeviceTreeModel deviceTreeModel = new DeviceTreeModel();
    private DeviceListModel deviceListModel;

    private List<PersisterUIController> supportedPersisters = new ArrayList<>();
    protected PersisterUIController currentPersister;
    
    public DataCollectorApp() {
        
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        boolean b = Boolean.getBoolean("DataCollectorApp.debug");
        addDataSampleControl.setVisible(b);
    }
    
    public DataCollectorApp set(DeviceListModel dlm, DataCollector dcs[], URL persist[]) throws IOException {

        deviceListModel = dlm;
        table.setItems(tblModel);

        // hold on to the references so that we we can unhook the listeners at the end
        //
        dataCollectors   = dcs;

        // device list model maintains the list of what is out there.
        // add a listener to it so that we can dynamically build a tree representation
        // of that information.
        //
        deviceListModel.getContents().addListener(deviceTreeModel);

        for(DataCollector dc : dataCollectors) {
            dc.addDataSampleListener(deviceTreeModel);
        }

        // create a data filter - it will act as as proxy between the data collector and
        // actual data consumers. all internal components with register with it for data
        // events.
        dataFilter = new DataFilter(deviceTreeModel);

        // add self as a listener so that we can show some busy
        // data in the central panel.
        //
        for(DataCollector dc : dataCollectors) {
            dc.addDataSampleListener(dataFilter);
        }

        dataFilter.addDataSampleListener(this);

        tree.setCellFactory(new Callback<TreeView<Object>,TreeCell<Object>>() {

            @Override
            public TreeCell<Object> call(TreeView<Object> param) {
                return new DeviceTreeCell();
            }
            
        });

        tree.setShowRoot(false);
        tree.setRoot(deviceTreeModel);

        final ToggleGroup group = new ToggleGroup();
        StackPane cards = new StackPane();
        persisterContainer.setCenter(cards);


        for (URL u : persist) {
            FXMLLoader loader = new FXMLLoader(u);
            Node parent = loader.load();
            final PersisterUIController controller = loader.getController();
            controller.setup();
            parent.setVisible(false);
            cards.getChildren().add(parent);
            String name = controller.getName();
            RadioButton btn = new RadioButton(name);
            btn.setPadding(new Insets(5, 5, 0, 0));
            btn.setUserData(controller);
            btns.getChildren().add(btn);
            group.getToggles().add(btn);
            supportedPersisters.add(controller);
            btn.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {

                    // Is the current persister running? Stop it first.
                    //
                    if("Stop".equals(startControl.getText())) {
                        clickStart(new ActionEvent());
                    }

                    currentPersister = controller;
                    for(Node n : cards.getChildren()) {
                        n.setVisible(false);
                    }
                    
                    parent.setVisible(true);
                }
            });
        }
        ((RadioButton)btns.getChildren().get(0)).fire();

        if(addDataSampleControl.isVisible()) {
            deviceListModel.getContents().add(new Device("MOCKED-DEVICE"));
            deviceListModel.getContents().add(new Device("MOCKED-NURSE"));
        }

        return this;
    }

    @FXML
    public void clickStart(ActionEvent evt) {
        if("Start".equals(startControl.getText()) && currentPersister != null) {
            boolean v;
            try {
                v = currentPersister.start();
                if (v) {
                    dataFilter.addDataSampleListener(currentPersister);
                    startControl.setText("Stop");
                }
            } catch (Exception e) {
                log.warn("Exception displayed to user", e);
                DialogUtils.ExceptionDialog(e);
            }

        } else if("Stop".equals(startControl.getText()) && currentPersister != null) {
            dataFilter.removeDataSampleListener(currentPersister);
            try {
                currentPersister.stop();
            } catch (Exception e) {
                log.warn("Exception displayed to user", e);
                DialogUtils.ExceptionDialog(e);
            }
            startControl.setText("Start");
        }

    }
    
    @FXML
    public void toggleRawDate(ActionEvent evt) {
    	currentPersister.setRawDateFormat(rawTimes.isSelected());
    }

    @FXML
    public void addDataSample(ActionEvent evt)  {

        String cmd = ((MenuItem)evt.getSource()).getText();
        Object data = getMockedSample(cmd);

        try {
            for(DataCollector dc : dataCollectors) {
                Method m = ReflectionUtils.findMethod(dc.getClass(), "add", data.getClass());
                if (m != null) {
                    m.invoke(dc, data);
                }
            }
        }
        catch(Exception ex) {
            DialogUtils.ExceptionDialog(ex);
        }
    }

    Object getMockedSample(String dateType) {
        switch(dateType) {
            default:
            case "Numeric":     return mockNumeric();
            case "SampleArray": return mockSampleArray();
            case "Observation": return mockPatientAssessment();
        }
    }

    private Object mockNumeric() {
        Object v =  NumericsDataCollector.toValue("MOCKED-DEVICE", "MOCKED_NUMERIC", 0, new Date(), 10.0*Math.random());
        return v;
    }

    private Object mockSampleArray() {
        Double arr[] = new Double[10];
        for(int i=0; i<arr.length; i++) {
            arr[i] = 10.0*Math.random();
        }
        Object v =  SampleArrayDataCollector.toValue("MOCKED-DEVICE", "MOCKED_NUMERIC", 0, new Date(), arr);
        return v;
    }

    private Object mockPatientAssessment() {
        Object v =  PatientAssessmentDataCollector.toValue("MOCKED-NURSE", new Date(), "123", "Fell off the bed");
        return v;
    }

    public void stop() throws Exception {
        for(DataCollector dc : dataCollectors) {
            dc.removeDataSampleListener(dataFilter);
        }

        deviceListModel.getContents().removeListener(deviceTreeModel);

        // if current persister is running, stop it now.
        //
        if("Stop".equals(startControl.getText()) && currentPersister != null) {
            dataFilter.removeDataSampleListener(currentPersister);
            try {
                currentPersister.stop();
            } catch (Exception e) {
                log.error("Failed to stop active persister " +  currentPersister.getName());
            }
        }
    }

    @Subscribe
    public void handleDataSampleEvent(NumericsDataCollector.NumericSampleEvent evt) throws Exception {
        // Add to the screen for visual.
        long ms = evt.getDevTime();
        String devTime = rawTimes.isSelected() ? Long.toString(ms) : DataCollector.dateFormats.get().format(new Date(ms));
        final Row row = new Row(evt.getUniqueDeviceIdentifier(), ""+evt.getInstanceId(),
                evt.getMetricId(), devTime, (float) evt.getValue());
        Platform.runLater(new Runnable() {
            public void run() {
                tblModel.add(0, row);
                if(tblModel.size()>250) {
                    tblModel.subList(250, tblModel.size()).clear();
                }
                
            }
        });
    }
}
