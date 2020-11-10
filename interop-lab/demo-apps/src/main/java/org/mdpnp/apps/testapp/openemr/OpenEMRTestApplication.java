package org.mdpnp.apps.testapp.openemr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.json.stream.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.util.EntityUtils;
import org.mdpnp.apps.testapp.patient.EMRFacade;
import org.mdpnp.apps.testapp.patient.OpenEMRImpl;
import org.mdpnp.apps.testapp.patient.EMRFacade.EMRFacadeFactory;
import org.mdpnp.apps.testapp.patient.EMRFacade.EMRType;
import org.mdpnp.apps.testapp.pumps.PumpControllerTestApplication;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.devices.PartitionAssignmentController;
import org.mdpnp.devices.MDSHandler.Connectivity.MDSEvent;
import org.mdpnp.devices.MDSHandler.Connectivity.MDSListener;
import org.mdpnp.devices.MDSHandler.Patient.PatientEvent;
import org.mdpnp.devices.MDSHandler.Patient.PatientListener;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.sql.SQLLogging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import com.rti.dds.subscription.Subscriber;
import com.sun.javafx.collections.ObservableListWrapper;

import ice.MDSConnectivity;
import ice.Patient;

public class OpenEMRTestApplication {

	private static final Logger log = LoggerFactory.getLogger(OpenEMRTestApplication.class);

	@FXML TextField servername;
	@FXML TextField username,password,scope,accessToken;
	@FXML TextArea patientList;
	@FXML ComboBox<String> sessionList;
	
	private Subscriber subscriber;
	private EventLoop eventLoop;
	private MDSHandler mdsHandler;
	private EMRFacade emr;
	
	private Connection dbconn;
	
	private PreparedStatement numericsStatement,samplesStatement,sessionStatement,pdStatement,commandsStatement,alarmsStatement,froaStatement;
	
	private Patient currentPatient;
	
	private final Map<String, Patient> deviceUdiToPatientMRN = Collections.synchronizedMap(new HashMap<String, Patient>());

	/**
	 * The default interval, in seconds.
	 */
	private static final int defaultInterval=60;

	/**
	 * The currently selected time interval between attempts to send to OpenEMR, in seconds.
	 */
	private int currentInterval;

	/**
	 * The last sequence number for numerics 
	 */
	private long maxNumericSeqNum;
	
	/**
	 * The last sequence number for samples
	 */
	private long maxSampleSeqNum;

	/**
	 * The next time we need to try and transmit data to the server.
	 */
	private long t_next;
	
	/**
	 * The last time that the Patient<->Device association handled association.
	 */
	private long lastPDStartTimeA;
	
	/**
	 * The last time that the Patient<->Device association handled dissociation.
	 */
	private long lastPDStartTimeD;
	
	/**
	 * The last time that commands were sent.
	 */
	private long lastCommandsTime;
	
	/**
	 * The last time that alarms were sent to the events table.
	 */
	private long lastAlarmsTime;
	
	/**
	 * The last time that froa_config entries were sent to the events table.
	 */
	private long lastFroaConfigTime;

	
	/**
	 * Session id for transferring data
	 */
	private String transferSession;

	/**
	 * The time at which this process was created - effectively the process start time.
	 * The assumption is that no device/patient association can occur before this, and
	 * so this time is used to select device/patient associations that occurred after this
	 * time. Knowing that, we can select numerics and samples produced by devices that are
	 * associated with the current patient, where the association occurred after this time
	 * and where the associated has not been ended.  That gives us devices, that we can then
	 * match to the numerics or samples produced by those devices.
	 */
	private final long absoluteStartTime;

	public OpenEMRTestApplication() {
		currentInterval=defaultInterval;
		absoluteStartTime=System.currentTimeMillis();
		log.info("Absolute start time is "+absoluteStartTime);
	}
	
	public void set(MDSHandler mdsHandler, EMRFacade emr) {
		this.mdsHandler=mdsHandler;
		this.emr=emr;
		maxNumericSeqNum=-1;	//We don't have a max seq num for numerics yet
		maxSampleSeqNum=-1;		//We don't have a max seq num for samples yet
	}
	
