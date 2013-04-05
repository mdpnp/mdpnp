package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class Type implements Value {
	private NomPartition nomPartition = NomPartition.Object;

	private OIDType oidType = OIDType.lookup(0);
	
	public Type() {
	}
	
	public Type(ObjectClass objClass) {
		this(NomPartition.Object, OIDType.lookup(objClass.asInt()));
	}
	
	public Type(NomPartition nomPartition, OIDType oidType) {
		this.nomPartition = nomPartition;
		this.oidType = oidType;
	}

	@Override
	public void parse(ByteBuffer bb) {
		this.nomPartition = NomPartition.valueOf(Bits.getUnsignedShort(bb));
		this.oidType = OIDType.lookup(Bits.getUnsignedShort(bb));
	}
	
	@Override
	public void format(ByteBuffer bb) {
		bb.putShort(nomPartition.asShort());
		oidType.format(bb);
		
	}
	
	public NomPartition getNomPartition() {
		return nomPartition;
	}
	public OIDType getOidType() {
		return oidType;
	}
	
	public void setNomPartition(NomPartition nomPartition) {
		this.nomPartition = nomPartition;
	}
	public void setOidType(OIDType oidType) {
		this.oidType = oidType;
	}
	
	@Override
	public java.lang.String toString() {

		return "[nomPartition="+nomPartition+",oidType="+oidType+"]";
	}
	
}
