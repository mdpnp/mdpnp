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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import javax.media.nativewindow.util.Dimension;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black, 1), vital.getLabel() + " ("+vital.getUnits()+")", 0, 0, Font.decode("fixed-20"), getForeground()));
            this.vital = vital;
            this.deviceListModel = deviceListModel;
            VitalBoundedRangleMulti range = new VitalBoundedRangleMulti(vital);
            slider = new JMultiSlider(range);
            slider.setRangeColor(0, Color.red);
            slider.setRangeColor(1, Color.yellow);
            slider.setRangeColor(2, Color.green);
            slider.setOpaque(false);
//            range.addChangeListener(this);
//            slider.setPaintLabels(true);
//            slider.setPaintTicks(true);
//            stateChanged(null);
            
            JButton delete = new JButton("Remove");
            delete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    vital.getParent().removeVital(vital);
                }
            });
            ignoreZero.setOpaque(false);
            ignoreZero.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    vital.setIgnoreZero(ignoreZero.isSelected());
                }
                
            });
            
            required.setOpaque(false);
            required.addActionListener(new ActionListener() {
               @Override
                public void actionPerformed(ActionEvent e) {
                   vital.setNoValueWarning(required.isSelected());
                } 
            });
            
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0);
            
            // FIRST ROW
            gbc.gridx = 0;
            gbc.weightx = 0.1;


            add(new JLabel("Limits:"), gbc);
            gbc.weightx = 10.0;
            gbc.gridx++;
            gbc.gridwidth = 3;
            add(slider, gbc);
            
            // SECOND ROW
            gbc.gridwidth = 1;
//            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 1.0;
            gbc.gridy++;
            gbc.gridx = 0;
            add(new JLabel("Configure:"), gbc);
            gbc.gridx++;
            add(ignoreZero, gbc);
            
            gbc.gridx++;
            add(required, gbc);
            
            gbc.gridx++;
            add(delete, gbc);
            
            // THIRD ROW
            gbc.gridy++;
            gbc.gridx = 0;
            add(new JLabel("Sources:"), gbc);
            
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx++;
            gbc.gridwidth = 3;
            vitalValues = new JPanel(new FlowLayout(FlowLayout.LEFT));
            vitalValues.setOpaque(false);
            updateData();

            add(vitalValues, gbc);
        }

        private final JCheckBox ignoreZero = new JCheckBox("Ignore Zero");
        private final JCheckBox required = new JCheckBox("Required");
//        @Override
//        public void stateChanged(ChangeEvent e) {
//            float range = vital.getMaximum() - vital.getMinimum();
//            int incr  = (int) (range / 5f);
//            if(incr != slider.getMajorTickSpacing()) {
//              slider.setLabelTable(slider.createStandardLabels(incr));
//              slider.setMajorTickSpacing(incr);
//            }
//        }
        
        private final Date date = new Date();
        private final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        
        public void updateData() {
            final int N = vital.getValues().isEmpty() ? 1 : vital.getValues().size();
            ignoreZero.setSelected(vital.isIgnoreZero());
            required.setSelected(vital.isNoValueWarning());
            try {
                if(N != vitalValues.getComponentCount()) {
                    Runnable r = new Runnable() {
                        public void run() {
                            
                            while(vitalValues.getComponentCount() < N) {
                                for(int i = 0; i < (N - vitalValues.getComponentCount()); i++) {
                                    JLabel lbl = new JLabel();
//                                    lbl.setFont(Font.decode("fixed-20"));
//                                    lbl.setMaximumSize(new java.awt.Dimension(100, 30));
//                                    lbl.setBorder(BorderFactory.createTitledBorder("  "));
                                    lbl.setBorder(BorderFactory.createLineBorder(Color.black));
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
//                ((JLabel)vitalValues.getComponent(0)).setForeground(Color.yellow);
//                ((JLabel)vitalValues.getComponent(0)).setBackground(Color.yellow);
                ((JLabel)vitalValues.getComponent(0)).setText("NONE");
            } else {
                for(int i = 0; i < vital.getValues().size(); i++) {
                    Value val = vital.getValues().get(i);
                    JLabel lbl = (JLabel) vitalValues.getComponent(i);
                    
//                    lbl.setForeground(val.isAtOrOutsideOfBounds() ? Color.yellow : Color.green);
//                    lbl.setText(Integer.toString((int)val.getNumeric().value));
                    Device device = deviceListModel.getByUniversalDeviceIdentifier(val.getUniversalDeviceIdentifier());
                    date.setTime(val.getSampleInfo().source_timestamp.sec*1000L + val.getSampleInfo().source_timestamp.nanosec / 1000000L);
                    if(null != device) {
//                        if(lbl.getBorder() instanceof TitledBorder) {
//                            ((TitledBorder)lbl.getBorder()).setTitle(device.getMakeAndModel());
//                        }
                        lbl.setText("<html>"+device.getMakeAndModel()+"<br/>"+Integer.toString((int)val.getNumeric().value)+" @ "+timeFormat.format(date));
                        if(!(lbl.getIcon() instanceof ScaledDeviceIcon) ||
                           !((ScaledDeviceIcon)lbl.getIcon()).getSource().equals(device.getIcon())) {
                            lbl.setIcon(new ScaledDeviceIcon(device.getIcon(), 0.5));
                        }
                        
                        
                    } else {
//                        if(lbl.getBorder() instanceof TitledBorder) {
//                            ((TitledBorder)lbl.getBorder()).setTitle("");
//                        }
                        lbl.setText(Integer.toString((int)val.getNumeric().value)+ " @ "+timeFormat.format(date));
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
                JVital jVital = new JVital(vital, deviceListModel);
                add(jVital, gbc);
                jVital.setOpaque(true);
                jVital.setBackground(Color.blue);
                gbc.gridy++;
            }
            JPanel panel = new JPanel();
            panel.setOpaque(false);
            
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
            gbc.weighty = 1.0;
            
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
            warningStatus.setOpaque(true);
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
