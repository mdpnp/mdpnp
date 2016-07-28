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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import org.mdpnp.apps.testapp.vital.VitalSign;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChartApplication implements ListChangeListener<Vital>, EventHandler<ActionEvent>, ChartApplicationFactory.WithVitalModel {
    protected static final Logger log = LoggerFactory.getLogger(ChartApplication.class);
    final DateAxis dateAxis = new DateAxis();
    @FXML Pane charts;
    VitalModel vitalModel; 
    private Timeline timeline;
    @FXML ComboBox<VitalSign> vitalSigns;
    private long interval = 2 * 60 * 60 * 1000L;
    @FXML ComboBox<TimeInterval> timeInterval;
    
    public enum TimeInterval {
        _10SECONDS("10 Seconds", 10000L),
        _1MINUTE("1 Minute", 60000L),
        _5MINUTES("5 Minutes", 5 * 60000L),
        _15MINUTES("15 Minutes", 15 * 60000L),
        _1HOUR("1 Hour", 60 * 60000L),
        _2HOURS("2 Hours", 2 * 60 * 60000L),
        _3HOURS("3 Hours", 3 * 60 * 60000L),
        _4HOURS("4 Hours", 4 * 60 * 60000L),
        _5HOURS("5 Hours", 5 * 60 * 60000L),
        _6HOURS("6 Hours", 6 * 60 * 60000L);
        
        
        private String label;
        private long interval;
        
        public long getInterval() {
            return interval;
        }
        public String getLabel() {
            return label;
        }
        
        private TimeInterval(String label, long interval) {
            this.label = label;
            this.interval = interval;
        }
        
        @Override
        public String toString() {
            return label;
        }
    }
    
    public ChartApplication() {
    }

    @Override
    public void setModel(VitalModel vitalModel) {
        timeInterval.setItems(FXCollections.observableArrayList(TimeInterval.values()));
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
        long now = System.currentTimeMillis();
        now -= now % 1000;
        dateAxis.setLowerBound(new Date(now - interval));
        dateAxis.setUpperBound(new Date(now));
    }

    @FXML public void addVitalSign() {
        VitalSign vs = vitalSigns.getSelectionModel().getSelectedItem();
        if(null != vs) {
            vs.addToModel(vitalModel);
        }
    }

    @FXML public void onTimeInterval(ActionEvent event) {
        TimeInterval ti = timeInterval.getSelectionModel().getSelectedItem();
        if(null != ti) {
            interval = ti.getInterval();
        }
    }
}
