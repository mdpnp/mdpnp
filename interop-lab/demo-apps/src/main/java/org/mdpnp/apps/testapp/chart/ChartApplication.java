package org.mdpnp.apps.testapp.chart;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import org.mdpnp.apps.testapp.pca.VitalSign;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.ComboBox;

public class ChartApplication implements ListChangeListener<Vital>, EventHandler<ActionEvent> {    
    protected static final Logger log = LoggerFactory.getLogger(ChartApplication.class);
    final DateAxis dateAxis = new DateAxis();
    @FXML Pane charts;
    VitalModel vitalModel; 
    private Timeline timeline;
    @FXML ComboBox<VitalSign> vitalSigns;
    
    
    public ChartApplication() {
    }
    
    public void setModel(VitalModel vitalModel) {
        vitalSigns.setItems(FXCollections.observableArrayList(VitalSign.values()));
        dateAxis.setAutoRanging(false);
        dateAxis.setAnimated(false);
        if(null != this.vitalModel) {
            this.vitalModel.removeListener(this);
            if(timeline!=null) {
                timeline.stop();
                timeline = null;
            }
        }
        this.vitalModel = vitalModel;
        if(null != vitalModel) {
            vitalModel.addListener(this);
            timeline = new Timeline(new KeyFrame(new Duration(1000.0), this));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        }

    }

    @Override
    public void onChanged(javafx.collections.ListChangeListener.Change<? extends Vital> c) {
        while(c.next()) {
            Iterator <? extends Vital> vitr = c.getRemoved().iterator();
            while(vitr.hasNext()) {
                final Vital vi = vitr.next();
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
            
            
            vitr = c.getAddedSubList().iterator();
            while(vitr.hasNext()) {
                final Vital vi = vitr.next();
                
                FXMLLoader loader = new FXMLLoader(Chart.class.getResource("Chart.fxml"));
                try {
                    Parent node = loader.load();
                    Chart chart = loader.getController();
                    chart.lineChart.titleProperty().bind(vi.labelProperty());
                    chart.setModel(vi, dateAxis);
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

    @Override
    public void handle(ActionEvent event) {
        long now = System.currentTimeMillis() % 1000L;
        dateAxis.setAutoRanging(false);
        dateAxis.setRange(new Object[] {new Date(now - 18000000L), new Date(now)}, false);
    }

    @FXML public void addVitalSign() {
        VitalSign vs = vitalSigns.getSelectionModel().getSelectedItem();
        if(null != vs) {
            vs.addToModel(vitalModel);
        }
    }
}