	public void start(EventLoop eventLoop, Subscriber subscriber) {

		if(emr.getEMRType()!=EMRType.OPENEMR) {
			//There is nothing to do.
			//TODO: give some indication that the app is not running.
			return;
		}
		
		OpenEMRImpl impl=(OpenEMRImpl)emr;
		/*
		 * Getting the URL could work several ways - we put a static method in the static class EMRFacadeFactory,
		 * which is hideous.  We could put the URL into the top level facade, which is probably best.
		 * Or for now, we just have a reference to the actual implementation type subclass,
		 * which will have to do.
		 */
		//TODO: What is the best way to get this URL?
		String emrURL=impl.getUrl();
		System.err.println("Current servername text is "+servername.getText());
		servername.setText(emrURL);
		emrLogin();

		Thread transferThread=new Thread() {
			@Override
			public void run() {
				//long t_tmp=0;
				while(true) {
					if(maxNumericSeqNum==-1) {
						maxNumericSeqNum=getMaxNumericSeqNum();
					}
					if(maxSampleSeqNum==-1) {
						maxSampleSeqNum=getMaxSampleSeqNum();
					}
					if(transferSession==null) {
						transferSession=getTransferSession();
					}
					try {
						log.info("About to sleep");
						sleep(currentInterval*1000);
						//We set this to allow time for sendData to execute without us missing any metrics that happen during that execution time.
						//t_tmp=System.currentTimeMillis();
						//log.info("set t_tmp to "+t_tmp);
					} catch (InterruptedException ie) {
						//TODO: Exit somehow...
					}

					if(sendData()) {
						//Nothing to do at the moment...
					}
				}
			}
		};

		transferThread.start();
		
		mdsHandler.addConnectivityListener(new MDSListener() {

			@Override
			public void handleConnectivityChange(MDSEvent evt) {
		        ice.MDSConnectivity c = (MDSConnectivity) evt.getSource();

		        String mrnPartition = PartitionAssignmentController.findMRNPartition(c.partition);

		        if(mrnPartition != null) {
		            Patient p = new Patient();
		            p.mrn = PartitionAssignmentController.toMRN(mrnPartition);
		            
		            if(currentPatient==null) {
		            	/*
		            	 * The patient has definitely changed - even if the selected patient is "Unassigned",
		            	 * then that "Patient" has an ID
		            	 */
		            	currentPatient=p;
		            	return;	//Nothing else to do.
		            }
		            if( ! currentPatient.mrn.equals(p.mrn) ) {
		            	//Patient has changed
		            	currentPatient=p;
		            }
		        }
		    }
			
		});
	}
	
	private boolean sendData() {
		log.info("sendData at least got called...");
		if(currentPatient==null) {
			log.warn("OpenEMR data export doing nothing because patient is not selected");
			return true;
		}
		if(dbconn==null) {
			dbconn=SQLLogging.getConnection();
		}
		try {
			sendNumerics();
			sendSamples();
			sendPatientDevice();
			sendCommands();
			sendEvents();
			return true;
		} catch (SQLException sqle) {
			log.error("Failed to export data",sqle);
			return false;
		}

	}
	
	/**
	 * A very simple holder for a running total (the value), and the count of elements that made up the total.
	 * @author simon
	 *
	 */
	class OpenIceNumeric {
		int t_sec;
		String udi;
		String metricId;
		float val;
		long seqNum;	//Possibly not needed.

		public OpenIceNumeric(int t_sec, String udi, String metricId, float val, long seqNum) {
			super();
			this.t_sec = t_sec;
			this.udi = udi;
			this.metricId = metricId;
			this.val = val;
			this.seqNum = seqNum;
		}
		
		public String getUDI() {
			return udi;
		}
		
		public String getMetricId() {
			return metricId;
		}
		
		public float getVal() {
			return val;
		}

		@Override
		public String toString() {
			return "t_sec "+t_sec+", udi "+udi+", metricId "+metricId+" val "+val+", seqNum "+seqNum;
		}
	}
	
	private boolean sendNumerics() throws SQLException {
			if(numericsStatement==null) {
				numericsStatement=dbconn.prepareStatement("SELECT nfe.t_sec,nfe.udi,nfe.metric_id,nfe.val,nfe.seqnum FROM numerics_for_export nfe INNER JOIN patientdevice ON nfe.udi=patientdevice.udi WHERE nfe.seqnum>? AND patientdevice.mrn=? AND patientdevice.associated>? AND patientdevice.dissociated IS NULL");
			}
			log.info("Using "+maxNumericSeqNum+" for numericsStatment");
			numericsStatement.setLong(1, maxNumericSeqNum);
			numericsStatement.setString(2, currentPatient.mrn);
			numericsStatement.setLong(3, (absoluteStartTime/1000));
			long newMaxNum=maxNumericSeqNum;
			//Hashtable<String,ValAndCount> numerics=new Hashtable<>();
			//ArrayList<OpenIceNumeric> results=new ArrayList<>();
			if(numericsStatement.execute()) {
				//We have a result set
				ResultSet rs=numericsStatement.getResultSet();
				JsonArrayBuilder resultsBuilder=Json.createArrayBuilder();
				float runningTotal=0;
				int numOfVals=0;
				while(rs.next()) {
					JsonArrayBuilder rowBuilder=Json.createArrayBuilder();
					rowBuilder.add(rs.getString(2));	//UDI
					rowBuilder.add(rs.getString(3));	//METRIC_ID
					rowBuilder.add(rs.getFloat(4));		//VALUE
					rowBuilder.add(rs.getInt(1));		//T_SEC
					rowBuilder.add(rs.getLong(5)); 		//SEQNUM
					newMaxNum=rs.getLong(5);
					resultsBuilder.add(rowBuilder);
					numOfVals++;
				}
				
				log.info("newMaxNum is "+newMaxNum);
				JsonArray allRows=resultsBuilder.build();
				JsonObjectBuilder builder=Json.createObjectBuilder();
				builder.add("sessionid", transferSession);
				builder.add("patientid", currentPatient.mrn);
				builder.add("payload", allRows);
				String jsonPayload=builder.build().toString();
				log.info("About to call sendNumericsOverApi with "+allRows.size()+" elements");
				if(sendNumericsOverApi(jsonPayload)) {
					maxNumericSeqNum=newMaxNum;	//Flip to the last known sequence number from the result set for the transfer just sent.
				}
				return true;
			} else {
				log.warn("Unexpected result from executing numericsStatement");
			}
		return false;
	}
	
