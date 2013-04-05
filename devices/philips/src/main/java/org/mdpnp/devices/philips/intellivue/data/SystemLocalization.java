package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class SystemLocalization implements Value {

	private long syslocal_revision;
	private Language language = Language.English;
	private StringFormat stringFormat = StringFormat.Unicode;
	
	public SystemLocalization() {
	}
	

	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedInt(bb, syslocal_revision);
		Bits.putUnsignedShort(bb, language.asShort());
		Bits.putUnsignedShort(bb,  stringFormat.asShort());
	}
	
	@Override
	public void parse(ByteBuffer bb) {
		syslocal_revision = Bits.getUnsignedInt(bb);
		language = Language.valueOf(Bits.getUnsignedShort(bb));
		stringFormat = StringFormat.valueOf(Bits.getUnsignedShort(bb));
	}

	@Override
	public java.lang.String toString() {
		return "[syslocal_revision="+syslocal_revision+",language="+language+",stringFormat="+stringFormat+"]";
	}

}
