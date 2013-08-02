package org.mdpnp.apps.testapp.pca;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.mdpnp.apps.testapp.RangeSlider;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelListener;

@SuppressWarnings("serial")
public class PCAConfig extends JComponent implements VitalModelListener {
    public PCAConfig() {
    }
    private VitalModel model;
    
    protected void updateVitals() {
        setVisible(false);
        removeAll();
        VitalModel model = this.model;
        if(model != null) {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1,1,1,1), 1, 1);
            for(int i = 0; i < model.getCount(); i++) {
                Vital vital = model.getVital(i);
                RangeSlider slider = new RangeSlider((int)vital.getMinimum(), (int) vital.getMaximum());
                slider.setModel(new VitalBoundedRange(vital));
                slider.setPaintLabels(true);
                slider.setPaintTicks(true);
                slider.setMajorTickSpacing(10);
                slider.setLabelTable(slider.createStandardLabels(10));
                gbc.gridx = 0;
                gbc.weightx = 0.8;
                add(new JLabel(vital.getLabel()+" ("+vital.getUnits()+")"), gbc);
                gbc.gridx++;
                gbc.weightx = 1.2;
                add(slider, gbc);
                gbc.gridy++;
            }
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setVisible(true);
            }
        });
        
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
    }
    @Override
    public void vitalChanged(VitalModel model, Vital vital) {
        
    }
    @Override
    public void vitalRemoved(VitalModel model, Vital vital) {
        
    }
    @Override
    public void vitalAdded(VitalModel model, Vital vital) {
        
    }
}