	private boolean sendSamples() throws SQLException {
//		return true;

		if(samplesStatement==null) {
			samplesStatement=dbconn.prepareStatement("SELECT sfe.t_sec,sfe.udi,sfe.metric_id,sfe.floats,sfe.seqnum FROM samples_for_export sfe INNER JOIN patientdevice ON sfe.udi=patientdevice.udi WHERE sfe.seqnum>? AND patientdevice.mrn=? AND patientdevice.associated>? AND patientdevice.dissociated IS NULL");
		}
		log.info("Using "+maxSampleSeqNum+" for samplesStatment");
		samplesStatement.setLong(1, maxSampleSeqNum);
		samplesStatement.setString(2, currentPatient.mrn);
		samplesStatement.setLong(3, (absoluteStartTime/1000));
		long newMaxNum=maxSampleSeqNum;
		if(samplesStatement.execute()) {
			//We have a result set
			ResultSet rs=samplesStatement.getResultSet();
			JsonArrayBuilder resultsBuilder=Json.createArrayBuilder();
			while(rs.next()) {
				JsonArrayBuilder rowBuilder=Json.createArrayBuilder();
				rowBuilder.add(rs.getString(2));	//UDI
				rowBuilder.add(rs.getString(3));	//METRIC_ID
				rowBuilder.add(rs.getString(4));		//VALUE
				rowBuilder.add(rs.getInt(1));		//T_SEC
				rowBuilder.add(rs.getLong(5)); 		//SEQNUM
				newMaxNum=rs.getLong(5);
				resultsBuilder.add(rowBuilder);
			}
			JsonArray allRows=resultsBuilder.build();
			JsonObjectBuilder builder=Json.createObjectBuilder();
			builder.add("sessionid", transferSession);
			builder.add("patientid", currentPatient.mrn);
			builder.add("payload", allRows);
			String jsonPayload=builder.build().toString();
			log.info("About to call sendSamplesOverApi with "+allRows.size()+" elements");
			if(sendSamplesOverApi(jsonPayload)) {
				maxSampleSeqNum=newMaxNum;	//Flip to the last known sequence number from the result set for the transfer just sent.
				return true;
			}
			return false;
		} else {
			log.warn("Unexpected result from executing samplesStatement");
		}
		return false;
	}
	
