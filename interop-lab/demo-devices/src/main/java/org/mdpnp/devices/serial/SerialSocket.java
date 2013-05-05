/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface SerialSocket {
	String getPortIdentifier();
	void close() throws IOException;
	InputStream getInputStream() throws IOException;
	OutputStream getOutputStream() throws IOException;
	enum StopBits {
		One,
		OneAndOneHalf,
		Two
	};
	enum DataBits {
		Seven,
		Eight
	}
	enum Parity {
		None,
		Odd,
		Even
	}
	void setSerialParams(int baud, DataBits dataBits, Parity parity, StopBits stopBits);
}
