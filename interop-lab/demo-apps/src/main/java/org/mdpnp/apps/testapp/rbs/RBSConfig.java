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

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.rti.dds.infrastructure.InstanceHandle_t;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import javafx.stage.FileChooser;
import javafx.util.Callback;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.mdpnp.apps.fxbeans.InfusionStatusFx;
import org.mdpnp.apps.fxbeans.InfusionStatusFxList;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.InfusionStatusFxListCell;
import org.mdpnp.apps.testapp.pca.InfusionPumpModel;
import org.mdpnp.apps.testapp.pca.VitalSign;
import org.mdpnp.apps.testapp.pca.VitalView;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModel.StateChange;
import org.mdpnp.devices.DeviceClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Jeff Plourde
 *
 */
public class RBSConfig implements ListChangeListener<Vital> {

    @FXML TextArea warningStatus;
    @FXML TextArea infusionStatus;
    @FXML TextArea interlockStatus;

    @FXML VBox vitalsPanel;

    @FXML Button   load;
    @FXML FlowPane controls;


    private static final ReadOnlyBooleanProperty OFF = new ReadOnlyBooleanWrapper(false);

    private VitalModel model;

    private static final Logger log = LoggerFactory.getLogger(RBSConfig.class);

    private ice.InfusionObjectiveDataWriter objectiveWriter;

    private Invocable invocable;


    public RBSConfig set(final ScheduledExecutorService executor, final ice.InfusionObjectiveDataWriter objectiveWriter,
                         final DeviceListModel deviceListModel, final InfusionStatusFxList infusionStatusList) {

        this.objectiveWriter = objectiveWriter;

        return this;
    }


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

        File ruleName = fc.showOpenDialog(null);
        if(null != ruleName) {

            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            InputStream is = new FileInputStream(ruleName);
            engine.eval(new InputStreamReader(is));
            is.close();

            invocable = (Invocable) engine;
            invocable.invokeFunction("create", model);
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
                if(null != jVital) {
                    vitalsPanel.getChildren().add(jVital);
                } else {
                    FXMLLoader loader = new FXMLLoader(VitalView.class.getResource("VitalView.fxml"));
                    try {
                        jVital = loader.load();
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

        updateVitals();
        if(model != null) {

            warningStatus.textProperty().bind(model.warningTextProperty());

            model.stateProperty().addListener(new ChangeListener<StateChange>() {

                @Override
                public void changed(ObservableValue<? extends StateChange> observable, StateChange oldValue, StateChange newValue) {
                    switch(newValue.state) {
                    case Alarm:
                        warningStatus.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));

                        try {
                            handleAlarm(newValue);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    case Warning:
                        warningStatus.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
                        break;
                    case Normal:
                        warningStatus.setBackground(new Background(new BackgroundFill(null, null, null)));
                        break;
                    default:
                        break;
                    }
                }

            });
        } else {
            vitalsPanel.getChildren().clear();
            warningStatus.textProperty().unbind();
            interlockStatus.textProperty().unbind();
        }
    }


    protected void handleAlarm(StateChange v) throws Exception {

        if(invocable == null)
            throw new IllegalStateException("No ruleset");

        ScriptObjectMirror result = (ScriptObjectMirror) invocable.invokeFunction("handleAlarm", v);

        VitalModel.State o = (VitalModel.State) result.get("status");
        System.out.println(o);

        /*
        if(result.isArray()) {
            Set<String> keys = result.keySet();
            for(String key: keys) {
                VitalModel.State o = (VitalModel.State) result.get(key);
                //String code = (String) ((ScriptObjectMirror) o.get("valueQuantity")).get("code");
                //Number value = (Number) ((ScriptObjectMirror) o.get("valueQuantity")).get("value");
                //
                //ScriptObjectMirror o = (ScriptObjectMirror) result.get(key);
                System.out.println(keys + "->" + o);
            }
        }
        */
    }


    public void setStop(InfusionStatusFx status, boolean stop) {
        ice.InfusionObjective obj = new ice.InfusionObjective();
        obj.requestor = "ME";
        obj.unique_device_identifier = status.getUnique_device_identifier();
        obj.stopInfusion = stop;
        objectiveWriter.write(obj, InstanceHandle_t.HANDLE_NIL);
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