	/**
	 * The strategy here is that we get all records where the associated OR dissociated time is greater than our last run time.
	 * If the associated time is greater than the last run time, send an association record.
	 * If the dissociated time is not null and greater than our last run time, send a dissociation record.<br/>
	 * 
	 * We need to select using both fields because if the device association lasts longer than one sleep interval, then the device
	 * association will be before the last interval, but the dissociation (if not null) will be later.  If we only got records later
	 * than the run time by association, then we'd miss the dissociation records, because dissociation appears in the same record as
	 * association, by that record being updated.<br/>
	 * 
	 * This makes it different to numerics/samples, because there are never updates to those records.  On the plus side, we don't need
	 * a sequence number here to ensure all records are handled, because there is only one source of data for these records (as opposed
	 * to devices, where multiple devices could be producing records at fractionally different times and a record could be missed if it
	 * arrives with the same timestamp, but that timestamp has already started to be processed.
	 * @return
	 */
	private boolean sendPatientDevice() throws SQLException {
		if(lastPDStartTimeA==0) {
			lastPDStartTimeA=absoluteStartTime/1000;	//These are second values.
			lastPDStartTimeD=absoluteStartTime/1000;	//These are second values.
		}
		if(pdStatement==null) {
			//TODO: Match the records against the current patient?
			//SELECT pd.mrn,pd.udi,pd.associated,pd.dissociated,concat(d.manufacturer,' ',d.model) FROM patientdevice pd INNER JOIN devices d ON pd.udi=d.udi WHERE
			pdStatement=dbconn.prepareStatement("SELECT pd.mrn,pd.udi,pd.associated,pd.dissociated,CONCAT(d.manufacturer,' ',d.model) FROM patientdevice pd INNER JOIN devices d ON pd.udi=d.udi WHERE pd.associated>? OR pd.dissociated>?");
		}
		long whenA=(lastPDStartTimeA);	//whenA is a value in seconds, to match that in the table
		long whenD=(lastPDStartTimeD);  //whenD is a value in seconds, to match that in the table
		pdStatement.setLong(1, whenA);
		pdStatement.setLong(2, whenD);
		if(pdStatement.execute()) {
			//We have a result set
			ResultSet rs=pdStatement.getResultSet();
			JsonArrayBuilder resultsBuilder=Json.createArrayBuilder();
			long latestTimeA=whenA; //latestTimeA is a value in seconds, to match that in the table
			long latestTimeD=whenD; //latestTimeD is a value in seconds, to match that in the table
			while(rs.next()) {
				if(rs.getLong(3)>whenA) {
					//Device was associated since last execution.
					JsonArrayBuilder rowBuilder=Json.createArrayBuilder();
					rowBuilder.add("A");				//Device was ASSOCIATED
					rowBuilder.add(rs.getString(1));	//MRN
					rowBuilder.add(rs.getString(2));	//UDI
					rowBuilder.add(rs.getLong(3));		//ASSOCIATED
					//We don't need the dissociation time (if any) to create this record.
					rowBuilder.add(rs.getString(5));	//DESCRIPTION
					resultsBuilder.add(rowBuilder);
					if(rs.getLong(3)>latestTimeA) {
						//New latest association time.
						latestTimeA=rs.getLong(3);
					}
				}
				if(rs.getLong(4)>whenD) {
					//Device was associated since last execution.
					JsonArrayBuilder rowBuilder=Json.createArrayBuilder();
					rowBuilder.add("D");				//Device was DISSOCIATED
					rowBuilder.add(rs.getString(1));	//MRN
					rowBuilder.add(rs.getString(2));	//UDI
					rowBuilder.add(rs.getLong(3));		//ASSOCIATED
					//In this case, we leave the association time in, in order to allow the receiving end to correctly identify the record to update
					rowBuilder.add(rs.getLong(4));		//DISSOCIATED
					//In this case we omit the device description as that was sent with the association record and does not change.
					if(rs.getLong(4)>latestTimeD) {
						latestTimeD=rs.getLong(4);
					}
					resultsBuilder.add(rowBuilder);
				}
			}
			JsonArray allRows=resultsBuilder.build();
			JsonObjectBuilder builder=Json.createObjectBuilder();
			builder.add("sessionid", transferSession);
			//TODO: Do we want the current patient id in the payload, or are we exporting all of them?
			//builder.add("patientid", currentPatient.mrn);
			builder.add("payload", allRows);
			String jsonPayload=builder.build().toString();
			log.info("About to call sendPatientDeviceOverApi with "+allRows.size()+" elements");
			log.info("Payload is "+jsonPayload);
			if(sendPatientDeviceOverApi(jsonPayload)) {
				/*
				 * Set the lastPDStartTime variable to be equal to the latest time seen in any record.
				 * This ensures that the next pass will not include any of the records we've already handled.
				 */
				lastPDStartTimeA=latestTimeA;
				lastPDStartTimeD=latestTimeD;
				return true;
			}
			
			return false;
		} else {
			log.warn("Unexpected result from executing pdStatement");
		}
		return false;
	}
	
	private boolean sendCommands() throws SQLException {
		if(lastCommandsTime==0) {
			lastCommandsTime=absoluteStartTime/1000;	//These are second values.
		}
		if(commandsStatement==null) {
			commandsStatement=dbconn.prepareStatement("SELECT t_millis, target_udi, target_type, requestedRate, source_id, source_type FROM flowrequest WHERE t_millis>? AND source_type IS NOT NULL");
		}
		long when=lastCommandsTime;	//when is a value in seconds, to match that in the table
		commandsStatement.setLong(1, when);
		if(commandsStatement.execute()) {
			//We have a result set
			ResultSet rs=commandsStatement.getResultSet();
			JsonArrayBuilder resultsBuilder=Json.createArrayBuilder();
			long latestTime=when; //latestTime is a value in seconds, to match that in the table
			while(rs.next()) {
				JsonArrayBuilder rowBuilder=Json.createArrayBuilder();
				rowBuilder.add(rs.getString(5));	//SOURCE ID
				rowBuilder.add(rs.getString(6));	//SOURCE_TYPE
				rowBuilder.add(rs.getString(2));	//TARGET_UDI
				rowBuilder.add(rs.getString(3));	//TARGET_TYPE
				rowBuilder.add(rs.getFloat(4));		//REQUESTED_RATE
				rowBuilder.add(rs.getLong(1));		//T_MILLIS
				resultsBuilder.add(rowBuilder);

				if(rs.getLong(1)>when) {
					//New latest association time.
					latestTime=rs.getLong(1);
				}
				
			}
			JsonArray allRows=resultsBuilder.build();
			JsonObjectBuilder builder=Json.createObjectBuilder();
			builder.add("sessionid", transferSession);
			//TODO: Do we want the current patient id in the payload, or are we exporting all of them?
			//builder.add("patientid", currentPatient.mrn);
			builder.add("payload", allRows);
			String jsonPayload=builder.build().toString();
			log.info("About to call sendCommands with "+allRows.size()+" elements");
			log.info("Payload is "+jsonPayload);
			if(sendCommandsOverApi(jsonPayload)) {
				/*
				 * Set the lastPDStartTime variable to be equal to the latest time seen in any record.
				 * This ensures that the next pass will not include any of the records we've already handled.
				 */
				lastCommandsTime=latestTime;
				return true;
			}
			return false;
		} else {
			log.warn("Unexpected result from executing pdStatement");
		}
		return false;
	}
	
