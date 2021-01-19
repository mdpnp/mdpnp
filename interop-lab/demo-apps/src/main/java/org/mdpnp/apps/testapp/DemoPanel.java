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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;

import org.mdpnp.apps.testapp.patient.PatientInfo;
import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.sql.SQLLogging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;

import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;

/**
 * @author Jeff Plourde
 *
 */
public class DemoPanel {
    private final static Logger log = LoggerFactory.getLogger(DemoPanel.class);

    @FXML
    protected Label clock;

    @FXML
    protected Button back, createAdapter;

    @FXML
    protected BorderPane content;
    
    @FXML 
    protected Label appTitle;

    private PartitionChooserModel partitionChooserModel;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public BorderPane getContent() {
        return content;
    }

    public Button getBack() {
        return back;
    }

    public ComboBox<PatientInfo> getPatientSelector() {
        return patientSelector;
    }

    public Button getCreateAdapter() {
        return createAdapter;
    }

    public DemoPanel setModel(PartitionChooserModel partitionChooserModel) {
        this.partitionChooserModel = partitionChooserModel;

        this.patientSelector.setItems(partitionChooserModel.getPatients());
        this.patientSelector.setValue(PartitionChooserModel.NOBODY);
        return this;
    }

    private String udiText = "";
    private String versionText = "";
    private DeviceListModel deviceListModel;

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

    public DemoPanel setDeviceListModel(DeviceListModel deviceListModel) {
        this.deviceListModel = deviceListModel;
        return this;
    }

    public DemoPanel() throws IOException {
        this.timeFuture =
                executor.scheduleAtFixedRate(
                        new Runnable() {
                            @Override
                            public void run() {
                                Platform.runLater(updateTimeUI);
                            }
                        },
                        1000L - (System.currentTimeMillis() % 1000L) + 10L, 1000L,
                        TimeUnit.MILLISECONDS);
    }

    public void stop() {
        timeFuture.cancel(true);
        executor.shutdownNow();
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

    @FXML
    BorderPane demoPanel;

    @FXML
    ComboBox<PatientInfo> patientSelector;
    
    @FXML
    Label patientsLabel;

    @FXML
    public void clickCreateAdapter(ActionEvent evt) {
        try {
            final Subscriber subscriber = partitionChooserModel.getSubscriber();
            final Configuration c = CreateAdapter.showDialog(subscriber.get_participant().get_domain_id());
            if(null != c) {
                Thread t = new Thread(() -> {
                    try {
                        
                        if (null != c) {
                            SubscriberQos qos = new SubscriberQos();
                            subscriber.get_qos(qos);
                            List<String> partition = new ArrayList<String>();
                            for (int i = 0; i < qos.partition.name.size(); i++) {
                                partition.add((String) qos.partition.name.get(i));
                            }
            
                            try {
                                // This must not use the same context as the app as it messes up DDS
                                //
                                final AbstractApplicationContext context = c.createContext("EmbeddedDeviceAdapterContext.xml");
                                final DeviceAdapterCommand.HeadlessAdapter da = new DeviceAdapterCommand.HeadlessAdapter(c.getDeviceFactory(), context, false) {
                                    // intercept stop to destroy the context specific to this device
                                    public void stop() {
                                        //Must get the UDI before calling calling super.stop and context.destroy.
                                        AbstractDevice d=getDevice();
                                        String udiToKill=d.getUniqueDeviceIdentifier();
                                        super.stop();
                                        context.destroy();
                                        try {
                                            Connection c=SQLLogging.getConnection();
                                            PreparedStatement ps=c.prepareStatement("UPDATE devices SET destroyed=? WHERE udi=? AND destroyed IS NULL");
                                            ps.setLong(1, System.currentTimeMillis()/1000);
                                            ps.setString(2, udiToKill);
	                                        /*
	                                         * ps.execute should return false here and we should get an update count.  Of course the update count should be 1
	                                         * -1 in this context indicates an error.
	                                         */
                                            if( ! ps.execute()) {
                                                log.info("Updated "+ps.getUpdateCount()+" rows in the devices table with destroyed time");
	                                        }
                                            
                                            ps=c.prepareStatement("UPDATE patientdevice SET dissociated=? WHERE udi=? AND dissociated IS NULL");
                                            ps.setLong(1, System.currentTimeMillis()/1000);
                                            ps.setString(2, udiToKill);
                                            if( ! ps.execute()) {
                                                log.info("Updated "+ps.getUpdateCount()+" rows in the patientdevice table with dissociated time");
	                                        }
	                                        c.close();
                                        } catch (SQLException sqle) {
                                            log.error("Failed to record device destruction in database",sqle);
                                        }
                                    };

                                    @Override
                                    public void init() throws Exception {
                                        super.init();
                                        AbstractDevice d=getDevice();
                                        //String logThis=d.getUniqueDeviceIdentifier()+" "+d.getManufacturer()+" "+d.getModel();
                                        //System.err.println("HeadlessAdapter init override has details "+logThis);
                                        /*
                                         * Although this init method throws Exception in signature, we still wrap this SQL stuff in a
                                         * try/catch so we don't cause the init to fail if we can't log.
                                         */
                                        try {
	                                        Connection c=SQLLogging.getConnection();
	                                        PreparedStatement ps=c.prepareStatement("INSERT INTO devices(created, manufacturer, model, udi) VALUES (?,?,?,?)");
	                                        ps.setLong(1, (System.currentTimeMillis()/1000) );
	                                        ps.setString(2, d.getManufacturer());
	                                        ps.setString(3, d.getModel());
	                                        ps.setString(4, d.getUniqueDeviceIdentifier());
	                                        ps.execute();
	                                        c.close();
                                        } catch (SQLException sqle) {
                                            log.error("Failed to record device creation in database",sqle);
                                        }
                                    }

                                };
                                // Use the current partition of the app container
                                da.setPartition(partition.toArray(new String[0]));
                                da.setAddress(c.getAddress());
                                da.init();

                                boolean connected=da.connect();
                                //Is this the answer?
                                Thread.sleep(1000);
                                
                                Platform.runLater( () -> {
                                	deviceListModel.getByUniqueDeviceIdentifier(da.getDevice().getUniqueDeviceIdentifier()).setHeadlessAdapter(da);
                                });

                            } catch (Exception e) {
                                log.error("Error in spawned DeviceAdapter", e);
                            }
                        }
            
                    } catch (Exception e) {
                        log.error("", e);
                    }
                });
                t.setDaemon(true);
                t.start();
            } else {
            	System.err.println("c is null outer");
            }
            
        } catch (IOException e) {
            log.error("Error getting configuration", e);
        }
    }

    @FXML
    public void changePatient(ActionEvent event) {
        PatientInfo pi = patientSelector.getSelectionModel().getSelectedItem();
        partitionChooserModel.changePartition(pi);
    }

}
