package org.mdpnp.apps.testapp.export;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import ice.Patient;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 */
public class MongoPersister extends FileAdapterApplicationFactory.PersisterUIController implements DataCollector.DataSampleEventListener  {

    private static final Logger log = LoggerFactory.getLogger(MongoPersister.class);

    private Invocable invocable;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    @FXML
    TextField fHost, fPortNumber, fDbName, fScriptName;

    @Override
    public String getName() {
        return "mongo";
    }

    @Override
    public void setup() {

    }

    @Override
    public void stop() throws Exception {
        if(mongoClient != null)
            mongoClient.close();
    }

    @Override
    public boolean start() throws Exception {

        String script = fScriptName.getText();
        if(isEmpty(script))
            return false;
        if(!initJSRuntime(script))
            return false;
        if(!makeMongoClient())
            return false;
        return true;
    }

    @Override
    public void handleDataSampleEvent(DataCollector.DataSampleEvent evt) throws Exception {
        Patient patient = evt.getPatient();
        Value vital = (Value)evt.getSource();
        persist(patient, vital);
    }

    void persist(Patient patient, Value value) throws Exception {

        try {
            if(mongoDatabase == null || value==null)
                throw new IllegalArgumentException("Mongo or value are null");

            ScriptObjectMirror result = (ScriptObjectMirror) invocable.invokeFunction("persist", mongoDatabase, patient, value);
            String status = (String) result.get("status");
            if(log.isDebugEnabled())
                log.debug(status);
        }
        catch(Exception ex) {
            log.error("Failed to save", ex);
        }
    }

    boolean initJSRuntime(String jsFile) throws ScriptException {

        // if starts with 'file:' - its already a valid url
        //
        if(jsFile.contains(File.separator) && !jsFile.startsWith("file:")) {
            File f = new File(jsFile);
            if(!f.exists())
                throw new ScriptException("File does not exist: " + jsFile);
            try {
                URL u  = f.toURI().toURL();
                jsFile  = u.toExternalForm();
            } catch (MalformedURLException ex) {
                throw new ScriptException("Failed to locate script " + jsFile);
            }
        }

        InputStream is = getClass().getResourceAsStream(jsFile);

        if(is != null) {
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            engine.eval(new InputStreamReader(is));
            invocable = (Invocable) engine;
        }
        return invocable != null;
    }

    private boolean makeMongoClient() throws Exception {

        String host = fHost.getText();
        String port = fPortNumber.getText();
        String dbName = fDbName.getText();

        if (isEmpty(host) || isEmpty(port) || isEmpty(dbName))
            return false;

        int p = Integer.parseInt(port);

        return makeMongoClient(host, p, dbName);
    }

    boolean makeMongoClient(String host, int port, String dbName) throws Exception {

        mongoClient = new MongoClient(new ServerAddress(host, port),
                                      new MongoClientOptions.Builder().build());

        if(!confirmDatabase(dbName)) {
            log.error("Database '" + dbName + "' not found on " + host + ":" + port);
            return false;
        }

        mongoDatabase = mongoClient.getDatabase(dbName);
        return true;
    }

    private boolean confirmDatabase(String dbName)
    {
        com.mongodb.client.MongoIterable<String> s = mongoClient.listDatabaseNames();
        com.mongodb.client.MongoCursor<String> c = s.iterator();

        while(c.hasNext()) {
            String n = c.next();
            if(dbName.equals(n))
                return true;
        }
        return false;
    }

    private static boolean isEmpty(String s) {
        return s == null || s.trim().length()==0;
    }
}
