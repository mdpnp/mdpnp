package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

public class StrAlMonInfo extends AlMonInfo {
    private final String string = new String();

    public String getString() {
        return string;
    }

    @Override
    public void format(ByteBuffer bb) {
        super.format(bb);
        string.format(bb);
    }

    @Override
    public void parse(ByteBuffer bb) {
        super.parse(bb);
        string.parse(bb);
    }

    @Override
    public java.lang.String toString() {
        return "[al_inst_no="+getAlInstNo()+",al_text="+getAlText()+",priority="+getPriority()+",flags="+getFlags()+",string="+string+"]";
    }
}
