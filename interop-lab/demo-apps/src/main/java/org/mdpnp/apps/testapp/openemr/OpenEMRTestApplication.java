package org.mdpnp.apps.testapp.openemr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
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
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.devices.PartitionAssignmentController;
import org.mdpnp.devices.MDSHandler.Connectivity.MDSEvent;
import org.mdpnp.devices.MDSHandler.Connectivity.MDSListener;
import org.mdpnp.devices.MDSHandler.Patient.PatientEvent;
import org.mdpnp.devices.MDSHandler.Patient.PatientListener;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.sql.SQLLogging;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import com.rti.dds.subscription.Subscriber;
import com.sun.javafx.collections.ObservableListWrapper;

import ice.MDSConnectivity;
import ice.Patient;

public class OpenEMRTestApplication {
	
	@FXML TextField servername;
	@FXML TextField username,password,scope,accessToken;
	@FXML TextArea patientList;
	@FXML ComboBox<String> sessionList;
	
	private Subscriber subscriber;
	private EventLoop eventLoop;
	private MDSHandler mdsHandler;
	
	private Connection dbconn;
	
	private PreparedStatement dataStatement,sessionStatement;
	
	private Patient currentPatient;
	
	private final Map<String, Patient> deviceUdiToPatientMRN = Collections.synchronizedMap(new HashMap<String, Patient>());

	public OpenEMRTestApplication() {
		
		
		
		
	}
	
	public void set(MDSHandler mdsHandler) {
		this.mdsHandler=mdsHandler;
	}
	
	public void start(EventLoop eventLoop, Subscriber subscriber) {
		mdsHandler.addPatientListener(new PatientListener() {

			@Override
			public void handlePatientChange(PatientEvent evt) {
				ice.Patient p=(ice.Patient)evt.getSource();
				System.err.println("p.family_name");
			}
			
		});
		
		mdsHandler.addConnectivityListener(new MDSListener() {

			@Override
			public void handleConnectivityChange(MDSEvent evt) {
		        ice.MDSConnectivity c = (MDSConnectivity) evt.getSource();

		        String mrnPartition = PartitionAssignmentController.findMRNPartition(c.partition);

		        if(mrnPartition != null) {
		            //log.info("udi " + c.unique_device_identifier + " is MRN=" + mrnPartition);

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
		            
		            deviceUdiToPatientMRN.put(c.unique_device_identifier, p);
		        }
		    }
			
		});
	}
	
	public void sendData() {
		String session=sessionList.getValue();
		if(dbconn==null) {
			dbconn=SQLLogging.getConnection();
		}
		try {
			if(dataStatement==null) {
				dataStatement=dbconn.prepareStatement("select allnumerics.t_sec,allnumerics.udi,allnumerics.metric_id,allnumerics.val from allnumerics inner join froa_config on allnumerics.udi=froa_config.bp_udi"+
						 " where allnumerics.t_sec>froa_config.starttime and allnumerics.t_sec<froa_config.endtime"+
						 " and froa_config.session=?"
				); 
			}
			dataStatement.setString(1, session);
			if(dataStatement.execute()) {
				//We have a result set
				ResultSet rs=dataStatement.getResultSet();
				JsonArrayBuilder resultsBuilder=Json.createArrayBuilder();
				while(rs.next()) {
					JsonArrayBuilder rowBuilder=Json.createArrayBuilder();
					rowBuilder.add(rs.getString(2));	//UDI
					rowBuilder.add(rs.getString(3));	//METRIC_ID
					rowBuilder.add(rs.getFloat(4));		//VALUE
					rowBuilder.add(rs.getInt(1));		//T_SEC
					resultsBuilder.add(rowBuilder);
				}
				JsonArray allRows=resultsBuilder.build();
				String jsonPayload=allRows.toString();
				sendOverApi(jsonPayload,99);
			} else {
				//Something went wrong!
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
	
	private void sendOverApi(String jsonPayload,int pid) {
		try {
			HttpPost numericsPost=new HttpPost("http://"+servername.getText()+"/apis/api/openice/"+pid+"/numerics");
			numericsPost.setHeader("Authorization", "Bearer "+accessToken.getText());
			numericsPost.setEntity(new StringEntity(jsonPayload));
			CloseableHttpClient client=HttpClients.createDefault();
			CloseableHttpResponse response=client.execute(numericsPost);
			response.getStatusLine();
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			
		}
		
	}
	
	
	public void emrLogin() {
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
				System.err.println("Couldn't parse the login response...");
			}
			EntityUtils.consume(responseEntity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				System.err.println("There are "+patientArray.size()+" patients");
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
				System.err.println("Couldn't parse the patient data");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
