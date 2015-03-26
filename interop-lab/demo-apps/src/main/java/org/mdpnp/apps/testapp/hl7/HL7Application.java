package org.mdpnp.apps.testapp.hl7;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;

public class HL7Application implements LineEmitterListener, StartStopListener {
    @FXML protected TextArea text;
    @FXML protected TextField host, port;
    @FXML protected Button startStop;
    
    protected static final Logger log = LoggerFactory.getLogger(HL7Application.class);
    
    private HL7Emitter model;
    
    public HL7Application() {

    }
    
    public void setModel(HL7Emitter model) {
        if(null == model) {
            if(null != this.model) {
                this.model.removeLineEmitterListener(this);
                this.model.removeStartStopListener(this);
                this.model = null;
            }
        } else {
            if(null != this.model) {
                if(this.model.equals(model)) {
                    // NO OP
                } else {
                    this.model.removeLineEmitterListener(this);
                    this.model.removeStartStopListener(this);
                    this.model = model;
                    this.model.addLineEmitterListener(this);
                    this.model.addStartStopListener(this);
                }
            } else {
                this.model = model;
                this.model.addLineEmitterListener(this);
                this.model.addStartStopListener(this);
            }
        }
        startStop.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if("Start".equals(startStop.getText())) {
                    HL7Emitter.Type type;
                    if(hl7version.getSelectedToggle().equals(hl7FhirDstu2)) {
                        type = HL7Emitter.Type.FHIR_DSTU2;
                    } else if(hl7version.getSelectedToggle().equals(hl7V26)) {
                        type = HL7Emitter.Type.V26;
                    } else {
                        return;
                    }
                    
                    startStop.setDisable(true);
                    int portNumber = port.getText().isEmpty() ? 0 : Integer.parseInt(port.getText());

                    
                    model.start(host.getText(), portNumber, type);
                    
                } else {
                    startStop.setDisable(true);
                    model.stop();
                    
                }
            }
            
        });
    }
    
    public void stop() {
        if(model != null) {
            model.stop();
            model = null;
        }
        text.clear();
    }

    @Override
    public void newLine( String line) {
        final String l = line.replaceAll("\\r", "\n");
        Platform.runLater(new Runnable() {
            public void run() {
                text.insertText(0, l);
                if(text.getLength()>MAX_CHARS) {
                    text.deleteText(MAX_CHARS, text.getLength());
                }
            }
        });
    }
    private static final int MAX_CHARS = 8000;
    @FXML ToggleGroup hl7version;
    @FXML RadioButton hl7FhirDstu2;
    @FXML RadioButton hl7V26;

    @Override
    public void started() {
        Platform.runLater(new Runnable() {
            public void run() {
                text.deleteText(0, text.getLength());
                startStop.setText("Stop");
                startStop.setDisable(false);
            }
        });

    }

    @Override
    public void stopped() {
        Platform.runLater(new Runnable() {
            public void run() {
                startStop.setText("Start");
                startStop.setDisable(false);                
            }
        });

    }
}
