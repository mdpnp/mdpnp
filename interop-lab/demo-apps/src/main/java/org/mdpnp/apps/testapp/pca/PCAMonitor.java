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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;

import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.vital.VitalListModelAdapterImpl;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class PCAMonitor extends JPanel implements VitalModelListener {
    @SuppressWarnings("rawtypes")
    private final JList list;
    private final JProgressAnimation pumpProgress;
    private final JTextArea pump = new JTextArea(" ") {
        @Override
        public void setOpaque(boolean isOpaque) {
            super.setOpaque(true);
        };
    };
    private final JTextArea warnings = new JTextArea(" ") {
        public void setOpaque(boolean isOpaque) {
            super.setOpaque(true);
        };
    };

    protected final StringBuilder fastStatusBuilder = new StringBuilder();
    private VitalModel model;
    @SuppressWarnings("rawtypes")
    private final static ListModel EMPTY_MODEL = new DefaultListModel();

    @SuppressWarnings("unchecked")
    public void setModel(VitalModel model) {
        if (this.model != null) {
            this.model.removeListener(this);
            this.list.setModel(EMPTY_MODEL);
        }
        this.model = model;
        if (this.model != null) {
            this.model.addListener(this);
            this.list.setModel(new VitalListModelAdapterImpl(this.model));
        }
    }

    public VitalModel getModel() {
        return model;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PCAMonitor(DeviceListModel deviceListModel, ScheduledExecutorService executor) {
        super(new BorderLayout());

        JPanel header = new JPanel(new GridLayout(1, 2, 10, 10));
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        add(panel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);
        JLabel l;
        header.add(l = new JLabel("Relevant Vitals"));
        l.setFont(l.getFont().deriveFont(20f));
        header.add(l = new JLabel("Infusion Status (Symbiq)"));
        l.setFont(l.getFont().deriveFont(20f));

        list = new JList(EMPTY_MODEL);

        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        list.setCellRenderer(new VitalsListCellRenderer(deviceListModel));

        JPanel panel2 = new JPanel(new GridLayout(2, 1, 10, 10));
        // pump.setBackground(Color.green);
        // warnings.setBackground(Color.white);
        JPanel panel4 = new JPanel(new GridLayout(1, 2, 0, 0));
        panel4.add(pumpProgress = new JProgressAnimation(executor));
        pumpProgress.setBackground(new Color(1f, 1f, 1f, .5f));
        pumpProgress.setOpaque(false);
        panel4.add(new JScrollPane(pump));
        panel2.add(panel4);
        JPanel panel3 = new JPanel(new BorderLayout());
        panel3.add(l = new JLabel("Informational Messages"), BorderLayout.NORTH);
        l.setFont(l.getFont().deriveFont(20f));
        panel3.add(new JScrollPane(warnings), BorderLayout.CENTER);
        panel2.add(panel3);
        panel.add(panel2);

        JButton resetButton = new JButton("Reset Demo");
        resetButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                model.resetInfusion();
            }

        });
        // resetButton.setHorizontalAlignment(SwingConstants.RIGHT);
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(resetButton, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);
        pump.setFont(pump.getFont().deriveFont(40f));
        pump.setEditable(false);
        pump.setLineWrap(true);
        pump.setWrapStyleWord(true);

        warnings.setFont(pump.getFont());
        warnings.setEditable(false);
        warnings.setLineWrap(true);
        warnings.setWrapStyleWord(true);
    }

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(PCAPanel.class);

    public void reflectState() {
        VitalModel model = this.model;
        if (null == model) {
            return;
        }

        if (model.isInfusionStopped()) {
            pumpProgress.stop();
            pump.setBackground(Color.red);
            pump.setText(model.getInterlockText());
        } else {
            pumpProgress.start();
            pump.setBackground(Color.green);
            pump.setText(model.getInterlockText());
        }

        switch (model.getState()) {
        case Alarm:
            warnings.setBackground(Color.red);
            break;
        case Normal:
            warnings.setBackground(Color.white);
            break;
        case Warning:
            warnings.setBackground(Color.yellow);
            break;
        default:
        }
        warnings.setText(model.getWarningText());
    }

    @Override
    public void vitalChanged(VitalModel model, org.mdpnp.apps.testapp.vital.Vital vital) {
        reflectState();
    }

    @Override
    public void vitalRemoved(VitalModel model, org.mdpnp.apps.testapp.vital.Vital vital) {
        reflectState();
    }

    @Override
    public void vitalAdded(VitalModel model, org.mdpnp.apps.testapp.vital.Vital vital) {
        reflectState();
    }
}
