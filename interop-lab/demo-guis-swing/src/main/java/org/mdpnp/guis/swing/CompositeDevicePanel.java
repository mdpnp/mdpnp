package org.mdpnp.guis.swing;

import ice.DeviceConnectivity;
import ice.DeviceIdentity;
import ice.Numeric;
import ice.SampleArray;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Duration_t;

@SuppressWarnings("serial")
public class CompositeDevicePanel extends JComponent {
    private final DeviceMonitor deviceMonitor;
    
    public CompositeDevicePanel(DomainParticipant participant, String udi) {
        super();
        setLayout(new GridLayout(1,1));
        final JLabel model = new JLabel("FUNK");
        add(model);
        
        deviceMonitor = new DeviceMonitor(participant, udi, new DeviceMonitorListener() {

            @Override
            public void deviceIdentity(DeviceIdentity di) {
                model.setText(di.model);
            }

            @Override
            public void deviceConnectivity(DeviceConnectivity dc) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void numeric(Numeric n) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void sampleArray(SampleArray sampleArray) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void addNumeric(int name) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void removeNumeric(int name) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void addSampleArray(int name) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void removeSampleArray(int name) {
                // TODO Auto-generated method stub
                
            }
            
        });
        
        Thread t = new Thread(new Runnable() {
           public void run() {
               Duration_t dur = new Duration_t(Duration_t.DURATION_INFINITE_SEC, Duration_t.DURATION_INFINITE_NSEC);
               while(true) {
                   deviceMonitor.waitForIt(dur);
               }
           }
        }, "CompositeDevicePanel data handler");
        t.setDaemon(true);
        t.start();
    }
}
