package org.mdpnp.apps.testapp.chart;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.Vital;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class Chart implements ListChangeListener<Value> {
    @FXML protected LineChart<Date, Number> lineChart;
    final ObservableList<XYChart.Series<Date, Number>> series = FXCollections.observableArrayList();
    private final List<ValueSeriesListener> values = new ArrayList<ValueSeriesListener>();
    @FXML Button removeButton;
    @FXML BorderPane main;
    
    private Vital vital;
    
    public Vital getVital() {
        return vital;
    }
    public Button getRemoveButton() {
        return removeButton;
    }
    
    public void setModel(Vital v, final DateAxis dateAxis) {
        
        if(null != this.vital) {
            this.vital.removeListener(this);
            Iterator <ValueSeriesListener> vslitr = values.iterator();
            while(vslitr.hasNext()) {
                ValueSeriesListener vsl = vslitr.next();
                vsl.v.timestampProperty().removeListener(vsl.l);
                series.remove(vsl.s);
                vslitr.remove();
            }
            main.setCenter(null);
            lineChart.titleProperty().unbind();
            lineChart = null;
        }
        this.vital = v;
        if(null != v) {
            NumberAxis yAxis = new NumberAxis();
            lineChart = new LineChart<>(dateAxis, yAxis);
            lineChart.setMinHeight(200.0);
            lineChart.setAnimated(false);
            lineChart.setCreateSymbols(false);
            
            lineChart.titleProperty().bind(v.labelProperty());
            lineChart.setData(series);
            main.setCenter(lineChart);
            BorderPane.setAlignment(lineChart, Pos.CENTER);
            v.addListener(this);
            yAxis.setAutoRanging(false);
            yAxis.setUpperBound(v.getMaximum());
            yAxis.setLowerBound(v.getMinimum());
        }
    }

    private static class ValueSeriesListener { 
        public XYChart.Series<Date, Number> s;
        public Value v;
        public ChangeListener<Number> l;
    }
    
    @Override
    public void onChanged(javafx.collections.ListChangeListener.Change<? extends Value> c) {
        while(c.next()) {
            Iterator <? extends Value> itr = c.getRemoved().iterator();
            while(itr.hasNext()) {
                Value v = itr.next();
                Iterator <ValueSeriesListener> vslitr = values.iterator();
                while(vslitr.hasNext()) {
                    ValueSeriesListener vsl = vslitr.next();
                    if(vsl.v.equals(v)) {
                        vsl.v.timestampProperty().removeListener(vsl.l);
                        series.remove(vsl.s);
                        vslitr.remove();
                    }
                }
            }
            
            itr = c.getAddedSubList().iterator();
            while(itr.hasNext()) {
                ValueSeriesListener vsl = new ValueSeriesListener();
                vsl.v = itr.next();
                values.add(vsl);
                
                final ObservableList<XYChart.Data<Date, Number>> data = FXCollections.observableArrayList();
                vsl.s = new XYChart.Series<>(vsl.v.getDevice().getMakeAndModel(), data);
                series.add(vsl.s);
                vsl.v.timestampProperty().addListener(vsl.l = new ChangeListener<Number>() {

                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        if(newValue != null) {
                            data.add(new XYChart.Data<>(new Date(newValue.longValue()), vsl.v.getValue()));
                        }
                    }
                    
                });
            }
        }

    }
}
