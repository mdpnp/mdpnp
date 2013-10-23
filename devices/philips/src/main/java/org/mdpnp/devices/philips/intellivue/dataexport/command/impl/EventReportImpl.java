package org.mdpnp.devices.philips.intellivue.dataexport.command.impl;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;
import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.data.RelativeTime;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportEvent;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportMessage;
import org.mdpnp.devices.philips.intellivue.dataexport.command.EventReport;
import org.mdpnp.devices.philips.intellivue.dataexport.command.EventReportResult;
import org.mdpnp.devices.philips.intellivue.dataexport.event.EventFactory;
import org.mdpnp.devices.philips.intellivue.util.Util;

public class EventReportImpl implements EventReport {
    private final ManagedObjectIdentifier managedObject = new ManagedObjectIdentifier();
    private final RelativeTime eventTime = new RelativeTime();
    private OIDType eventType;
    private DataExportEvent event;

    private DataExportMessage parent;

    @Override
    public EventReportResult createConfirm() {
        EventReportResultImpl eri = new EventReportResultImpl();
        eri.getManagedObject().setOidType(managedObject.getOidType());
        eri.getManagedObject().getGlobalHandle().setMdsContext(managedObject.getGlobalHandle().getMdsContext());
        eri.getManagedObject().getGlobalHandle().setHandle(managedObject.getGlobalHandle().getHandle());
        eri.getEventTime().setRelativeTime(0);
        eri.setEventType(eventType);
        return eri;
    }

    @Override
    public DataExportMessage getMessage() {
        return parent;
    }
    @Override
    public void setMessage(DataExportMessage message) {
        this.parent = message;
    }
    @Override
    public void setEventType(OIDType oid) {
        this.eventType = oid;
    }

    public void setEvent(DataExportEvent event) {
        this.event = event;
    }

    @Override
    public void parseMore(ByteBuffer bb) {
        parse(bb, false);
    }

    @Override
    public void parse(ByteBuffer bb) {
        parse(bb, true);
    }



    private void parse(ByteBuffer bb, boolean clear) {
        managedObject.parse(bb);
        eventTime.parse(bb);
        eventType = OIDType.parse(bb);
        int length = Bits.getUnsignedShort(bb);
        if(clear) {
            event = EventFactory.buildEvent(eventType);
        }
        if(null == event) {
            bb.position(bb.position() + length);
        } else {
            event.parse(bb);
        }
    }
    @Override
    public void format(ByteBuffer bb) {
        managedObject.format(bb);
        eventTime.format(bb);
        eventType.format(bb);
        if(event != null) {
            Util.PrefixLengthShort.write(bb, event);
        } else {
            Bits.putUnsignedShort(bb, 0);
        }
    }
    @Override
    public ManagedObjectIdentifier getManagedObject() {
        return managedObject;
    }
    @Override
    public OIDType getEventType() {
        return eventType;
    }
    @Override
    public String toString() {
        return "[eventType="+eventType+",eventTime="+eventTime+",managedObject="+managedObject+",event="+event+"]";
    }
    @Override
    public DataExportEvent getEvent() {
        return event;
    }

    @Override
    public RelativeTime getEventTime() {
        return eventTime;
    }
}
