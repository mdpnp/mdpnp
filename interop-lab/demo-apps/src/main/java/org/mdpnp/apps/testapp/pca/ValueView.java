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
package org.mdpnp.apps.testapp.pca;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.mdpnp.apps.testapp.DeviceController;
import org.mdpnp.apps.testapp.vital.Value;

import com.rti.dds.subscription.SampleInfo;

/**
 * @author Jeff Plourde
 *
 */
public class ValueView {
    @FXML protected ImageView icon, crossout;
    @FXML protected Label time, metric_id, instance_id;
    @FXML protected Text value;
    @FXML protected DeviceController deviceController;

    public ValueView() {

    }
    
    private static class TimestampProperty extends SimpleStringProperty implements InvalidationListener {
        private final ReadOnlyObjectProperty<Date> source;

        public TimestampProperty(ReadOnlyObjectProperty<Date> readOnlyDateProperty) {
            this.source = readOnlyDateProperty;
            // TODO register listener weakly
            readOnlyDateProperty.addListener(this);
        }
        @Override
        public void set(String newValue) {
            throw new UnsupportedOperationException();
        }
        @Override
        public String get() {
            return timeFormat.format(source.get());
        }
        @Override
        public void invalidated(Observable observable) {
            fireValueChangedEvent();
        }
    }


    
    protected static final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    
    public void set(final Value value) {
        if(null != value) {
            this.value.textProperty().bind(value.valueProperty().asString("%.0f"));
            this.metric_id.textProperty().bind(value.metricIdProperty());
            this.instance_id.textProperty().bind(value.instanceIdProperty().asString());
            this.time.textProperty().bind(new TimestampProperty(value.timestampProperty()));
        } else {
            this.value.textProperty().unbind();
            this.metric_id.textProperty().unbind();
            this.instance_id.textProperty().unbind();
            this.time.textProperty().unbind();
        }
        deviceController.bind(value.getDevice());
    }
    
    public void update(Value value, Image icon, String deviceName, ice.Numeric numeric, SampleInfo si, long valueMsBelowLow, long valueMsAboveHigh) {
        this.icon.setImage(icon);

        if (si != null && numeric != null) {
//            date.setTime(1000L * si.source_timestamp.sec + si.source_timestamp.nanosec / 1000000L);
            String s = Integer.toString(Math.round(numeric.value));
            while (s.length() < 3) {
                s = " " + s;
            }
            this.value.setText(s);

//            this.time.setText(timeFormat.format(date));
        } else {
            this.value.setText("   ");
            this.time.setText("");
        }

    }
    
    public static class ValueViewApp extends Application {
        public static void main(String[] args) {
            launch(args);
        }
        @Override
        public void start(Stage primaryStage) throws Exception {
            
        }
    }
}
