package org.mdpnp.apps.testapp.vital;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.testapp.FxRuntimeSupport;
import org.mdpnp.apps.testapp.pca.VitalView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;

/**
 */
public class MultiRangeSliderTest {

    private static final Logger log = LoggerFactory.getLogger(MultiRangeSliderTest.class);

    @BeforeClass
    public static void setUpClass() throws Exception {

        FxRuntimeSupport.initialize();
    }

    protected Error t;
    private void testOnFxThread(Runnable run) throws InterruptedException {
        t = null;
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(new Runnable() {
            public void run() {
                try {
                    run.run();
                } catch (Error t1) {
                    t = t1;
                }

                latch.countDown();
            }
        });
        latch.await();
        if(null != t) {
            throw t;
        }
    }

    @Test
    public void testBindViewToModel() throws InterruptedException {
        testOnFxThread(() -> {

            MultiRangeSlider mrs = new MultiRangeSlider();

            SimpleListProperty<NumericFx> numericList = new SimpleListProperty<>();
            VitalModel model = new VitalModelImpl(null, numericList);

            // Create a vital from a template and assert that all values had been carried over properly
            //
            Vital vital = VitalSign.Test.addToModel(model);

            StringBuilder sb = new StringBuilder("VitalSign values:");
            sb.append("\nVitalSign values:");
            sb.append("\n\tMin ").append(VitalSign.Test.minimum);
            sb.append("\n\tCriticalLow ").append(VitalSign.Test.criticalLow);
            sb.append("\n\tStartingLow ").append(VitalSign.Test.startingLow);
            sb.append("\n\tStartingHigh ").append(VitalSign.Test.startingHigh);
            sb.append("\n\tCriticalHigh ").append(VitalSign.Test.criticalHigh);
            sb.append("\n\tMax ").append(VitalSign.Test.maximum);
            log.info(sb.toString());

            Assert.assertEquals("Failed to set", VitalSign.Test.minimum, vital.getMinimum(), 0.0);
            Assert.assertEquals("Failed to set", VitalSign.Test.criticalLow,  vital.getCriticalLow(), 0.0);
            Assert.assertEquals("Failed to set", VitalSign.Test.startingLow,  vital.getWarningLow(), 0.0);
            Assert.assertEquals("Failed to set", VitalSign.Test.startingHigh, vital.getWarningHigh(), 0.0);
            Assert.assertEquals("Failed to set", VitalSign.Test.criticalHigh, vital.getCriticalHigh(), 0.0);
            Assert.assertEquals("Failed to set", VitalSign.Test.maximum,      vital.getMaximum(), 0.0);

            // Bind the model to the view; confirm that the view had been updated to reflect the state
            // of the model.
            //
            VitalView.bindSlider(mrs, vital);

            sb = new StringBuilder("Slider values:");
            sb.append("\n\tMin ").append(mrs.getMin());
            sb.append("\n\tLowestValue ").append(mrs.getLowestValue());
            sb.append("\n\tLowerValue ").append(mrs.getLowerValue());
            sb.append("\n\tHigherValue ").append(mrs.getHigherValue());
            sb.append("\n\tHighestValue ").append(mrs.getHighestValue());
            sb.append("\n\tMax ").append(mrs.getMax());
            log.info(sb.toString());

            Assert.assertEquals("Failed to set", VitalSign.Test.minimum,      mrs.getMin(), 0.0);
            Assert.assertEquals("Failed to set", VitalSign.Test.criticalLow,  mrs.getLowestValue(), 0.0);
            Assert.assertEquals("Failed to set", VitalSign.Test.startingLow,  mrs.getLowerValue(), 0.0);
            Assert.assertEquals("Failed to set", VitalSign.Test.startingHigh, mrs.getHigherValue(), 0.0);
            Assert.assertEquals("Failed to set", VitalSign.Test.criticalHigh, mrs.getHighestValue(), 0.0);
            Assert.assertEquals("Failed to set", VitalSign.Test.maximum,      mrs.getMax(), 0.0);


        });
    }
}