/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
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

/**
 * @author Jeff Plourde
 *
 */
public class LogStatus implements DataWriterListener {
    private final Logger log;

    public LogStatus(final Logger log) {
        this.log = log;
    }

    @Override
    public void on_application_acknowledgment(DataWriter arg0, AcknowledgmentInfo arg1) {
        log.debug("on_application_acknowledgment:" + arg1.toString());
    }

    @Override
    public Object on_data_request(DataWriter arg0, Cookie_t arg1) {
        log.debug("on_data_request:" + arg1.toString());
        return null;
    }

    @Override
    public void on_data_return(DataWriter arg0, Object arg1, Cookie_t arg2) {
        log.debug("on_data_return:" + arg1.toString() + arg2.toString());
    }

    @Override
    public void on_destination_unreachable(DataWriter arg0, InstanceHandle_t arg1, Locator_t arg2) {
        log.debug("on_destination_unreachable:" + arg1.toString() + arg2.toString());
    }

    @Override
    public void on_instance_replaced(DataWriter arg0, InstanceHandle_t arg1) {
        log.debug("on_instance_replaced:" + arg1.toString());
    }

    @Override
    public void on_liveliness_lost(DataWriter arg0, LivelinessLostStatus arg1) {
        log.debug("on_liveliness_lost:" + arg1.toString());
    }

    @Override
    public void on_offered_deadline_missed(DataWriter arg0, OfferedDeadlineMissedStatus arg1) {
        log.debug("on_offered_deadline_missed:" + arg1.toString());
    }

    @Override
    public void on_offered_incompatible_qos(DataWriter arg0, OfferedIncompatibleQosStatus arg1) {
        log.debug("on_offered_incompatible_qos:" + arg1.toString());
    }

    @Override
    public void on_publication_matched(DataWriter arg0, PublicationMatchedStatus arg1) {
        log.debug("on_publication_matched:" + arg1.toString());
    }

    @Override
    public void on_reliable_reader_activity_changed(DataWriter arg0, ReliableReaderActivityChangedStatus arg1) {
        log.debug("on_reliable_reader_activity_changed:" + arg1.toString());
    }

    @Override
    public void on_reliable_writer_cache_changed(DataWriter arg0, ReliableWriterCacheChangedStatus arg1) {
        log.debug("on_reliable_writer_cache_changed:" + arg1.toString());
    }

    @Override
    public void on_sample_removed(DataWriter arg0, Cookie_t arg1) {
        log.debug("on_sample_removed:" + arg1);
    }

}
