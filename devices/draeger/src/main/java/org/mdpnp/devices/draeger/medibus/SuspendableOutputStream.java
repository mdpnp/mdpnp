package org.mdpnp.devices.draeger.medibus;

import java.io.IOException;
import java.io.OutputStream;

public class SuspendableOutputStream extends java.io.FilterOutputStream {

	private final SuspendableInputStream in;
	
	public SuspendableOutputStream(OutputStream out, SuspendableInputStream in) {
		super(out);
		this.in = in;
	}
	
	/**
	 * Writes a byte after awaiting a release from suspension
	 */
	@Override
	public void write(int b) throws IOException {
		in.suspended();
		super.write(b);
	}
}
