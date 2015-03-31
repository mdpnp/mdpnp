package org.mdpnp.devices.simulation;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mfeinberg
 */
public class NumberWithGradientTest {

    private final static Logger log = LoggerFactory.getLogger(NumberWithGradientTest.class);

    @Test
    public void testLevelToPositive() throws Exception {

        Number n = new NumberWithGradient(3, 10, 2);
        int expected[] = { 3, 5, 7, 9, 10, 10, 10, 10};
        for(int idx=0; idx<expected.length; idx++) {
            int i = n.intValue();
            log.info("iteration " + idx + " value="+i);
            Assert.assertEquals("Invalid value at iteration " + idx, expected[idx], i);
        }
    }

    @Test
    public void testLevelToNegative() throws Exception {

        Number n = new NumberWithGradient(3, -7, 2);
        int expected[] = { 3, 1, -1, -3, -5, -7, -7, -7};
        for(int idx=0; idx<expected.length; idx++) {
            int i = n.intValue();
            Assert.assertEquals("Invalid value at iteration " + idx, expected[idx], i);
        }
    }

    @Test
    public void testLevelToJitter() throws Exception {

        Number val = new NumberWithJitter<Integer>(15, 1, 2);
        Number n = new NumberWithGradient(3, val, 2);
        int expected[] = { 3, 5, 7, 9, 11, 13 };
        for(int idx=0; idx<expected.length; idx++) {
            int i = n.intValue();
            Assert.assertEquals("Step 1: Invalid value at iteration " + idx, expected[idx], i);
        }
        for(int idx=0; idx<10; idx++) {
            int i = n.intValue();
            Assert.assertTrue("Step 2: Invalid value at iteration " + idx, Math.abs(i - 15) <= 2);
        }
    }

    @Test
    public void testApplyValue() throws Exception {

        // make sure that we can re-assign the gradient without loosing the original start value.
        // this is to emulate the case when the user changes the simulator controls via the gui
        // and that sends increments for every drag of the slider.
        //
        Number initial = new Integer(15);

        for (int idx = 1; idx < 10; idx++) {
            Number target = new Integer(15 + idx);
            initial = new NumberWithGradient(initial, target, 1);
            log.info("change initial value to " + initial);
        }

        Assert.assertEquals("failed to preserve original start value", 15, initial.intValue());
    }

}
