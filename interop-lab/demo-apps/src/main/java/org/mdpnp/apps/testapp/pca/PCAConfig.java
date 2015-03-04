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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.MyInfusionStatus;
import org.mdpnp.apps.testapp.MyInfusionStatusItems;
import org.mdpnp.apps.testapp.MyInfusionStatusListCell;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelListener;
import org.mdpnp.rtiapi.data.InfusionStatusInstanceModel;

import com.rti.dds.infrastructure.InstanceHandle_t;

/**
 * @author Jeff Plourde
 *
 */
public class PCAConfig implements VitalModelListener /*, ListDataListener*/ {

    @FXML protected ListView<MyInfusionStatus> pumpList;
    @FXML protected TextArea warningStatus;
    @FXML protected ComboBox<Integer> warningsToAlarm;
    @FXML protected ComboBox<VitalSign> vitalSigns;
    
    
//    JCheckBox configureModeBox = new JCheckBox("Configuration Mode");
//    JPanel configurePanel = new JPanel();
    
    private ice.InfusionObjectiveDataWriter objectiveWriter;

    public PCAConfig set(ScheduledExecutorService executor, ice.InfusionObjectiveDataWriter objectiveWriter, DeviceListModel deviceListModel) {
        this.objectiveWriter = objectiveWriter;
        pumpList.setCellFactory(new Callback<ListView<MyInfusionStatus>, ListCell<MyInfusionStatus>>() {

            @Override
            public ListCell<MyInfusionStatus> call(ListView<MyInfusionStatus> param) {
                return new MyInfusionStatusListCell(deviceListModel);
            }
            
        });
        List<Integer> values = new ArrayList<Integer>();
        for (int i = 0; i < VitalSign.values().length; i++) {
            values.add(i + 1);
        }
        warningsToAlarm.setItems(FXCollections.observableList(values));
        vitalSigns.setItems(FXCollections.observableArrayList(VitalSign.values()));
        
        pumpList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MyInfusionStatus>() {

            @Override
            public void changed(ObservableValue<? extends MyInfusionStatus> observable, MyInfusionStatus oldValue, MyInfusionStatus newValue) {
                vitalChanged(model, null);
              if (null == newValue) {
//                  pumpProgress.setPopulated(false);
              } else {
//                  contentsChanged(null);
              }
            }
            
        });
        
        return this;
    }
    
    public void warningsToAlarmSet(ActionEvent evt) {
        model.setCountWarningsBecomeAlarm(warningsToAlarm.getSelectionModel().getSelectedItem());
    }
    
    public void pumpProgressClicked(MouseEvent evt) {
        model.resetInfusion();
    }
    
    public void addVitalSign(ActionEvent evt) {
        ((VitalSign) vitalSigns.getSelectionModel().getSelectedItem()).addToModel(model);
    }
    
    public PCAConfig() {
//        configureModeBox.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                configurePanel.setVisible(configureModeBox.isSelected());
//                for (Component c : vitalsPanel.getComponents()) {
//                    if (c instanceof JVital) {
//
//                        ((JVital) c).setShowConfiguration(configureModeBox.isSelected());
//                    }
//                }
//            }
//
//        });


        buildGUI();
    }

    private void buildGUI() {


//        gbc.gridwidth = 3;
//        add(configureModeBox, gbc);
//
//        gbc.gridy++;
//        gbc.gridwidth = 4;
//
//        add(vitalsPanel, gbc);
//        // gbcVitalsStart = (GridBagConstraints) gbc.clone();
//
//        gbc.gridy++;
//        configurePanel.setVisible(configureModeBox.isSelected());
//        add(configurePanel, gbc);
    }

    private VitalModel model;
//    private InfusionStatusInstanceModel pumpModel;

    protected void updateVitals() {
//        if (SwingUtilities.isEventDispatchThread()) {
//            _updateVitals();
//        } else {
//            SwingUtilities.invokeLater(new Runnable() {
//                public void run() {
//                    _updateVitals();
//                    validate();
//                    // repaint();
//                }
//            });
//        }
    }

//    private final JProgressAnimation2 pumpProgress;

