package org.mdpnp.apps.testapp.validate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Series;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.mdpnp.apps.device.OnListChange;
import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.Vital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VitalValidator {
    private final Vital vital;
    
    private final Map<Value, ValueValidator> valueValidators = Collections.synchronizedMap(new HashMap<Value, ValueValidator>());
    private final ObservableList<Series<String, Number>> data;
//    private final ObservableList<Series<String, Number>> bell;
//    private final Series<String, Number> bellSeries;
    private final IntegerProperty maxDataPoints;
    private final DoubleProperty sigma = new SimpleDoubleProperty(this, "sigma", 0.0),
    n = new SimpleDoubleProperty(this ,"n", 0.0),
            rsd = new SimpleDoubleProperty(this ,"rsd", 0.0),
                    mean = new SimpleDoubleProperty(this ,"mean", 0.0);
    private final BooleanProperty validated = new SimpleBooleanProperty(this, "validated", false);
    private ValidationOracle validationOracle;
    private final IntegerProperty countValues = new SimpleIntegerProperty(this, "countValues", 0);
    
    public Vital getVital() {
        return vital;
    }
    
    public ReadOnlyIntegerProperty countValuesProperty() {
        return countValues;
    }
    
    public VitalValidator(final IntegerProperty maxDataPoints, final DoubleProperty maxRsd, final Vital vital, final ObservableList<Series<String, Number>> data, /*final ObservableList<Series<String, Number>> bell,*/ ValidationOracle validationOracle) {
        this.validationOracle = validationOracle;
        this.maxDataPoints = maxDataPoints;
        this.vital = vital;
        this.data = data;
//        this.bell = bell;
        
        validated.bind(countValues.greaterThan(1).and(rsd.lessThanOrEqualTo(maxRsd)));
//        this.bellSeries = new XYChart.Series<String,Number>();
//        int low = (int) (vital.getCriticalLow()==null?vital.getMinimum():vital.getCriticalLow());
//        int high = (int) (vital.getCriticalHigh()==null?vital.getMaximum():vital.getCriticalHigh());
//        for(int i = low; i <= high; i++) {
//            bellSeries.getData().add(new XYChart.Data<String,Number>(""+i, 0));
//        }
//        bell.clear();
//        bell.add(bellSeries);
        vital.addListener(valueListener);
        vital.forEach((t)->add(t));
    }
    private static final Logger log = LoggerFactory.getLogger(VitalValidator.class);
    public void recompute() {
        double basis = 0.0;
        int count = 0;
        double max = 0.0;
        double sumsigma = 0.0;
        
        for(ValueValidator v : valueValidators.values()) {
            basis += v.getStats().getMean() * v.getStats().getN();
            count += v.getStats().getN();
            sumsigma += v.getStats().getVariance() / v.getStats().getN();
            max = Math.max(max, v.getStats().getMax());
        }
        if(count <= 0 || sumsigma <= 0.0) {
            return;
        }
        log.trace("max="+max+" basis="+basis+" count="+count+" sumsigma="+sumsigma);
        final NormalDistribution normal = new NormalDistribution(basis / count, Math.sqrt(sumsigma));
        final int N = count;
        Platform.runLater( ()-> {
            double mu = normal.getMean();
            double stdev = normal.getStandardDeviation();
            mean.set(mu);
            sigma.set(stdev);
            Double criticalLow = vital.getCriticalLow();
            double low = vital.getMinimum();
            if(null != criticalLow && mu > criticalLow) {
                    low = criticalLow;
            }
            if(mu > low) {
                mu -= low;
            }
            
            rsd.set(stdev/mu*100.0);
            n.set(N);
        });
//        int low = (int) (vital.getCriticalLow()==null?vital.getMinimum():vital.getCriticalLow());
//        int high = (int) (vital.getCriticalHigh()==null?vital.getMaximum():vital.getCriticalHigh());
//        for(int i = 0; i < bellSeries.getData().size(); i++) {
//            int value = low + i;
//            bellSeries.getData().get(i).setYValue(max * normal.density(value));
//            log.info("AT " + value + " density " + (max * normal.density(value)));
//        }
    }
    
    private OnListChange<Value> valueListener = new OnListChange<Value>(
            (t)->add(t), null, (t)->remove(t));
    
    
    public ReadOnlyBooleanProperty validatedProperty() {
        return validated;
    }

    public ReadOnlyDoubleProperty meanProperty() {
        return mean;
    }
    
    public ReadOnlyDoubleProperty sigmaProperty() {
        return sigma;
    }
    public ReadOnlyDoubleProperty sigmaPctProperty() {
        return rsd;
    }
    public ReadOnlyDoubleProperty nProperty() {
        return n;
    }
    
    private void add(Value v) {
        ValueValidator vv = new ValueValidator(this, maxDataPoints, v, validationOracle);
        data.add(vv.getSeries());
        countValues.set(data.size());
        valueValidators.put(v, vv);
    }
    private void remove(Value v) {
        ValueValidator vv = valueValidators.remove(v);
        if(null != vv) {
            data.remove(vv.getSeries());
            
        }
        countValues.set(data.size());
    }

}
