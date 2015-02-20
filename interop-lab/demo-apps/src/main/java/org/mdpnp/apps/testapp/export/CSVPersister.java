package org.mdpnp.apps.testapp.export;


import org.apache.log4j.Level;
import org.mdpnp.apps.testapp.vital.Value;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

class CSVPersister extends FileAdapterApplicationFactory.PersisterUI implements DataCollector.DataSampleEventListener  {

    static ThreadLocal<SimpleDateFormat> dateFormats = new ThreadLocal<SimpleDateFormat>()
    {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmssZ");
        }
    };

    @Override
    public String getName() {
        return "xls (csv)";
    }

    @Override
    public boolean start() throws Exception {
        return true;
    }

    @Override
    public void stop() throws Exception {
        appender.rollOver();
    }

    static String toCSVLine(Value value) {
        StringBuilder sb = new StringBuilder();

        long ms = DataCollector.toMilliseconds(value.getNumeric().device_time);
        String devTime = dateFormats.get().format(new Date(ms));

        sb.append(value.getUniqueDeviceIdentifier()).append(",")
            .append(value.getMetricId()).append(",")
            .append(value.getInstanceId()).append(",")
            .append(devTime).append(",")
            .append(value.getNumeric().value);

        return sb.toString();
    }

    @Override
    public void handleDataSampleEvent(DataCollector.DataSampleEvent evt) throws Exception {
        Value vital = (Value)evt.getSource();

        String s = toCSVLine(vital);

        // LoggingEvent le = new LoggingEvent("", null, Level.ALL, sb.toString(), null);
        cat.info(s);
    }

    public CSVPersister() {

        super();

        JComboBox backupIndex = new JComboBox(new String[] { "1", "5", "10", "20"});
        backupIndex.addActionListener(new ActionListener()
                                      {
                                          @Override
                                          public void actionPerformed(ActionEvent e) {

                                              Object o = ((JComboBox)e.getSource()).getSelectedItem();
                                              if(appender != null) {
                                                  appender.setMaxBackupIndex(Integer.parseInt(o.toString()));
                                                  appender.activateOptions();
                                              }
                                          }
                                      }
        );
        backupIndex.setSelectedIndex(0);

        JComboBox fSize = new JComboBox(new String[] { "1MB", "5MB", "50MB", "500MB", "1GB", "5GB", "50GB", "500GB", "1000GB"});
        fSize.addActionListener(new ActionListener()
                                {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {

                                        Object o = ((JComboBox)e.getSource()).getSelectedItem();
                                        if(appender != null) {
                                            appender.setMaxFileSize(o.toString());
                                            appender.activateOptions();
                                        }
                                    }
                                }
        );
        fSize.setSelectedIndex(1);

        this.setLayout(new GridLayout(2, 1));

        final File defaultLogFileName = new File("demo-app.csv");

        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel("Logging to: ", JLabel.LEFT));
        final JLabel filePathLabel = new JLabel(defaultLogFileName.getAbsolutePath());
        p.add(filePathLabel);

        // Help me here. How do I get JFileChooser have  'new file name' text box on mac os?
        // And FileDialog's file filter does no work.
        //
            /*
            final JFileChooser fc = new JFileChooser();
            JButton fileSelector = new JButton("Change File");
            fileSelector.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int returnVal = fc.showOpenDialog(CSVPersister.this);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        filePathLabel.setText(file.getAbsolutePath());
                        appender.setFile(file.getAbsolutePath());
                        appender.activateOptions();
                    }
                }
            });
            fc.setDialogType(JFileChooser.SAVE_DIALOG);
            fc.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f != null && f.getName().endsWith(".csv");
                }

                @Override
                public String getDescription() {
                    return "CSV Files";
                }
            });
            */


        JButton fileSelector = new JButton("Change");
        fileSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Component frame = CSVPersister.this;
                while(frame != null && !(frame instanceof JFrame))
                    frame = frame.getParent();
                if(frame == null)
                    throw new IllegalStateException("Could not locate window frame");

                FileDialog fd = new FileDialog((JFrame)frame, "Choose a file", FileDialog.SAVE);
                fd.setDirectory(defaultLogFileName.getParent());
                fd.setVisible(true);

                String fName = fd.getFile();
                String dir = fd.getDirectory();
                if(dir != null && fName != null) {
                    File f = new File(dir, fName);
                    filePathLabel.setText(f.getAbsolutePath());
                    appender.setFile(f.getAbsolutePath());
                    appender.activateOptions();
                }
            }
        });

        p.add(fileSelector);
        this.add(p);

        // add file size controls.
        //
        JPanel fControl = new JPanel();
        fControl.setLayout(new FlowLayout(FlowLayout.LEFT));

        fControl.add(new JLabel("Number of files to keep around:", JLabel.LEFT));
        fControl.add(backupIndex);
        fControl.add(new JLabel("Max file size:"));
        fControl.add(fSize);

        this.add(fControl);

        appender = new org.apache.log4j.RollingFileAppender();
        appender.setFile(defaultLogFileName.getAbsolutePath());
        appender.setMaxBackupIndex(Integer.parseInt(backupIndex.getSelectedItem().toString())-1);
        appender.setMaxFileSize(fSize.getSelectedItem().toString());
        appender.setAppend(true);
        appender.setLayout(new org.apache.log4j.PatternLayout("%m%n"));
        appender.setThreshold(Level.ALL);
        appender.activateOptions();
        cat.setAdditivity(false);
        cat.setLevel(Level.ALL);
        cat.addAppender(appender);
    }

    private org.apache.log4j.RollingFileAppender appender = null;
    private org.apache.log4j.Category cat = org.apache.log4j.Logger.getLogger("VitalSimpleTable.CVS");
}