	private boolean sendEvents() throws SQLException {
		
		/*
		 * This is shared between the alams_for_export and froa_config queries/results.
		 */
		JsonArrayBuilder resultsBuilder=Json.createArrayBuilder();
		
		if(lastAlarmsTime==0) {
			lastAlarmsTime=absoluteStartTime/1000;	//These are second values.
		}
		/*
		 * Now, in the long term, events could include different things, and therefore we'd need multiple markers for last time etc.,
		 * and we'd have a more complex question of how to make sure we got everything.  But for now, we just need alarms from the FROA
		 * application. The FROA application also populates "alarms_for_export" with a narrative of the alarm description, so we just take
		 * that as the source statement here.
		 */
		if(alarmsStatement==null) {
			alarmsStatement=dbconn.prepareStatement("SELECT t_sec, mrn, source, sourcetype, alarmtype, alarmvalue, local_time FROM alarms_for_export WHERE t_sec>?");
		}
		long alarmWhen=lastAlarmsTime;	//alarmWhen is a value in seconds, to match that in the table
		alarmsStatement.setLong(1,alarmWhen);
		//latestAlarmTime is a value in seconds, to match that in the table - it holds the latest time we encounter processing the upcoming results.
		long latestAlarmTime=alarmWhen;
		if(alarmsStatement.execute()) {
			//We have a result set
			ResultSet rs=alarmsStatement.getResultSet();
			while(rs.next()) {
				JsonArrayBuilder rowBuilder=Json.createArrayBuilder();
				rowBuilder.add(rs.getString(2)); 	//MRN
				rowBuilder.add(rs.getString(3));	//SOURCE
				rowBuilder.add(rs.getString(4));	//SOURCETYPE
				rowBuilder.add(rs.getInt(1));		//T_SEC
				rowBuilder.add(3);					//TYPE=ALARM
				
				JsonObjectBuilder pairs=Json.createObjectBuilder();
				pairs.add("alarmtype", rs.getString(5));
				pairs.add("alarmvalue", rs.getString(6));
				pairs.add("localtime", rs.getString(7));
				
				rowBuilder.add(pairs);
				
				resultsBuilder.add(rowBuilder);
				
				if(rs.getLong(1)>alarmWhen) {
					//New latest alarm time.
					latestAlarmTime=rs.getLong(1);
				}
				
			}
		} else {
			log.warn("Unexpected result from executing alarmStatement");
		}
		
		if(lastFroaConfigTime==0) {
			lastFroaConfigTime=absoluteStartTime/1000;	//These are second values.
		}
		
		if(froaStatement==null) {
			froaStatement=dbconn.prepareStatement("SELECT mode,target_sys,target_dia,sys_alarm,dia_alarm,pump_udi,bp_udi,inf_rate,starttime,patient_id,session,endtime,bp_numeric FROM froa_config WHERE starttime>? OR endtime>?");
		}
		long froaWhen=lastFroaConfigTime;	//alarmWhen is a value in seconds, to match that in the table
		froaStatement.setLong(1,froaWhen);
		froaStatement.setLong(2,froaWhen);
		//latestFroaTime is a value in seconds, to match that in the table - it holds the latest time we encounter processing the upcoming results.
		long latestFroaTime=froaWhen;
		if(froaStatement.execute()) {
			//We have a result set
			ResultSet rs=froaStatement.getResultSet();
			while(rs.next()) {
				JsonArrayBuilder rowBuilder=Json.createArrayBuilder();
				rowBuilder.add(rs.getString(10)); 	//MRN
				rowBuilder.add("ClosedLoopControl");	//SOURCE
				rowBuilder.add("A");				//SOURCETYPE=APPLICATION
				int testTime=0;
				//Get the starttime as a test value...
				testTime=rs.getInt(9);
				boolean startOfTherapy=true;
				if(rs.wasNull()) {
					//No start time, so there SHOULD be an endtime
					testTime=rs.getInt(12);
					startOfTherapy=false;
					if(rs.wasNull()) {
						log.error("Illegal froa_config with no start OR end time");
						continue;
					}
				}
				rowBuilder.add(testTime);
				if(startOfTherapy) {
					rowBuilder.add(5);	//THERAPY START
				} else {
					rowBuilder.add(6);	//THERAPY END
				}
				//If we got really flash here, we could use ResultSetMetaData to get the column names, and loop through all the columns
				//to ge the key names that we wanted, and set the values from the column in the result set.  Definitely a TODO: use meta data!
				JsonObjectBuilder pairs=Json.createObjectBuilder();
				pairs.add("mode",rs.getInt(1));
				pairs.add("target_sys",rs.getInt(2));
				pairs.add("target_dia",rs.getInt(3));
				pairs.add("sys_alarm",rs.getInt(4));
				pairs.add("dia_alarm",rs.getInt(5));
				pairs.add("pump_udi",rs.getString(6));
				pairs.add("bp_udi",rs.getString(7));
				pairs.add("bp_numeric", rs.getString(13));
				pairs.add("inf_rate",rs.getFloat(8));
				//starttime is used in the event object, so we don't include it here
				//patientid is used in the event object, so we don't include it here
				String testSession=rs.getString(11);
				if(rs.wasNull()) {
					System.err.println("Null session...");
					testSession="MISSING!";
				}
				pairs.add("session",testSession );
				//endtime is used in the event object if it exists.

				rowBuilder.add(pairs);
				
				resultsBuilder.add(rowBuilder);
				
				if(testTime>froaWhen) {
					//New latest alarm time.
					latestFroaTime=testTime;
				}
				
			}
		} else {
			log.warn("Unexpected result from executing alarmStatement");
		}
		
		
		
		
		JsonArray allRows=resultsBuilder.build();
		JsonObjectBuilder builder=Json.createObjectBuilder();
		builder.add("sessionid", transferSession);
		//TODO: Do we want the current patient id in the payload, or are we exporting all of them?
		//builder.add("patientid", currentPatient.mrn);
		builder.add("payload", allRows);
		String jsonPayload=builder.build().toString();
		log.info("About to call sendEventsOverApi with "+allRows.size()+" elements");
		log.info("Payload is "+jsonPayload);
		if(sendEventsOverApi(jsonPayload)) {
			/*
			 * Set the lastEvents variable to be equal to the latest time seen in any record.
			 * This ensures that the next pass will not include any of the records we've already handled.
			 */
			lastAlarmsTime=latestAlarmTime;
			lastFroaConfigTime=latestFroaTime;
			return true;
		}
		return false;
		
		//return false;
	}

