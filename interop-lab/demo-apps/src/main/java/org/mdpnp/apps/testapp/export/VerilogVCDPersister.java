package org.mdpnp.apps.testapp.export;


import ice.Time_t;
import org.mdpnp.apps.testapp.vital.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class VerilogVCDPersister extends FileAdapterApplicationFactory.PersisterUI implements DataCollector.DataSampleEventListener {

    private static final Logger log = LoggerFactory.getLogger(VerilogVCDPersister.class);

    static ThreadLocal<SimpleDateFormat> dateFormats = new ThreadLocal<SimpleDateFormat>()
    {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy.MMddHH.mmss");
        }
    };

    static ThreadLocal<DecimalFormat> floatFormats = new ThreadLocal<DecimalFormat>()
    {
        protected DecimalFormat initialValue() {
            return new DecimalFormat("#0.0000000");
        }
    };


    OneWavePerVCD controller = null;
    final JLabel filePathLabel;

    public VerilogVCDPersister() {

        setLayout(new GridLayout(1, 2));
        add(new JLabel("Logging trace files to directory: ", JLabel.RIGHT));
        filePathLabel = new JLabel();
        add(filePathLabel);
    }


    @Override
    public void handleDataSampleEvent(DataCollector.DataSampleEvent evt) throws Exception {
        Value vital = (Value)evt.getSource();
        controller.persist(vital);
    }

    @Override
    public void stop() throws Exception {
        if(controller != null)
            controller.stop();
        controller = null;
    }

    @Override
    public boolean start() throws Exception {
        String now = dateFormats.get().format(new Date());
        File f = new File(now);
        f.mkdirs();
        filePathLabel.setText(f.getAbsolutePath());

        controller = new OneWavePerVCD(f);
        controller.start();
        return true;
    }

    static class OneWavePerVCD {

        final File baseDir;

        Map<String, VCDFileHandler> cache = new HashMap<>();

        public OneWavePerVCD(File f) {
            baseDir = f;
        }

        public boolean start() throws Exception {
            return true;
        }

        public void stop() throws Exception {
            for(VCDFileHandler swh : cache.values()) {
                swh.stop();
            }
        }

        public void persist(Value vital) throws Exception {
            String key = vital.getInstanceId() + "-" + vital.getMetricId();

            VCDFileHandler fileHandler = cache.get(key);
            if (fileHandler == null) {

                Time_t t = vital.getNumeric().device_time;

                PrintStream ps = makeStream(key);

                fileHandler = new VCDFileHandler(ps, key, t);
                cache.put(key, fileHandler);
            }

            fileHandler.persist(vital);
        }

        protected PrintStream makeStream(String key) throws IOException {
            File f = new File(baseDir, key + ".vcd");
            log.info("Opening File " + f.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(f);
            return new PrintStream(fos);
        }

        static class VCDFileHandler {

            final PrintStream ps;
            final long firstTimeTic;

            VCDFileHandler(PrintStream out, String key, Time_t t) {

                ps = out;
                firstTimeTic = t.sec * 1000L + t.nanosec / 1000000L;

                ps.println("$date");
                ps.println("\t\t" + dateFormats.get().format(new Date(firstTimeTic)));
                ps.println("$end");

                ps.println("$version");
                ps.println("\t\t" + "MDPNP V0.1 2015");
                ps.println("$end");

                ps.println("$timescale");
                ps.println("\t\t" + "1ms");
                ps.println("$end");

                ps.println("$scope module top $end");

                ps.print("$var real 32 ");
                ps.print(" *");
                ps.print(" ");
                ps.print(key);
                ps.println(" $end");
            }

            public void stop() throws Exception {
                ps.flush();
                ps.close();
            }

            public void persist(Value value) throws Exception {

                Time_t t = value.getNumeric().device_time;
                long baseTime = t.sec * 1000L + t.nanosec / 1000000L;

                StringBuilder sb = new StringBuilder();
                sb.append("#").append(baseTime - firstTimeTic).append("\n");
                float f = value.getNumeric().value;
                String s = floatFormats.get().format(f);
                sb.append("r").append(s).append(" *").append("\n");

                ps.print(sb.toString());
                ps.flush();
            }
        }
    }

    /* MAYBE figure out the time sync and dump multiple traces into a single file.
    //
    static class MultiWaveHandler {
        public boolean start() throws Exception {
            return true;
        }

        public void stop() throws Exception {
            // bleed the buffer out
            while (!cache.isEmpty()) {
                long minT = cache.firstKey();
                Map<String, Float> data = cache.remove(minT);
                dump(minT, data);
            }
        }

        public void persist(Value value) throws Exception {

            long t = value.getNumeric().device_time.sec;

            if (cache.size() != 0) {
                long minT = cache.firstKey();
                long maxT = cache.lastKey();

                // we got the value that is too much in the past from what we can keep in the buffer
                if (maxT - t > bufferSize) {
                    log.warn("Some clocks must be off: got 'out of range' stale value: " + value.toString());
                    return;
                }

                buffer(value);

                // we have buffered enough data and the new sample is outside of the range. We
                // have to assume that the clocks are synchronized to some narrower range than the
                // cache that we are tracking.

                while (maxT - minT > bufferSize) {
                    Map<String, Float> data = cache.remove(minT);
                    dump(minT, data);
                    minT = cache.firstKey();
                }

            } else { // first tick
                buffer(value);
            }
        }

        void dump(long t, Map<String, Float> data) {

            PrintStream ps = System.out;

            if (headerDone == null) {

                headerDone = new ArrayList<String>(data.keySet());


                ps.println("$date");
                ps.println("Wed Jan 28 10:17:06 2015");
                ps.println("$end");
                ps.println("$version");
                ps.println("MDPNP V0.1 2015");
                ps.println("$end");
                ps.println("$timescale");
                ps.println("1sec");
                ps.println("$end");
                ps.println("$scope module top $end");

                for (String s : headerDone) {
                    ps.print("$var real 32 ");
                    ps.print(" *");
                    ps.print(headerDone.indexOf(s));
                    ps.print(" ");
                    ps.print(s);
                    ps.println(" $end");
                }

                firstTimeTic = t;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("#").append(t - firstTimeTic).append("\n");
            for (Map.Entry<String, Float> entry : data.entrySet()) {
                float f = entry.getValue();
                sb.append("r").append(f).append(" *").append(headerDone.indexOf(entry.getKey())).append("\n");
            }
            ps.println(sb.toString());
        }

        void buffer(Value value) {

            long t = value.getNumeric().device_time.sec;

            Map<String, Float> data = cache.get(t);
            if (data == null) {
                data = new HashMap<>();
                cache.put(t, data);
            }

            String key = value.getMetricId() + "-" + value.getInstanceId();
            data.put(key, value.getNumeric().value);
        }

        TreeMap<Long, Map<String, Float>> cache = new TreeMap<>();

        public void setBuffetSize(int sz, TimeUnit tu) {
            bufferSize = (int) tu.toSeconds(sz);
        }

        long firstTimeTic;
        int bufferSize = 10;
        List<String> headerDone = null;
    }
    */
}
