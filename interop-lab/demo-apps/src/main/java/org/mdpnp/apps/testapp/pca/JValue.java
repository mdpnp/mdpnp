package org.mdpnp.apps.testapp.pca;

import ice.Numeric;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.mdpnp.apps.testapp.vital.JValueChart;
import org.mdpnp.apps.testapp.vital.Value;

import com.rti.dds.subscription.SampleInfo;

public class JValue extends JComponent {
    private final JLabel icon = new JLabel();
    private final JLabel deviceName = new JLabel();
    private final JLabel value = new JLabel();
    private final JLabel valueMsAbove = new JLabel();
    private final JLabel valueMsBelow = new JLabel();
    private final JValueChart valueChart = new JValueChart(null);
    
    public JValue() {
        icon.setOpaque(false);
        deviceName.setOpaque(false);
        value.setOpaque(false);
        valueMsAbove.setOpaque(false);
        valueMsBelow.setOpaque(false);
        
        value.setForeground(Color.blue);
        
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0);
        gbc.gridheight = 2;
        add(icon, gbc);
        
        gbc.gridheight = 1;
        gbc.gridx++;

        add(deviceName, gbc);
        
        gbc.gridy++;
        
        add(value, gbc);
        
        gbc.gridy = 0;
        gbc.gridx++;
        
        add(valueMsAbove, gbc);
        
        gbc.gridy++;
        add(valueMsBelow, gbc);
        
        gbc.gridx++;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        valueChart.setMinimumSize(new Dimension(100, 20));
        valueChart.setPreferredSize(new Dimension(100, 20));
        gbc.insets = new Insets(1,1,1,1);
//        valueChart.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        add(valueChart, gbc);
        
        
    }
    
    private final Date date = new Date();
    private final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    
    public void update(Value value, Icon icon, String deviceName, Numeric numeric, SampleInfo si, long valueMsBelowLow, long valueMsAboveHigh) {
//        if(value.isAtOrOutsideOfBounds()) {
//            setBackground(Color.yellow);
//        } else if(value.isAtOrOutsideOfCriticalBounds()) {
//            setBackground(Color.red);
//        } else {
//            setBackground(getParent().getBackground());
//        }
        this.icon.setIcon(icon);
        this.deviceName.setText(deviceName);
        if(si != null && numeric != null) {
            date.setTime(1000L * si.source_timestamp.sec + si.source_timestamp.nanosec / 1000000L);
            this.value.setText(Integer.toString((int)numeric.value)+" @ "+timeFormat.format(date));
        } else {
            this.value.setText("");
        }
        this.valueMsAbove.setText(0L == valueMsAboveHigh ? "" : Long.toString(valueMsAboveHigh/1000L));
        this.valueMsBelow.setText(0L == valueMsBelowLow ? "" : Long.toString(valueMsBelowLow / 1000L));
        this.valueChart.setValue(value);
        
    }
}