	private boolean sendNumericsOverApi(String jsonPayload) {
		try {
			HttpPost numericsPost=new HttpPost("http://"+servername.getText()+"/apis/api/openice/numerics");
			numericsPost.setHeader("Authorization", "Bearer "+accessToken.getText());
			numericsPost.setEntity(new StringEntity(jsonPayload));
			CloseableHttpClient client=HttpClients.createDefault();
			CloseableHttpResponse response=client.execute(numericsPost);
			response.getStatusLine();
			return true;
		} catch (UnsupportedEncodingException e) {
			log.error("Exception sending numerics", e);
		} catch (ClientProtocolException e) {
			log.error("Exception sending numerics", e);
		} catch (IOException e) {
			log.error("Exception sending numerics", e);
		}
		return false;
	}

	private boolean sendSamplesOverApi(String jsonPayload) {
		try {
			HttpPost numericsPost=new HttpPost("http://"+servername.getText()+"/apis/api/openice/samples");
			numericsPost.setHeader("Authorization", "Bearer "+accessToken.getText());
			numericsPost.setEntity(new StringEntity(jsonPayload));
			CloseableHttpClient client=HttpClients.createDefault();
			CloseableHttpResponse response=client.execute(numericsPost);
			response.getStatusLine();
			return true;
		} catch (UnsupportedEncodingException e) {
			log.error("Exception sending samples", e);
		} catch (ClientProtocolException e) {
			log.error("Exception sending samples", e);
		} catch (IOException e) {
			log.error("Exception sending samples", e);
		}
		return false;
	}
	
	private boolean sendPatientDeviceOverApi(String jsonPayload) {
		try {
			HttpPost numericsPost=new HttpPost("http://"+servername.getText()+"/apis/api/openice/patientdevice");
			numericsPost.setHeader("Authorization", "Bearer "+accessToken.getText());
			numericsPost.setEntity(new StringEntity(jsonPayload));
			CloseableHttpClient client=HttpClients.createDefault();
			CloseableHttpResponse response=client.execute(numericsPost);
			response.getStatusLine();
			return true;
		} catch (UnsupportedEncodingException e) {
			log.error("Exception sending samples", e);
		} catch (ClientProtocolException e) {
			log.error("Exception sending samples", e);
		} catch (IOException e) {
			log.error("Exception sending samples", e);
		}
		return false;
	}
	
	private boolean sendCommandsOverApi(String jsonPayload) {
		try {
			HttpPost numericsPost=new HttpPost("http://"+servername.getText()+"/apis/api/openice/commands");
			numericsPost.setHeader("Authorization", "Bearer "+accessToken.getText());
			numericsPost.setEntity(new StringEntity(jsonPayload));
			CloseableHttpClient client=HttpClients.createDefault();
			CloseableHttpResponse response=client.execute(numericsPost);
			response.getStatusLine();
			return true;
		} catch (UnsupportedEncodingException e) {
			log.error("Exception sending commands", e);
		} catch (ClientProtocolException e) {
			log.error("Exception sending commands", e);
		} catch (IOException e) {
			log.error("Exception sending commands", e);
		}
		return false;
	}
	
