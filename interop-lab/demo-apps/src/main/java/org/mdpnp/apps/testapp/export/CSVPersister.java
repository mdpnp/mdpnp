package org.mdpnp.apps.testapp.export;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import com.google.common.eventbus.Subscribe;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class CSVPersister extends DataCollectorAppFactory.PersisterUIController implements Initializable {

    static ThreadLocal<SimpleDateFormat> dateFormats = new ThreadLocal<SimpleDateFormat>()
    {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmssZ");
        }
    };
    
    @FXML Label filePathLabel;
    @FXML ComboBox<String> backupIndex, fSize;
    @FXML Button changeButton;
    @FXML TextField sepChar;
    
    //TODO: Can it be a problem that this is static?
    private static boolean rawDateFormat=false;
    
    private static String separator=",";

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
    	//only process controls if they exist - otherwise tests fail.
    	if(backupIndex!=null) {
	    	backupIndex.setDisable(true);
	    	fSize.setDisable(true);
	    	changeButton.setDisable(true);
	        if(sepChar.getText().length()>0) {
	            if(sepChar.getText().equals("\\t")) {
	                //If they wrote two characters in the box as first character \ and second t, they wanted a tab...
	                separator="\t";
                } else {
                    separator=sepChar.getText();
                }
            }
        }

        return true;
    }

    @Override
    public void stop() throws Exception {
    	backupIndex.setDisable(false);
    	fSize.setDisable(false);
    	changeButton.setDisable(false);
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

        sb.append(3).append(separator).append(value.getUniqueDeviceIdentifier()).append(separator)
                .append(devTime).append(separator).append(mrn).append(separator).append(1).append(separator)
                .append(value.getValue().getKey()).append(separator)
                .append(value.getValue().getValue());

        return sb.toString();
    }

    static String toCSVLine(SampleArrayDataCollector.SampleArrayEvent value) {
        StringBuilder sb = new StringBuilder();

        long ms = value.getDevTime();
        String devTime = rawDateFormat ? Long.toString(ms) : dateFormats.get().format(new Date(ms));
        Number v[] = value.getValues();
        String mrn = value.getPatientId();

        sb.append(2).append(separator).append(value.getUniqueDeviceIdentifier()).append(separator)
                .append(value.getMetricId()).append(separator)
                .append(value.getInstanceId()).append(separator)
                .append(devTime).append(separator).append(mrn).append(separator).append(v.length);

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

        sb.append(1).append(separator).append(value.getUniqueDeviceIdentifier()).append(separator)
            .append(value.getMetricId()).append(separator)
            .append(value.getInstanceId()).append(separator)
            .append(devTime).append(separator).append(mrn).append(separator).append(1).append(separator)
            .append(valueFormat.format(value.getValue()));

        return sb.toString();
    }

    @Subscribe
    public void handleDataSampleEvent(NumericsDataCollector.NumericSampleEvent evt) throws Exception {
        String s = toCSVLine(evt);
        LogRecord record=new LogRecord(java.util.logging.Level.INFO, s);
        fileHandler.publish(record);
    }

    @Subscribe
    public void handleDataSampleEvent(SampleArrayDataCollector.SampleArrayEvent evt) throws Exception {
        String s = toCSVLine(evt);
        LogRecord record=new LogRecord(java.util.logging.Level.INFO, s);
        fileHandler.publish(record);
    }

    @Subscribe
    public void handleDataSampleEvent(PatientAssessmentDataCollector.PatientAssessmentEvent evt) throws Exception {
        String s = toCSVLine(evt);
        LogRecord record=new LogRecord(java.util.logging.Level.INFO, s);
        fileHandler.publish(record);
    }

    public CSVPersister() {
        super();
    }
    
    @FXML public void clickBackupIndex(ActionEvent evt) {
        String s = backupIndex.getSelectionModel().getSelectedItem();
        //if(appender != null) {
        	configureLoggerFromSettings2(Integer.parseInt(s), null, null);
            //appender.setMaxBackupIndex(Integer.parseInt(s));
            //appender.activateOptions();
        //}
    }
    
    @FXML public void clickFSize(ActionEvent evt) {
        String s = fSize.getSelectionModel().getSelectedItem();
        //if(appender != null) {
        	configureLoggerFromSettings2(-1,s,null);
            //appender.setMaxFileSize(s);
            //appender.activateOptions();
        //}
    }
    
    @FXML public void clickChange(ActionEvent evt) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose a file");
        fc.setInitialDirectory(defaultLogFileName.getParentFile());
        
        File f = fc.showSaveDialog(null);
        if(null != f) {
            filePathLabel.setText(f.getAbsolutePath());
            configureLoggerFromSettings2(-1,null,f.getAbsolutePath());
            //appender.setFile(f.getAbsolutePath());
            //appender.activateOptions();
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
    	
    	configureLoggerFromSettings2(maxBackupIndex, maxFileSize, null);
    	
    	

    }
    
    private void configureLoggerFromSettings2(int maxBackupIndex, String maxFileSize, String fileName) {
    	
    	if(maxBackupIndex==-1) {
    		maxBackupIndex=Integer.parseInt(backupIndex.getValue());
    	}
    	
    	if(maxFileSize==null) {
    		maxFileSize=fSize.getValue();
    	}
    	
    	if(fileName==null) {
    		fileName=defaultLogFileName.getAbsolutePath();
    	}
    	String fileWithoutSuffix=null;
    	String suffix=null;
    	if(fileName.indexOf('.')!=-1) {
    		fileWithoutSuffix=fileName.substring(0,fileName.lastIndexOf('.'));
        	suffix=fileName.substring(fileName.lastIndexOf('.'));
    	} else {
    		fileWithoutSuffix=fileName;
    		suffix="";
    	}
    	String fileNamePattern=fileWithoutSuffix+"-%g"+suffix;
    	
    	int numericMax=getMaxFileSize(maxFileSize);
    	
    	try {
			 fileHandler=new FileHandler(fileNamePattern, numericMax, maxBackupIndex);
			 fileHandler.setFormatter(new PlainTextFormatter());
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    /**
     * 
     * @param strMax the string format of size such as 500MB, 1GB
     * @return
     */
    private int getMaxFileSize(String strMax) throws RuntimeException {
    	
    	int i;
    	char chars[]=strMax.toCharArray();
    	for(i=0;i<chars.length;i++) {
    		if(Character.isAlphabetic(chars[i])) {
    			break;
    		}
    	}
    	int base=Integer.parseInt(strMax.substring(0,i));
    	String factor=strMax.substring(i);
    	
    	if(factor.equals("MB")) {
    		return base*1024*1024;
    	}
    	if(factor.equals("GB")) {
    		return base*1024*1024*1024;
    	}
    	
    	throw new RuntimeException("Unknown file size suffix "+factor);
    }
    
    private class PlainTextFormatter extends Formatter {
    	
    	String newLine=System.getProperty("line.separator");

		@Override
		public String format(LogRecord record) {
			return record.getMessage()+newLine;
		}
    	
    }

    private FileHandler fileHandler = null;
}
