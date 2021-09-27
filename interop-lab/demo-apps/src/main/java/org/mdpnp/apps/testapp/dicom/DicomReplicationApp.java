package org.mdpnp.apps.testapp.dicom;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import javax.swing.JFrame;

import org.mdpnp.devices.MDSHandler;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.StoredFilePathStrategy;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import com.pixelmed.display.SourceImage;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.FindSOPClassSCU;
import com.pixelmed.network.GetSOPClassSCU;
import com.pixelmed.network.IdentifierHandler;
import com.pixelmed.network.ReceivedObjectHandler;
import com.pixelmed.utils.DateUtilities;
import com.rti.dds.subscription.Subscriber;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.*;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.transfer.s3.CompletedUpload;
import software.amazon.awssdk.transfer.s3.S3ClientConfiguration;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.Upload;

/**
 * All the credit for DICOM related code in this app should go to
 * 
 * https://saravanansubramanian.com/dicomtutorials
 * 
 * the site provides comprehensive examples that were more than good enough to get us going
 * on all the DICOM things.  Many thanks to that author.  Their github is at
 * 
 * https://github.com/SaravananSubramanian/dicom
 * 
 * Dicom communication library provided by http://www.pixelmed.com/dicomtoolkit.html
 * 
 * @author Simon Kelly
 *
 */
public class DicomReplicationApp {
	
	/**
	 * Our logger instance.
	 */
	private static final Logger log = LoggerFactory.getLogger(DicomReplicationApp.class);
	
	@FXML
	private TextField ipAddress;
	
	@FXML
	private TextField portNumber;
	
	@FXML
	private TextField patientName;
	
	@FXML
	private TableView<DicomFileForTable> filesTable;
	
	@FXML
	private DatePicker startDate;
	
	@FXML
	private DatePicker endDate;
	
	@FXML
	private ToggleGroup runGroup;
	
	@FXML
	private RadioButton runOnce;
	
	@FXML
	private RadioButton runLoop;
	
	ArrayList<AttributeList> knownEntries;
	
	/**
	 * Directory in which to store retrieved images.
	 */
	File dicomImageDirectory=new File("dicom-images");

	/**
	 * Identifier for the remote system.  This needs to be configurable later
	 */
	private final String REMOTE_AE="ORTHANC";
	//private final String REMOTE_AE="MEDCONN";
	
	/**
	 * Identifier for the local system.  This needs to be configurable later
	 */
	private final String OPENICE_AE="OpenICE";
	
	/**
	 * The S3 transfer manager.  We will do many transfers, but they can all use this.
	 */
	private S3TransferManager s3TransferManager;
	
	/**
	 * The main processing thread.
	 */
	private Thread queryThread;
	
	/**
	 * Indicator to tell the processing thread to stop.
	 */
	private boolean pleaseStop;
	
	/**
	 * The start date to get images for if one is specified in the UI
	 */
	private LocalDate rangeStartDate;
	
	/**
	 * The end date to get images for if one is specified in the UI
	 */
	private LocalDate rangeEndDate;
	
	/**
	 * A parser for dates in the format returned in DICOM records for AcquisitionDate
	 */
	private DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyyMMdd");
	
	public DicomReplicationApp() {
		knownEntries=new ArrayList<>();
		initS3();
	}
	
	private void initS3() {
		Region region=Region.US_EAST_1;
        S3ClientConfiguration clientConfig=S3ClientConfiguration.builder().region(region).build();
        
        s3TransferManager=S3TransferManager.builder().s3ClientConfiguration(clientConfig).build();
	}

	public void set(ApplicationContext parentContext, MDSHandler mdsHandler, Subscriber subscriber) {
		// TODO Auto-generated method stub
		
	}

	public void start(EventLoop eventLoop, Subscriber subscriber) {
		// TODO Auto-generated method stub
		
	}
	
