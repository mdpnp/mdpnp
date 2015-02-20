package org.mdpnp.apps.testapp.export;


import ice.Time_t;
import org.mdpnp.apps.testapp.vital.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public static final long FZ_1MB =1000000L;
    public static final long FZ_10MB=10000000L;

    OneWavePerVCD controller = null;
    final JTextField filePathLabel;
    final JTextField maxSizeLabel;

    public VerilogVCDPersister() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(2, 5, 2, 5);

        setLayout(gridbag);
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        Label label1 = new Label("Configuration:", Label.RIGHT);
        gridbag.setConstraints(label1, configureLabel(constraints, 0));
        add(label1);

        JLabel dl = new JLabel("Directory:", JLabel.RIGHT);
        gridbag.setConstraints(dl, configureLabel(constraints, 1));
        add(dl);

        String f = "<" + dateFormats.get().toPattern() + ">";
        String p = (new File(f)).getAbsolutePath();
        filePathLabel = new JTextField(p);
        filePathLabel.setEditable(false);
        gridbag.setConstraints(filePathLabel, configureValue(constraints, 1));
        add(filePathLabel);

        JLabel sl = new JLabel("Max file size:", JLabel.RIGHT);
        gridbag.setConstraints(sl, configureLabel(constraints, 2));
        add(sl);

        maxSizeLabel = new JTextField("10MB");
        maxSizeLabel.setEditable(false);
        gridbag.setConstraints(maxSizeLabel, configureValue(constraints, 2));
        add(maxSizeLabel);
    }



    private GridBagConstraints configureLabel(GridBagConstraints constraints, int row) {
        buildConstraints(constraints, 0, row, 1, 1, 0.0, 1.0);
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        return constraints;
    }

    private GridBagConstraints configureValue(GridBagConstraints constraints, int row) {
        buildConstraints(constraints, 1, row, GridBagConstraints.REMAINDER, 1, 1.0, 1.0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.EAST;
        return constraints;
    }

    private void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, double wx, double wy) {
        gbc.gridx = gx;
        gbc.gridy = gy;
        gbc.gridwidth = gw;
        gbc.gridheight = gh;
        gbc.weightx = wx;
        gbc.weighty = wy;
    }

    @Override
    public void handleDataSampleEvent(DataCollector.DataSampleEvent evt) throws Exception {
        Value vital = (Value)evt.getSource();
        controller.persist(vital);
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

        public void persist(Value vital) throws Exception {

            String key = vital.getUniqueDeviceIdentifier() + "-" + vital.getMetricId() + "-" + vital.getInstanceId();

            VCDFileHandler fileHandler = cache.get(key);
            if (fileHandler == null) {

                Time_t t = vital.getNumeric().device_time;

                OutputStream os = makeStream(key);

                fileHandler = new VCDFileHandler(os, key, t);
                cache.put(key, fileHandler);
            }

            if(fileHandler.getSize()<maxFileSize)
                fileHandler.persist(vital);
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

            VCDFileHandler(OutputStream out, String key, Time_t t) {

                os = out;
                ps = new PrintStream(out);

                firstTimeTic = DataCollector.toMilliseconds(t);

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

            public void persist(Value value) throws Exception {

                Time_t t = value.getNumeric().device_time;
                long baseTime = DataCollector.toMilliseconds(t);

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
