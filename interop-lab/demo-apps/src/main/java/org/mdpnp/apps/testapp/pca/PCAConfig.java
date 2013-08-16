package org.mdpnp.apps.testapp.pca;

import ice.DeviceIdentity;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
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
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mdpnp.apps.testapp.DeviceIcon;
import org.mdpnp.apps.testapp.co2.Capno;
import org.mdpnp.apps.testapp.co2.CapnoListModel;
import org.mdpnp.apps.testapp.pump.Pump;
import org.mdpnp.apps.testapp.pump.PumpListModel;
import org.mdpnp.apps.testapp.pump.PumpModel;
import org.mdpnp.apps.testapp.pump.PumpModelListener;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelListener;

@SuppressWarnings("serial")
public class PCAConfig extends JComponent implements VitalModelListener, PumpModelListener {
    
    JCheckBox configureModeBox = new JCheckBox("Configuration Mode");
    JPanel configurePanel = new JPanel();
    
    public PCAConfig(ScheduledExecutorService executor) {
        setLayout(new GridBagLayout());
        pumpProgress = new JProgressAnimation2(executor);
        pumpProgress.setForeground(Color.green);
//        pumpProgress.setBackground(new Color(1f,1f,1f,.5f));
//        pumpProgress.setFont(Font.decode("fixed-20"));

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
        
        
        pumpStatus.setFont(Font.decode("verdana-20"));
        warningStatus.setFont(pumpStatus.getFont());
        
        List<Integer> values = new ArrayList<Integer>();
        for(int i = 0; i < VitalSign.values().length; i++) {
            values.add(i+1);
        }
        warningsToAlarm.setModel(new DefaultComboBoxModel(values.toArray(new Integer[0])));
//        warningsToAlarm.setSelectedItem((Integer)model.getCountWarningsBecomeAlarm());
        configurePanel.add(warningsToAlarm);
        configurePanel.add(new JLabel("Warnings become an alarm"));
        warningsToAlarm.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                model.setCountWarningsBecomeAlarm((Integer)warningsToAlarm.getSelectedItem());
            }
            
        });
        
        
        final JComboBox vitalSigns = new JComboBox(VitalSign.values());
        configurePanel.add(vitalSigns);
        
        JButton add = new JButton("Add");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((VitalSign)vitalSigns.getSelectedItem()).addToModel(model);
            }
        });
        configurePanel.add(add);

        
        configureModeBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                configurePanel.setVisible(configureModeBox.isSelected());
                for(Component c : getComponents()) {
                    if(c instanceof JVital) {
                        
                        ((JVital)c).setShowConfiguration(configureModeBox.isSelected());
                    }
                }
            }
            
        });
        
        pumpList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                vitalChanged(model, null);
                if(null == pumpList.getSelectedValue()) {
                    pumpProgress.setPopulated(false);
                } else {
                    pumpChanged(pumpModel, (Pump) pumpList.getSelectedValue());
                }
            }
            
        });
        pumpList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                if(value != null && value instanceof Pump) {
                    String udi = ((Pump)value).getInfusionStatus().universal_device_identifier;
                    VitalModel model = PCAConfig.this.model;
                    if(model != null) {
                        ice.DeviceIdentity di = model.getDeviceIdentity(udi);
                        if(null != di) {
                            value = di.manufacturer + " " + di.model;
                            
                        }
                    }
                }
                
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                return c;
            }
        });
    }
    private VitalModel model;
    private PumpModel pumpModel;
    
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
    
    private final JProgressAnimation2 pumpProgress;
    private final JTextArea pumpStatus = new JTextArea(" ");
    private final JList pumpList = new JList();
    private final JTextArea warningStatus = new JTextArea(" ");
    private final JComboBox warningsToAlarm = new JComboBox();
    protected void _updateVitals() {
        for(Component c : getComponents()) {
            if(c instanceof JVital) {

            }
        }
        
        // TODO this has become unnecessary ... vitals can be selectively added and removed with a little bit of caution instead of this current rebuilding at every change 
        removeAll();

        final VitalModel model = this.model;
        if(model != null) {
            GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0);
            
            // ARGH
//            warningStatus.setBackground(Color.white);
//            pumpStatus.setBackground(Color.white);
//            warningStatus.setOpaque(true);
//            pumpStatus.setOpaque(true);

            
            JPanel pumpPanel = new JPanel(new GridLayout(1,4));
            pumpPanel.add(pumpProgress);
            JScrollPane scrollPane;
            pumpPanel.add(scrollPane = new JScrollPane(pumpList));
            scrollPane.setBorder(null);
            pumpPanel.add(scrollPane = new JScrollPane(pumpStatus));
            scrollPane.setBorder(null);
            pumpPanel.add(scrollPane = new JScrollPane(warningStatus));
            scrollPane.setBorder(null);
            
            gbc.weighty = 5.0;
            add(pumpPanel, gbc);
            
            gbc.weighty = 1.0;
            gbc.gridy++;
            
            gbc.gridwidth = 3;
            add(configureModeBox, gbc);
            
            gbc.gridy++;
            gbc.gridwidth = 1;
            
            for(int i = 0; i < model.getCount(); i++) {
                final Vital vital = model.getVital(i);
                JVital jVital = new JVital(vital);
                jVital.setShowConfiguration(configureModeBox.isSelected());

                add(jVital, gbc);
                jVital.setOpaque(true);
                gbc.gridy++;
            }
            gbc.weighty = 0.1;
            configurePanel.setVisible(configureModeBox.isSelected());
            add(configurePanel, gbc);
            
        }
    }
    
    public void setModel(VitalModel model, PumpModel pumpModel) {
        String selectedUdi = null;
        Object selected = pumpList.getSelectedValue();
        if(null != selected && selected instanceof Pump) {
            selectedUdi  =((Pump)selected).getInfusionStatus().universal_device_identifier;
        }
        pumpList.setModel(null == pumpModel ? new DefaultListModel() : new PumpListModel(pumpModel));
        if(null != selectedUdi && pumpModel != null) {
            for(int i = 0; i < pumpModel.getCount(); i++) {
                if(selectedUdi.equals(pumpModel.getPump(i).getInfusionStatus().universal_device_identifier)) {
                    pumpList.setSelectedValue(pumpModel.getPump(i), true);
                }
            }
        }
        
        
        
        if(this.pumpModel != null) {
            this.pumpModel.removeListener(this);
        }
        this.pumpModel = pumpModel;
        if(this.pumpModel != null) {
            this.pumpModel.addListener(this);
        }
        
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
            Object o = pumpList.getSelectedValue();
            Pump p = null;
            if(o instanceof Pump) {
                p = (Pump) o;
            }
            if(model.isInfusionStopped()) {
                if(null != p) {
                    p.setStop(true);
                }
                pumpStatus.setText(model.getInterlockText());
//                pumpProgress.stop();
                pumpStatus.setBackground(Color.red);
            } else {
                if(null != p) {
                    p.setStop(false);
                    pumpStatus.setText("Drug:"+p.getInfusionStatus().drug_name+"\nVTBI:"+p.getInfusionStatus().volume_to_be_infused_ml+" mL\nDuration:"+p.getInfusionStatus().infusion_duration_seconds+" seconds\nProgress:"+Integer.toString( (int) (100f * p.getInfusionStatus().infusion_fraction_complete))+"%");
                }
//                pumpProgress.start();
                
                pumpStatus.setBackground(getBackground());
            }
            
            // TODO setOpaque(false) is running rampant

            switch(model.getState()) {
            case Alarm:
                warningStatus.setBackground(Color.red);
                break;
            case Warning:
                warningStatus.setBackground(Color.yellow);
                break;
            case Normal:
                warningStatus.setBackground(getBackground());
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

    @Override
    public void pumpAdded(PumpModel model, Pump pump) {
        vitalChanged(this.model,  null);
    }

    @Override
    public void pumpRemoved(PumpModel model, Pump pump) {
        vitalChanged(this.model, null);
    }

    @Override
    public void pumpChanged(PumpModel model, Pump pump) {
        if(pump!=null&&pump.equals(pumpList.getSelectedValue())) {
            if(pump.getInfusionStatus().infusionActive) {
                pumpProgress.start();
            } else {
                pumpProgress.stop();
            }
        }
    }
}
