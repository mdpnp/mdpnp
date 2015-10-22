/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.apps.testapp.rbs;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.mdpnp.apps.testapp.pca.VitalView;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModel.StateChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.net.URL;
import java.util.*;


public class RBSConfig implements ListChangeListener<Vital> {

    @FXML
    WebView ruleInformation;
    @FXML
    TextArea warningStatus;

    @FXML
    VBox vitalsPanel;

    @FXML
    Button load;
    @FXML
    FlowPane controls;


    private static final ReadOnlyBooleanProperty OFF = new ReadOnlyBooleanWrapper(false);

    private VitalModel model;

    private static final Logger log = LoggerFactory.getLogger(RBSConfig.class);

    private static class ActiveRule {
        ActiveRule(Invocable invocable, URL context, String welcome) {
            this.context = context;
            this.invocable = invocable;
            this.welcome = welcome;
        }

        void load(WebView webView, String pageName) throws Exception {
            URL url = new URL(context, pageName);
            webView.getEngine().load(url.toExternalForm());
        }

        final Invocable invocable;
        final URL       context;
        final String    welcome;
    }

    ActiveRule activeRule = null;


    @FXML public void loadVitalRule(ActionEvent evt) throws  Exception {

        FileChooser fc = new FileChooser();
        fc.setTitle("Choose a file");
        fc.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("OpenICE Rules", "*.js"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File ruleFile = fc.showOpenDialog(null);
        if(null != ruleFile) {

            model.clear();

            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

            InputStream is = new FileInputStream(ruleFile);
            engine.eval(new InputStreamReader(is));
            is.close();

            Invocable invocable = (Invocable) engine;

            ScriptObjectMirror result = (ScriptObjectMirror) invocable.invokeFunction("create", model);

            String pageName = (String) result.get("ruleDescription");

            // MIKEFIX: CHANGE THIS IF WE WANT TO LOAD RULED FROM THE JAR
            URL url = ruleFile.getParentFile().toURI().toURL();
            activeRule = new ActiveRule(invocable, url, pageName);

            handleStateChange(model.stateProperty().get());
        }

    }

    public RBSConfig() {
    }

    protected void updateVitals() {
        if(Platform.isFxApplicationThread()) {
            _updateVitals();
        } else {
            Platform.runLater(new Runnable() {
                public void run() {
                    _updateVitals();
                }
            });
        }
    }


    protected void _updateVitals() {
        Map<Vital, Node> existentJVitals = new HashMap<Vital, Node>();

        for(Iterator<Node> itr = vitalsPanel.getChildren().iterator(); itr.hasNext();) {
            Node n = itr.next();
            existentJVitals.put((Vital)n.getUserData(), n);
            itr.remove();
        }

        final VitalModel model = this.model;
        if (model != null) {
            for( Iterator<Vital> itr = model.iterator(); itr.hasNext(); ) {
                final Vital vital = itr.next();

                Node jVital = existentJVitals.get(vital);
                if(null == jVital) {
                    FXMLLoader loader = new FXMLLoader(VitalView.class.getResource("VitalView.fxml"));
                    try {
                        jVital = loader.load();
                        jVital.setUserData(vital);
                    } catch (IOException e) {
                        log.warn("",e);
                        continue;
                    }
                    VitalView view = loader.getController();
                    view.set(vital, OFF);
                }
                vitalsPanel.getChildren().add(jVital);
            }
        }
    }

    public void setModel(VitalModel model) {
        if (this.model != null) {
            this.model.removeListener(this);
        }
        this.model = model;

        if (this.model != null) {
            this.model.addListener(this);
        }

        String welcomeUrl = getClass().getResource("welcome.html").toExternalForm();
        ruleInformation.getEngine().load(welcomeUrl);

        updateVitals();

        if(model != null) {

            warningStatus.textProperty().bind(model.warningTextProperty());

            model.stateProperty().addListener(new ChangeListener<StateChange>() {

                @Override
                public void changed(ObservableValue<? extends StateChange> observable, StateChange oldValue, StateChange newValue) {
                    try {
                        handleStateChange(newValue);
                    } catch (Exception e) {
                        log.error("Failed to handle alarm", e);
                    }
                }

            });
        } else {
            vitalsPanel.getChildren().clear();
            warningStatus.textProperty().unbind();
        }
    }

    private synchronized void handleStateChange(StateChange v) throws Exception {
        switch(v.state) {
            case Alarm:
                handleAlarm(v);
                break;
            case Warning:
                warningStatus.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
                break;
            case Normal:
                handleNormal(v);
                break;
            default:
                break;
        }
    }

    private void handleAlarm(StateChange v) throws Exception {

        warningStatus.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));

        // fx thread will call us while the model is loading so no active rule is an ok condition.
        //
        if(activeRule != null) {

            ScriptObjectMirror result = (ScriptObjectMirror) activeRule.invocable.invokeFunction("handleAlarm");
            if (result.hasMember("statusInformation")) {
                String pageName = (String) result.get("statusInformation");
                activeRule.load(ruleInformation, pageName);
            }
        }
    }

    private void handleNormal(StateChange v) throws Exception {

        warningStatus.setBackground(new Background(new BackgroundFill(null, null, null)));

        // fx thread will call us while the model is loading so no active rule is an ok condition.
        //
        if(activeRule != null) {
            activeRule.load(ruleInformation, activeRule.welcome);
        }
    }

    @Override
    public void onChanged(Change<? extends Vital> c) {
        while(c.next()) {
            if(c.wasAdded() || c.wasRemoved()) {
                updateVitals();
            }
        }
    }

    @FXML public void interlockStatusClicked(MouseEvent event) {

    }
}
