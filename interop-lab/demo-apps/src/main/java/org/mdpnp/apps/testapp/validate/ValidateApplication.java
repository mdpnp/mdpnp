package org.mdpnp.apps.testapp.validate;

import java.io.IOException;
import java.util.Iterator;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;

import org.mdpnp.apps.testapp.pca.VitalSign;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidateApplication implements ListChangeListener<Vital> {    
    protected static final Logger log = LoggerFactory.getLogger(ValidateApplication.class);

//    private Timeline timeline;
    private VitalModel vitalModel;
    @FXML Pane charts;
    @FXML ComboBox<VitalSign> vitalSigns;

    
    public ValidateApplication() {
    }
    
    public void setModel(VitalModel vitalModel) {
        if(null != this.vitalModel) {
            this.vitalModel.removeListener(this);
//            if(timeline!=null) {
//                timeline.stop();
//                timeline = null;
//            }
           
        }
        vitalSigns.setItems(FXCollections.observableArrayList(VitalSign.values()));
        this.vitalModel = vitalModel;
        if(null != vitalModel) {
            vitalModel.addListener(this);
//            timeline = new Timeline(new KeyFrame(new Duration(1000.0), this));
//            timeline.setCycleCount(Animation.INDEFINITE);
//            timeline.play();
        }

    }

    @Override
    public void onChanged(javafx.collections.ListChangeListener.Change<? extends Vital> c) {
        while(c.next()) {
            if(c.wasRemoved()) {
                Iterator <? extends Vital> vitr = c.getRemoved().iterator();
                while(vitr.hasNext()) {
                    final Vital vi = vitr.next();
                    Iterator<Node> childitr = charts.getChildren().iterator();
                    while(childitr.hasNext()) {
                        Node n = childitr.next();
                        Chart chart = (Chart) n.getUserData();
                        if(chart.getVital().equals(vi)) {
                            chart.setModel(null);
                            childitr.remove();
                        }
                    }
                }
            }
            
            if(c.wasAdded()) {
                Iterator <? extends Vital> vitr = c.getAddedSubList().iterator();
                while(vitr.hasNext()) {
                    final Vital vi = vitr.next();
                    FXMLLoader loader = new FXMLLoader(Chart.class.getResource("Chart.fxml"));
                    try {
                        Parent node = loader.load();
                        Chart chart = loader.getController();
                        chart.setModel(vi);
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
            }
        }
        
    }
    
    @FXML public void addVitalSign() {
        VitalSign vs = vitalSigns.getSelectionModel().getSelectedItem();
        if(null != vs) {
            vs.addToModel(vitalModel);
        }
    }

}
