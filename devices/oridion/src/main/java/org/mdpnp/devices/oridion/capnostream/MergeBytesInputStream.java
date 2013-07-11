package org.mdpnp.devices.oridion.capnostream;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MergeBytesInputStream extends java.io.FilterInputStream {
		public static final int HEADER = -2;
		public static final int EOF = -1;
		
		private final Logger log = LoggerFactory.getLogger(MergeBytesInputStream.class);
		
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
					log.trace("0x80 + read:"+Integer.toHexString(b1));
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