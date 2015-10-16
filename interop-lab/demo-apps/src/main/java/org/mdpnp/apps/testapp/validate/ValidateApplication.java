package org.mdpnp.apps.testapp.validate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.layout.Pane;

import org.mdpnp.apps.device.OnListChange;
import org.mdpnp.apps.testapp.vital.VitalSign;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidateApplication implements Runnable {    
    protected static final Logger log = LoggerFactory.getLogger(ValidateApplication.class);

    private VitalModel vitalModel;
    @FXML Pane charts;
    @FXML ComboBox<VitalSign> vitalSigns;
    private ValidationOracle validationOracle;

    private final IntegerProperty maxDataPoints = new SimpleIntegerProperty(this, "maxDataPoints", 20);
    private final DoubleProperty maxRsd = new SimpleDoubleProperty(this, "maxRsd", 0.0);
    private final DoubleProperty minKurtosis = new SimpleDoubleProperty(this, "minKurtosis", 0.0);
    
    private List<VitalValidator> vitalValidators = Collections.synchronizedList(new ArrayList<VitalValidator>());
    
    public ValidateApplication() {
    }
    
    private final OnListChange<Vital> vitalListener = new OnListChange<Vital>((v)->add(v), null, (v)->remove(v));

    @FXML Spinner<Number> maxDataPointsSpinner;

    @FXML Spinner<Number> maxRsdSpinner;
    
    @FXML Spinner<Number> minKurtosisSpinner;
    
    private void add(Vital vi) {
        FXMLLoader loader = new FXMLLoader(Chart.class.getResource("Chart.fxml"));
        try {
            Parent node = loader.load();
            Chart chart = loader.getController();
            chart.setModel(maxDataPoints, maxRsd, vi, validationOracle, minKurtosis);
            node.setUserData(chart);
            vitalValidators.add(chart.getVitalValidator());
            chart.getRemoveButton().setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    vitalModel.remove(vi);
                }
                
            });
            charts.getChildren().add(node);
        } catch (IOException e) {
            log.error("Unable to create a Chart", e);
        }
    }
    
    private void remove(Vital vi) {
        Iterator<Node> childitr = charts.getChildren().iterator();
        while(childitr.hasNext()) {
            Node n = childitr.next();
            Chart chart = (Chart) n.getUserData();
            
            if(chart.getVital().equals(vi)) {
                vitalValidators.remove(chart.getVitalValidator());
                chart.setModel(null, null, null, null, null);
                childitr.remove();
            }
        }
    }
    
    public void setModel(VitalModel vitalModel, ScheduledExecutorService executor, ValidationOracle validationOracle) {
        this.validationOracle = validationOracle;
        maxDataPoints.bind(maxDataPointsSpinner.valueProperty());
        maxRsd.bind(maxRsdSpinner.valueProperty());
        minKurtosis.bind(minKurtosisSpinner.valueProperty());

        executor.scheduleWithFixedDelay(this, 1000L, 3000L, TimeUnit.MILLISECONDS);
        if(null != this.vitalModel) {
            this.vitalModel.removeListener(vitalListener);
            this.vitalModel.forEach((v)->remove(v));
        }
        vitalSigns.setItems(FXCollections.observableArrayList(VitalSign.values()));
        this.vitalModel = vitalModel;
        if(null != vitalModel) {
            vitalModel.addListener(vitalListener);
            vitalModel.forEach((v)->add(v));
        }

    }
    
    @FXML public void addVitalSign() {
        VitalSign vs = vitalSigns.getSelectionModel().getSelectedItem();
        if(null != vs) {
            vs.addToModel(vitalModel);
        }
    }
    
    public void run() {
        try {
            for(VitalValidator vv : vitalValidators.toArray(new VitalValidator[0])) {
                log.trace("RECOMPUTE "+vv.getVital().getLabel());
                vv.recompute();
            }
        } catch (Throwable t) {
            log.error("",t);
        }
    }

}
