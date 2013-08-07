package org.mdpnp.apps.testapp.pca;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ScheduledExecutorService;

import javax.media.nativewindow.util.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.ScaledDeviceIcon;
import org.mdpnp.apps.testapp.vital.JMultiSlider;
import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelListener;

@SuppressWarnings("serial")
public class PCAConfig extends JComponent implements VitalModelListener {
    private final DeviceListModel deviceListModel;
    public PCAConfig(DeviceListModel deviceListModel, ScheduledExecutorService executor) {
        setLayout(new GridBagLayout());
        this.deviceListModel = deviceListModel;
        pumpProgress = new JProgressAnimation(executor);
        pumpProgress.setBackground(new Color(1f,1f,1f,.5f));
        pumpProgress.setOpaque(false);
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
    
    private final static class JVital extends JComponent {
        private final Vital vital;
        private final JMultiSlider slider;

        private final JPanel vitalValues;
        private final DeviceListModel deviceListModel;
        
        public Vital getVital() {
            return vital;
        }
        
        public JVital(final Vital vital, final DeviceListModel deviceListModel) {
            setBorder(BorderFactory.createTitledBorder(null, vital.getLabel() + " ("+vital.getUnits()+")", 0, 0, Font.decode("fixed-20"), getForeground()));
            this.vital = vital;
            this.deviceListModel = deviceListModel;
            setOpaque(false);
            VitalBoundedRangleMulti range = new VitalBoundedRangleMulti(vital);
            slider = new JMultiSlider(range);
            slider.setRangeColor(0, Color.red);
            slider.setRangeColor(1, Color.yellow);
            slider.setOpaque(false);
//            range.addChangeListener(this);
//            slider.setPaintLabels(true);
//            slider.setPaintTicks(true);
//            stateChanged(null);
            
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0);
            gbc.gridx = 0;
            gbc.weightx = 0.1;
            

            ignoreZero.setOpaque(false);
            ignoreZero.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    vital.setIgnoreZero(ignoreZero.isSelected());
                }
                
            });
            add(new JLabel("Limits"), gbc);
            
            gbc.gridx++;
            gbc.weightx = 10.0;
            add(slider, gbc);
            
            gbc.weightx = 0.3;
            gbc.gridx++;
            add(ignoreZero, gbc);
            
            gbc.gridx++;
            JButton delete = new JButton("Remove");
            delete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    vital.getParent().removeVital(vital);
                }
            });
            add(delete, gbc);
            
            gbc.gridy++;
            
            gbc.weightx = 1.0;
            gbc.gridx = 0;
            gbc.gridwidth = 5;
            vitalValues = new JPanel(new FlowLayout());
            vitalValues.setOpaque(false);
            updateData();

            add(vitalValues, gbc);
        }

        private final JCheckBox ignoreZero = new JCheckBox("Ignore Zero");
//        @Override
//        public void stateChanged(ChangeEvent e) {
//            float range = vital.getMaximum() - vital.getMinimum();
//            int incr  = (int) (range / 5f);
//            if(incr != slider.getMajorTickSpacing()) {
//              slider.setLabelTable(slider.createStandardLabels(incr));
//              slider.setMajorTickSpacing(incr);
//            }
//        }
        
        public void updateData() {
            final int N = vital.getValues().isEmpty() ? 1 : vital.getValues().size();
            ignoreZero.setSelected(vital.isIgnoreZero());
            try {
                if(N != vitalValues.getComponentCount()) {
                    Runnable r = new Runnable() {
                        public void run() {
                            
                            while(vitalValues.getComponentCount() < N) {
                                for(int i = 0; i < (N - vitalValues.getComponentCount()); i++) {
                                    JLabel lbl = new JLabel();
                                    lbl.setFont(Font.decode("fixed-20"));
//                                    lbl.setMaximumSize(new java.awt.Dimension(100, 30));
                                    lbl.setBorder(BorderFactory.createTitledBorder("  "));
                                    vitalValues.add(lbl);
                                }
                                validate();
                            }
                            while(N < vitalValues.getComponentCount()) {
                                for(int i = 0; i < (vitalValues.getComponentCount() - N); i++) {
                                    vitalValues.remove(0);
                                }
                                validate();
                            }
                            
                        }
                    };
                    if(SwingUtilities.isEventDispatchThread()) {
                        r.run();
                    } else {
                        SwingUtilities.invokeAndWait(r);
                    }
                }
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            
            if(vital.getValues().isEmpty()) {
                ((JLabel)vitalValues.getComponent(0)).setForeground(Color.yellow);
                ((JLabel)vitalValues.getComponent(0)).setText("<NO SOURCES>");
            } else {
                for(int i = 0; i < vital.getValues().size(); i++) {
                    Value val = vital.getValues().get(i);
                    JLabel lbl = (JLabel) vitalValues.getComponent(i);
                    
                    lbl.setForeground(val.isAtOrOutsideOfBounds() ? Color.yellow : Color.green);
                    lbl.setText(Integer.toString((int)val.getNumeric().value));
                    Device device = deviceListModel.getByUniversalDeviceIdentifier(val.getUniversalDeviceIdentifier());
                    
                    if(null != device) {
                        if(lbl.getBorder() instanceof TitledBorder) {
                            ((TitledBorder)lbl.getBorder()).setTitle(device.getMakeAndModel());
                        }
                        if(!(lbl.getIcon() instanceof ScaledDeviceIcon) ||
                           !((ScaledDeviceIcon)lbl.getIcon()).getSource().equals(device.getIcon())) {
                            lbl.setIcon(new ScaledDeviceIcon(device.getIcon(), 0.5));
                        }
                        
                        
                    } else {
                        if(lbl.getBorder() instanceof TitledBorder) {
                            ((TitledBorder)lbl.getBorder()).setTitle("");
                        }
                        lbl.setIcon(null);
                    }
                }
            }
        }
    }
    
    
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
    
    protected void _updateVitals() {
        removeAll();

        final VitalModel model = this.model;
        if(model != null) {
            GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0);
            
            for(int i = 0; i < model.getCount(); i++) {
                final Vital vital = model.getVital(i);
                JVital jVital = new JVital(vital, deviceListModel);
                add(jVital, gbc);
                gbc.gridy++;
            }
            JPanel panel = new JPanel();
            panel.setOpaque(false);
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
            pumpPanel.add(pumpStatus);
            pumpPanel.add(warningStatus);
            gbc.weighty = 0.1;
            add(panel, gbc);
            
            gbc.gridy++;
            gbc.weighty = 1.0;
            
            add(pumpPanel, gbc);
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
            if(model.isInfusionStopped()) {
                pumpProgress.stop();
    //            pumpStatus.setBackground(Color.red);
            } else {
                pumpProgress.start();
    //            pumpStatus.setBackground(Color.green);
            }
            pumpStatus.setText(model.getInterlockText());
            
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
