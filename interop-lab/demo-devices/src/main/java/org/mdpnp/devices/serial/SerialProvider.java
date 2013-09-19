/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.serial;

import java.util.List;

public interface SerialProvider {
    List<String> getPortNames();
    SerialSocket connect(String portIdentifier, long timeout) throws java.io.IOException;
    void cancelConnect();
    void setDefaultSerialSettings(int baudrate, SerialSocket.DataBits dataBits, SerialSocket.Parity parity, SerialSocket.StopBits stopBits);

}
