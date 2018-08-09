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
package org.mdpnp.apps.device;

import java.util.Set;

import org.mdpnp.apps.fxbeans.BypassStatusFx;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * @author Jeff Plourde
 *
 */
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
		deviceMonitor.getBypassStatusModel().addListener(BypassStatusListener);
		deviceMonitor.getBypassStatusModel().forEach((t) -> add(t));
	}

	@Override
	public void destroy() {
		super.destroy();
		deviceMonitor.getBypassStatusModel().removeListener(BypassStatusListener);
	}

	protected void add(BypassStatusFx data) {
		final String DEGREE = "\u00b0";
		active.textProperty().bind(data.bypassActiveProperty().asString());
		flowRate.textProperty().bind(data.bypass_flow_lminProperty().asString("%.2f").concat(" L/min"));
		pumpSpeed.textProperty().bind(data.bypass_speed_rpmProperty().asString().concat(" rpms"));
		bloodTemp.textProperty().bind(data.blood_temp_celsiusProperty().asString("%.1f").concat("C"));
		bloodPress.textProperty().bind(data.blood_press_mmhgProperty().asString("%.2f").concat(" mmHg"));
		bypassedVol.textProperty().bind(data.volume_bypassed_mlProperty().asString().concat(" L"));
		bypassTime.textProperty().bind(data.bypass_duration_secondsProperty().asString().concat(" seconds"));
	}

	protected void remove(BypassStatusFx data) {
		active.textProperty().unbind();
		flowRate.textProperty().unbind();
		pumpSpeed.textProperty().unbind();
		bloodTemp.textProperty().unbind();
		bloodPress.textProperty().unbind();
		bypassedVol.textProperty().unbind();
		bypassTime.textProperty().unbind();
	}

	private final OnListChange<BypassStatusFx> BypassStatusListener = new OnListChange<BypassStatusFx>((t) -> add(t),
			null, (t) -> remove(t));

}
