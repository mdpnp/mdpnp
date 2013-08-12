package org.mdpnp.apps.testapp.pca;

import ice.Numeric;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
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
    private static class JFillLabel extends JLabel {
        public JFillLabel() {
            enableEvents(ComponentEvent.COMPONENT_EVENT_MASK);
        }
        private final Dimension size = new Dimension();
        private void adjustWidth() {
//            String s = getText();
//            FontMetrics fm = getFontMetrics(getFont());
//            getSize(size);
//            while(fm.getHeight() < size.height) {
//                setFont(getFont().deriveFont(getFont().getSize2D() + 1f));
//                fm = getFontMetrics(getFont());
//            }
//            while(fm.getHeight() > size.height) {
//                setFont(getFont().deriveFont(getFont().getSize2D() - 1f));
//                fm = getFontMetrics(getFont());
//            }
//            int w = fm.stringWidth(s);
//            setSize(w+5, size.height);
        }
        
        @Override
        protected void processComponentEvent(ComponentEvent e) {
            switch(e.getID()) {
            case ComponentEvent.COMPONENT_RESIZED:
                adjustWidth();
                break;
            }
            super.processComponentEvent(e);
        }
        
        @Override
        public void setText(String text) {
            super.setText(text);
            adjustWidth();
        }
        
    }
    
    
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
        
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0);
        
        
        gbc.gridheight = 3;
        add(value, gbc);
        gbc.gridx++;
        
        add(icon, gbc);
        
        gbc.gridx++;
        gbc.gridheight = 1;
        
        add(deviceName, gbc);
        gbc.gridy++;
        
        valueChart.setMinimumSize(new Dimension(100, 20));
        valueChart.setPreferredSize(new Dimension(100, 20));
        gbc.insets = new Insets(1,1,1,1);
//        valueChart.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        add(valueChart, gbc);
        
        gbc.gridy++;
        add(time, gbc);
        
        gbc.gridy = 1;
        gbc.gridx++;
        
        add(valueMsAbove, gbc);
        
        gbc.gridy++;
        add(valueMsBelow, gbc);
        


        
        
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
            this.value.setText(Integer.toString((int)numeric.value));
            this.time.setText(timeFormat.format(date));
        } else {
            this.value.setText("");
            this.time.setText("");
        }
        this.valueMsAbove.setText(0L == valueMsAboveHigh ? "" : Long.toString(valueMsAboveHigh/1000L));
        this.valueMsBelow.setText(0L == valueMsBelowLow ? "" : Long.toString(valueMsBelowLow / 1000L));
        this.valueChart.setValue(value);
        
    }
}
