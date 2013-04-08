package org.mdpnp.devices.philips.intellivue.util;

import static org.mdpnp.devices.io.util.Bits.getUnsignedShort;
import static org.mdpnp.devices.io.util.Bits.putUnsignedShort;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;

public class Util {
	private Util() {
		
	}
	

	
	public static final char toHexChar(int b) {
		switch(0XF & b) {
		case 0xF:
			return 'F';
		case 0xE:
			return 'E';
		case 0xD:
			return 'D';
		case 0xC:
			return 'C';
		case 0xB:
			return 'B';
		case 0xA:
			return 'A';
		default:
			return (char)('0'+(0xF&b));
		
		}
	}
	
	public static final String toHexString(int b) {
		return ""+toHexChar(b>>4)+toHexChar(b);
	}
	
	public static final String dump(ByteBuffer bb) {
		return dump(bb, Integer.MAX_VALUE);
	}
	
	public static final String dump(ByteBuffer bb, int bytesPerLine) {
		StringBuilder sb = new StringBuilder("[");
		bb.mark();
		int c = 0;
		while(bb.hasRemaining()) {
			int b = 0xFF & bb.get();
			sb.append(toHexString(b)).append(" ");
			c++;
			if(c == bytesPerLine) {
				sb.append("\n ");
				c = 0;
			}
		}
		if(sb.length()>1) {
			sb.delete(sb.length()-1, sb.length());
		}
		bb.reset();
		sb.append("]");
		return sb.toString();
	}
	
	public static final boolean startsWith(ByteBuffer haystack, byte[] needle) {
		if(needle == null || needle.length == 0) {
			return true;
		}
		
		if(haystack.remaining() < needle.length) {
			return false;
		}
		
		for(int i = 0; i < needle.length; i++) {
			if(haystack.get(haystack.position()+i) != needle[i]) {
				return false;
			}
		}
		return true;
	}
	// CRUDE
	public static final boolean advancePast(ByteBuffer haystack, byte[][] needle) {
		while(haystack.hasRemaining()) {
			for(int i = 0; i < needle.length; i++) {
				if(startsWith(haystack, needle[i])) {
					haystack.position(haystack.position()+needle[i].length);
					return true;
				}
			}
			haystack.get();
		}
		return false;
	}
	public static final long toUnsignedInt(byte[] b) {
		return (0xFF000000L & (b[0]>>24)) |
				(0x00FF0000L & (b[1]>>16)) |
				(0x0000FF00L & (b[2]>>8)) |
				(0x000000FFL & (b[3]>>0));
	}
	
	public static  class PrefixLengthShort {
		public interface Builder<T extends Parseable> {
			T build();
		}
		
		public void begin(ByteBuffer bb) {
			putUnsignedShort(bb, 0);
		}
		
		public static <T extends Parseable> void read(ByteBuffer bb, Collection<T> list, Class<? extends T> clazz) {
			read(bb, list, true, clazz);
		}
		@SuppressWarnings("unused")
        public static <T extends Parseable> void read(ByteBuffer bb, Collection<T> list, boolean clear, Class<? extends T> clazz) {
			if(clear) {
				list.clear();
			}
			try {
				int count = getUnsignedShort(bb);
				int length = getUnsignedShort(bb);
				for(int i = 0; i < count; i++) {
					T p = clazz.newInstance();
					p.parse(bb);
					list.add(p);
				}
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			}

		}
		
		public static <T extends Parseable> void read(ByteBuffer bb, Collection<T> list, Builder<T> b) {
			read(bb, list, true, b);
		}
		
		@SuppressWarnings("unused")
        public static <T extends Parseable> void read(ByteBuffer bb, Collection<T> list, boolean clear, Builder<T> b) {
			if(clear) {
				list.clear();
			}
			
			int count = getUnsignedShort(bb);
			int length = getUnsignedShort(bb);
			for(int i = 0; i < count; i++) {
				T p = b.build();
				p.parse(bb);
				list.add(p);
			}
		}
		
		public static void write(ByteBuffer bb, Collection<? extends Formatable> list) {
			if(list.isEmpty()) {
				putUnsignedShort(bb, 0);
				putUnsignedShort(bb, 0);
			} else {
				putUnsignedShort(bb, list.size());
				putUnsignedShort(bb, 0);
				int pos = bb.position();
				for(Formatable f : list) {
					f.format(bb);
				}
				int length = bb.position() - pos;
				bb.position(pos - 2);
				putUnsignedShort(bb, length);
				bb.position(bb.position() + length);
			}
		}
		
		public static void write(ByteBuffer bb, Formatable f) {
			putUnsignedShort(bb, 0);
			int pos = bb.position();
			f.format(bb);
			int length = bb.position() - pos;
			bb.position(pos - 2);
			putUnsignedShort(bb, length);
			bb.position(bb.position() + length);
		}
	}
}
