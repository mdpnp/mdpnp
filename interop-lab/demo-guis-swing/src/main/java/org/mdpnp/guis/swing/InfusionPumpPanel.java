package org.mdpnp.guis.swing;

import ice.InfusionStatus;
import ice.Numeric;
import ice.SampleArray;

import java.awt.GridLayout;
import java.util.Set;

import javax.swing.JLabel;

import com.rti.dds.subscription.SampleInfo;

public class InfusionPumpPanel extends DevicePanel {

    private final JLabel active = new JLabel(), drugName = new JLabel(),
           drugMass = new JLabel(), solutionVolume = new JLabel(),
           vtbiMl = new JLabel(), durationSec = new JLabel(),
           fracComplete = new JLabel();


    public InfusionPumpPanel() {
        super(new GridLayout(7, 2));
        add(new JLabel("Active"));
        add(active);
        add(new JLabel("Drug Name"));
        add(drugName);
        add(new JLabel("Drug Mass (mcg)"));
        add(drugMass);
        add(new JLabel("Solution Volume (mL)"));
        add(solutionVolume);
        add(new JLabel("VTBI (mL)"));
        add(vtbiMl);
        add(new JLabel("Duration (seconds)"));
        add(durationSec);
        add(new JLabel("Percent complete"));
        add(fracComplete);
    }

    @Override
    public void numeric(Numeric numeric, String metric_id, SampleInfo sampleInfo) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sampleArray(SampleArray sampleArray, String metric_id, SampleInfo sampleInfo) {
        // TODO Auto-generated method stub

    }

    public static boolean supported(Set<String> numerics) {
        return false;
    }

    @Override
    public void infusionStatus(InfusionStatus infusionStatus, SampleInfo sampleInfo) {
        if(aliveAndValidData(sampleInfo)) {
            active.setText(Boolean.toString(infusionStatus.infusionActive));
            drugMass.setText(Integer.toString(infusionStatus.drug_mass_mcg) + " mcg");
            drugName.setText(infusionStatus.drug_name);
            durationSec.setText(Integer.toString(infusionStatus.infusion_duration_seconds)+ " seconds");
            fracComplete.setText(Integer.toString((int)(100f * infusionStatus.infusion_fraction_complete))+"%");
            solutionVolume.setText(Integer.toString(infusionStatus.solution_volume_ml) + " mL");
            vtbiMl.setText(Integer.toString(infusionStatus.volume_to_be_infused_ml)+" mL");
        }
    }

}
