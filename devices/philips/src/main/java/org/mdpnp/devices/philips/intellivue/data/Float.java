package org.mdpnp.devices.philips.intellivue.data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("serial")
public class Float extends Number implements Value {

	private double flt;

	private final static double[] powersOfTen = new double[] {
		1.0E-127,1.0E-126,1.0E-125,1.0E-124,1.0E-123,1.0E-122,1.0E-121,1.0E-120,1.0E-119,1.0E-118,1.0E-117,1.0E-116,1.0E-115,1.0E-114,1.0E-113,1.0E-112,1.0E-111,1.0E-110,1.0E-109,1.0E-108,1.0E-107,1.0E-106,1.0E-105,1.0E-104,1.0E-103,1.0E-102,1.0E-101,1.0E-100,1.0E-99,1.0E-98,1.0E-97,1.0E-96,1.0E-95,1.0E-94,1.0E-93,1.0E-92,1.0E-91,1.0E-90,1.0E-89,1.0E-88,1.0E-87,1.0E-86,1.0E-85,1.0E-84,1.0E-83,1.0E-82,1.0E-81,1.0E-80,1.0E-79,1.0E-78,1.0E-77,1.0E-76,1.0E-75,1.0E-74,1.0E-73,1.0E-72,1.0E-71,1.0E-70,1.0E-69,1.0E-68,1.0E-67,1.0E-66,1.0E-65,1.0E-64,1.0E-63,1.0E-62,1.0E-61,1.0E-60,1.0E-59,1.0E-58,1.0E-57,1.0E-56,1.0E-55,1.0E-54,1.0E-53,1.0E-52,1.0E-51,1.0E-50,1.0E-49,1.0E-48,1.0E-47,1.0E-46,1.0E-45,1.0E-44,1.0E-43,1.0E-42,1.0E-41,1.0E-40,1.0E-39,1.0E-38,1.0E-37,1.0E-36,1.0E-35,1.0E-34,1.0E-33,1.0E-32,1.0E-31,1.0E-30,1.0E-29,1.0E-28,1.0E-27,1.0E-26,1.0E-25,1.0E-24,1.0E-23,1.0E-22,1.0E-21,1.0E-20,1.0E-19,1.0E-18,1.0E-17,1.0E-16,1.0E-15,1.0E-14,1.0E-13,1.0E-12,1.0E-11,1.0E-10,1.0E-9,1.0E-8,1.0E-7,1.0E-6,1.0E-5,1.0E-4,1.0E-3,1.0E-2,1.0E-1,1.0E0,1.0E1,1.0E2,1.0E3,1.0E4,1.0E5,1.0E6,1.0E7,1.0E8,1.0E9,1.0E10,1.0E11,1.0E12,1.0E13,1.0E14,1.0E15,1.0E16,1.0E17,1.0E18,1.0E19,1.0E20,1.0E21,1.0E22,1.0E23,1.0E24,1.0E25,1.0E26,1.0E27,1.0E28,1.0E29,1.0E30,1.0E31,1.0E32,1.0E33,1.0E34,1.0E35,1.0E36,1.0E37,1.0E38,1.0E39,1.0E40,1.0E41,1.0E42,1.0E43,1.0E44,1.0E45,1.0E46,1.0E47,1.0E48,1.0E49,1.0E50,1.0E51,1.0E52,1.0E53,1.0E54,1.0E55,1.0E56,1.0E57,1.0E58,1.0E59,1.0E60,1.0E61,1.0E62,1.0E63,1.0E64,1.0E65,1.0E66,1.0E67,1.0E68,1.0E69,1.0E70,1.0E71,1.0E72,1.0E73,1.0E74,1.0E75,1.0E76,1.0E77,1.0E78,1.0E79,1.0E80,1.0E81,1.0E82,1.0E83,1.0E84,1.0E85,1.0E86,1.0E87,1.0E88,1.0E89,1.0E90,1.0E91,1.0E92,1.0E93,1.0E94,1.0E95,1.0E96,1.0E97,1.0E98,1.0E99,1.0E100,1.0E101,1.0E102,1.0E103,1.0E104,1.0E105,1.0E106,1.0E107,1.0E108,1.0E109,1.0E110,1.0E111,1.0E112,1.0E113,1.0E114,1.0E115,1.0E116,1.0E117,1.0E118,1.0E119,1.0E120,1.0E121,1.0E122,1.0E123,1.0E124,1.0E125,1.0E126,1.0E127,1.0E128,
	};
	
