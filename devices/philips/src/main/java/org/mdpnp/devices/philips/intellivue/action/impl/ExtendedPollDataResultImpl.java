package org.mdpnp.devices.philips.intellivue.action.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.action.ExtendedPollDataResult;
import org.mdpnp.devices.philips.intellivue.action.SingleContextPoll;
import org.mdpnp.devices.philips.intellivue.data.AbsoluteTime;
import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.data.RelativeTime;
import org.mdpnp.devices.philips.intellivue.data.Type;
import org.mdpnp.devices.philips.intellivue.dataexport.command.ActionResult;
import org.mdpnp.devices.philips.intellivue.util.Util;

public class ExtendedPollDataResultImpl implements ExtendedPollDataResult {
    private int pollNumber;
    private int sequenceNumber;
    private final RelativeTime relativeTimestamp= new RelativeTime();
    private final AbsoluteTime absoluteTimestamp = new AbsoluteTime();
    private final Type polledObjectType = new Type();
    private OIDType polledAttributeGroup = OIDType.lookup(0);
    private final List<SingleContextPoll> pollInfoList = new ArrayList<SingleContextPoll>();

    private ActionResult action;

    @Override
    public ActionResult getAction() {
        return this.action;
    }

    @Override
    public void setAction(ActionResult action) {
        this.action = action;
    }

    @Override
    public void parse(ByteBuffer bb) {
        parse(bb, false);
    }

    @Override
    public void parseMore(ByteBuffer bb) {
        parse(bb, true);
    }
    private void parse(ByteBuffer bb, boolean more) {
        pollNumber = Bits.getUnsignedShort(bb);
        sequenceNumber = Bits.getUnsignedShort(bb);
        relativeTimestamp.parse(bb);
        absoluteTimestamp.parse(bb);
        polledObjectType.parse(bb);
        polledAttributeGroup = OIDType.parse(bb);
        Util.PrefixLengthShort.read(bb, pollInfoList, !more, SingleContextPollImpl.class);

    }

    @Override
    public void format(ByteBuffer bb) {
        Bits.putUnsignedShort(bb, pollNumber);
        Bits.putUnsignedShort(bb, sequenceNumber);
        relativeTimestamp.format(bb);
        absoluteTimestamp.format(bb);
        polledObjectType.format(bb);
        polledAttributeGroup.format(bb);
        Util.PrefixLengthShort.write(bb, pollInfoList);
    }

    @Override
    public String toString() {
        return "[pollNumber="+pollNumber+",sequenceNumber="+sequenceNumber+",relativeTimestamp="+relativeTimestamp+",absoluteTimestamp="+absoluteTimestamp+",polledObjectType="+polledObjectType+",polledAttributeGroup="+polledAttributeGroup+",pollInfoList="+pollInfoList+"]";
    }
    public void setPolledAttributeGroup(OIDType polledAttributeGroup) {
        this.polledAttributeGroup = polledAttributeGroup;
    }
    @Override
    public int getPollNumber() {
        return pollNumber;
    }

    @Override
    public void setPollNumber(int pollNumber) {
        this.pollNumber = pollNumber;
    }

    @Override
    public AbsoluteTime getAbsoluteTime() {
        return absoluteTimestamp;
    }

    @Override
    public OIDType getPolledAttributeGroup() {
        return polledAttributeGroup;
    }

    @Override
    public Type getPolledObjType() {
        return polledObjectType;
    }

    @Override
    public List<SingleContextPoll> getPollInfoList() {
        return pollInfoList;
    }

    @Override
    public RelativeTime getRelativeTime() {
        return relativeTimestamp;
    }

    @Override
    public int getSequenceNumber() {
        return sequenceNumber;
    }
}
