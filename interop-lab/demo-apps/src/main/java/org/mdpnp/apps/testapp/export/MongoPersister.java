package org.mdpnp.apps.testapp.export;

import com.google.common.eventbus.Subscribe;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Persister to route data to the mongo database. The actual saving of the value is delegated to the javascript
 * handler which is required to implement a function 'persist' that with the following signature:
 *
 * var persist = function(mongoDatabase, patient, value)
 * 1. com.mongodb.client.MongoDatabase mongoDatabase
 * 2. ice.Patient patient
 * 3. org.mdpnp.apps.testapp.export.Value value
 *
 * The function should return { "status" : "OK" } as an indication of success or a description of a failure otherwise.
 *
 */
public class MongoPersister extends DataCollectorAppFactory.PersisterUIController  {

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

        // This is a hack to ease data entry if running locally in the lab
        //
        try {
            InetAddress address = InetAddress.getByName("arvi.jsn.mdpnp");
            fHost.setText(address.getHostAddress());
            fDbName.setText("warfighter");
            fScriptName.setText("MongoPersisterWF.js");

        } catch (UnknownHostException nothere) {
            // OK, not running in the lab
        }

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

    @Subscribe
    public void handleDataSampleEvent(final NumericsDataCollector.NumericSampleEvent evt) throws Exception {
        persist(evt);
    }

    void persist(final NumericsDataCollector.NumericSampleEvent evt) throws Exception {

        try {
            if(mongoDatabase == null || evt==null)
                throw new IllegalArgumentException("Mongo or value are null");

            ScriptObjectMirror result = (ScriptObjectMirror) invocable.invokeFunction("persistNumeric", mongoDatabase, evt);
            String status = (String) result.get("status");
            if(!"OK".equals(status)) {
                log.error("Failed to save:" + status);
            }
        }
        catch(Exception ex) {
            log.error("Failed to save", ex);
        }
    }

    boolean initJSRuntime(String jsFile) throws ScriptException {

        InputStream is = null;

        if(jsFile.contains(File.separator)) {

            if(!jsFile.contains(":")) {
                File f = new File(jsFile);
                if (!f.exists())
                    throw new ScriptException("File does not exist: " + jsFile);
                try {
                    URL url = f.toURI().toURL();
                    is = url.openStream();
                } catch (Exception ex) {
                    throw new ScriptException("Failed to locate script " + jsFile);
                }
            }
            else {
                try {
                    URL url = new URL(jsFile);
                    is = url.openStream();
                } catch (Exception ex) {
                    throw new ScriptException("Failed to locate script " + jsFile);
                }
            }
        }
        else {
            is = getClass().getResourceAsStream(jsFile);
        }

        if(is != null) {
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            engine.eval(new InputStreamReader(is));
            invocable = (Invocable) engine;

            try {
                is.close();
            } catch (IOException toobad) {}
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
