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
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

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
	
	private PreparedStatement numericsStatement,samplesStatement,sessionStatement;
	
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
	 * The last sequence number 
	 */
	private long maxNumericSeqNum;

	/**
	 * The next time we need to try and transmit data to the server.
	 */
	private long t_next;

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
		maxNumericSeqNum=-1;	//We don't have a max seq num yet
	}
	
	public void start(EventLoop eventLoop, Subscriber subscriber) {

		if(emr.getEMRType()!=EMRType.OPENEMR) {
			//There is nothing to do.
			//TODO: give some indication that the app is not running.
			return;
		}

		emrLogin();

		Thread transferThread=new Thread() {
			@Override
			public void run() {
				//long t_tmp=0;
				while(true) {
					if(maxNumericSeqNum==-1) {
						maxNumericSeqNum=getMaxNumericSeqNum();
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
			if(numericsStatement==null) {
				numericsStatement=dbconn.prepareStatement("SELECT allnumerics.t_sec,allnumerics.udi,allnumerics.metric_id,allnumerics.val,allnumerics.seqnum FROM allnumerics INNER JOIN patientdevice ON allnumerics.udi=patientdevice.udi WHERE allnumerics.seqnum>? AND patientdevice.mrn=? AND patientdevice.associated>? AND patientdevice.dissociated IS NULL");
			}
			log.info("Using "+maxNumericSeqNum+" for numericsStatment");
			numericsStatement.setLong(1, maxNumericSeqNum);
			numericsStatement.setString(2, currentPatient.mrn);
			numericsStatement.setLong(3, (absoluteStartTime/1000));
			long newMaxNum=maxNumericSeqNum;
			if(numericsStatement.execute()) {
				//We have a result set
				ResultSet rs=numericsStatement.getResultSet();
				JsonArrayBuilder resultsBuilder=Json.createArrayBuilder();
				while(rs.next()) {
					JsonArrayBuilder rowBuilder=Json.createArrayBuilder();
					rowBuilder.add(rs.getString(2));	//UDI
					rowBuilder.add(rs.getString(3));	//METRIC_ID
					rowBuilder.add(rs.getFloat(4));		//VALUE
					rowBuilder.add(rs.getInt(1));		//T_SEC
					rowBuilder.add(rs.getLong(5)); 		//SEQNUM
					newMaxNum=rs.getLong(5);
					resultsBuilder.add(rowBuilder);
				}
				log.info("newMaxNum is "+newMaxNum);
				JsonArray allRows=resultsBuilder.build();
				JsonObjectBuilder builder=Json.createObjectBuilder();
				builder.add("sessionid", transferSession);
				builder.add("patientid", currentPatient.mrn);
				builder.add("payload", allRows);
				String jsonPayload=builder.build().toString();
				log.info("About to call sendNumericsOverApi with "+allRows.size()+" elements");
				sendNumericsOverApi(jsonPayload);
				maxNumericSeqNum=newMaxNum;	//Flip to the last known sequence number from the result set for the transfer just sent.
			} else {
				log.warn("Unexpected result from executing numericsStatement");
			}

			/*
			if(samplesStatement==null) {
				samplesStatement=dbconn.prepareStatement("select allsamples.t_sec,allsamples.udi,allsamples.metric_id,allsamples.floats from allsamples where allsamples.t_sec>?");
			}
			log.info("Using "+(t_last/1000)+" for samplesStatment");
			samplesStatement.setLong(1, (t_last/1000));
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
					resultsBuilder.add(rowBuilder);
				}
				JsonArray allRows=resultsBuilder.build();
				JsonObjectBuilder builder=Json.createObjectBuilder();
				builder.add("sessionid", transferSession);
				builder.add("payload", allRows);
				String jsonPayload=builder.build().toString();
				log.info("About to call sendSamplesOverApi with "+allRows.size()+" elements");
				//sendSamplesOverApi(jsonPayload);
				return true;
			} else {
				log.warn("Unexpected result from executing samplesStatement");
			}
			*/

		} catch (SQLException sqle) {
			log.error("Error sending data", sqle );
		}
		return false;
	}

	private void sendNumericsOverApi(String jsonPayload) {
		try {
			HttpPost numericsPost=new HttpPost("http://"+servername.getText()+"/apis/api/openice/numerics");
			numericsPost.setHeader("Authorization", "Bearer "+accessToken.getText());
			numericsPost.setEntity(new StringEntity(jsonPayload));
			CloseableHttpClient client=HttpClients.createDefault();
			CloseableHttpResponse response=client.execute(numericsPost);
			response.getStatusLine();
		} catch (UnsupportedEncodingException e) {
			log.error("Exception sending numerics", e);
		} catch (ClientProtocolException e) {
			log.error("Exception sending numerics", e);
		} catch (IOException e) {
			log.error("Exception sending numerics", e);
		}
	}

	private void sendSamplesOverApi(String jsonPayload) {
		try {
			HttpPost numericsPost=new HttpPost("http://"+servername.getText()+"/apis/api/openice/samples");
			numericsPost.setHeader("Authorization", "Bearer "+accessToken.getText());
			numericsPost.setEntity(new StringEntity(jsonPayload));
			CloseableHttpClient client=HttpClients.createDefault();
			CloseableHttpResponse response=client.execute(numericsPost);
			response.getStatusLine();
			
		} catch (UnsupportedEncodingException e) {
			log.error("Exception sending samples", e);
		} catch (ClientProtocolException e) {
			log.error("Exception sending samples", e);
		} catch (IOException e) {
			log.error("Exception sending samples", e);
		}
		
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
	 * Used to retrieve the max sequence number from openemr for this machine
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
