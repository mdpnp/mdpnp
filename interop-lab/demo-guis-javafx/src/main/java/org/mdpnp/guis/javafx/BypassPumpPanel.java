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
package org.mdpnp.guis.javafx;

import ice.BypassStatus;
import ice.BypassStatusDataReader;

import java.util.Set;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import org.mdpnp.rtiapi.data.DeviceDataMonitor;
import org.mdpnp.rtiapi.data.ReaderInstanceModel;
import org.mdpnp.rtiapi.data.InstanceModelListener;

import com.rti.dds.subscription.SampleInfo;

public class BypassPumpPanel extends DevicePanel {

	private final Label active = new Label(), flowRate = new Label(), pumpSpeed = new Label(), bloodTemp = new Label(),
			bloodPress = new Label(), bypassedVol = new Label(), bypassTime = new Label();

	public BypassPumpPanel() {
		GridPane gridPane = new GridPane();
		gridPane.add(new Label("Active"), 0, 0);
		gridPane.add(active, 1, 0);
		gridPane.add(new Label("Flow Rate"), 0, 1);
		gridPane.add(flowRate, 1, 1);
		gridPane.add(new Label("Pump Speed"), 0, 2);
		gridPane.add(pumpSpeed, 1, 2);
		gridPane.add(new Label("Blood Temperature"), 0, 3);
		gridPane.add(bloodTemp, 1, 3);
		gridPane.add(new Label("Blood Pressure"), 0, 4);
		gridPane.add(bloodPress, 1, 4);
		gridPane.add(new Label("Volume Bypassed"), 0, 5);
		gridPane.add(bypassedVol, 1, 5);
		gridPane.add(new Label("Time on Bypass"), 0, 6);
		gridPane.add(bypassTime, 1, 6);
		setCenter(gridPane);
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