	public boolean isNaN() {
		return Double.isNaN(flt);
	}
	public boolean isPositiveInfinity() {
		return Double.isInfinite(flt) && flt>=0.0;
	}
	public boolean isNegativeInfinity() {
		return Double.isInfinite(flt) && flt<0.0;
	}
	public boolean isNotAtThisResolution() {
		return 0x0010000000000000L==Double.doubleToRawLongBits(flt);
	}
	
	private final static double toDouble(int mantissa, byte exp) {
		switch(mantissa) {
		case 0x7FFFFF:
			return java.lang.Double.NaN;
		case 0x800000:
			return java.lang.Double.MIN_NORMAL;
		case 0x7FFFFE:
			return java.lang.Double.POSITIVE_INFINITY;
		case 0x800002:
			return java.lang.Double.NEGATIVE_INFINITY;
		default:
			return mantissa * powersOfTen[exp+127];
		}
	}
	private static class MantissaExponent {
		private final int mantissa;
		private final byte exponent;
		
		public MantissaExponent(int mantissa, byte exponent) {
			this.mantissa = mantissa;
			this.exponent = exponent;
		}
		public byte getExponent() {
			return exponent;
		}
		public int getMantissa() {
			return mantissa;
		}
	}
	@Override
	public void parse(ByteBuffer bb) {
		// Isn't this pretty much IEEE ?
		byte exp = bb.get();
		int mantissa = (0xFF0000) & (bb.get() << 16);
		mantissa |= (0xFF00 & (bb.get() << 8));
		mantissa |= (0xFF & bb.get());
		
		flt = toDouble(mantissa, exp);
	}
	
	private static MantissaExponent mantissa(double flt) {
		if(0==Double.compare(0.0, flt)) {
			return new MantissaExponent(0, (byte)1);
		}
		int exponent = (int) Math.log10(Math.abs(flt))-5;
		int mantissa;
		
		if(Double.isNaN(flt)) {
			mantissa = 0x7FFFFF;
		} else if(Double.isInfinite(flt)) {
			if(flt < 0) {
				mantissa = 0x800002;
			} else {
				mantissa = 0x7FFFFE;
			}
		} else if(Double.doubleToRawLongBits(flt)==0x0010000000000000L) {
			mantissa = 0x800000;
		} else {
			mantissa = (int) (flt / powersOfTen[exponent+127]);
		}
		return new MantissaExponent(mantissa, (byte)exponent);
	}
	
	public int exponent() {
		return (int) Math.sqrt(Math.abs(flt));
	}
	
	@Override
	public void format(ByteBuffer bb) {
		MantissaExponent me = mantissa(flt);
		bb.put(me.getExponent());
		bb.put((byte) (0xFF&(me.getMantissa()>>16)));
		bb.put((byte) (0xFF&(me.getMantissa()>>8)));
		bb.put((byte) (0xFF&me.getMantissa()));
	}

	public void setFloat(double flt) {
		this.flt = flt;
	}
	public double getDouble() {
		return flt;
	}
	public float getFloat() {
		return (float)flt;
	}
	
	@Override
	public java.lang.String toString() {
		return java.lang.Float.toString((float)flt);
	}
	public static void main(java.lang.String[] args) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(40);
		ByteBuffer bb1 = ByteBuffer.allocate(40);
		bb.order(ByteOrder.BIG_ENDIAN);
		bb1.order(ByteOrder.BIG_ENDIAN);
		bb.putInt(0x00000040);
		bb.putInt(0xfd007d00); // 32 
		bb.putInt(0xff000140); // 32
		bb.putInt(0x00800002); // -infinity
		bb.putInt(0x007FFFFF); // NaN
		bb.putInt(0x00800000); // NRes
		bb.putInt(0x007FFFFE); // +infinity
		bb.putInt(0x00800002); // -infinity
		bb.flip();
		Float f = new Float();
		while(bb.hasRemaining()) {
			f.parse(bb);
			f.format(bb1);
			System.out.println(f.getDouble()+ " " + f.isNaN() + " " + f.isNotAtThisResolution() + " " + f.isNegativeInfinity() + " " + f.isPositiveInfinity());
		}
		bb1.flip();
		while(bb1.hasRemaining()) {
			f.parse(bb1);
			System.out.println(f.getDouble()+ " " + f.isNaN() + " " + f.isNotAtThisResolution() + " " + f.isNegativeInfinity() + " " + f.isPositiveInfinity());
		}
		
	}
	@Override
	public int intValue() {
		return (int) flt;
	}
	@Override
	public long longValue() {
		return (long) flt;
	}
	@Override
	public float floatValue() {
		return (float) flt;
	}
	@Override
	public double doubleValue() {
		return flt;
	}
}
