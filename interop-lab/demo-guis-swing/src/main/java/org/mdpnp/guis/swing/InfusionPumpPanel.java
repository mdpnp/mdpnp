/*******************************************************************************
 * Copyright (c) 2017, MD PnP Program
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
package org.mdpnp.guis.swing;

import ice.InfusionStatus;
import ice.InfusionStatusDataReader;

import java.awt.GridLayout;
import java.util.Set;

import javax.swing.JLabel;

import org.mdpnp.rtiapi.data.DeviceDataMonitor;
import org.mdpnp.rtiapi.data.ReaderInstanceModel;
import org.mdpnp.rtiapi.data.InstanceModelListener;

import com.rti.dds.subscription.SampleInfo;

/**
 * @author Jeff Plourde
 *
 */
public class InfusionPumpPanel extends DevicePanel {

	private final JLabel active = new JLabel(), drugName = new JLabel(), drugMass = new JLabel(),
			solutionVolume = new JLabel(), vtbiMl = new JLabel(), durationSec = new JLabel(),
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

	public static boolean supported(Set<String> numerics) {
		return false;
	}

	@Override
	public void set(DeviceDataMonitor deviceMonitor) {
		super.set(deviceMonitor);
		deviceMonitor.getInfusionStatusModel().iterateAndAddListener(infusionStatusListener);
	}

	@Override
	public void destroy() {
		super.destroy();
		deviceMonitor.getInfusionStatusModel().removeListener(infusionStatusListener);
	}

	private final InstanceModelListener<ice.InfusionStatus, ice.InfusionStatusDataReader> infusionStatusListener = new InstanceModelListener<ice.InfusionStatus, ice.InfusionStatusDataReader>() {

		@Override
		public void instanceAlive(ReaderInstanceModel<InfusionStatus, InfusionStatusDataReader> model,
				InfusionStatusDataReader reader, InfusionStatus data, SampleInfo sampleInfo) {

		}

		@Override
		public void instanceNotAlive(ReaderInstanceModel<InfusionStatus, InfusionStatusDataReader> model,
				InfusionStatusDataReader reader, InfusionStatus keyHolder, SampleInfo sampleInfo) {

		}

		@Override
		public void instanceSample(ReaderInstanceModel<InfusionStatus, InfusionStatusDataReader> model,
				InfusionStatusDataReader reader, InfusionStatus data, SampleInfo sampleInfo) {
			active.setText(Boolean.toString(data.infusionActive));
			drugMass.setText(Integer.toString(data.drug_mass_mcg) + " mcg");
			drugName.setText(data.drug_name);
			durationSec.setText(Integer.toString(data.infusion_duration_seconds) + " seconds");
			fracComplete.setText(Integer.toString((int) (data.infusion_fraction_complete)) + "%");
			solutionVolume.setText(Integer.toString(data.solution_volume_ml) + " mL");
			vtbiMl.setText(Integer.toString(data.volume_to_be_infused_ml) + " mL");
		}
	};

}
