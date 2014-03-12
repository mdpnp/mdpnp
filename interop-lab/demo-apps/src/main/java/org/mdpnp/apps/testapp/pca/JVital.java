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
package org.mdpnp.apps.testapp.pca;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

@SuppressWarnings("serial")
public final class JVital extends JPanel {
    private final Vital vital;
    private final JMultiSlider slider, slider2;

    private final JLabel name = new JLabel();
    private final JPanel vitalValues = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final JButton deleteButton = new JButton("Remove");
    private final JCheckBox ignoreZeroBox = new JCheckBox("Ignore Zero");
    private final JCheckBox requiredBox = new JCheckBox("Required");

    // private JLabel limitsLabel = new JLabel("Limits:");
    // private JLabel configureLabel = new JLabel("Configure:");

    // private boolean showConfiguration = false;

    public Vital getVital() {
        return vital;
    }

    public JVital(final Vital vital) {
        setBorder(null);
        // setBackground(Color.black);
        // setForeground(Color.green);
        this.vital = vital;
        // name.setWrapStyleWord(true);
        // name.setEditable(false);
        // name.setLineWrap(true);
        String lbl = vital.getLabel();
        if (lbl.contains(" ")) {
            lbl = lbl.replaceAll("\\ +", "<br/>");
            lbl = lbl + " (" + vital.getUnits() + ")";
            lbl = "<html>" + lbl + "</html>";
            // System.out.println("Label:"+lbl);
        } else {
            lbl = lbl + " (" + vital.getUnits() + ")";
        }
        // lbl = lbl + " (" + vital.getUnits() + ")";
        Font font = Font.decode("verdana-20");
        name.setText(lbl);
        ;
        name.setFont(Font.decode("verdana-30"));
        VitalBoundedRangeMulti range = new VitalBoundedRangeMulti(vital);
        slider = new JMultiSlider(range);
        slider.setFont(font);
        // slider.setPreferredSize(new Dimension(100, Integer.MAX_VALUE));
        // slider.setMaximumSize(new Dimension(100, Integer.MAX_VALUE));
        // slider.setSize(100, slider.getPreferredSize().height);
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

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,
                0, 0, 0), 0, 0);

        // FIRST ROW
        gbc.gridheight = 3;
        add(name, gbc);

        gbc.gridheight = 1;
        gbc.weightx = 10.0;
        gbc.gridx++;
        gbc.gridwidth = 3;
        add(slider, gbc);

        gbc.gridy++;
        add(slider2, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        // gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;

        add(ignoreZeroBox, gbc);

        gbc.gridx++;
        add(requiredBox, gbc);

        gbc.gridx++;
        add(deleteButton, gbc);

        gbc.weightx = 10.0;
        // DATA area
        gbc.gridy = 0;
        gbc.gridx++;
        gbc.gridheight = 3;
        // add(new JLabel("Sources:"), gbc);

        gbc.fill = GridBagConstraints.NONE;
        // gbc.gridx++;
        // gbc.gridwidth = 3;

        add(vitalValues, gbc);

        setShowConfiguration(false);

        refreshContents();
    }

    public void setShowConfiguration(boolean showConfiguration) {
        slider.setDrawThumbs(showConfiguration);
        // slider.setVisible(showConfiguration);
        slider2.setVisible(showConfiguration);
        ignoreZeroBox.setVisible(showConfiguration);
        requiredBox.setVisible(showConfiguration);
        deleteButton.setVisible(showConfiguration);

        validate();

    }

    public void refreshContents() {
        Runnable r = new Runnable() {
            public void run() {
                _refreshContents();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
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
        updateData();
    }

    // @Override
    // public void stateChanged(ChangeEvent e) {
    // float range = vital.getMaximum() - vital.getMinimum();
    // int incr = (int) (range / 5f);
    // if(incr != slider.getMajorTickSpacing()) {
    // slider.setLabelTable(slider.createStandardLabels(incr));
    // slider.setMajorTickSpacing(incr);
    // }
    // }

    public void updateData() {
        final int N = vital.getValues().isEmpty() ? 1 : vital.getValues().size();
        ignoreZeroBox.setSelected(vital.isIgnoreZero());
        requiredBox.setSelected(vital.isNoValueWarning());
        // name.setText(vital.getLabel());
        try {
            if (N != vitalValues.getComponentCount()) {
                Runnable r = new Runnable() {
                    public void run() {

                        while (vitalValues.getComponentCount() < N) {
                            for (int i = 0; i < (N - vitalValues.getComponentCount()); i++) {
                                JValue val = new JValue();
                                val.setBorder(new LineBorder(Color.black));
                                vitalValues.add(val);
                            }
                            validate();
                        }
                        while (N < vitalValues.getComponentCount()) {
                            for (int i = 0; i < (vitalValues.getComponentCount() - N); i++) {
                                vitalValues.remove(0);
                            }
                            validate();
                        }

                    }
                };
                if (SwingUtilities.isEventDispatchThread()) {
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

        if (vital.getValues().isEmpty()) {
            // ((JLabel)vitalValues.getComponent(0)).setForeground(Color.yellow);
            // ((JLabel)vitalValues.getComponent(0)).setBackground(Color.yellow);
            ((JValue) vitalValues.getComponent(0)).update(null, null, "<NO SOURCES>", null, null, 0L, 0L);
        } else {
            for (int i = 0; i < vital.getValues().size(); i++) {
                Value val = vital.getValues().get(i);
                JValue lbl = (JValue) vitalValues.getComponent(i);

                ice.DeviceIdentity di = vital.getParent().getDeviceIdentity(val.getUniqueDeviceIdentifier());
                DeviceIcon dicon = vital.getParent().getDeviceIcon(val.getUniqueDeviceIdentifier());
                // dicon = null == dicon ? null : new ScaledDeviceIcon(dicon,
                // 0.5);
                if (null != di) {
                    String s = di.manufacturer.equals(di.model) ? di.manufacturer : (di.manufacturer + " " + di.model);
                    lbl.update(val, dicon, s, val.getNumeric(), val.getSampleInfo(), val.getValueMsBelowLow(), val.getValueMsAboveHigh());
                } else {
                    lbl.update(val, dicon, "", val.getNumeric(), val.getSampleInfo(), val.getValueMsBelowLow(), val.getValueMsAboveHigh());
                }

            }
        }
    }
}
