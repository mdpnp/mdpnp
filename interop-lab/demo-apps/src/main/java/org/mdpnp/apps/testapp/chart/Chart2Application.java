package org.mdpnp.apps.testapp.chart;

import com.rti.dds.infrastructure.Time_t;
import himss.AssessmentEntry;
import himss.PatientAssessment;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.devices.DomainClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class Chart2Application implements Initializable, ChartApplicationFactory.WithVitalModel {
    protected static final Logger log = LoggerFactory.getLogger(Chart2Application.class);

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");


    @FXML
    ComboBox<ObservationType> observationCodes;
    @FXML
    TextField observationEffectiveTime;
    @FXML
    ChartApplication vitalSignsController;
    @FXML
    RadioButton timeNow;
    @FXML
    RadioButton timeAsOf;

    Timeline datePickUpdate;

    public Chart2Application() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePickUpdate = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(timeNow.isSelected()) {
                    String v = dateFormat.format(new Date());
                    observationEffectiveTime.setText(v);
                }
            }
        }));
        datePickUpdate.setCycleCount(Timeline.INDEFINITE);
        datePickUpdate.play();
    }

    public void setModel(VitalModel vitalModel) {

        List<ObservationType> list = readObservationTypes();

        observationCodes.setItems(FXCollections.observableArrayList(list));

        vitalSignsController.setModel(vitalModel);
    }

    @FXML
    public void setEffectiveDate(ActionEvent event) {
        Object src = event.getSource();
        observationEffectiveTime.setVisible(timeAsOf == src);
    }

    @FXML
    public void addObservation() {
        ObservationType oc = observationCodes.getSelectionModel().getSelectedItem();

        Date date;
        try {
            String td = observationEffectiveTime.getText();
            date = dateFormat.parse(td);
        } catch (ParseException e) {
            observationEffectiveTime.setText(dateFormat.toPattern());
            date = null;
        }
        if(date != null && oc != null) {

            AssessmentEntry ae = new AssessmentEntry();
            ae.name = oc.label;
            ae.value = Integer.toString(oc.id);

            PatientAssessment pa = new PatientAssessment();
            pa.assessments.userData.add(ae);

            Time_t t = DomainClock.toDDSTime(date.getTime());
            pa.date_and_time.seconds = t.sec;
            pa.date_and_time.nanoseconds = t.nanosec;

            log.info("Added new PatientAssessment " + pa);
        }
    }

    List<ObservationType> readObservationTypes() {
        try {
            URL u = Chart2Application.class.getResource("ObservationTypes.csv");
            final InputStream is = u.openStream();
            List<ObservationType> list = readObservationTypesFile(is);
            is.close();
            return list;
        }
        catch(IOException ex) {
            log.error("Failed to load observation types.", ex);
            return Collections.emptyList();
        }
    }

    List<ObservationType> readObservationTypesFile(InputStream is) throws IOException {
        List<ObservationType> codes = new ArrayList<>();
        InputStreamReader fr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        while (null != (line = br.readLine())) {
            if (!line.startsWith("#")) {
                String arr[] = line.split("[,]");
                if(arr.length==2)
                    codes.add(new ObservationType(Integer.parseInt(arr[0]), arr[1]));
            }
        }
        br.close();

        Collections.sort(codes, (o1, o2) -> o1.label.compareTo(o2.label));
        return codes;
    }

    public static class ObservationType {
        final int id;
        final String label;

        public ObservationType(int id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
