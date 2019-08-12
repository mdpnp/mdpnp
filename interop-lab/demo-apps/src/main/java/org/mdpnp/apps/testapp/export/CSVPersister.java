package org.mdpnp.apps.testapp.export;


import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import com.google.common.eventbus.Subscribe;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import org.apache.log4j.Level;

public class CSVPersister extends DataCollectorAppFactory.PersisterUIController implements Initializable {

    static ThreadLocal<SimpleDateFormat> dateFormats = new ThreadLocal<SimpleDateFormat>()
    {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmssZ");
        }
    };
    
    @FXML Label filePathLabel;
    @FXML ComboBox<String> backupIndex, fSize;
    
    //TODO: Can it be a problem that this is static?
    private static boolean rawDateFormat=false;
    
    @Override
    public void setRawDateFormat(boolean raw) {
    	this.rawDateFormat=raw;
    };

    @Override
    public String getName() {
        return "xls (csv)";
    }

    @Override
    public boolean start() throws Exception {
        appender.activateOptions();
        final File f = new File(appender.getFile());
        if(f.exists() && f.length() != 0)
            appender.rollOver();
        return true;
    }

    @Override
    public void stop() throws Exception {
        final File f = new File(appender.getFile());
        // test for canWrite just to be safe in case delete fails for whatever reason
        if(f.exists() && f.length()==0 && f.canWrite())
            f.delete();
    }
    
    private static NumberFormat valueFormat = NumberFormat.getNumberInstance();
    static {
        valueFormat.setMaximumFractionDigits(4);
        valueFormat.setMinimumFractionDigits(2);
        //Issue 14 - disable grouping to prevent additional , characters appearing in outputs.
        valueFormat.setGroupingUsed(false);
    }

    // scientific notation, three decimal places, one exponent digit
    private static DecimalFormat scientificFormat = new DecimalFormat("0.000E0");

    static String toCSVLine(PatientAssessmentDataCollector.PatientAssessmentEvent value) {
        StringBuilder sb = new StringBuilder();

        long ms = value.getDevTime();
        String devTime = rawDateFormat ? Long.toString(ms) : dateFormats.get().format(new Date(ms));
        String mrn = value.getPatientId();

        sb.append(3).append(",").append(value.getUniqueDeviceIdentifier()).append(",")
                .append(devTime).append(",").append(mrn).append(",").append(1).append(",")
                .append(value.getValue().getKey()).append(",")
                .append(value.getValue().getValue());

        return sb.toString();
    }

    static String toCSVLine(SampleArrayDataCollector.SampleArrayEvent value) {
        StringBuilder sb = new StringBuilder();

        long ms = value.getDevTime();
        String devTime = rawDateFormat ? Long.toString(ms) : dateFormats.get().format(new Date(ms));
        Number v[] = value.getValues();
        String mrn = value.getPatientId();

        sb.append(2).append(",").append(value.getUniqueDeviceIdentifier()).append(",")
                .append(value.getMetricId()).append(",")
                .append(value.getInstanceId()).append(",")
                .append(devTime).append(",").append(mrn).append(",").append(v.length);

        for(Number n : v) {
            sb.append(",").append(scientificFormat.format(n.doubleValue()));
        }

        return sb.toString();
    }

    static String toCSVLine(NumericsDataCollector.NumericSampleEvent value) {
        StringBuilder sb = new StringBuilder();

        long ms = value.getDevTime();
        String devTime = rawDateFormat ? Long.toString(ms) : dateFormats.get().format(new Date(ms));
        String mrn = value.getPatientId();

        sb.append(1).append(",").append(value.getUniqueDeviceIdentifier()).append(",")
            .append(value.getMetricId()).append(",")
            .append(value.getInstanceId()).append(",")
            .append(devTime).append(",").append(mrn).append(",").append(1).append(",")
            .append(valueFormat.format(value.getValue()));

        return sb.toString();
    }

    @Subscribe
    public void handleDataSampleEvent(NumericsDataCollector.NumericSampleEvent evt) throws Exception {
        String s = toCSVLine(evt);
        cat.info(s);
    }

    @Subscribe
    public void handleDataSampleEvent(SampleArrayDataCollector.SampleArrayEvent evt) throws Exception {
        String s = toCSVLine(evt);
        cat.info(s);
    }

    @Subscribe
    public void handleDataSampleEvent(PatientAssessmentDataCollector.PatientAssessmentEvent evt) throws Exception {
        String s = toCSVLine(evt);
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
    
    private File defaultLogFileName = new File("openicedataexport.csv");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backupIndex.getSelectionModel().select(0);
        fSize.getSelectionModel().select(1);
        filePathLabel.setText(defaultLogFileName.getAbsolutePath());
    }

    public void setup() {
        int maxBackupIndex = Integer.parseInt(backupIndex.getSelectionModel().getSelectedItem());
        String maxFileSize = fSize.getSelectionModel().getSelectedItem();
        setup(maxBackupIndex, maxFileSize);
    }

    void setup(int maxBackupIndex, String maxFileSize) {


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
        appender.setMaxBackupIndex(maxBackupIndex);
        appender.setMaxFileSize(maxFileSize);
        appender.setAppend(true);
        appender.setLayout(new org.apache.log4j.PatternLayout("%m%n"));
        appender.setThreshold(Level.ALL);
        cat.setAdditivity(false);
        cat.setLevel(Level.ALL);
        cat.addAppender(appender);

    }

    private org.apache.log4j.RollingFileAppender appender = null;
    private org.apache.log4j.Category cat = org.apache.log4j.Logger.getLogger("OpenICEDataExport.CVS");
}
