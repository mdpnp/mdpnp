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
package org.mdpnp.apps.testapp;

import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;

import org.mdpnp.apps.fxbeans.InfusionStatusFx;

public class InfusionStatusFxListCell extends ListCell<InfusionStatusFx> {
	private final DeviceListModel deviceListModel;
	private DeviceController deviceController;

	public InfusionStatusFxListCell(final DeviceListModel deviceListModel) {
		this.deviceListModel = deviceListModel;
	}

	private Parent root;

	@Override
	protected void updateItem(InfusionStatusFx item, boolean empty) {
		super.updateItem(item, empty);

		if (null == root) {
			FXMLLoader loader = new FXMLLoader(DeviceController.class.getResource("Device.fxml"));
			try {
				root = loader.load();
				deviceController = loader.getController();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			setTooltip(new Tooltip(""));
			setGraphic(root);
		}

		if (null == item) {
			deviceController.bind(null);
			textProperty().unbind();
			getTooltip().textProperty().unbind();
			getTooltip().setText("");
			setText("");
			setGraphic(null);
		} else {
			getTooltip().textProperty().bind(item.unique_device_identifierProperty());
			Device device = deviceListModel.getByUniqueDeviceIdentifier(item.getUnique_device_identifier());
			if (null == device) {
				deviceController.bind(null);
				textProperty().bind(item.unique_device_identifierProperty());
			} else {
				deviceController.bind(device);
				textProperty().bind(Bindings.concat("\nDrug: ").concat(item.drug_nameProperty()));
			}
			setGraphic(root);
		}
	}
}
