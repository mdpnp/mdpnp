package org.mdpnp.apps.testapp.patient;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.Executor;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue.ValueType;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.mdpnp.apps.testapp.Main;
import org.mdpnp.apps.testapp.patient.PatientInfo.Gender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenEMRImpl extends EMRFacade {
	
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
	private String openEMRURL;
	private String accessToken;
	long expiryTime;
	//Next three fields are set by reading properties file
	private String username;
	private String password;
	private String scope;

	public OpenEMRImpl(Executor executor) {
		super(executor);
		loadProps();
		emrType=EMRType.OPENEMR;
	}

	public OpenEMRImpl(ListHandler handler) {
		super(handler);
		loadProps();
	}
	
	public OpenEMRImpl() {
		super(NOOP_HANDLER);
		loadProps();
	}

	private void loadProps() {
		Properties p=new Properties();
		String userHome=System.getProperty("user.home");
		File f=new File(userHome,"iceopenemr.properties");
		if( ! f.exists() || ! f.canRead() ) {
			log.error("iceopenemr.properties is not accessible in user home directory");
			return;
		}
		try {
			p.load(new FileInputStream(f));
			this.username=p.getProperty("username");
			this.password=p.getProperty("password");
			this.scope=p.getProperty("scope");
		} catch (Exception e) {
			log.error("Could not read iceopenemr.properties in user home directory",e);
		}
	}
	
	public void setUrl(String url) {
		openEMRURL=url;
	}
	
	public String getUrl() {
		return openEMRURL;
	}

	@Override
	List<PatientInfo> fetchAllPatients() {
		List<PatientInfo> returnList=new ArrayList<>();
		if(accessToken==null || expired()) {
			try {
				login();
			} catch (Exception ex) {
				log.error("Could not log in to OpenEMR", ex);
				return returnList;
			}
		}
		try {
			addOpenEMRPatients(returnList);
		} catch (Exception ex) {
			log.error("Could not retrieve patient list from OpenEMR", ex);
		}
		return returnList;
	}
	
	private void addOpenEMRPatients(List returnList) throws Exception {
		HttpGet patientGet=new HttpGet("http://"+openEMRURL+"/apis/api/patient");
		patientGet.setHeader("Authorization", "Bearer "+accessToken);
        CloseableHttpClient client=HttpClients.createDefault();
        CloseableHttpResponse response=client.execute(patientGet);
        response.getStatusLine();
        HttpEntity responseEntity=response.getEntity();
        InputStream is=responseEntity.getContent();
        
        JsonReader reader=Json.createReader(new InputStreamReader(is));
        JsonArray patientArray=reader.readArray();
        log.info("OpenEMRImpl has "+patientArray.size()+" patients");
        
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        patientArray.forEach( v -> {
                if(v.getValueType().equals(ValueType.OBJECT)) {
                    JsonObject jo=(JsonObject)v;
                    Gender gender;
                    //TODO: Check these "sex" values from OpenEMR.
                    if(jo.getString("sex").equals("Male")) {
                    	gender=Gender.M;
                    } else {
                    	gender=Gender.F;
                    }
                    Date d=null;
                    try {
                    	d=df.parse(jo.getString("DOB"));
                    } catch (ParseException pe) {
                    	log.error("Could not parse date "+jo.getString("DOB"), pe);
                    	d=new Date(0);
                    }
                    PatientInfo pi=new PatientInfo(
                		jo.getString("id"),
                		jo.getString("fname"),
                		jo.getString("lname"),
                		gender,
                		d
            		);
                    returnList.add(pi);
                }
        });
	}
	
	/**
	 * Perform a login to OpenEMR using the URL and credentials in the fields.
	 * Sets the access token.
	 */
	private void login() throws Exception {
		HttpPost loginPost=new HttpPost("http://"+openEMRURL+"/apis/api/auth");
        JsonObjectBuilder builder=Json.createObjectBuilder();
        builder.add("grant_type","password");
        builder.add("username", username);
        builder.add("password", password);
        builder.add("scope", scope);
        JsonObject jsonObj=builder.build();
        loginPost.setEntity(new StringEntity(jsonObj.toString()));
        CloseableHttpClient client=HttpClients.createDefault();
        CloseableHttpResponse response=client.execute(loginPost);
        response.getStatusLine();
        HttpEntity responseEntity=response.getEntity();
        InputStream is=responseEntity.getContent();

        JsonReader reader=Json.createReader(new InputStreamReader(is));
        JsonObject loginObject=reader.readObject();
        String accessToken=loginObject.getString("access_token");
        this.accessToken=accessToken;
        //loginTime=System.currentTimeMillis();
        String expiresIn=loginObject.getString("expires_in");
        long duration=Long.parseLong(expiresIn);
        expiryTime=System.currentTimeMillis()+(duration*1000);
        
        EntityUtils.consume(responseEntity);

	}
	
	/**
	 * Assuming the expiry time is 1 hour in OpenEMR API,
	 * we calculate if the login has expired.  Later, introduce
	 * some sort of margin for this so that we say expired if
	 * there is only one minute on the clock or similar. 
	 */
	private boolean expired() {
		if(System.currentTimeMillis() < expiryTime) {
			//We are good
			return false;
		}
		return true;
	}

	@Override
	public void deleteDevicePatientAssociation(DevicePatientAssociation assoc) {
		// TODO Auto-generated method stub

	}

	@Override
	public DevicePatientAssociation updateDevicePatientAssociation(DevicePatientAssociation assoc) {
		//This does nothing for now...
		return assoc;
	}

}
