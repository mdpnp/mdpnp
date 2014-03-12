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
package org.mdpnp.apps.testapp.pump;

import com.rti.dds.subscription.SampleInfo;

/**
 * @author Jeff Plourde
 *
 */
public class PumpImpl implements Pump {
    private final PumpModel parent;
    private final ice.InfusionStatus infusionStatus = new ice.InfusionStatus();
    private final SampleInfo sampleInfo = new SampleInfo();

    @Override
    public String toString() {
        return infusionStatus.unique_device_identifier;
    }

    public PumpImpl(PumpModel parent, ice.InfusionStatus infusionStatus, SampleInfo sampleInfo) {
        this.parent = parent;
        this.infusionStatus.copy_from(infusionStatus);
        this.sampleInfo.copy_from(sampleInfo);
    }

    public ice.InfusionStatus getInfusionStatus() {
        return infusionStatus;
    }

    public SampleInfo getSampleInfo() {
        return sampleInfo;
    }

    @Override
    public PumpModel getParent() {
        return parent;
    }

    @Override
    public void setStop(boolean stop) {
        parent.setStop(this, stop);
    }
}
