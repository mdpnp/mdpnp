package org.mdpnp.devices.impl.oridion;

import java.io.IOException;

public final class MergeBytesInputStream extends java.io.FilterInputStream {
		public static final int HEADER = -2;
		public static final int EOF = -1;
		
		public MergeBytesInputStream(java.io.InputStream inputStream) {
			super(inputStream);
		}
		
		@Override
		public int read() throws IOException {
			int b1;
			int b = super.read();
//			log.trace("read:"+Integer.toHexString(b));
			if(b < 0) {
				return EOF;
			} else {
				switch(b) {
				case 0x85:
					return HEADER;
				case 0x80:
					b1 = super.read();
//					log.trace("read:"+Integer.toHexString(b1));
					if(b1 < 0) {
						return EOF;
					} else {
						return (0x80 + b1);
					}
				default:
					return b;
				}
			}
		}
	}