package org.mdpnp.devices.philips.intellivue.association;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Message;
import org.mdpnp.devices.philips.intellivue.association.impl.AssociationAbortImpl;
import org.mdpnp.devices.philips.intellivue.association.impl.AssociationAcceptImpl;
import org.mdpnp.devices.philips.intellivue.association.impl.AssociationConnectImpl;
import org.mdpnp.devices.philips.intellivue.association.impl.AssociationDisconnectImpl;
import org.mdpnp.devices.philips.intellivue.association.impl.AssociationFinishImpl;
import org.mdpnp.devices.philips.intellivue.association.impl.AssociationRefuseImpl;
import org.mdpnp.devices.philips.intellivue.data.LengthInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssociationProtocolImpl implements AssociationProtocol {
    private AssociationMessageType type = AssociationMessageType.Connect;
    private final LengthInformation length = new LengthInformation();


    public static final class ParameterIdentifier {
        private final short id;
        private final byte[] data;
        public ParameterIdentifier(short id, byte[] data) {
            this.id = id;
            this.data = data;
        }
    }
    private final List<ParameterIdentifier> parameterIdentifiers = new ArrayList<ParameterIdentifier>();

    public List<ParameterIdentifier> getParameterIdentifiers() {
        return parameterIdentifiers;
    }

    public LengthInformation getLength() {
        return length;
    }

    public AssociationMessageType getType() {
        return type;
    }

    public void setType(AssociationMessageType type) {
        this.type = type;
        parameterIdentifiers.clear();
        switch(type) {
        case Connect:
        case Accept:
            parameterIdentifiers.add(new ParameterIdentifier((short)0x05, new byte[] {0x13, 0x01, 0x00, 0x16, 0x01, 0x02, (byte)0x80, 0x00}));
            parameterIdentifiers.add(new ParameterIdentifier((short)0x14, new byte[] {0x00, 0x02}));
            break;
        case Abort:
            parameterIdentifiers.add(new ParameterIdentifier((short) 0x11, new byte[] {0x03}));
            break;
        case Refuse:
            parameterIdentifiers.add(new ParameterIdentifier((short)0x32, new byte[] {0x00}));
            break;
        case Disconnect:
        case Finish:
            break;
        }
    }


    private final LengthInformation presentationLength = new LengthInformation(new byte[] {(byte)0xC1});

    private static final AssociationMessage buildMessage(AssociationMessageType type) {
        // TODO manage lifecycle better?  Don't use this for data messages
        switch(type) {
        case Connect:
            return new AssociationConnectImpl();
        case Abort:
            return new AssociationAbortImpl();
        case Accept:
            return new AssociationAcceptImpl();
        case Disconnect:
            return new AssociationDisconnectImpl();
        case Finish:
            return new AssociationFinishImpl();
        case Refuse:
            return new AssociationRefuseImpl();
        default:
            return null;
        }
    }

    @Override
    public void format(Message message, ByteBuffer bb) {
        if(message instanceof AssociationMessage) {
            format((AssociationMessage)message, bb);
        }
    }

    private int sessionLength() {
        int count = 0;
        for(ParameterIdentifier pi : parameterIdentifiers) {
            count+=2;
            count+=pi.data.length;
        }
        return count;
    }

    @Override
    public void format(AssociationMessage message, ByteBuffer bb) {
        if(null == message) {
            throw new IllegalStateException("No ApplicationMessage set");
        }
        setType(message.getType());

        ByteBuffer bbAppMessage = ByteBuffer.allocate(5000);
        message.format(bbAppMessage);
        presentationLength.setLength(bbAppMessage.position()+message.getPresentationHeader().length+message.getPresentationTrailer().length);
        length.setLength(presentationLength.getByteCount()+sessionLength()+presentationLength.getLength());
        bbAppMessage.flip();

        Bits.putUnsignedByte(bb, type.asShort());
        length.format(bb);

        LengthInformation li = new LengthInformation();

        for(ParameterIdentifier pi : parameterIdentifiers) {
            Bits.putUnsignedByte(bb, pi.id);
            li.setLength(pi.data.length);
            li.format(bb);
            bb.put(pi.data);
        }

        presentationLength.format(bb);
        bb.put(message.getPresentationHeader());
        bb.put(bbAppMessage);
        bb.put(message.getPresentationTrailer());

    }

    private static final Logger log = LoggerFactory.getLogger(AssociationProtocolImpl.class);

    @Override
    public AssociationMessage parse(ByteBuffer bb) {
        // Type and length are the session header
        type = AssociationMessageType.valueOf(Bits.getUnsignedByte(bb));
        length.parse(bb);
        long end_pos = bb.position() + length.getLength();

        LengthInformation li = new LengthInformation();
        parameterIdentifiers.clear();

        // Reads the session data as [identifier, length, payload]
        short pi = Bits.getUnsignedByte(bb);

        //
        while(pi != 0xC1 && bb.position() < end_pos) {
            li.parse(bb);
            log.trace("Parameter Identifier:"+Integer.toHexString(pi));
            byte[] bytes = new byte[ (int) li.getLength()];
            bb.get(bytes);
            parameterIdentifiers.add(new ParameterIdentifier(pi, bytes));
            if(bb.position() < end_pos) {
                pi = Bits.getUnsignedByte(bb);
            }
        }
        // Presentation header starts with 0xC1 followed by a length info
        if(0xC1 == pi && bb.position() < end_pos) {
            li.parse(bb);
        }

        AssociationMessage message = buildMessage(getType());

        if(null == message) {
            log.warn("Unimplemented message type:"+getType());
        } else {
            message.advancePastPresentationHeader(bb);
            message.parse(bb);
        }
        bb.position(bb.position()+message.getPresentationTrailer().length);
        return message;
    }

}