	private boolean sendEventsOverApi(String jsonPayload) {
		try {
			HttpPost numericsPost=new HttpPost("http://"+servername.getText()+"/apis/api/openice/events");
			numericsPost.setHeader("Authorization", "Bearer "+accessToken.getText());
			numericsPost.setEntity(new StringEntity(jsonPayload));
			CloseableHttpClient client=HttpClients.createDefault();
			CloseableHttpResponse response=client.execute(numericsPost);
			response.getStatusLine();
			return true;
		} catch (UnsupportedEncodingException e) {
			log.error("Exception sending commands", e);
		} catch (ClientProtocolException e) {
			log.error("Exception sending commands", e);
		} catch (IOException e) {
			log.error("Exception sending commands", e);
		}
		return false;
	}
	
	public void refresh() {
		try {
			if(dbconn==null) {
				dbconn=SQLLogging.getConnection();
			}
			if(sessionStatement==null) {
				sessionStatement=dbconn.prepareStatement("select session from froa_config where endtime is not null");
			}
			boolean r=sessionStatement.execute();
			if(r) {
				ArrayList sessions=new ArrayList<String>();
				ResultSet rs=sessionStatement.getResultSet();
				while(rs.next()) {
					sessions.add(rs.getString(1));
				}
				sessionList.setItems(new ObservableListWrapper(sessions));
			}
		} catch (SQLException sqle) {
			log.error("Exception getting froa_config data", sqle);
		}
	}

	private void emrLogin() {
		HttpPost loginPost=new HttpPost("http://"+servername.getText()+"/apis/api/auth");
		JsonObjectBuilder builder=Json.createObjectBuilder();
		builder.add("grant_type","password");
		builder.add("username", username.getText());
		builder.add("password", password.getText());
		builder.add("scope", scope.getText());
		JsonObject jsonObj=builder.build();
		try {
			loginPost.setEntity(new StringEntity(jsonObj.toString()));
			CloseableHttpClient client=HttpClients.createDefault();
			CloseableHttpResponse response=client.execute(loginPost);
			response.getStatusLine();
			HttpEntity responseEntity=response.getEntity();
			InputStream is=responseEntity.getContent();

			try {
				JsonReader reader=Json.createReader(new InputStreamReader(is));
				JsonObject loginObject=reader.readObject();
				String accessToken=loginObject.getString("access_token");
				this.accessToken.setText(accessToken);
			} catch (JsonException je) {
				log.error("Could not parse the login response",je);
			}
			EntityUtils.consume(responseEntity);
		} catch (UnsupportedEncodingException e) {
			log.error("Exception logging into EMR", e);
		} catch (ClientProtocolException e) {
			log.error("Exception logging into EMR", e);
		} catch (IOException e) {
			log.error("Exception logging into EMR", e);
		}
	}

	/**
	 * Used to retrieve a session id from OpenEMR.
	 */
	private String getTransferSession() {
		String hostname="localhost";	//Fallback value - and potentially a confusing one.
		try {
			hostname=InetAddress.getLocalHost().getHostName();
			if(hostname.indexOf('.')!=-1) {
				// the dot character is not legal in the dispatch handler for OpenEMR, so hostnames
				// must be trimmed down.
				hostname=hostname.substring(0,hostname.indexOf('.'));
			}
		} catch (UnknownHostException e) {
			log.error("Failed to get local hostname",e);
		}
		HttpGet sessionGet=new HttpGet("http://"+servername.getText()+"/apis/api/openice/session/"+hostname);
		try {
			sessionGet.setHeader("Authorization", "Bearer "+accessToken.getText());
			CloseableHttpClient client=HttpClients.createDefault();
			CloseableHttpResponse response=client.execute(sessionGet);
			response.getStatusLine();
			HttpEntity responseEntity=response.getEntity();
			InputStream is=responseEntity.getContent();

			try {
				JsonReader reader=Json.createReader(new InputStreamReader(is));
				JsonObject returnVal=reader.readObject();
				JsonValue val=returnVal.get("sessionid");
				String v=val.toString();
				return v;
			} catch (JsonException je) {
				log.error("Couldn't parse the transfer session data",je);
			}
		} catch (UnsupportedEncodingException e) {
			log.error("Exception getting transfer session", e);
		} catch (ClientProtocolException e) {
			log.error("Exception getting transfer session", e);
		} catch (IOException e) {
			log.error("Exception getting transfer session", e);
		}
		return "";
	}
	
