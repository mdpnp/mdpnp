package org.mdpnp.devices.clamp;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import purejavacomm.CommPort;
import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;

public class Clampy {

    @FXML ComboBox<CommPortIdentifier> serialPorts;
    @FXML TextField loadCell;
    @FXML TextField batteryLife;
    @FXML CheckBox triggerSolenoid;
    @FXML CheckBox lockoutSolenoid;
    @FXML CheckBox triggerSwitch;
    @FXML CheckBox tubeSwitchIn;
    @FXML CheckBox doorSwitchShut;
    @FXML Button connectButton;
    @FXML public void changeSerialPort(ActionEvent event) {
        
    }
    
    
    protected static class CommPortIdentifierListCell extends ListCell<CommPortIdentifier> {
        @Override
        protected void updateItem(CommPortIdentifier item, boolean empty) {
            super.updateItem(item, empty);
            if(null != item) {
                setText(item.getName());
            }
        }

    }
    public void initialize() {
        ObservableList<CommPortIdentifier> items = FXCollections.observableArrayList();
        
        Enumeration<?> e = CommPortIdentifier.getPortIdentifiers();
        while(e.hasMoreElements()) {
            items.add((CommPortIdentifier) e.nextElement());
        }
        serialPorts.setButtonCell(new CommPortIdentifierListCell());
        serialPorts.setCellFactory(new Callback<ListView<CommPortIdentifier>,ListCell<CommPortIdentifier>>() {
            @Override
            public ListCell<CommPortIdentifier> call(ListView<CommPortIdentifier> param) {
                return new CommPortIdentifierListCell();
            }
            
        });
        serialPorts.setItems(items);
    }
    
    
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });
    
    public Clampy() {
    }
    
    public void stop() {
        executor.shutdownNow();
        SerialPort port = this.port;
        if(null != port) {
            port.close();
        }
    }
    private Clamp clamp;
    private SerialPort port;
    private ScheduledFuture<?> heartbeatFuture;
    private boolean closing;
    
    private final Runnable heartbeat = new Runnable() {
        public void run() {
            try {
                Clamp clamp = Clampy.this.clamp;
                if(null != clamp) {
                    clamp.sendHeartbeatResponse();
                }
            } catch(Throwable t) {
                t.printStackTrace();
            }
        }
    };
    
    @FXML public void connect(ActionEvent event) {
        if("Disconnect".equals(connectButton.getText())) {
            SerialPort port = this.port;
            if(null != port) {
                closing = true;
                port.close();
            }
        } else {
            if(null == port) {
                connectButton.setText("Disconnect");
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            CommPortIdentifier identifier = serialPorts.getSelectionModel().getSelectedItem();
                            port = (SerialPort) identifier.open("ClampApp", 5000);
                            port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
                            port.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                            
                            clamp = new Clamp(port.getInputStream(), port.getOutputStream()) {
                                @Override
                                public void receiveMessage(final int loadCell, final boolean triggerSolenoidStatus, final boolean lockoutSolenoidStatus,
                                        final boolean triggerSwitchStatus, final boolean tubeSwitchStatusIn, final boolean doorSwitchStatusShut, final int batteryLife) {
                                    Platform.runLater(new Runnable() {
                                        public void run() {
                                            Clampy.this.loadCell.setText(""+loadCell);
                                            Clampy.this.triggerSolenoid.setSelected(triggerSolenoidStatus);
                                            Clampy.this.lockoutSolenoid.setSelected(lockoutSolenoidStatus);
                                            Clampy.this.triggerSwitch.setSelected(triggerSwitchStatus);
                                            Clampy.this.tubeSwitchIn.setSelected(tubeSwitchStatusIn);
                                            Clampy.this.doorSwitchShut.setSelected(doorSwitchStatusShut);
                                            Clampy.this.batteryLife.setText(""+batteryLife);
                                        }
                                    });
        
                                }
                            };
                            
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        heartbeatFuture = executor.scheduleAtFixedRate(heartbeat, 500L, 1000L, TimeUnit.MILLISECONDS);
                                        clamp.receive();
                                    } catch (IOException e) {
                                        if(!closing) {
                                            e.printStackTrace();
                                        }
                                    } finally {
                                        if(null != heartbeatFuture) {
                                            heartbeatFuture.cancel(false);
                                            heartbeatFuture = null;
                                        }
                                        clamp = null;
                                        port = null;
                                        Platform.runLater(new Runnable() {
                                            public void run() {
                                                connectButton.setText("Connect");
                                                batteryLife.setText("");
                                                loadCell.setText("");
                                                triggerSolenoid.setSelected(false);
                                                lockoutSolenoid.setSelected(false);
                                                triggerSwitch.setSelected(false);
                                                tubeSwitchIn.setSelected(false);
                                                doorSwitchShut.setSelected(false);
                                                closing = false;
                                            }
                                        });
                                        
                                    }
                                }
                            }).start();
                            
                        } catch (PortInUseException | IOException | UnsupportedCommOperationException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }
        }
    }

    @FXML public void lockTrigger(ActionEvent event) throws IOException {
        executor.submit(new Callable<Void>() { public Void call() throws Exception { Clamp clamp = Clampy.this.clamp; if(clamp != null) clamp.lockTriggerSolenoid();  return null; } });
    }

    @FXML public void unlockTrigger(ActionEvent event) throws IOException {
        executor.submit(new Callable<Void>() { public Void call() throws Exception { Clamp clamp = Clampy.this.clamp; if(clamp != null) clamp.unlockTriggerSolenoid();  return null; } });
    }

    @FXML public void lockLockout(ActionEvent event) throws IOException {
        executor.submit(new Callable<Void>() { public Void call() throws Exception { Clamp clamp = Clampy.this.clamp; if(clamp != null) clamp.lockLockoutSolenoid();  return null; } });
    }

    @FXML public void unlockLockout(ActionEvent event) throws IOException {
        executor.submit(new Callable<Void>() { public Void call() throws Exception { Clamp clamp = Clampy.this.clamp; if(clamp != null) clamp.unlockLockoutSolenoid();  return null; } });
    }

    @FXML public void powerRed(ActionEvent event) throws IOException {
        executor.submit(new Callable<Void>() { public Void call() throws Exception { Clamp clamp = Clampy.this.clamp; if(clamp != null) clamp.setPowerLedRed();  return null; } });
    }

    @FXML public void powerGreen(ActionEvent event) throws IOException {
        executor.submit(new Callable<Void>() { public Void call() throws Exception { Clamp clamp = Clampy.this.clamp; if(clamp != null) clamp.setPowerLedGreen();  return null; } });
    }

    @FXML public void powerOff(ActionEvent event) throws IOException {
        executor.submit(new Callable<Void>() { public Void call() throws Exception { Clamp clamp = Clampy.this.clamp; if(clamp != null) clamp.setPowerLedOff();  return null; } });
    }

    @FXML public void readyRed(ActionEvent event) throws IOException {
        executor.submit(new Callable<Void>() { public Void call() throws Exception { Clamp clamp = Clampy.this.clamp; if(clamp != null) clamp.setReadyLedRed();  return null; } });
    }

    @FXML public void readyGreen(ActionEvent event) throws IOException {
        executor.submit(new Callable<Void>() { public Void call() throws Exception { Clamp clamp = Clampy.this.clamp; if(clamp != null) clamp.setReadyLedGreen();  return null; } });
    }

    @FXML public void readyOff(ActionEvent event) throws IOException {
        executor.submit(new Callable<Void>() { public Void call() throws Exception { Clamp clamp = Clampy.this.clamp; if(clamp != null) clamp.setReadyLedOff();  return null; } });
    }

}
