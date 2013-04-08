/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.comms.serial;

public class SerialProviderFactory {

	private static SerialProvider defaultProvider;
	private static final String DEFAULT_PROVIDER = "org.mdpnp.comms.serial.PureJavaCommSerialProvider";
	
	public static final void setDefaultProvider(SerialProvider serialProvider) {
			SerialProviderFactory.defaultProvider = serialProvider;
	}
	public static final SerialProvider getDefaultProvider() {
		if(null == defaultProvider) {
			try {
				defaultProvider = (SerialProvider) Class.forName(DEFAULT_PROVIDER).getConstructor(new Class<?>[0]).newInstance(new Object[0]);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return defaultProvider;
	}
}
