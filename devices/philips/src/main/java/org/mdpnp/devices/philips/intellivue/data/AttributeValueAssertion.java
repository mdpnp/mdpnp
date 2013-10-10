package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.io.util.HexUtil;
import org.mdpnp.devices.philips.intellivue.attribute.Attribute;
import org.mdpnp.devices.philips.intellivue.util.Util;

public class AttributeValueAssertion implements Value, Attribute<ByteArray> {
    private OIDType oidType;
    private ByteArray value;

    public AttributeValueAssertion() {

    }

//	public AttributeValueAssertion(OIDType type, byte[] value) {
//		this.oidType = type;
//		this.value = new ByteArray(value);
//	}



    @Override
    public void parse(ByteBuffer bb) {
        // Keep the OID and the length in the buffer so other attributes can parse
        // with the same logic
        int pos = bb.position();
        oidType = OIDType.lookup(Bits.getUnsignedShort(bb));
        int length = Bits.getUnsignedShort(bb);
        bb.position(pos);
        // TODO this could be a memory issue
        value = new ByteArray(new byte[length+4]);
        value.parse(bb);
    }

    public ByteArray getValue() {
        return value;
    }

    public void setValue(byte[] b) {
        this.value = new ByteArray(b);
    }

    @Override
    public java.lang.String toString() {
        return "[oid="+oidType+",value="+HexUtil.dump(ByteBuffer.wrap(value.getArray()))+"]";
    }

    @Override
    public void format(ByteBuffer bb) {
        // OID and length are stored in the buffer
//		oidType.format(bb);
//		Bits.putUnsignedShort(bb, value.getArray().length-4);
        value.format(bb);
    }

    @Override
    public OIDType getOid() {
        return this.oidType;
    }
    public void setOid(OIDType oid) {
        this.oidType = oid;
    }


}
