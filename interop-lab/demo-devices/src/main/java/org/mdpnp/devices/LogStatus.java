package org.mdpnp.devices;

import org.slf4j.Logger;

import com.rti.dds.infrastructure.Cookie_t;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.Locator_t;
import com.rti.dds.publication.AcknowledgmentInfo;
import com.rti.dds.publication.DataWriter;
import com.rti.dds.publication.DataWriterListener;
import com.rti.dds.publication.LivelinessLostStatus;
import com.rti.dds.publication.OfferedDeadlineMissedStatus;
import com.rti.dds.publication.OfferedIncompatibleQosStatus;
import com.rti.dds.publication.PublicationMatchedStatus;
import com.rti.dds.publication.ReliableReaderActivityChangedStatus;
import com.rti.dds.publication.ReliableWriterCacheChangedStatus;

public class LogStatus implements DataWriterListener {
    private final Logger log;

    public LogStatus(final Logger log) {
        this.log = log;
    }

    @Override
    public void on_application_acknowledgment(DataWriter arg0, AcknowledgmentInfo arg1) {
        log.debug("on_application_acknowledgment:"+arg1.toString());
    }

    @Override
    public Object on_data_request(DataWriter arg0, Cookie_t arg1) {
        log.debug("on_data_request:"+arg1.toString());
        return null;
    }

    @Override
    public void on_data_return(DataWriter arg0, Object arg1, Cookie_t arg2) {
        log.debug("on_data_return:"+arg1.toString()+arg2.toString());
    }

    @Override
    public void on_destination_unreachable(DataWriter arg0, InstanceHandle_t arg1, Locator_t arg2) {
        log.debug("on_destination_unreachable:"+arg1.toString()+arg2.toString());
    }

    @Override
    public void on_instance_replaced(DataWriter arg0, InstanceHandle_t arg1) {
        log.debug("on_instance_replaced:"+arg1.toString());
    }

    @Override
    public void on_liveliness_lost(DataWriter arg0, LivelinessLostStatus arg1) {
        log.debug("on_liveliness_lost:"+arg1.toString());
    }

    @Override
    public void on_offered_deadline_missed(DataWriter arg0, OfferedDeadlineMissedStatus arg1) {
        log.debug("on_offered_deadline_missed:"+arg1.toString());
    }

    @Override
    public void on_offered_incompatible_qos(DataWriter arg0, OfferedIncompatibleQosStatus arg1) {
        log.debug("on_offered_incompatible_qos:"+arg1.toString());
    }

    @Override
    public void on_publication_matched(DataWriter arg0, PublicationMatchedStatus arg1) {
        log.debug("on_publication_matched:"+arg1.toString());
    }

    @Override
    public void on_reliable_reader_activity_changed(DataWriter arg0, ReliableReaderActivityChangedStatus arg1) {
        log.debug("on_reliable_reader_activity_changed:"+arg1.toString());
    }

    @Override
    public void on_reliable_writer_cache_changed(DataWriter arg0, ReliableWriterCacheChangedStatus arg1) {
        log.debug("on_reliable_writer_cache_changed:"+arg1.toString());
    }

    @Override
    public void on_sample_removed(DataWriter arg0, Cookie_t arg1) {
        log.debug("on_sample_removed:"+arg1);
    }


}
