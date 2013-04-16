package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class MetricSpecification implements Value {
	private final RelativeTime updatePeriod = new RelativeTime();
	private MetricCategory category;
	private MetricAccess access = new MetricAccess();
	private final MetricStructure structure = new MetricStructure();
	private int relevance;
	
	
	public MetricSpecification() {
	}
	
	
	@Override
	public void parse(ByteBuffer bb) {
		updatePeriod.parse(bb);
		category = MetricCategory.valueOf(Bits.getUnsignedShort(bb));
		access.parse(bb);
		structure.parse(bb);
		relevance = Bits.getUnsignedShort(bb);
	}

	@Override
	public void format(ByteBuffer bb) {
		updatePeriod.format(bb);
		Bits.putUnsignedShort(bb, category.asInt());
		access.format(bb);
		structure.format(bb);
		Bits.putUnsignedShort(bb, relevance);
	}
	
	public java.lang.String toString() {
		return "[updatePeriod="+updatePeriod+",category="+category+",access="+access+",structure="+structure+",relevance="+relevance+"]";
	}

}