	/**
	 * Used to retrieve the max numeric sequence number from openemr for this machine
	 */
	private int getMaxNumericSeqNum() {
		String hostname="localhost";	//Fallback value - and potentially a confusing one.
		try {
			hostname=InetAddress.getLocalHost().getHostName();
			if(hostname.indexOf('.')!=-1) {
				// the dot character is not legal in the dispatch handler for OpenEMR, so hostnames
				// must be trimmed down.
				hostname=hostname.substring(0,hostname.indexOf('.'));
			}
		} catch (UnknownHostException e) {
			log.error("Failed to get local hostname",e);
		}
		HttpGet sessionGet=new HttpGet("http://"+servername.getText()+"/apis/api/openice/maxnumseq/"+hostname);
		try {
			sessionGet.setHeader("Authorization", "Bearer "+accessToken.getText());
			CloseableHttpClient client=HttpClients.createDefault();
			CloseableHttpResponse response=client.execute(sessionGet);
			response.getStatusLine();
			HttpEntity responseEntity=response.getEntity();
			InputStream is=responseEntity.getContent();

			try {
				JsonReader reader=Json.createReader(new InputStreamReader(is));
				JsonObject returnVal=reader.readObject();
				JsonValue val=returnVal.get("seqnum");
				String v=val.toString();
				// "null" in this case is the literal string value returned by OpenEMR when no records matched.
				if(v.equals("null")) {
					return -1;
				}
				return Integer.parseInt(v.replaceAll("\"", ""));	//Not sure why it has the double quotes in it?
			} catch (JsonException je) {
				log.error("Couldn't parse the sequence number data",je);
			}
		} catch (UnsupportedEncodingException e) {
			log.error("Exception getting transfer session", e);
		} catch (ClientProtocolException e) {
			log.error("Exception getting transfer session", e);
		} catch (IOException e) {
			log.error("Exception getting transfer session", e);
		}
		return -1;
	}
	
	/**
	 * Used to retrieve the max sample sequence number from openemr for this machine
	 */
	private int getMaxSampleSeqNum() {
		String hostname="localhost";	//Fallback value - and potentially a confusing one.
		try {
			hostname=InetAddress.getLocalHost().getHostName();
			if(hostname.indexOf('.')!=-1) {
				// the dot character is not legal in the dispatch handler for OpenEMR, so hostnames
				// must be trimmed down.
				hostname=hostname.substring(0,hostname.indexOf('.'));
			}
		} catch (UnknownHostException e) {
			log.error("Failed to get local hostname",e);
		}
		HttpGet sessionGet=new HttpGet("http://"+servername.getText()+"/apis/api/openice/maxsamseq/"+hostname);
		try {
			sessionGet.setHeader("Authorization", "Bearer "+accessToken.getText());
			CloseableHttpClient client=HttpClients.createDefault();
			CloseableHttpResponse response=client.execute(sessionGet);
			response.getStatusLine();
			HttpEntity responseEntity=response.getEntity();
			InputStream is=responseEntity.getContent();

			try {
				JsonReader reader=Json.createReader(new InputStreamReader(is));
				JsonObject returnVal=reader.readObject();
				JsonValue val=returnVal.get("seqnum");
				String v=val.toString();
				// "null" in this case is the literal string value returned by OpenEMR when no records matched.
				if(v.equals("null")) {
					return -1;
				}
				return Integer.parseInt(v.replaceAll("\"", ""));	//Not sure why it has the double quotes in it?
			} catch (JsonException je) {
				log.error("Couldn't parse the sequence number data",je);
			}
		} catch (UnsupportedEncodingException e) {
			log.error("Exception getting transfer session", e);
		} catch (ClientProtocolException e) {
			log.error("Exception getting transfer session", e);
		} catch (IOException e) {
			log.error("Exception getting transfer session", e);
		}
		return -1;
	}

	public void getPatients() {
		if(accessToken.getText().length()==0) {
			Alert alert=new Alert(AlertType.ERROR,"Access Token must be set by logging in",ButtonType.OK);
			alert.show();
			return;
		}
		HttpGet patientGet=new HttpGet("http://"+servername.getText()+"/apis/api/patient");
		try {
			patientGet.setHeader("Authorization", "Bearer "+accessToken.getText());
			CloseableHttpClient client=HttpClients.createDefault();
			CloseableHttpResponse response=client.execute(patientGet);
			response.getStatusLine();
			HttpEntity responseEntity=response.getEntity();
			InputStream is=responseEntity.getContent();
			
			try {
				JsonReader reader=Json.createReader(new InputStreamReader(is));
				JsonArray patientArray=reader.readArray();
				log.info("There are "+patientArray.size()+" patients");
				StringBuilder sb=new StringBuilder();
				patientArray.forEach( v -> {
					if(v.getValueType().equals(ValueType.OBJECT)) {
						JsonObject jo=(JsonObject)v;
						sb.append("id: "+jo.getString("id"));
						sb.append(" pubpid: "+jo.getString("pubpid"));
						sb.append(" Name: "+jo.getString("fname")+" "+jo.getString("lname"));
					}
					sb.append("\n");
				});
				patientList.setText(sb.toString());
			} catch (JsonException je) {
				log.error("Couldn't parse the patient data",je);
			}
		} catch (UnsupportedEncodingException e) {
			log.error("Exception getting patient data", e);
		} catch (ClientProtocolException e) {
			log.error("Exception getting patient data", e);
		} catch (IOException e) {
			log.error("Exception getting patient data", e);
		}
	}

}