	public void startProcessing() {
		System.err.println("Start pressed...");
		String dicomServer=ipAddress.getText();
		String dicomPort=portNumber.getText();
		
		filesTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("imageName"));
		filesTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("localFile"));
		
		if(startDate.getValue()!=null) {
			rangeStartDate=startDate.getValue();
		}
		
		if(endDate.getValue()!=null) {
			rangeEndDate=endDate.getValue();
		}
		
		try {
            // use the default character set for VR encoding - override this as necessary
            final SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet((String[])null);
            final AttributeList identifier = new AttributeList();
            
            //build the attributes that you would like to retrieve as well as passing in any search criteria
            identifier.putNewAttribute(TagFromName.QueryRetrieveLevel).addValue("STUDY"); //specific query root
            identifier.putNewAttribute(TagFromName.PatientName,specificCharacterSet).addValue(patientName.getText());
            identifier.putNewAttribute(TagFromName.PatientID,specificCharacterSet);
            identifier.putNewAttribute(TagFromName.PatientBirthDate);
            identifier.putNewAttribute(TagFromName.PatientSex);
            identifier.putNewAttribute(TagFromName.StudyInstanceUID);
            identifier.putNewAttribute(TagFromName.SOPInstanceUID);
            identifier.putNewAttribute(TagFromName.StudyDescription);
            identifier.putNewAttribute(TagFromName.StudyDate);
            identifier.putNewAttribute(TagFromName.SOPClassesInStudy);
            identifier.putNewAttribute(TagFromName.AcquisitionDate);
            
            //TODO - this fails with secure settings turned on on the local orthanc. How do we "Authenticate" properly?
            
           
            
            
            queryThread=new Thread() {
            	@Override
            	public void run() {
            		
            		while(!pleaseStop) {
	            		try {
	            			System.err.println("Thread start");
							new FindSOPClassSCU(dicomServer,
							        Integer.parseInt(dicomPort),
							        REMOTE_AE,
							        OPENICE_AE,
							        SOPClass.StudyRootQueryRetrieveInformationModelFind,
							        identifier,
							        new OpenICEIdentifierHandler());
							System.err.println("Did FindSOPClassSCU");
						} catch (NumberFormatException e) {
							log.error("Invalid port number", e);
						} catch (DicomNetworkException e) {
							log.error("Dicom network problem",e);
						} catch (DicomException e) {
							log.error("General Dicom Error",e);
						} catch (IOException e) {
							e.printStackTrace();
						}
	            		Toggle t=runGroup.getSelectedToggle();
	            		if(t.equals(runOnce)) {
	            			return;
	            		}
	            		try {
							sleep(60000);
						} catch (InterruptedException ie) {
							if(pleaseStop) {
								System.err.println("stop request");
							}
							return;
						}
	            		
            		}
            	}
            	
            };
            
            queryThread.start();
            
            
        } catch (Exception e) {
            log.error("Top Level Exception",e);
        }
		
	}
	
	public void stopProcessing() {
		System.err.println("Stop called");
		pleaseStop=true;
		queryThread.interrupt();
	}
	
	class OpenICEIdentifierHandler extends IdentifierHandler {
		
		

		@Override
		public void doSomethingWithIdentifier(AttributeList attributeListForFindResult) throws DicomException {
			System.err.println("doSomethingWithIdentifier called");
			// TODO Auto-generated method stub
			if(!knownEntries.contains(attributeListForFindResult)) {
				knownEntries.add(attributeListForFindResult);
				Attribute a=attributeListForFindResult.get(TagFromName.PatientName);
				Attribute d=attributeListForFindResult.get(TagFromName.AcquisitionDate);
				System.err.println("Attribute a has "+a.getStringValues().length+" entries and first is "+a.getStringValues()[0]);
				if(d!=null && d.getStringValues()!=null && d.getStringValues()[0].length()>0) {
					/*
					 * At least for the images we have to hand, not all images have an acquisiton date.  The resulting attribute can
					 * certainly be not null, but there are no getStringValues() and evaluating them seems to cause an NPE.  
					 */
					System.err.println("Acquisition Date is "+d.getStringValues()[0]);
						//Date acquistionDate=DateUtilities.yyyymmddFormat.parse(d.getStringValues()[0]);
					
					TemporalAccessor ta=dtf.parse(d.getStringValues()[0]);
					LocalDate acquisitionDate=LocalDate.from(ta);
					if(rangeStartDate!=null && rangeStartDate.isAfter(acquisitionDate)) {
						System.err.println("Skipping record with date "+acquisitionDate+" as it's before start date for search");
						return;
					}
					if(rangeEndDate!=null && rangeEndDate.isBefore(acquisitionDate)) {
						System.err.println("Skipping record with date "+acquisitionDate+" as it's after end date for search");
						return;
					}
				} else {
					System.err.println("No AcquisitionDate on this record");
					if(rangeStartDate!=null || rangeEndDate!=null) {
						System.err.println("Skipping record with no acquisiton date as start or end date was specified");
						return;
					}
				}
				
				String studyInstanceUID = attributeListForFindResult.get(TagFromName.StudyInstanceUID)
	                    .getSingleStringValueOrEmptyString();
	            System.out.println("studyInstanceUID of matched result:" + studyInstanceUID);

	            Set<String> setofSopClassesExpected = new HashSet<String>();
	            Attribute sopClassesInStudy = attributeListForFindResult.get(TagFromName.SOPClassesInStudy);
	            if (sopClassesInStudy != null) {
	                String[] sopClassesInStudyList = sopClassesInStudy.getStringValues();
	                for (String sopClassInStudy : sopClassesInStudyList) {
	                    setofSopClassesExpected.add(sopClassInStudy);
	                }
	            } else {
	                //if SOP class data for study is not found, then supply all storage SOP classes
	                setofSopClassesExpected = (Set<String>) SOPClass.getSetOfStorageSOPClasses();
	            }
				
	            AttributeList identifier = new AttributeList();
                {
                    AttributeTag tag = TagFromName.QueryRetrieveLevel;
                    Attribute attribute = new CodeStringAttribute(tag);
                    attribute.addValue("STUDY");
                    identifier.put(tag, attribute);
                }
                {
                    AttributeTag tag = TagFromName.StudyInstanceUID;
                    Attribute attribute = new UniqueIdentifierAttribute(tag);
                    attribute.addValue(studyInstanceUID);
                    identifier.put(tag, attribute);
                }
                
                try {
                	System.err.println("dicomImageDirectory is "+dicomImageDirectory.getAbsolutePath());
                	if( ! dicomImageDirectory.exists()) {
                		dicomImageDirectory.mkdirs();
                	}
					new GetSOPClassSCU(ipAddress.getText(), 
					        Integer.parseInt(portNumber.getText()), 
					        REMOTE_AE, 
					        OPENICE_AE,
					        SOPClass.StudyRootQueryRetrieveInformationModelGet, 
					        identifier, 
					        new IdentifierHandler(), //override and provide your own handler if you need to do anything else
					        dicomImageDirectory, 
					        StoredFilePathStrategy.BYSOPINSTANCEUIDINSINGLEFOLDER,
					        new OpenICECGetOperationStoreHandler(), 
					        setofSopClassesExpected, 
					        0, 
					        true, 	//originally true
					        false, 	//originally false
					        false);	//originally false
					System.err.println("Did GetSOPClassSCU");
				}  catch (NumberFormatException e) {
					log.error("Invalid port number", e);
				} catch (DicomNetworkException e) {
					log.error("Dicom network problem",e);
					e.printStackTrace();
				} catch (DicomException e) {
					log.error("General Dicom Error",e);
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} else {
				System.err.println("Entry was already known");
			}
		}
		
	}
	
	class OpenICECGetOperationStoreHandler extends ReceivedObjectHandler {
        
        @Override
        public void sendReceivedObjectIndication(String filename, String transferSyntax, String calledAetTitle)
                throws DicomNetworkException, DicomException, IOException {
            
            System.out.println("Incoming data from " + calledAetTitle + "...");
            System.out.println("filename:" + filename);
            System.out.println("transferSyntax:" + transferSyntax);
            
            File testFile=new File(dicomImageDirectory,filename);
            if(testFile.exists()) {
            	System.err.println("Received file "+testFile.getAbsolutePath()+" has size "+testFile.length());
            }
            
            String s3Key=filename.substring(filename.lastIndexOf(File.separatorChar)+1);
            
            Upload upload=s3TransferManager.upload( b -> b.putObjectRequest(r -> r.bucket("openicedicom").key(s3Key))
            		.source(Paths.get(filename)));
            CompletedUpload completedUpload=upload.completionFuture().join();
            System.out.println("Uploaded "+filename+" to s3 entity "+completedUpload.response().eTag());
            
            DicomFileForTable dfft=new DicomFileForTable(s3Key, filename);
            filesTable.getItems().add(dfft);
            
        }

    }
	
	public void viewImage() {
		DicomFileForTable dfft=filesTable.getSelectionModel().getSelectedItem();
		String fileName=dfft.localFile;
		
		try {
	        JFrame frame = new JFrame();
	        SourceImage sImg = new SourceImage(fileName);
	        System.out.println("Number of frames: " + sImg.getNumberOfFrames());
	        DicomViewerPanel singleImagePanel = new DicomViewerPanel(sImg);
	        frame.add(singleImagePanel);
	        frame.setBackground(Color.BLACK);
	        frame.setSize(sImg.getWidth(),sImg.getHeight());
	        frame.setTitle("Demo for view, scroll and window width/level operations");
	        frame.setVisible(true);
	
	    } catch (Exception e) {
	        e.printStackTrace(); //in real life, do something about this exception
	    }
		
		
		
		
	}
	
	public class DicomFileForTable {
		private String imageName;
		private String localFile;
		
		public DicomFileForTable(String imageName, String localFile) {
			this.imageName=imageName;
			this.localFile=localFile;
		}

		public String getImageName() {
			return imageName;
		}

		public String getLocalFile() {
			return localFile;
		}
	}

}
