package org.mdpnp.apps.testapp.patient;

import ice.MDSConnectivity;
import ice.MDSConnectivityObjective;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.devices.MDSHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.List;
import java.util.function.Predicate;

public class PatientInfoController implements ListChangeListener<Device>, MDSHandler.Connectivity.MDSListener {

    private static final Logger log = LoggerFactory.getLogger(PatientInfoController.class);

    private DeviceListModel deviceListDataModel;
    private DataSource      jdbcDB;
    private MDSHandler      mdsConnectivity;

    @FXML ComboBox<Device> deviceList;
    @FXML ComboBox<PatientInfo> patientList;
    @FXML Button connectBtn;

    @FXML TableView<DevicePatientAssociation> tableView;
    @FXML TableColumn<DevicePatientAssociation, String> tableViewActionColumn;

    protected ObservableList<DevicePatientAssociation> tblModel = FXCollections.observableArrayList();
    protected ObservableList<Device> deviceListModel = FXCollections.observableArrayList();
    protected ObservableList<PatientInfo> patientListModel = FXCollections.observableArrayList();

    public DeviceListModel getDeviceListDataModel() {
        return deviceListDataModel;
    }

    public void setDeviceListDataModel(DeviceListModel dlm) {
        deviceListDataModel = dlm;
    }

    public DataSource getJdbcDB() {
        return jdbcDB;
    }

    public void setJdbcDB(DataSource db) {
        jdbcDB = db;
    }

    public MDSHandler getMdsConnectivity() {
        return mdsConnectivity;
    }

    public void setMdsConnectivity(MDSHandler mdsConnectivity) {
        this.mdsConnectivity = mdsConnectivity;
    }

    /**
     * UI callback from the table when row is selected.
     * @param oldValue
     * @param newValue
     */
    void onAssociationSelected(DevicePatientAssociation oldValue, DevicePatientAssociation newValue)
    {
        /*
        if(oldValue != null) {
            uniqueDeviceIdentifier.textProperty().unbindBidirectional(oldValue.deviceIdentifierProperty());
            patientIdentifier.textProperty().unbindBidirectional(oldValue.patientNameProperty());
            patientName.textProperty().unbindBidirectional(oldValue.patientIdentifierProperty());
        }
        
        uniqueDeviceIdentifier.textProperty().bindBidirectional(newValue.deviceIdentifierProperty());
        patientIdentifier.textProperty().bindBidirectional(newValue.patientNameProperty());
        patientName.textProperty().bindBidirectional(newValue.patientIdentifierProperty());
        */
    }


    public void removeDeviceAssociation(DevicePatientAssociation assoc)  {
        DevicePatientAssociation.delete(jdbcDB, assoc);
        tblModel.remove(assoc);
        deviceListModel.add(assoc.getDevice());
    }

    void addDeviceAssociation(Device d, PatientInfo p) {
        DevicePatientAssociation assoc = associate(d, p);
        tblModel.add(assoc);
        deviceListModel.remove(d);
    }

    private DevicePatientAssociation associate(Device d, PatientInfo p)  {

        DevicePatientAssociation dpa = DevicePatientAssociation.update(jdbcDB, new DevicePatientAssociation(d, p));

        ice.MDSConnectivityObjective mds=new ice.MDSConnectivityObjective();
        mds.unique_device_identifier = d.getUDI();
        mds.partition = p.getPatientName();
        mdsConnectivity.publish(mds);

        return dpa;
    }

    public void handleDeviceLifecycleEvent(Device d, boolean added)  {

        if(added && !inUse(d))
            deviceListModel.add(d);
        else
            deviceListModel.remove(d);
    }

    /**
     * @return true if device already associated with the patient.
     */
    private boolean inUse(final Device d) {
        FilteredList<DevicePatientAssociation> l = tblModel.filtered(new Predicate<DevicePatientAssociation>() {
            @Override
            public boolean test(DevicePatientAssociation devicePatientAssociation) {
                return devicePatientAssociation.isForDevice(d);
            }
        });
        return l.size()!=0;
    }

    private static class DeviceListCell extends ListCell<Device> {
        @Override
        protected void updateItem(Device item, boolean empty) {
            super.updateItem(item, empty);
            super.setText(getDisplayTextFor(item));
        }
    };

    static String getDisplayTextFor(Device d) {
        if(d == null || d.equals(NO_DEVICE)) {
            return "";
        }
        else {
            String mm = d.getMakeAndModel(); if(mm==null)mm="???";
            String sn = d.getHostname();if(sn==null)sn="???";
            String s=mm+"/"+sn;
            return s;
        }
    }
    /**
     * A table cell containing a button deleting the row.
     **/
    private class DeleteAssociationCell extends TableCell<DevicePatientAssociation, String> {

        final Button button = new Button();
        // pads and centers the add button in the cell.
        final StackPane paddedButton = new StackPane();

