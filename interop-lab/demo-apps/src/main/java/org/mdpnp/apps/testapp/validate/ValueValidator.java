package org.mdpnp.apps.testapp.validate;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

import org.mdpnp.apps.testapp.vital.Value;

public class ValueValidator implements ChangeListener<Number> {
    private static class DataPoint {
        
        private long tm;
        private int value;
        
        public DataPoint(long tm, int value) {
            set(tm, value);
        }
        public DataPoint set(long tm, int value) {
            this.tm = tm;
            this.value = value;
            return this;
        }
        public long getTime() {
            return tm;
        }
        public int getValue() {
            return value;
        }
    }
    
    private static class DataPointBuffer {
        private final List<DataPoint> dataPoints = new LinkedList<DataPoint>();
        
        public Integer add(long tm, int value) {
            if(dataPoints.size() < MAX_POINTS) {
                dataPoints.add(new DataPoint(tm, value));
                return null;
            } else {
                DataPoint pt = dataPoints.remove(0);
                int oldValue = pt.getValue();
                dataPoints.add(pt.set(tm, value));
                return oldValue;
            }

        }
        public DataPoint remove() {
            if(dataPoints.isEmpty()) {
                return null;
            } else {
                return dataPoints.remove(0);
            }
        }
    }
    
    public XYChart.Series<String, Number> getSeries() {
        return series;
    }
    
    
    private final Value value;
    private final DataPointBuffer recentValues = new DataPointBuffer();
    private final ObservableList<Data<String,Number>> data = FXCollections.observableArrayList();
    private final XYChart.Series<String, Number> series;
    private static final int MAX_POINTS = 20;
    
    public ValueValidator(final Value value) {
        this.value = value;
        for(int i = (int) value.getParent().getMinimum(); i <= (int)value.getParent().getMaximum(); i++) {
            data.add(new Data<String,Number>(""+i, 0));
        }
        series = new XYChart.Series<String,Number>(data);
        series.nameProperty().bind(value.getDevice().makeAndModelProperty());
        value.timestampProperty().addListener(this);
    }
    
    private void addToBins(DataPoint dataPoint, int count) {
        addToBins(dataPoint.getValue(), count);
    }
    
    private void addToBins(int value, int count) {
        Iterator<Data<String,Number>> itr = data.iterator();
        while(itr.hasNext()) {
            Data<String,Number> d = itr.next();
            if(d.getXValue().equals(""+value)) {
                Number y = d.getYValue();
                count += y.intValue();
                if(count == 0) {
                    itr.remove();
                } else {
                    d.setYValue(count);
                }
                return;
            }
        }
        if(data.isEmpty() || 
           value > Integer.parseInt(data.get(data.size()-1).getXValue())) {
            data.add(new Data<String,Number>(""+value, count));
        } else if(value < Integer.parseInt(data.get(0).getXValue())) {
            data.add(0, new Data<String,Number>(""+value, count));
        } else {
            int i;
            for(i = 0; i < data.size(); i++) {
                if(value > Integer.parseInt(data.get(i).getXValue())) {
                    data.add(i+1, new Data<String,Number>(""+value, count));
                    break;
                }
            }
        }
    }

    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        int value = (int) this.value.getValue();
        long tm = this.value.getTimestamp();
        Integer displacedValue = recentValues.add(tm, value);
        if(null != displacedValue) {
            addToBins(displacedValue, -1);
        }                            
        addToBins(value, 1);        
    }
}
