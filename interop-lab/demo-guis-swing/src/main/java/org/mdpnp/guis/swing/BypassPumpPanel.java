/*******************************************************************************
 * Copyright (c) 2018, MD PnP Program
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


import ice.BypassStatus;
import ice.BypassStatusDataReader;

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
public class BypassPumpPanel extends DevicePanel {

	private final JLabel active = new JLabel(), flowRate = new JLabel(), pumpSpeed = new JLabel(),
			bloodTemp = new JLabel(), bloodPress = new JLabel(), bypassedVol = new JLabel(), bypassTime = new JLabel();

	public BypassPumpPanel() {
		super(new GridLayout(7, 2));
		add(new JLabel("Active"), 0, 0);
		add(active, 1, 0);
		add(new JLabel("Flow Rate"), 0, 1);
		add(flowRate, 1, 1);
		add(new JLabel("Pump Speed"), 0, 2);
		add(pumpSpeed, 1, 2);
		add(new JLabel("Blood Temperature"), 0, 3);
		add(bloodTemp, 1, 3);
		add(new JLabel("Blood Pressure"), 0, 4);
		add(bloodPress, 1, 4);
		add(new JLabel("Volume Bypassed"), 0, 5);
		add(bypassedVol, 1, 5);
		add(new JLabel("Time on Bypass"), 0, 6);
		add(bypassTime, 1, 6);
	}

	public static boolean supported(Set<String> numerics) {
		return false;
	}

	@Override
	public void set(DeviceDataMonitor deviceMonitor) {
		super.set(deviceMonitor);
		deviceMonitor.getBypassStatusModel().iterateAndAddListener(bypassStatusListener);
	}

	@Override
	public void destroy() {
		super.destroy();
		deviceMonitor.getBypassStatusModel().removeListener(bypassStatusListener);
	}

	private final InstanceModelListener<ice.BypassStatus, ice.BypassStatusDataReader> bypassStatusListener = new InstanceModelListener<ice.BypassStatus, ice.BypassStatusDataReader>() {

		@Override
		public void instanceAlive(ReaderInstanceModel<BypassStatus, BypassStatusDataReader> model,
				BypassStatusDataReader reader, BypassStatus data, SampleInfo sampleInfo) {

		}

		@Override
		public void instanceNotAlive(ReaderInstanceModel<BypassStatus, BypassStatusDataReader> model,
				BypassStatusDataReader reader, BypassStatus keyHolder, SampleInfo sampleInfo) {

		}

		@Override
		public void instanceSample(ReaderInstanceModel<BypassStatus, BypassStatusDataReader> model,
				BypassStatusDataReader reader, BypassStatus data, SampleInfo sampleInfo) {
			final String DEGREE = "\u00b0";
			active.setText(Boolean.toString(data.bypassActive));
			flowRate.setText(Double.toString(data.bypass_flow_lmin) + " L/min");
			pumpSpeed.setText(Double.toString(data.bypass_speed_rpm) + " rpms");
			bloodTemp.setText(Double.toString(data.blood_temp_celsius) + DEGREE + "C");
			bloodPress.setText(Double.toString((int) (data.blood_press_mmhg)) + " mmHg");
			bypassedVol.setText(Double.toString(data.volume_bypassed_ml) + " L");
			bypassTime.setText(Integer.toString(data.bypass_duration_seconds) + " seconds");
		}
	};

}
