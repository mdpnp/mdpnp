package org.mdpnp.apps.testapp.vital;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Test {
    private final ObjectProperty<Float> warningLow = new SimpleObjectProperty<Float>(this, "warningLow", null) {
        @Override
        protected void invalidated() {

        }
        public void set(Float newValue) {
            Float low = newValue;
            if (null != low) {
                if (criticalLow.get() != null && low < criticalLow.get()) {
                    low = criticalLow.get();
                } else if (warningHigh.get() != null && low > warningHigh.get()) {
                    low = warningHigh.get();
                }
            }
            super.set(low);
        };
    };
    private final ObjectProperty<Float> warningHigh = new SimpleObjectProperty<Float>(this, "warningHigh", null);
    private final ObjectProperty<Float> criticalLow = new SimpleObjectProperty<Float>(this, "criticalLow", null);
    private final ObjectProperty<Float> criticalHigh = new SimpleObjectProperty<Float>(this, "criticalHigh", null);

    public static void main(String[] args) {
        ObjectProperty<Float> bound = new SimpleObjectProperty<Float>(); 
        Test t = new Test();
        t.criticalLow.set(5f);
        
//        t.warningLow.bind(bound);
        bound.bindBidirectional(t.warningLow);
//        t.warningLow.set(4f);
//        t.warningLow.set(4f);
//        
        bound.set(4f);
        System.out.println(t.warningLow.get());
        System.out.println(bound.get());
        bound.set(6f);
        System.out.println(t.warningLow.get());
        System.out.println(bound.get());
    }
}
