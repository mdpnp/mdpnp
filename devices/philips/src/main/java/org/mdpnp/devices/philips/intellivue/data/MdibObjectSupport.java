package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.util.Util;

public class MdibObjectSupport implements Value {
	private final List<MdibObjectSupportEntry> list = new ArrayList<MdibObjectSupportEntry>();
	
	public void addClass(ObjectClass objClass) {
		list.add(new MdibObjectSupportEntry(objClass));
	}
	
	public void addClass(ObjectClass objClass, long maxInstances) {
		list.add(new MdibObjectSupportEntry(objClass, maxInstances));
	}
	
	public static class MdibObjectSupportEntry implements Value {
		private final Type type;
		private long maxInstances = MAX_INSTANCES_UNDEFINED;
		public static final long MAX_INSTANCES_UNDEFINED = 0xFFFFFFFFL;
		
		public MdibObjectSupportEntry() {
			this.type = new Type();
		}
		
		public MdibObjectSupportEntry(ObjectClass c) {
			this(c, MAX_INSTANCES_UNDEFINED);
		}
		
		public MdibObjectSupportEntry(ObjectClass c, long maxInstances) {
			this.type = new Type(c);
			this.maxInstances = maxInstances;
		}
		
		@Override
		public void format(ByteBuffer bb) {
			type.format(bb);
			Bits.putUnsignedInt(bb, maxInstances);
		}
		@Override
		public void parse(ByteBuffer bb) {
			type.parse(bb);
			maxInstances = Bits.getUnsignedInt(bb);
		}
		
		@Override
		public java.lang.String toString() {
			return "[type="+type+",maxInstances="+maxInstances+"]";
		}
	}

	@Override
	public void parse(ByteBuffer bb) {
		Util.PrefixLengthShort.read(bb, list, true, MdibObjectSupportEntry.class);
	}

	@Override
	public void format(ByteBuffer bb) {
		Util.PrefixLengthShort.write(bb, list);
	}

	@Override
	public java.lang.String toString() {
		return list.toString();
	}

}