        DeleteAssociationCell() {

            try {
                ImageView img = new ImageView(new javafx.scene.image.Image(getClass().getResourceAsStream("stop.png")));
                img.setFitHeight(15.0);
                img.setFitWidth(15.0);
                button.setGraphic(img);
            } catch (Exception noImg) {
                log.error("Failed to load image for disconnect button", noImg);
                button.setText("Disconnect");
            }
            paddedButton.setPadding(new Insets(1));
            paddedButton.getChildren().add(button);

            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    TableRow<DevicePatientAssociation> row = getTableRow();
                    DevicePatientAssociation obj = row.getTableView().getItems().get(row.getIndex());
                    removeDeviceAssociation(obj);
                }
            });
        }

        /**
         * places a button in the row only if the row is not empty.
         **/
        @Override
        protected void updateItem(String item, boolean empty) {
            int row = getTableRow().getIndex();
            super.updateItem(item, empty);
            if (!empty) {
                log.info("render " + row + "->" + item);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(paddedButton);
            }
            else {
                setContentDisplay(ContentDisplay.TEXT_ONLY);
                setGraphic(null);
            }
        }
    }


    /**
     * called by the IOC framework when the component is all wired up and ready to go
     * @throws Exception when things go wrong
     */
    public void start() throws Exception
    {
        deviceListDataModel.getContents().addListener(this);

        Callback<TableColumn<DevicePatientAssociation, String>, TableCell<DevicePatientAssociation, String>> columnFac =
            new Callback<TableColumn<DevicePatientAssociation, String>, TableCell<DevicePatientAssociation, String>>() {
            @Override public TableCell<DevicePatientAssociation, String> call(TableColumn<DevicePatientAssociation, String> personBooleanTableColumn) {
                return new DeleteAssociationCell();
            }
        };

        tableViewActionColumn.setCellFactory(columnFac);

        tableView.setItems(tblModel);

        Callback<ListView<Device>,ListCell<Device>> fac = new Callback<ListView<Device>,ListCell<Device>>() {
            @Override
            public ListCell<Device> call(ListView<Device> param) {
                return new DeviceListCell();
            }
        };

        deviceListModel.add(0, NO_DEVICE);
        patientListModel.add(0, NO_PATIENT);

        deviceList.setCellFactory(fac);
        deviceList.setButtonCell(new DeviceListCell());
        deviceList.setItems(deviceListModel);

        patientList.setItems(patientListModel);

        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DevicePatientAssociation>() {
            @Override
            public void changed(ObservableValue<? extends DevicePatientAssociation> observable,
                                DevicePatientAssociation oldValue,
                                DevicePatientAssociation newValue) {
                onAssociationSelected(oldValue, newValue);
            }
        });


        connectBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Device d = deviceList.getSelectionModel().getSelectedItem();
                PatientInfo p = patientList.getSelectionModel().getSelectedItem();
                if (d != NO_DEVICE && p != NO_PATIENT) {
                    proposeDeviceAssociation(d, p);
                }
            }
        });

        List<PatientInfo> p =  PatientInfo.queryAll(jdbcDB);
        patientListModel.addAll(p);

        mdsConnectivity.addConnectivityListener(this);
    }

    public void proposeDeviceAssociation(Device d, PatientInfo p) {
        ice.MDSConnectivityObjective mds=new ice.MDSConnectivityObjective();
        mds.unique_device_identifier = d.getUDI();
        mds.partition = p.getPatientName();
        mdsConnectivity.publish(mds);
    }

    /**
     * called by the IOC framework when the component is about to be shut down.
     * @throws Exception when things go wrong
     */
    public void stop() throws Exception {
        mdsConnectivity.removeConnectivityListener(this);
        deviceListDataModel.getContents().removeListener(this);
    }

    @Override
    public void handleDataSampleEvent(MDSHandler.Connectivity.MDSEvent evt) {

        MDSConnectivity state = (MDSConnectivity)evt.getSource();
        Device d = findDevice(state.unique_device_identifier, deviceList.getItems());
        PatientInfo p = findPatient(state.partition, patientList.getItems());
        if(d != null && p != null)
            addDeviceAssociation(d, p);
    }

    private static Device findDevice(String uid, ObservableList<Device> items) {
        for (Device item : items) {
            if(item.getUDI().equals(uid))
                return item;
        }
        return null;
    }

    private static PatientInfo findPatient(String partition, ObservableList<PatientInfo> items) {
        for (PatientInfo item : items) {
            if(item.getPatientName().equals(partition))
                return item;
        }
        return null;
    }

    @Override
    public void onChanged(Change<? extends Device> c) {
        while (c.next()) {
            if (c.wasAdded() || c.wasRemoved()) {
                for (Device d : c.getRemoved()) {
                    log.info("Device Removed", d.toString());
                    handleDeviceLifecycleEvent(d, false);
                }
                for (Device d : c.getAddedSubList()) {
                    log.info("Device Added", d.toString());
                    handleDeviceLifecycleEvent(d, true);
                }
            }
        }
    }

    private static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public PatientInfoController() {
        super();
    }

    private static final Device NO_DEVICE = new Device("");
    private static final PatientInfo NO_PATIENT = new PatientInfo("");
}