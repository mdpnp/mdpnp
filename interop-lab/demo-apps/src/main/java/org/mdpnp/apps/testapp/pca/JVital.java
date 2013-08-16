package org.mdpnp.apps.testapp.pca;

import ice.DeviceIdentity;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.mdpnp.apps.testapp.DeviceIcon;
import org.mdpnp.apps.testapp.vital.JMultiSlider;
import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.Vital;

public final class JVital extends JPanel {
        private final Vital vital;
        private final JMultiSlider slider, slider2;

        private final JLabel name = new JLabel();
        private final JPanel vitalValues = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        private final JButton deleteButton = new JButton("Remove");
        private final JCheckBox ignoreZeroBox = new JCheckBox("Ignore Zero");
        private final JCheckBox requiredBox = new JCheckBox("Required");
        
//        private JLabel limitsLabel = new JLabel("Limits:");
//        private JLabel configureLabel = new JLabel("Configure:");
        
        private boolean showConfiguration = false;
        
        public Vital getVital() {
            return vital;
        }
        
        public JVital(final Vital vital) {
            setBorder(null);
//            setBackground(Color.black);
//            setForeground(Color.green);
            this.vital = vital;
//            name.setWrapStyleWord(true);
//            name.setEditable(false);
//            name.setLineWrap(true);
            String lbl = vital.getLabel();
            if(lbl.contains(" ")) {
                lbl = lbl.replaceAll("\\ +", "<br/>");
                lbl = "<html>"+lbl+"</html>";
//                System.out.println("Label:"+lbl);
            }
            Font font = Font.decode("verdana-20");
            name.setText(lbl);
            name.setFont(Font.decode("verdana-30"));
            VitalBoundedRangeMulti range = new VitalBoundedRangeMulti(vital);
            slider = new JMultiSlider(range);
            slider.setFont(font);
//            slider.setSize(100, slider.getPreferredSize().height);
            slider.setRangeColor(0, Color.red);
            slider.setRangeColor(1, Color.yellow);
            slider.setRangeColor(2, Color.green);
            VitalValueMsBoundedRangeMulti range2 = new VitalValueMsBoundedRangeMulti(vital);
            slider2 = new JMultiSlider(range2);
            slider2.setFont(font);
            slider2.setRangeColor(0, Color.yellow);
            slider2.setRangeColor(1, Color.green);
            slider2.setDrawThumbs(true);
            
            
            
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    vital.getParent().removeVital(vital);
                }
            });
            
            ignoreZeroBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    vital.setIgnoreZero(ignoreZeroBox.isSelected());
                }
                
            });
            
            requiredBox.addActionListener(new ActionListener() {
                @Override
                 public void actionPerformed(ActionEvent e) {
                    vital.setNoValueWarning(requiredBox.isSelected());
                 } 
             });

            _refreshContents();
        }
        
        public void setShowConfiguration(boolean showConfiguration) {
            slider.setDrawThumbs(showConfiguration);
            if(showConfiguration ^ this.showConfiguration) {
                this.showConfiguration = showConfiguration;
                refreshContents();
            }
            
        }
        
        public void refreshContents() {
            Runnable r = new Runnable() {
                public void run() {
                    _refreshContents();
                }
            };
            if(SwingUtilities.isEventDispatchThread()) {
                r.run();
            } else {
                try {
                    SwingUtilities.invokeAndWait(r);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        
        public void _refreshContents() {
            removeAll();
            
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0);
            
            // FIRST ROW
            
            add(name, gbc);
            
            gbc.weightx = 10.0;
            gbc.gridx++;
            gbc.gridwidth = 3;
            add(slider, gbc);
            
            if(showConfiguration) {
                gbc.gridy++;
                add(slider2, gbc);
                
                // SECOND ROW
                gbc.gridwidth = 1;
    //            gbc.fill = GridBagConstraints.NONE;
                gbc.weightx = 1.0;

                gbc.gridx++;
                add(ignoreZeroBox, gbc);
                
                gbc.gridx++;
                add(requiredBox, gbc);
                
                gbc.gridx++;
                add(deleteButton, gbc);
            }

            // DATA area
            gbc.gridy = 0;
            gbc.gridx = 5;
            gbc.gridheight = showConfiguration ? 2 : 1;
//            add(new JLabel("Sources:"), gbc);
            
            gbc.fill = GridBagConstraints.HORIZONTAL;
//            gbc.gridx++;
//            gbc.gridwidth = 3;
            

            updateData();

            add(vitalValues, gbc);
            
            validate();
        }

        
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
            ignoreZeroBox.setSelected(vital.isIgnoreZero());
            requiredBox.setSelected(vital.isNoValueWarning());
//            name.setText(vital.getLabel());
            try {
                if(N != vitalValues.getComponentCount()) {
                    Runnable r = new Runnable() {
                        public void run() {
                            
                            while(vitalValues.getComponentCount() < N) {
                                for(int i = 0; i < (N - vitalValues.getComponentCount()); i++) {
                                    JValue val = new JValue();
                                    val.setBorder(new LineBorder(Color.black));
                                    vitalValues.add(val);
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
//                ((JLabel)vitalValues.getComponent(0)).setForeground(Color.yellow);
//                ((JLabel)vitalValues.getComponent(0)).setBackground(Color.yellow);
                ((JValue)vitalValues.getComponent(0)).update(null, null, "<NO SOURCES>", null, null, 0L, 0L);
            } else {
                for(int i = 0; i < vital.getValues().size(); i++) {
                    Value val = vital.getValues().get(i);
                    JValue lbl = (JValue) vitalValues.getComponent(i);
                    
//                    lbl.setForeground(val.isAtOrOutsideOfBounds() ? Color.yellow : Color.green);
//                    lbl.setText(Integer.toString((int)val.getNumeric().value));
//                    Device device = deviceListModel.getByUniversalDeviceIdentifier(val.getUniversalDeviceIdentifier());
//                    DeviceIdentity di = vital.getParent().getDeviceIdentity(val.getUniversalDeviceIdentifier());
                    
//                    Device device = new Device();
//                    SoftReference<Device> ref = udiToDeviceIcon.get(val.getUniversalDeviceIdentifier());
                    SoftReference<DeviceIdentity> ref = PCAConfig.udiToDeviceIdentity.get(val.getUniversalDeviceIdentifier());
                    DeviceIdentity di = null == ref ? null : ref.get();
                    if(null == di) {
                        di = vital.getParent().getDeviceIdentity(val.getUniversalDeviceIdentifier());
                        PCAConfig.udiToDeviceIdentity.put(val.getUniversalDeviceIdentifier(), new SoftReference<DeviceIdentity>(di));
                    }
                    
                    SoftReference<DeviceIcon> ref2 = PCAConfig.udiToDeviceIcon.get(val.getUniversalDeviceIdentifier());
                    DeviceIcon dicon = null == ref2 ? null : ref2.get();
                    if(null == dicon && di != null) {
                        dicon = new DeviceIcon(di.icon, 0.75);
                        dicon.setConnected(true);
                        PCAConfig.udiToDeviceIcon.put(val.getUniversalDeviceIdentifier(), new SoftReference<DeviceIcon>(dicon));
                    }
                    
                    if(null != di) {
                        String s = di.manufacturer.equals(di.model) ? di.manufacturer : (di.manufacturer + " " + di.model);
                        lbl.update(val, dicon, s, val.getNumeric(), val.getSampleInfo(), val.getValueMsBelowLow(), val.getValueMsAboveHigh());
                    } else {
                        lbl.update(val, dicon, "", val.getNumeric(), val.getSampleInfo(), val.getValueMsBelowLow(), val.getValueMsAboveHigh());
                    }

                }
            }
        }
    }