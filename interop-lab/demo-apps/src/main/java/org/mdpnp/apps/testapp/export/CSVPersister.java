package org.mdpnp.apps.testapp.export;


import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.common.eventbus.Subscribe;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import org.apache.log4j.Level;

public class CSVPersister extends FileAdapterApplicationFactory.PersisterUIController {

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
    
    private static NumberFormat valueFormat = NumberFormat.getNumberInstance();
    static {
        valueFormat.setMaximumFractionDigits(2);
        valueFormat.setMinimumFractionDigits(2);
    }

    static String toCSVLine(Value value) {
        StringBuilder sb = new StringBuilder();

        long ms = value.getDevTime();
        String devTime = dateFormats.get().format(new Date(ms));

        sb.append(value.getUniqueDeviceIdentifier()).append(",")
            .append(value.getMetricId()).append(",")
            .append(value.getInstanceId()).append(",")
            .append(devTime).append(",")
            .append(valueFormat.format(value.getValue()));

        return sb.toString();
    }

    @Subscribe
    public void handleDataSampleEvent(NumericsDataCollector.NumericSampleEvent evt) throws Exception {
        Value vital = evt.getValue();

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
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose a file");
        fc.setInitialDirectory(defaultLogFileName.getParentFile());
        
        File f = fc.showSaveDialog(null);
        if(null != f) {
            filePathLabel.setText(f.getAbsolutePath());
            appender.setFile(f.getAbsolutePath());
            appender.activateOptions();
        }
    }
    
    private File defaultLogFileName = new File("openice.csv");
    
    public void setup() {
        backupIndex.getSelectionModel().select(0);
        fSize.getSelectionModel().select(1);

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
        appender.setMaxBackupIndex(Integer.parseInt(backupIndex.getSelectionModel().getSelectedItem()));
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
