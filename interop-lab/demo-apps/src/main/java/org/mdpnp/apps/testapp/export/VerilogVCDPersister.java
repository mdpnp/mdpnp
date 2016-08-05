package org.mdpnp.apps.testapp.export;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * The purpose of this component is to demonstrate persistence of the data in the standard IEEE-1364
 * format that could be read by a variety of tools http://en.wikipedia.org/wiki/Waveform_viewer.
 * Overview of the history of the format is http://en.wikipedia.org/wiki/Value_change_dump
 *
 * The document describing the format of the data (from cadence's Verilog-XL Reference, Product Version 5.6)
 * is included with the source of the project.
 *
 **/
public class VerilogVCDPersister extends DataCollectorAppFactory.PersisterUIController {

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

    public static final long FZ_1MB =1000000L;
    public static final long FZ_10MB=10000000L;

    OneWavePerVCD controller = null;
    @FXML TextField filePathLabel, maxSizeLabel;

    public VerilogVCDPersister() {

    }

    public void setup() {
        String f = "<" + dateFormats.get().toPattern() + ">";
        String p = (new File(f)).getAbsolutePath();
        filePathLabel.setText(p);        
    }

    @Subscribe
    public void handleDataSampleEvent(NumericsDataCollector.NumericSampleEvent evt) throws Exception {
        controller.persist(evt);
    }

    @Subscribe
    public void handleDataSampleEvent(SampleArrayDataCollector.SampleArrayEvent evt) throws Exception {
        controller.persist(evt);
    }

    @Override
    public String getName() {
        return "vcd (ieee-1364)";
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

        controller = new OneWavePerVCD(f, FZ_10MB);
        controller.start();
        return true;
    }

    static class OneWavePerVCD {

        final File baseDir;
        final long maxFileSize;

        Map<String, VCDFileHandler> cache = new HashMap<>();

        public OneWavePerVCD(File f, long sz) {
            baseDir = f;
            maxFileSize = sz;
        }

        public boolean start() throws Exception {
            return true;
        }

        public void stop() throws Exception {
            for(VCDFileHandler swh : cache.values()) {
                swh.stop();
            }
        }

        public void persist(NumericsDataCollector.NumericSampleEvent evt) throws Exception {

            final VCDFileHandler fileHandler = getVcdFileHandler(evt);

            if(fileHandler.getSize()<maxFileSize) {
                long baseTime = evt.getDevTime();
                double v =  evt.getValue();
                fileHandler.persist(evt, baseTime, v);
            }
        }

        public void persist(SampleArrayDataCollector.SampleArrayEvent evt) throws Exception {

            final VCDFileHandler fileHandler = getVcdFileHandler(evt);

            if(fileHandler.getSize()<maxFileSize) {
                SampleArrayDataCollector.ArrayToNumeric.convert(evt, (DataCollector.DataSampleEvent meta, long ms, double v) -> {
                    fileHandler.persist(meta, ms, v);
                });
            }
        }


        private VCDFileHandler getVcdFileHandler(DataCollector.DataSampleEvent evt) throws IOException {

            String key = evt.getUniqueDeviceIdentifier() + "-" + evt.getMetricId() + "-" + evt.getInstanceId();

            VCDFileHandler fileHandler = cache.get(key);
            if (fileHandler == null) {

                OutputStream os = makeStream(key);

                fileHandler = new VCDFileHandler(os, key, evt.getDevTime());
                cache.put(key, fileHandler);
            }
            return fileHandler;
        }

        protected OutputStream makeStream(String key) throws IOException {
            File f = new File(baseDir, key + ".vcd");
            log.info("Opening File " + f.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(f);
            return fos;
        }

        static class VCDFileHandler {

            final OutputStream os;
            final PrintStream  ps;
            final long firstTimeTic;

            VCDFileHandler(OutputStream out, String key, long t) {

                os = out;
                ps = new PrintStream(out);

                firstTimeTic = t;

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

            synchronized public void stop() throws Exception {
                ps.flush();
                os.flush();
                ps.close();
                os.close();
            }

            synchronized long getSize() throws Exception {
                if(os instanceof FileOutputStream) {
                    long l = ((FileOutputStream)os).getChannel().size();
                    return l;
                }
                else {
                    return 0;
                }
            }

            void persist(DataCollector.DataSampleEvent evt, long ms, double v) throws Exception {

                StringBuilder sb = new StringBuilder();
                sb.append("#").append(ms - firstTimeTic).append("\n");;
                String s = floatFormats.get().format((float)v);
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
