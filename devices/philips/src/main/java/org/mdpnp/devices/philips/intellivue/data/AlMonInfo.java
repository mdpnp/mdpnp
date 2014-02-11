package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;

public abstract class AlMonInfo implements Parseable, Formatable {
    private int al_inst_no;
    private final TextId al_text = new TextId();
    private int priority;
    private final AlertFlags flags = new AlertFlags();

    @Override
    public void format(ByteBuffer bb) {
        Bits.putUnsignedShort(bb, al_inst_no);
        al_text.format(bb);
        Bits.putUnsignedShort(bb, priority);
        flags.format(bb);
    }

    @Override
    public void parse(ByteBuffer bb) {
        al_inst_no = Bits.getUnsignedShort(bb);
        al_text.parse(bb);
        Bits.getUnsignedShort(bb);
        flags.parse(bb);
    }

    public int getAlInstNo() {
        return al_inst_no;
    }

    public TextId getAlText() {
        return al_text;
    }

    public int getPriority() {
        return priority;
    }

    public AlertFlags getFlags() {
        return flags;
    }

    @Override
    public java.lang.String toString() {
        return "[al_inst_no="+al_inst_no+",al_text="+al_text+",priority="+priority+",flags="+flags+"]";
    }
}
