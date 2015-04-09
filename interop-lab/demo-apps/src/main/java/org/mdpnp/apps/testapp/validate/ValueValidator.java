package org.mdpnp.apps.testapp.validate;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

import org.mdpnp.apps.testapp.vital.Value;

public class ValueValidator implements ChangeListener<Date> {
    private final List<Integer> dataPoints = new LinkedList<Integer>();
    
    public XYChart.Series<String, Number> getSeries() {
        return series;
    }
    
    
    private final Value value;
    private final ObservableList<Data<String,Number>> data = FXCollections.observableArrayList();
    private final XYChart.Series<String, Number> series;
    private final IntegerProperty maxDataPoints;
    
    public ValueValidator(final IntegerProperty maxDataPoints, final Value value) {
        this.maxDataPoints = maxDataPoints;
        this.value = value;
//        for(int i = (int) value.getParent().getMinimum(); i <= (int)value.getParent().getMaximum(); i++) {
//            data.add(new Data<String,Number>(""+i, 0));
//        }
        series = new XYChart.Series<String,Number>(data);
        series.nameProperty().bind(value.getDevice().makeAndModelProperty());
        value.timestampProperty().addListener(this);
        newTimestamp(value.getTimestamp());
    }
    
    private void addToBins(int value, int count) {
        Iterator<Data<String,Number>> itr = data.iterator();
        while(itr.hasNext()) {
            Data<String,Number> d = itr.next();
            if(d.getXValue().equals(""+value)) {
                Number y = d.getYValue();
                count += y.intValue();
                d.setYValue(count);
                return;
            }
        }
        if(data.isEmpty() || 
           value > Integer.parseInt(data.get(data.size()-1).getXValue())) {
            // Add after
            data.add(new Data<String,Number>(""+value, count));
        } else if(value < Integer.parseInt(data.get(0).getXValue())) {
            // Add before
            data.add(0, new Data<String,Number>(""+value, count));
        } else {
            int i;
            for(i = 0; i < data.size(); i++) {
                if(value < Integer.parseInt(data.get(i).getXValue())) {
                    data.add(i, new Data<String,Number>(""+value, count));
                    break;
                }
            }
        }
    }

    public void newTimestamp(Date newValue) {
        int value = (int) this.value.getValue();
        dataPoints.add(value);
        final int max = maxDataPoints.get();
        while(dataPoints.size()>max) {
            Integer x = dataPoints.remove((int)0);
            addToBins(x, -1);
        }        
        addToBins(value, 1);        

    }
    
    @Override
    public void changed(ObservableValue<? extends Date> observable, Date oldValue, Date newValue) {
        newTimestamp(newValue);
    }
}
