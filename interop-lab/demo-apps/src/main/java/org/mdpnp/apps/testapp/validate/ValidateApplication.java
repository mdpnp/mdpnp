package org.mdpnp.apps.testapp.validate;

import java.io.IOException;
import java.util.Iterator;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;

import org.mdpnp.apps.device.OnListChange;
import org.mdpnp.apps.testapp.pca.VitalSign;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidateApplication {    
    protected static final Logger log = LoggerFactory.getLogger(ValidateApplication.class);

    private VitalModel vitalModel;
    @FXML Pane charts;
    @FXML ComboBox<VitalSign> vitalSigns;

    private final IntegerProperty maxDataPoints = new SimpleIntegerProperty(this, "maxDataPoints", 20);
    
    public ValidateApplication() {
    }
    
    private final OnListChange<Vital> vitalListener = new OnListChange<Vital>((v)->add(v), null, (v)->remove(v));
    
    private void add(Vital vi) {
        FXMLLoader loader = new FXMLLoader(Chart.class.getResource("Chart.fxml"));
        try {
            Parent node = loader.load();
            Chart chart = loader.getController();
            chart.setModel(maxDataPoints, vi);
            node.setUserData(chart);
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
                chart.setModel(null, null);
                childitr.remove();
            }
        }
    }
    
    public void setModel(VitalModel vitalModel) {
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

}
