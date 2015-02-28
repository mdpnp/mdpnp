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
package org.mdpnp.apps.testapp;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Jeff Plourde
 *
 */
public class DemoPanel implements Runnable {
    @FXML
    protected Label bedLabel, clock, version, udi;
    
    @FXML
    protected Button back, changePartition, createAdapter;
    
    @FXML
    protected BorderPane content;

    private PartitionChooserModel partitionChooserModel;
    
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();


    public BorderPane getContent() {
        return content;
    }

    public Label getUdi() {
        return udi;
    }

    public Label getBedLabel() {
        return bedLabel;
    }

    public Button getBack() {
        return back;
    }
    
    public Button getChangePartition() {
        return changePartition;
    }
    
    public Button getCreateAdapter() {
        return createAdapter;
    }
    
    public Label getVersion() {
        return version;
    }    

    public DemoPanel setModel(PartitionChooserModel partitionChooserModel) {
        this.partitionChooserModel = partitionChooserModel;
        return this;
    }
    
    public DemoPanel setUdi(String udi) {
        this.udi.setText(udi);
        return this;
    }
    
    public DemoPanel setVersion(String version) {
        this.version.setText(version);
        return this;
    }
    
    public DemoPanel() throws IOException {
        this.timeFuture = executor.scheduleAtFixedRate(this, 1000L - (System.currentTimeMillis() % 1000L) + 10L, 1000L, TimeUnit.MILLISECONDS);
    }
    
    @FXML public void clickChangePartition(ActionEvent evt) throws IOException {
        final Stage dialog = new Stage(StageStyle.UTILITY);
        dialog.setAlwaysOnTop(true);
        
        
        FXMLLoader loader = new FXMLLoader(PartitionChooser.class.getResource("PartitionChooser.fxml"));
        BorderPane root = loader.load();
        PartitionChooser partitionChooser = loader.getController();
        partitionChooser.setItems(FXCollections.observableArrayList()).setModel(partitionChooserModel);
        partitionChooser.setHide(new Runnable() {
            public void run() {
                dialog.hide();
            }
        });
        Scene scene = new Scene(root);
        dialog.setScene(scene);
        dialog.sizeToScene();
        
        dialog.showAndWait();
////    partitionChooser.refresh();
////    partitionChooser.setLocationRelativeTo(panel);
////    partitionChooser.setVisible(true);
    }

    public void stop() {
        timeFuture.cancel(true);
    }
    
    private ScheduledFuture<?> timeFuture;
    private final Date date = new Date();
    private final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    
    private Runnable updateTimeUI = new Runnable() {
        public void run() {
            date.setTime(System.currentTimeMillis());
            if(clock != null) {
                clock.setText(timeFormat.format(date));
            }
        }
    };
    
    @Override
    public void run() {
        Platform.runLater(updateTimeUI);
    }
    
    @FXML public void clickURL(ActionEvent evt) {
        String url = ((Hyperlink) evt.getSource()).getText();
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                URI uri = new URI(url);
                desktop.browse(uri);
            } catch (IOException ex) {

            } catch (URISyntaxException ex) {

            }
        }
    }

}
