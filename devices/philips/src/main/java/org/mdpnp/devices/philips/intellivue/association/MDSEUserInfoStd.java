package org.mdpnp.devices.philips.intellivue.association;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;
import org.mdpnp.devices.philips.intellivue.attribute.Attribute;
import org.mdpnp.devices.philips.intellivue.attribute.AttributeFactory;
import org.mdpnp.devices.philips.intellivue.data.AttributeValueList;
import org.mdpnp.devices.philips.intellivue.data.MdibObjectSupport;
import org.mdpnp.devices.philips.intellivue.data.PollProfileSupport;

public class MDSEUserInfoStd implements Formatable, Parseable {
	public static final long MDDL_VERSION1 = 0x80000000L;
	private long protocolVersion = MDDL_VERSION1;
	
	public static final long NOMEN_VERSION = 0x40000000L;
	private long nomenclatureVersion = NOMEN_VERSION;
	
	private long functionalUnits = 0L;
	
	public static final long SYST_CLIENT = 0x80000000L;
	public static final long SYST_SERVER = 0x00800000L;
	private long systemType = SYST_CLIENT;
	
	public static final long HOT_START =  0x80000000L;
	public static final long WARM_START = 0x40000000L;
	public static final long COLD_START = 0x20000000L;
	private long startupMode = COLD_START;
	
	private final AttributeValueList optionList = new AttributeValueList();
	private final AttributeValueList supportedAProfiles = new AttributeValueList();
	
	private final Attribute<PollProfileSupport> pollProfileSupport = AttributeFactory.getPollProfileSupport();
	private final Attribute<MdibObjectSupport> mdibObjectSupport = AttributeFactory.getMdibObjectSupport();
	
	@Override
	public void format(ByteBuffer bb) {
	    Bits.putUnsignedInt(bb, protocolVersion);
	    Bits.putUnsignedInt(bb, nomenclatureVersion);
	    Bits.putUnsignedInt(bb, functionalUnits);
	    Bits.putUnsignedInt(bb, systemType);
	    Bits.putUnsignedInt(bb, startupMode);
		
		optionList.reset();
		supportedAProfiles.reset();
		
		supportedAProfiles.add(pollProfileSupport);
		supportedAProfiles.add(mdibObjectSupport);
		
//		pollProfileSupport.format(supportedAProfiles);
//		mdibObjectSupport.format(supportedAProfiles);
		
		optionList.format(bb);
		supportedAProfiles.format(bb);

	}

	@Override
	public String toString() {
		return "[protocolVersion="+protocolVersion+",nomenclatureVersion="+nomenclatureVersion+",functionalUnits="+functionalUnits+",systemType="+systemType+",startupMode="+startupMode+",optionList="+optionList+",supportedAProfiles="+supportedAProfiles+"]";
	}
	
	@Override
	public void parse(ByteBuffer bb) {
		protocolVersion = Bits.getUnsignedInt(bb);
		nomenclatureVersion = Bits.getUnsignedInt(bb);
		functionalUnits = Bits.getUnsignedInt(bb);
		systemType = Bits.getUnsignedInt(bb);
		startupMode = Bits.getUnsignedInt(bb);
		optionList.parse(bb);
		supportedAProfiles.parse(bb);
		
		supportedAProfiles.get(pollProfileSupport);
		supportedAProfiles.get(mdibObjectSupport);
	}
	
	public MdibObjectSupport getMdibObjectSupport() {
		return mdibObjectSupport.getValue();
	}
	
	public long getFunctionalUnits() {
		return functionalUnits;
	}
	public long getNomenclatureVersion() {
		return nomenclatureVersion;
	}

	public PollProfileSupport getPollProfileSupport() {
		return pollProfileSupport.getValue();
	}
	public long getProtocolVersion() {
		return protocolVersion;
	}
	public long getStartupMode() {
		return startupMode;
	}

	public long getSystemType() {
		return systemType;
	}

}