//    private final JPanel vitalsPanel = new JPanel();

    protected void _updateVitals() {
//
//        Map<Vital, JVital> existentJVitals = new HashMap<Vital, JVital>();
//
//        for (Component c : vitalsPanel.getComponents()) {
//            if (c instanceof JVital) {
//                vitalsPanel.remove(c);
//                existentJVitals.put(((JVital) c).getVital(), (JVital) c);
//            }
//        }
//        // remove(configurePanel);
//
//        // GridBagConstraints gbc = (GridBagConstraints) gbcVitalsStart.clone();
//
//        final VitalModel model = this.model;
//        if (model != null) {
//            vitalsPanel.setLayout(new GridLayout(model.getCount(), 1));
//            for (int i = 0; i < model.getCount(); i++) {
//                final Vital vital = model.getVital(i);
//
//                JVital jVital = existentJVitals.containsKey(vital) ? existentJVitals.remove(vital) : new JVital(vital);
//                jVital.setShowConfiguration(configureModeBox.isSelected());
//
//                vitalsPanel.add(jVital);
//                jVital.setOpaque(true);
//                // gbc.gridy++;
//            }
//            // gbc.weighty = 0.1;
//            // configurePanel.setVisible(configureModeBox.isSelected());
//            // add(configurePanel, gbc);
//
//        }
    }

    public void setModel(VitalModel model, InfusionStatusInstanceModel pumpModel) {
        String selectedUdi = null;
        MyInfusionStatus selected = pumpList.getSelectionModel().getSelectedItem();
        if (null != selected) {
            selectedUdi = selected.getUnique_device_identifier();
        }
        ObservableList<MyInfusionStatus> items;
        if(pumpModel == null) {
            items = FXCollections.observableArrayList();
        } else {
            items = new MyInfusionStatusItems().setModel(pumpModel).getItems();
        }
        pumpList.setItems(items);
        if (null != selectedUdi) {
            for (int i = 0; i < pumpList.getItems().size(); i++) {
                MyInfusionStatus status = pumpList.getItems().get(i);
                if (selectedUdi.equals(status.getUnique_device_identifier())) {
                    pumpList.getSelectionModel().select(status);
                }
            }
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
            warningsToAlarm.getSelectionModel().select(model.getCountWarningsBecomeAlarm());
            MyInfusionStatus p = pumpList.getSelectionModel().getSelectedItem();

            if (model.isInfusionStopped()) {
                if (null != p) {
                    setStop(p, true);
                }
//                pumpProgress.setInterlockText(model.getInterlockText());
            } else {
                if (null != p) {
                    setStop(p, false);
                }
//                pumpProgress.setInterlockText(null);
            }

            switch (model.getState()) {
            case Alarm:
//                warningStatus.setBackground(new Background(Color.RED));
                break;
            case Warning:
//                warningStatus.setBackground(Color.YELLOW);
                break;
            case Normal:
//                warningStatus.setBackground(getBackground());
                break;
            }
            warningStatus.setText(model.getWarningText());
        }
        if (vital != null) {
//            for (Component c : vitalsPanel.getComponents()) {
//                if (c instanceof JVital && ((JVital) c).getVital().equals(vital)) {
//                    ((JVital) c).updateData();
//                    return;
//                }
//            }
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

//    @Override
//    public void intervalAdded(ListDataEvent e) {
//        vitalChanged(this.model, null);
//    }
//
//    @Override
//    public void intervalRemoved(ListDataEvent e) {
//        vitalChanged(this.model, null);
//    }
//
//    @Override
//    public void contentsChanged(ListDataEvent e) {
//        ice.InfusionStatus status = pumpList.getSelectedValue();
//        if(null != status) {
//            if (status.infusionActive) {
//                pumpProgress.start(status.drug_name, status.volume_to_be_infused_ml,
//                        status.infusion_duration_seconds, status.infusion_fraction_complete);
//            } else {
//                pumpProgress.stop();
//            }
//        }
//    }

    public void setStop(MyInfusionStatus status, boolean stop) {
        ice.InfusionObjective obj = new ice.InfusionObjective();
        obj.requestor = "ME";
        obj.unique_device_identifier = status.getUnique_device_identifier();
        obj.stopInfusion = stop;
        objectiveWriter.write(obj, InstanceHandle_t.HANDLE_NIL);
    }
}
