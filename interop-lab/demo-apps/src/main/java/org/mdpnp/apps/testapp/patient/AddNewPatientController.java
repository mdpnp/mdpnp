package org.mdpnp.apps.testapp.patient;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * @author mfeinberg
 */
public class AddNewPatientController implements Initializable {

    @FXML
    Button createNewPatient;
    @FXML
    TextField newPatientMRN;
    @FXML
    TextField newPatientFirstName;
    @FXML
    TextField newPatientLastName;
    @FXML
    ComboBox<PatientInfo.Gender> newPatientGender;
    @FXML
    DatePicker newPatientDOB;

    @FXML
    void createNewPatientHandler(ActionEvent actionEvent) {

        String mrn = newPatientMRN.getText();
        String lName = newPatientLastName.getText();
        String fName = newPatientFirstName.getText();
        LocalDate localDate = newPatientDOB.getValue();
        PatientInfo.Gender g = newPatientGender.getValue();

        if (!isEmpty(fName) && !isEmpty(lName) && !isEmpty(mrn) && localDate != null && g != null) {

            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            pi = new PatientInfo(mrn.trim(), fName.trim(), lName.trim(), g, date);
        }
        dialogStage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        newPatientGender.setValue(PatientInfo.Gender.M);
        newPatientDOB.setPromptText("MM/dd/yyyy");

        // From
        // http://stackoverflow.com/questions/32346893/javafx-datepicker-not-updating-value
        // This deals with the bug located here where the datepicker value is not updated on focus lost
        // https://bugs.openjdk.java.net/browse/JDK-8092295?page=com.atlassian.jira.plugin.system.issuetabpanels:all-tabpanel

        newPatientDOB.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue){
                    newPatientDOB.setValue(newPatientDOB.getConverter().fromString(newPatientDOB.getEditor().getText()));
                }
            }
        });
    }

    @FXML
    void cancelHandler(ActionEvent actionEvent) {
        dialogStage.close();
    }

    protected ObservableList<PatientInfo.Gender> genderListModel = FXCollections.observableArrayList();
    {
        genderListModel.addAll(PatientInfo.Gender.values());
    }

    PatientInfo getPatientInfo() {
        return pi;
    }

    private PatientInfo pi=null;
    private Stage       dialogStage=null;

    private static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    void setDialogStage(Stage ds) {
        dialogStage = ds;
        newPatientGender.setItems(genderListModel);
        String mrn = Long.toHexString(System.currentTimeMillis());
        newPatientMRN.setText(mrn);
    }
}

