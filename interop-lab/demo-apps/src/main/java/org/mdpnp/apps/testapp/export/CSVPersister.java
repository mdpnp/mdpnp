package org.mdpnp.apps.testapp.export;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import org.apache.log4j.Level;
import org.mdpnp.apps.testapp.vital.Value;

public class CSVPersister extends FileAdapterApplicationFactory.PersisterUIController implements DataCollector.DataSampleEventListener  {

    static ThreadLocal<SimpleDateFormat> dateFormats = new ThreadLocal<SimpleDateFormat>()
    {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmssZ");
        }
    };
    
    @FXML Label filePathLabel;
    @FXML ComboBox<String> backupIndex, fSize;

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

        long ms = value.getTimestamp(); // //DataCollector.toMilliseconds(value.getNumeric().device_time);
        String devTime = dateFormats.get().format(new Date(ms));

        sb.append(value.getUniqueDeviceIdentifier()).append(",")
            .append(value.getMetricId()).append(",")
            .append(value.getInstanceId()).append(",")
            .append(devTime).append(",")
            .append(value.getValue());

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
    }
    
    @FXML public void clickBackupIndex(ActionEvent evt) {
        String s = backupIndex.getSelectionModel().getSelectedItem();
        if(appender != null) {
            appender.setMaxBackupIndex(Integer.parseInt(s));
            appender.activateOptions();
        }
    }
    
    @FXML public void clickFSize(ActionEvent evt) {
        String s = fSize.getSelectionModel().getSelectedItem();
        if(appender != null) {
            appender.setMaxFileSize(s);
            appender.activateOptions();
        }
    }
    
    @FXML public void clickChange(ActionEvent evt) {
//        while(frame != null && !(frame instanceof JFrame))
//            frame = frame.getParent();
//        if(frame == null)
//            throw new IllegalStateException("Could not locate window frame");

        FileChooser fc = new FileChooser();
        fc.setTitle("Choose a file");
//        fc.setInitialDirectory(defaultLogFileName.getParent());
        
//        fd.setDirectory(defaultLogFileName.getParent());
//        fd.setVisible(true);

//        fc.showSaveDialog(null);
//        String fName = fc.getFile();
//        String dir = fc.getDirectory();
//        if(dir != null && fName != null) {
//            File f = new File(dir, fName);
        File f = fc.showSaveDialog(null);
            filePathLabel.setText(f.getAbsolutePath());
            appender.setFile(f.getAbsolutePath());
            appender.activateOptions();
//        }
    }
    
    public void set() {
        backupIndex.getSelectionModel().select(0);
        fSize.getSelectionModel().select(1);

        final File defaultLogFileName = new File("demo-app.csv");
        
        filePathLabel.setText(defaultLogFileName.getAbsolutePath());


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


        // add file size controls.
        appender = new org.apache.log4j.RollingFileAppender();
        appender.setFile(defaultLogFileName.getAbsolutePath());
        appender.setMaxBackupIndex(Integer.parseInt(backupIndex.getSelectionModel().getSelectedItem())-1);
        appender.setMaxFileSize(fSize.getSelectionModel().getSelectedItem());
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
