/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.io.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {
	private IOUtil() {
		
	}
	public static final int readFully(InputStream is, byte[] bytes) throws IOException {
		return readFully(is, bytes, 0);
	}
	
	public static final int readFully(InputStream is, byte[] bytes, int len) throws IOException {
		return readFully(is, bytes, len, bytes.length);
	}
	
	public static final int readFully(InputStream is, byte[] bytes, int len, int totalRequired) throws IOException {
		while(len < totalRequired) {
			int b = is.read(bytes, len, totalRequired - len);
			if(b < 0) {
				throw new EOFException("Reached EOF unexpectedly");
			} else {
				len += b;
			}
		}
		return len;
	}
}
