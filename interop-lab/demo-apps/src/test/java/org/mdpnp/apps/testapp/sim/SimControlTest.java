package org.mdpnp.apps.testapp.sim;

import org.junit.Test;
import org.mdpnp.apps.testapp.ConfigurationTest;
import org.mdpnp.devices.simulation.NumberWithJitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.util.Arrays;

/**
 * @author mfeinberg
 */
public class SimControlTest {

    private final static Logger log = LoggerFactory.getLogger(ConfigurationTest.class);

    @Test
    public void testDataGenerationBound1() throws Exception {
        testBoundDataGeneration(new SimControl.NumericValue("SpO2", rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, 0, 100, 90, 5), 400);
    }

    @Test
    public void testDataGenerationAverage1() throws Exception {
        testAveDataGeneration(new SimControl.NumericValue("SpO2", rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, 30, 70, 50, 3), 10, 200);
    }

    @Test
    public void testDataGenerationAverage2() throws Exception {
        testAveDataGeneration(new SimControl.NumericValue("SpO2", rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, 0, 100, 50, 2), 10, 200);
    }



    private void testAveDataGeneration(SimControl.NumericValue nv, double delta, int nSamples) {

        log.info("Initial=" + nv.initialValue + " Max delta=" + delta);

        PrettyLinePrinter p = new PrettyLinePrinter(System.err, nv);

        p.printLine("\n\ntestAveDataGeneration\n");

        Number d = new NumberWithJitter<Double>(nv.initialValue, nv.increment, delta);

        p.printLine(nv, nv.initialValue);

        for(int n=0; n<nSamples; n++) {
            double nextSample = d.doubleValue();
            p.printLine(nv, nextSample);
        }
    }

    private void testBoundDataGeneration(SimControl.NumericValue nv, int nSamples) {

        System.err.println("\n\ntestBoundDataGeneration\n");

        char arr[] = new char[(int)(nv.upperBound-nv.lowerBound)];
        Arrays.fill(arr, '.');

        double f = nv.initialValue;
        for(int n=0; n<nSamples; n++) {

            double diff = (nv.increment-(2*nv.increment*Math.random()));
            if(f+diff<=nv.lowerBound) {
                f -= diff;
            }
            else if(f+diff>=nv.upperBound) {
                f -= diff;
            }
            else {
                f += diff;
            }

            arr[(int)f] = 'X';
            System.err.println(new String(arr));
            arr[(int)f] = '.';
        }
    }

    static class RunningStat {

        private int count = 0;
        private double average = 0.0;
        private double pwrSumAvg = 0.0;
        private double stdDev = 0.0;

        /**
         * Incoming new values used to calculate the running statistics
         *
         * @param value
         */
        public double put(double value) {
            count++;
            average += (value - average) / count;
            pwrSumAvg += (value * value - pwrSumAvg) / count;
            stdDev = Math.sqrt((pwrSumAvg * count - count * average * average) / (count - 1));
            return value;
        }

        public double getAverage() {
            return average;
        }

        public double getStandardDeviation() {
            //double stdDev = Math.sqrt((pwrSumAvg * count - count * average * average) / (count - 1));
            return Double.isNaN(stdDev) ? 0.0 : stdDev;
        }

    }

    private class PrettyLinePrinter {
        private final PrintStream out;
        private final char arr[];
        private final RunningStat stats = new RunningStat();

        public PrettyLinePrinter(PrintStream out, SimControl.NumericValue nv) {
            this.out = out;
            this.arr = new char[(int)(nv.upperBound-nv.lowerBound)];
            Arrays.fill(this.arr, '.');
            this.arr[(int)(nv.initialValue-nv.lowerBound)] = '|';
        }

        void printLine(String msg) {
            out.println(msg);
            out.flush();
        }

        void printLine(SimControl.NumericValue nv, double currentValue) {
            stats.put(currentValue);

            int idx = (int)(currentValue-nv.lowerBound);
            arr[idx] = 'X';

            out.format("%s ave=%3.3f sigma=%2.3f%n",
                       new String(arr),
                       stats.getAverage(),
                       stats.getStandardDeviation());

            out.flush();

            arr[idx]    = '.';
            arr[(int)(nv.initialValue-nv.lowerBound)] = '|';
        }
    }
}
