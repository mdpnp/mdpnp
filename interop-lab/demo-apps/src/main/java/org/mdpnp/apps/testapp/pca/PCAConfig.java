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

import java.awt.BorderLayout;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.DefaultComboBoxModel;
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
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mdpnp.apps.testapp.DeviceListCellRenderer;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelListener;
import org.mdpnp.rtiapi.data.InfusionStatusInstanceModel;

import com.rti.dds.infrastructure.InstanceHandle_t;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class PCAConfig extends JComponent implements VitalModelListener, ListDataListener {

    JCheckBox configureModeBox = new JCheckBox("Configuration Mode");
    JPanel configurePanel = new JPanel();
    
    private final ice.InfusionObjectiveDataWriter objectiveWriter;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PCAConfig(ScheduledExecutorService executor, ice.InfusionObjectiveDataWriter objectiveWriter, DeviceListCellRenderer deviceCellRenderer) {
        setLayout(new GridBagLayout());
        this.objectiveWriter = objectiveWriter;
        pumpProgress = new JProgressAnimation2(executor);
        pumpProgress.setForeground(Color.green);
        // pumpProgress.setBackground(new Color(1f,1f,1f,.5f));
        // pumpProgress.setFont(Font.decode("fixed-20"));

        pumpProgress.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                model.resetInfusion();
                super.mouseClicked(e);
            }
        });

        warningStatus.setEditable(false);
        warningStatus.setLineWrap(true);
        warningStatus.setWrapStyleWord(true);

        Font font = Font.decode("verdana-20");
        warningStatus.setFont(font);

        List<Integer> values = new ArrayList<Integer>();
        for (int i = 0; i < VitalSign.values().length; i++) {
            values.add(i + 1);
        }
        warningsToAlarm.setModel(new DefaultComboBoxModel(values.toArray(new Integer[0])));
        // warningsToAlarm.setSelectedItem((Integer)model.getCountWarningsBecomeAlarm());
        configurePanel.add(warningsToAlarm);
        configurePanel.add(new JLabel("Warnings become an alarm"));
        warningsToAlarm.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                model.setCountWarningsBecomeAlarm((Integer) warningsToAlarm.getSelectedItem());
            }

        });

        final JComboBox vitalSigns = new JComboBox(VitalSign.values());
        configurePanel.add(vitalSigns);

        JButton add = new JButton("Add");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((VitalSign) vitalSigns.getSelectedItem()).addToModel(model);
            }
        });
        configurePanel.add(add);

        configureModeBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                configurePanel.setVisible(configureModeBox.isSelected());
                for (Component c : vitalsPanel.getComponents()) {
                    if (c instanceof JVital) {

                        ((JVital) c).setShowConfiguration(configureModeBox.isSelected());
                    }
                }
            }

        });

        pumpList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                vitalChanged(model, null);
                if (null == pumpList.getSelectedValue()) {
                    pumpProgress.setPopulated(false);
                } else {
                    contentsChanged(null);
                }
            }

        });
        pumpList.setCellRenderer(deviceCellRenderer);
        buildGUI();
    }

    private void buildGUI() {
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0,
                0), 0, 0);

        // ARGH
        // warningStatus.setBackground(Color.white);
        // pumpStatus.setBackground(Color.white);
        // warningStatus.setOpaque(true);
        // pumpStatus.setOpaque(true);

        JPanel pumpPanel = new JPanel(new GridLayout(1, 3));
        JScrollPane scrollPane;

        JPanel panel = new JPanel(new BorderLayout());

        Font font = Font.decode("verdana-20");
        JLabel lbl = new JLabel("Select Infusion Pump");
        lbl.setFont(font);
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(scrollPane = new JScrollPane(pumpList), BorderLayout.CENTER);
        pumpList.setFont(font);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        pumpList.setOpaque(false);
        pumpPanel.add(panel);

        pumpPanel.add(pumpProgress);

        lbl = new JLabel("Informational Messages");
        lbl.setFont(font);
        panel = new JPanel(new BorderLayout());
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(scrollPane = new JScrollPane(warningStatus), BorderLayout.CENTER);
        scrollPane.setBorder(null);
        warningStatus.setFont(font);
        pumpPanel.add(panel);

        gbc.weighty = 5.0;
        add(pumpPanel, gbc);

        gbc.weighty = 1.0;
        gbc.gridy++;

        gbc.gridwidth = 3;
        add(configureModeBox, gbc);

        gbc.gridy++;
        gbc.gridwidth = 4;

        add(vitalsPanel, gbc);
        // gbcVitalsStart = (GridBagConstraints) gbc.clone();

        gbc.gridy++;
        configurePanel.setVisible(configureModeBox.isSelected());
        add(configurePanel, gbc);
    }

    private VitalModel model;
    private InfusionStatusInstanceModel pumpModel;

    protected void updateVitals() {
        if (SwingUtilities.isEventDispatchThread()) {
            _updateVitals();
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    _updateVitals();
                    validate();
                    // repaint();
                }
            });
        }
    }

    private final JProgressAnimation2 pumpProgress;
    private final JList<ice.InfusionStatus> pumpList = new JList<ice.InfusionStatus>();
    private final JTextArea warningStatus = new JTextArea(" ");
    @SuppressWarnings("rawtypes")
    private final JComboBox warningsToAlarm = new JComboBox();
    private final JPanel vitalsPanel = new JPanel();

    protected void _updateVitals() {

        Map<Vital, JVital> existentJVitals = new HashMap<Vital, JVital>();

        for (Component c : vitalsPanel.getComponents()) {
            if (c instanceof JVital) {
                vitalsPanel.remove(c);
                existentJVitals.put(((JVital) c).getVital(), (JVital) c);
            }
        }
        // remove(configurePanel);

        // GridBagConstraints gbc = (GridBagConstraints) gbcVitalsStart.clone();

        final VitalModel model = this.model;
        if (model != null) {
            vitalsPanel.setLayout(new GridLayout(model.getCount(), 1));
            for (int i = 0; i < model.getCount(); i++) {
                final Vital vital = model.getVital(i);

                JVital jVital = existentJVitals.containsKey(vital) ? existentJVitals.remove(vital) : new JVital(vital);
                jVital.setShowConfiguration(configureModeBox.isSelected());

                vitalsPanel.add(jVital);
                jVital.setOpaque(true);
                // gbc.gridy++;
            }
            // gbc.weighty = 0.1;
            // configurePanel.setVisible(configureModeBox.isSelected());
            // add(configurePanel, gbc);

        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void setModel(VitalModel model, InfusionStatusInstanceModel pumpModel) {
        String selectedUdi = null;
        ice.InfusionStatus selected = pumpList.getSelectedValue();
        if (null != selected) {
            selectedUdi = selected.unique_device_identifier;
        }
        pumpList.setModel(null == pumpModel ? new DefaultListModel() : pumpModel);
        if (null != selectedUdi && pumpModel != null) {
            for (int i = 0; i < pumpModel.getSize(); i++) {
                ice.InfusionStatus status = pumpModel.getElementAt(i);
                if (selectedUdi.equals(status.unique_device_identifier)) {
                    pumpList.setSelectedValue(status, true);
                }
            }
        }
        pumpList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        if (this.pumpModel != null) {
            this.pumpModel.removeListDataListener(this);
        }
        this.pumpModel = pumpModel;
        if (this.pumpModel != null) {
            this.pumpModel.addListDataListener(this);
        }

        if (this.model != null) {
            this.model.removeListener(this);
        }
        this.model = model;
        if (this.model != null) {
            this.model.addListener(this);
        }
        updateVitals();
        vitalChanged(this.model, null);
    }

    @Override
    public void vitalChanged(VitalModel model, Vital vital) {
        if (model != null) {
            warningsToAlarm.setSelectedItem((Integer) model.getCountWarningsBecomeAlarm());
            ice.InfusionStatus p = pumpList.getSelectedValue();

            if (model.isInfusionStopped()) {
                if (null != p) {
                    setStop(p, true);
                }
                pumpProgress.setInterlockText(model.getInterlockText());
            } else {
                if (null != p) {
                    setStop(p, false);
                }
                pumpProgress.setInterlockText(null);
            }

            switch (model.getState()) {
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
        if (vital != null) {
            for (Component c : vitalsPanel.getComponents()) {
                if (c instanceof JVital && ((JVital) c).getVital().equals(vital)) {
                    ((JVital) c).updateData();
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
    public void intervalAdded(ListDataEvent e) {
        vitalChanged(this.model, null);
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        vitalChanged(this.model, null);
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        ice.InfusionStatus status = pumpList.getSelectedValue();
        if(null != status) {
            if (status.infusionActive) {
                pumpProgress.start(status.drug_name, status.volume_to_be_infused_ml,
                        status.infusion_duration_seconds, status.infusion_fraction_complete);
            } else {
                pumpProgress.stop();
            }
        }
    }

    public void setStop(ice.InfusionStatus status, boolean stop) {
        ice.InfusionObjective obj = new ice.InfusionObjective();
        obj.requestor = "ME";
        obj.unique_device_identifier = status.unique_device_identifier;
        obj.stopInfusion = stop;
        objectiveWriter.write(obj, InstanceHandle_t.HANDLE_NIL);
    }
}
