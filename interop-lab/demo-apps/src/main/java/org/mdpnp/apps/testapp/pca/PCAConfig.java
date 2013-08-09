package org.mdpnp.apps.testapp.pca;

import ice.DeviceIdentity;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.mdpnp.apps.testapp.DeviceIcon;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelListener;

@SuppressWarnings("serial")
public class PCAConfig extends JComponent implements VitalModelListener {
    public PCAConfig(ScheduledExecutorService executor) {
        setLayout(new GridBagLayout());
        pumpProgress = new JProgressAnimation(executor);
        pumpProgress.setBackground(new Color(1f,1f,1f,.5f));

        pumpProgress.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                model.resetInfusion();
                super.mouseClicked(e);
            }
        });
        pumpStatus.setEditable(false);
        pumpStatus.setLineWrap(true);
        pumpStatus.setWrapStyleWord(true);
        warningStatus.setEditable(false);
        warningStatus.setLineWrap(true);
        warningStatus.setWrapStyleWord(true);
    }
    private VitalModel model;
    
    static final Map<String,SoftReference<DeviceIdentity>> udiToDeviceIdentity = Collections.synchronizedMap(new HashMap<String, SoftReference<DeviceIdentity>>());
    static final Map<String,SoftReference<DeviceIcon>> udiToDeviceIcon = Collections.synchronizedMap(new HashMap<String, SoftReference<DeviceIcon>>()); 
    
    
    protected void updateVitals() {
        if(SwingUtilities.isEventDispatchThread()) {
            _updateVitals();
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    _updateVitals();
                    validate();
//                    repaint();
                }
            });
        }
    }
    
    private final JProgressAnimation pumpProgress;
    private final JTextArea pumpStatus = new JTextArea(" ");
    private final JTextArea warningStatus = new JTextArea(" ");
    private final JComboBox warningsToAlarm = new JComboBox();
    protected void _updateVitals() {
        for(Component c : getComponents()) {
            if(c instanceof JVital) {

            }
        }
        removeAll();

        final VitalModel model = this.model;
        if(model != null) {
            GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0);
            
            for(int i = 0; i < model.getCount(); i++) {
                final Vital vital = model.getVital(i);
                JVital jVital = new JVital(vital);
                

                add(jVital, gbc);
                jVital.setOpaque(true);
                gbc.gridy++;
            }
            JPanel panel = new JPanel();

            
            List<Integer> values = new ArrayList<Integer>();
            for(int i = 0; i < VitalSign.values().length; i++) {
                values.add(i+1);
            }
            warningsToAlarm.setModel(new DefaultComboBoxModel(values.toArray(new Integer[0])));
            warningsToAlarm.setSelectedItem((Integer)model.getCountWarningsBecomeAlarm());
            panel.add(warningsToAlarm);
            panel.add(new JLabel("Warnings become an alarm"));
            warningsToAlarm.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    model.setCountWarningsBecomeAlarm((Integer)warningsToAlarm.getSelectedItem());
                }
                
            });
            
            
            final JComboBox vitalSigns = new JComboBox(VitalSign.values());
            panel.add(vitalSigns);
            
            JButton add = new JButton("Add");
            add.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ((VitalSign)vitalSigns.getSelectedItem()).addToModel(model);
                }
            });
            panel.add(add);
            
            JPanel pumpPanel = new JPanel(new GridLayout(1,3));
            pumpPanel.add(pumpProgress);
            pumpPanel.add(new JScrollPane(pumpStatus));
            pumpPanel.add(new JScrollPane(warningStatus));
            
            
            
            gbc.weighty = 0.1;
            add(panel, gbc);
            
            gbc.gridy++;
            gbc.weighty = 5.0;
            
            add(pumpPanel, gbc);
            
         // ARGH
            warningStatus.setBackground(Color.white);
            pumpStatus.setBackground(Color.white);
            warningStatus.setOpaque(true);
            pumpStatus.setOpaque(true);
        }
    }
    
    public void setModel(VitalModel model) {
        if(this.model != null) {
            this.model.removeListener(this);
        }
        this.model = model;
        if(this.model != null) {
            this.model.addListener(this);
        }
        updateVitals();
        vitalChanged(this.model, null);
    }
    @Override
    public void vitalChanged(VitalModel model, Vital vital) {
        if(model != null) {
            warningsToAlarm.setSelectedItem((Integer)model.getCountWarningsBecomeAlarm());
            if(model.isInfusionStopped()) {
                pumpProgress.stop();
    //            pumpStatus.setBackground(Color.red);
            } else {
                pumpProgress.start();
    //            pumpStatus.setBackground(Color.green);
            }
            pumpStatus.setText(model.getInterlockText());
            // TODO setOpaque(false) is running rampant

            switch(model.getState()) {
            case Alarm:
                warningStatus.setBackground(Color.red);
                break;
            case Warning:
                warningStatus.setBackground(Color.yellow);
                break;
            case Normal:
                warningStatus.setBackground(Color.white);
                break;
            }
            warningStatus.setText(model.getWarningText());
            repaint();
        }
        if(vital != null) {
            for(Component c : getComponents()) {
                if(c instanceof JVital && ((JVital)c).getVital().equals(vital)) {
                    ((JVital)c).updateData();
                    return;
                }
            }
            // fell through if the specified vital was not found
            updateVitals();
        }
        

    }
    @Override
    public void vitalRemoved(VitalModel model, Vital vital) {
        updateVitals();
        vitalChanged(model, null);
    }
    @Override
    public void vitalAdded(VitalModel model, Vital vital) {
        vitalChanged(model, vital);
    }
}
