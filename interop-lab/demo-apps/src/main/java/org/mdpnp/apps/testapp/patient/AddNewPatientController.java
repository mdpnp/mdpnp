package org.mdpnp.apps.testapp.patient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author mfeinberg
 */
public class AddNewPatientController {

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

