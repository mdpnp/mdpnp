package org.mdpnp.apps.testapp.chart;

import java.util.Date;
import java.util.Iterator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChartApplication {    
    protected static final Logger log = LoggerFactory.getLogger(ChartApplication.class);
    @FXML LineChart<Date, Number> chart;

    final ObservableList<XYChart.Series<Date, Number>> series = FXCollections.observableArrayList();
    
    public ChartApplication() {
    }
    
    public void setModel(VitalModel vitalModel) {
        chart.setData(series);
        vitalModel.addListener(new ListChangeListener<Vital>() {

            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends Vital> c) {
                while(c.next()) {
                    Iterator <? extends Vital> vitr = c.getAddedSubList().iterator();
                    while(vitr.hasNext()) {
                        final Vital vi = vitr.next();
                        vi.addListener(new ListChangeListener<Value>() {
    
                            @Override
                            public void onChanged(javafx.collections.ListChangeListener.Change<? extends Value> c) {
                                while(c.next()) {
                                    Iterator <? extends Value> itr = c.getAddedSubList().iterator();
                                    while(itr.hasNext()) {
                                        Value v = itr.next();
                                        final ObservableList<XYChart.Data<Date, Number>> data = FXCollections.observableArrayList();
                                        series.add(new XYChart.Series<>(v.getMetricId(), data));
                                        v.timestampProperty().addListener(new ChangeListener<Number>() {
        
                                            @Override
                                            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                                                if(newValue != null) {
                                                    data.add(new XYChart.Data<>(new Date(newValue.longValue()), v.getValue()));
                                                }
                                            }
                                            
                                        });
                                        
                                    }
                                }
                            }
                            
                        });
    
                    }
                }
            }
        });
        
    }
}
