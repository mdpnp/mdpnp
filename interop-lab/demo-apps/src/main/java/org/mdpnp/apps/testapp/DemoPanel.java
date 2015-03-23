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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;

/**
 * @author Jeff Plourde
 *
 */
public class DemoPanel implements Runnable {
    private final static Logger log = LoggerFactory.getLogger(DemoPanel.class);

    @FXML
    protected Label clock;

    @FXML
    protected Button back, changePartition, createAdapter;

    @FXML
    protected BorderPane content;

    private PartitionChooserModel partitionChooserModel;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public BorderPane getContent() {
        return content;
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

    private MyPublicationBuiltinTopicDataItems items;

    public DemoPanel setModel(DomainParticipant participant, EventLoop eventLoop) {
        items = new MyPublicationBuiltinTopicDataItems();
        this.partitions.setItems(items.getPartitions());
        items.setModel(participant, eventLoop);
        return this;
    }

    public DemoPanel setModel(PartitionChooserModel partitionChooserModel) {
        this.partitionChooserModel = partitionChooserModel;
        return this;
    }

    private String udiText = "";
    private String versionText = "";

    private void setTooltip() {
        clock.setTooltip(new Tooltip(udiText + "\n" + versionText));
    }

    public DemoPanel setUdi(String udi) {
        udiText = udi;
        setTooltip();
        return this;
    }

    public DemoPanel setVersion(String version) {
        versionText = version;
        setTooltip();
        return this;
    }

    public DemoPanel() throws IOException {
        this.timeFuture = executor.scheduleAtFixedRate(this, 1000L - (System.currentTimeMillis() % 1000L) + 10L, 1000L, TimeUnit.MILLISECONDS);
    }

    public DemoPanel set(AbstractApplicationContext context) {
        this.context = context;
        return this;
    }

    @FXML
    public void clickChangePartition(ActionEvent evt) throws IOException {
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
    }

    public void stop() {
        timeFuture.cancel(true);
        executor.shutdownNow();
        if (null != items) {
            items.stop();
            items = null;
        }
    }

    private ScheduledFuture<?> timeFuture;
    private final Date date = new Date();
    private final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    private Runnable updateTimeUI = new Runnable() {
        public void run() {
            date.setTime(System.currentTimeMillis());
            if (clock != null) {
                clock.setText(timeFormat.format(date));
            }
        }
    };

    @Override
    public void run() {
        Platform.runLater(updateTimeUI);
    }

    @FXML
    public void clickURL(ActionEvent evt) {
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

    private AbstractApplicationContext context;

    @FXML
    BorderPane demoPanel;

    @FXML
    ComboBox<String> partitions;

    @FXML
    public void clickCreateAdapter(ActionEvent evt) {
        try {
            final Subscriber subscriber = partitionChooserModel.getSubscriber();
            Configuration c = CreateAdapter.showDialog(subscriber.get_participant().get_domain_id());
            if (null != c) {
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        try {

                            DomainParticipantQos pQos = new DomainParticipantQos();
                            DomainParticipantFactory.get_instance().get_default_participant_qos(pQos);
                            pQos.discovery.initial_peers.clear();
                            // for (int i = 0; i <
                            // discoveryPeers.peers.getSize(); i++) {
                            // pQos.discovery.initial_peers.add(discoveryPeers.peers.getElementAt(i));
                            // System.err.println("PEER:" +
                            // discoveryPeers.peers.getElementAt(i));
                            // }
                            DomainParticipantFactory.get_instance().set_default_participant_qos(pQos);
                            SubscriberQos qos = new SubscriberQos();
                            subscriber.get_qos(qos);
                            List<String> partition = new ArrayList<String>();
                            for (int i = 0; i < qos.partition.name.size(); i++) {
                                partition.add((String) qos.partition.name.get(i));
                            }
                            DeviceAdapter da = DeviceAdapter.newGUIAdapter(c.getDeviceFactory(), context);
                            da.setInitialPartition(partition.toArray(new String[0]));
                            da.start(c.getAddress());

                            log.info("DeviceAdapter ended");
                        } catch (Exception e) {
                            log.error("Error in spawned DeviceAdapter", e);
                        }
                    }
                });
                t.setDaemon(true);
                t.start();
            }

        } catch (IOException e) {
            log.error("", e);
        }

    }

    @FXML
    public void changePartition(ActionEvent event) {
        String p = this.partitions.getSelectionModel().getSelectedItem();
        if(null != p) {
            final List<String> partitions = new ArrayList<String>(1);
            partitions.add(p);
            partitionChooserModel.set(partitions);
        }
        
    }

}
