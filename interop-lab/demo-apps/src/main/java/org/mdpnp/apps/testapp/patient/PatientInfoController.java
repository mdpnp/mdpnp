package org.mdpnp.apps.testapp.patient;

import ice.MDSConnectivity;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.devices.PartitionAssignmentController;
import org.mdpnp.sql.SQLLogging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class PatientInfoController implements ListChangeListener<Device>, MDSHandler.Connectivity.MDSListener {

    private static final Logger log = LoggerFactory.getLogger(PatientInfoController.class);

    private DeviceListModel deviceListDataModel;
    private EMRFacade       emr;
    private MDSHandler      mdsConnectivity;

    @FXML Button connectBtn;
    @FXML TableView<PatientInfo> patientView;
    @FXML TableView<Device> deviceView;

    @FXML Button createNewPatient;

    @FXML TableView<DevicePatientAssociation> associationTableView;
    @FXML TableColumn<DevicePatientAssociation, String> associationTableActionColumn;

    protected ObservableList<DevicePatientAssociation> associationModel = FXCollections.observableArrayList();
    protected ObservableList<Device> unassignedDevices = FXCollections.observableArrayList();

    private Map<String, String> deviceAssignments = new HashMap<>();
    
    private Connection conn;
    private PreparedStatement assocStatement;
    private PreparedStatement dissocStatement;
    
    public DeviceListModel getDeviceListDataModel() {
        return deviceListDataModel;
    }

    public void setDeviceListDataModel(DeviceListModel dlm) {
        deviceListDataModel = dlm;
    }

    public EMRFacade getEmr() {
        return emr;
    }

    public void setEmr(EMRFacade db) {
        emr = db;
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

    }


    public void removeDeviceAssociation(DevicePatientAssociation assoc)  {
        emr.deleteDevicePatientAssociation(assoc);
        assoc.getMrn();
        assoc.getDevice().getUDI();
        Connection c=null;
        try {
            dissocStatement.setLong(1,System.currentTimeMillis()/1000);
            dissocStatement.setString(2,assoc.getMrn());
            dissocStatement.setString(3, assoc.getDevice().getUDI());
            if( ! dissocStatement.execute() ) {
                log.info("Updated "+dissocStatement.getUpdateCount()+" rows in patientdevice");
            } else {
                log.error("Unexpected outcome for update statement in removeDeviceAssociation");
            }
        } catch (SQLException sqle) {
            log.error("Failed to delete patient device association",sqle);
        } finally {
        	try {
        		c.close();
        	} catch (SQLException sqle) {
        		
        	}
        }
        associationModel.remove(assoc);

        Device d = assoc.getDevice();
        unassignedDevices.add(d);
        proposeDeviceAssociation(d, null);
    }

    void addDeviceAssociation(Device d, PatientInfo p) {
        DevicePatientAssociation assoc = associateEMR(d, p);
        associationModel.add(assoc);
        unassignedDevices.remove(d);
    }

    void addDeviceAssociation(Device d, String p) {

        if(d==null) throw new IllegalArgumentException("Null device passed for association");
        if(p==null) throw new IllegalArgumentException("Null partition passed for association");

        String mrn = PartitionAssignmentController.toMRN(p);

        // passing device as 3rd argument will create transient patient info objects based on the
        // device information. Passing null will only lookup existing patients
        // and will not handle devices assigned to unknown partitions.
        //
        PatientInfo pi = findPatient(mrn, patientView.getItems(), d);
        if (pi != null)
            addDeviceAssociation(d, pi);
        else
            log.warn("No patient found for " + p);
    }

    private DevicePatientAssociation associateEMR(Device d, PatientInfo p)  {
        DevicePatientAssociation dpa = emr.updateDevicePatientAssociation(new DevicePatientAssociation(d, p));
        try {
            assocStatement.setString(1,p.getMrn());
            assocStatement.setString(2, d.getUDI());
            assocStatement.setLong(3, System.currentTimeMillis()/1000);
            assocStatement.execute();
        } catch (SQLException sqle) {
            log.error("Failed to record device association in database",sqle);
        }
        return dpa;
    }

    public void handleDeviceLifecycleEvent(Device d, boolean added)  {

        // deviceAssignments get populated listening to the MDS assignments;
        // those notification could arrive before the list of devices is build.
        //
        // As device connectivity flickers, it will rebroadcast its availability, but
        // the patient assignment will not be repeated. So we do not remove it from the
        // pendingAssociation list as it will be consulted every time device reconnects to
        // the network.
        //
        DevicePatientAssociation assoc = findAssociation(d);

        if(added) {
            String p = deviceAssignments.get(d.getUDI());
            if(p == null) {
                unassignedDevices.add(d);
            }
            else if(assoc == null) {
                addDeviceAssociation(d, p);
            }
        }
        else {
            unassignedDevices.remove(d);

            if(assoc != null) {
                emr.deleteDevicePatientAssociation(assoc);
                associationModel.remove(assoc);
            }
        }
    }

    /**
     * @return current device association or null
     */
    private DevicePatientAssociation findAssociation(final Device d) {
        FilteredList<DevicePatientAssociation> l = associationModel.filtered(new Predicate<DevicePatientAssociation>() {
            @Override
            public boolean test(DevicePatientAssociation devicePatientAssociation) {
                return devicePatientAssociation.isForDevice(d);
            }
        });
        return l.isEmpty()?null:l.get(0);
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
                ImageView img = new ImageView(new javafx.scene.image.Image(getClass().getResourceAsStream("disconnect.png")));
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

        associationTableActionColumn.setCellFactory(columnFac);

        associationTableView.setItems(associationModel);
        deviceView.setItems(unassignedDevices);
        patientView.setItems(emr.getPatients());

        associationTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DevicePatientAssociation>() {
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
                Device d = getSelectedDevice();
                PatientInfo p = getSelectedPatient();
                if (d != null && p != null) {
                    proposeDeviceAssociation(d, p);
                }
            }
        });

        createNewPatient.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("AddNewPatient.fxml"));
                    Parent ui = loader.load();
                    Stage dialog = new Stage(StageStyle.DECORATED);
                    dialog.setTitle("Add New Patient");
                    dialog.setAlwaysOnTop(true);
                    Scene scene = new Scene(ui);
                    dialog.setScene(scene);
                    dialog.sizeToScene();
                    dialog.initModality(Modality.WINDOW_MODAL);
                    dialog.initOwner(
                            ((Node)actionEvent.getSource()).getScene().getWindow() );

                    // Set the person into the controller
                    AddNewPatientController controller = loader.getController();
                    controller.setDialogStage(dialog);

                    // Show the dialog and wait until the user closes it
                    dialog.showAndWait();

                    PatientInfo pi = controller.getPatientInfo();
                    if(pi != null) {
                        addPatient(pi);
                    }
                }
                catch(Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        mdsConnectivity.addConnectivityListener(this);
    }

    PatientInfo getSelectedPatient() {
        return patientView.getSelectionModel().getSelectedItem();
    }

    Device getSelectedDevice() {
        return deviceView.getSelectionModel().getSelectedItem();
    }

    void setConnectHandler(EventHandler<ActionEvent> a) {
        connectBtn.setOnAction(a);
    }

    public void proposeDeviceAssociation(Device d, PatientInfo p) {
        ice.MDSConnectivityObjective mds=new ice.MDSConnectivityObjective();
        mds.unique_device_identifier = d.getUDI();
        mds.partition = p==null? "" : PartitionAssignmentController.toPartition(p.getMrn());
        mdsConnectivity.publish(mds);
    }

    private boolean addPatient(PatientInfo pi) {
        if(emr.createPatient(pi)) {
            mdsConnectivity.publish(pi.asIcePatient());
            return true;
        }
        return false;
    }

    /**
     * called by the IOC framework when the component is about to be shut down.
     * @throws Exception when things go wrong
     */
    public void stop() throws Exception {
        mdsConnectivity.removeConnectivityListener(this);
        deviceListDataModel.getContents().removeListener(this);
    }

    /**
     * The app will listen for all devices advertise their patient association.
     * @param evt
     */
    @Override
    public void handleConnectivityChange(MDSHandler.Connectivity.MDSEvent evt) {

        MDSConnectivity state = (MDSConnectivity)evt.getSource();

        String mrnPartition = PartitionAssignmentController.findMRNPartition(state.partition);

        if(mrnPartition==null)
            return;

        deviceAssignments.put(state.unique_device_identifier, mrnPartition);

        Device d = findDevice(state.unique_device_identifier, unassignedDevices);
        if(d != null) {
            addDeviceAssociation(d, mrnPartition);
        }
    }

    private static Device findDevice(String uid, ObservableList<Device> items) {
        for (Device item : items) {
            if(item.getUDI().equals(uid))
                return item;
        }
        return null;
    }



    private static PatientInfo findPatient(String mrn, ObservableList<PatientInfo> items, Device d) {
        for (PatientInfo item : items) {
            if(mrn.equals(item.getMrn()))
                return item;
        }

        if(d != null) {
            PatientInfo pi = new PatientInfo(mrn, d.getHostname(), PatientInfo.UNKNOWN_NAME, PatientInfo.Gender.U, new Date());
            items.add(pi);
            return pi;
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

    public PatientInfoController() {
        super();
        try {
        	conn=SQLLogging.getConnection();
        	assocStatement=conn.prepareStatement("INSERT INTO patientdevice(mrn, udi, associated) VALUES (?,?,?)");
        	dissocStatement=conn.prepareStatement("UPDATE patientdevice set dissociated=? WHERE mrn=? AND udi=?");
        } catch (SQLException sqle) {
        	log.error("Failed to connect to database and prepare statements",sqle);
        }
    }

}