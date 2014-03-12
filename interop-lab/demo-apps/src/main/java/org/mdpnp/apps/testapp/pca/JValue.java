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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.mdpnp.apps.testapp.vital.JValueChart;
import org.mdpnp.apps.testapp.vital.Value;

import com.rti.dds.subscription.SampleInfo;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class JValue extends JComponent {
    private final JLabel icon = new JLabel();
    private final JLabel deviceName = new JLabel();

    private final JLabel time = new JLabel();
    private final JLabel valueMsAbove = new JLabel();
    private final JLabel valueMsBelow = new JLabel();
    private final JValueChart valueChart = new JValueChart(null);

    private final JLabel value = new JLabel();

    public JValue() {

        icon.setOpaque(false);
        deviceName.setOpaque(false);
        value.setOpaque(false);
        valueMsAbove.setOpaque(false);
        valueMsBelow.setOpaque(false);
        time.setOpaque(false);
        value.setFont(Font.decode("verdana-60"));
        value.setForeground(Color.blue);
        deviceName.setFont(value.getFont().deriveFont(16f));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0,
                0), 0, 0);

        gbc.gridheight = 3;
        add(icon, gbc);

        gbc.gridx++;
        gbc.gridheight = 1;

        add(deviceName, gbc);
        gbc.gridy++;

        valueChart.setMinimumSize(new Dimension(150, 20));
        valueChart.setPreferredSize(new Dimension(150, 20));
        gbc.insets = new Insets(1, 1, 1, 1);
        add(valueChart, gbc);

        gbc.gridy++;
        add(time, gbc);

        gbc.gridy = 1;
        gbc.gridx++;

        add(valueMsAbove, gbc);

        gbc.gridy++;
        add(valueMsBelow, gbc);

        gbc.gridx++;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        add(value, gbc);

    }

    private final Date date = new Date();
    private final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");

    public void update(Value value, Icon icon, String deviceName, ice.Numeric numeric, SampleInfo si, long valueMsBelowLow, long valueMsAboveHigh) {
        // if(value.isAtOrOutsideOfBounds()) {
        // setBackground(Color.yellow);
        // } else if(value.isAtOrOutsideOfCriticalBounds()) {
        // setBackground(Color.red);
        // } else {
        // setBackground(getParent().getBackground());
        // }
        this.icon.setIcon(icon);

        this.deviceName.setText(deviceName);
        if (si != null && numeric != null) {
            date.setTime(1000L * si.source_timestamp.sec + si.source_timestamp.nanosec / 1000000L);
            String s = Integer.toString(Math.round(numeric.value));
            while (s.length() < 3) {
                s = " " + s;
            }
            this.value.setText(s);

            this.time.setText(timeFormat.format(date));
        } else {
            this.value.setText("   ");
            this.time.setText("");
        }
        this.valueMsAbove.setText(0L == valueMsAboveHigh ? "" : Long.toString(valueMsAboveHigh / 1000L));
        this.valueMsBelow.setText(0L == valueMsBelowLow ? "" : Long.toString(valueMsBelowLow / 1000L));
        this.valueChart.setValue(value);

    }
}
