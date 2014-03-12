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
package org.mdpnp.apps.testapp.co2;

import com.rti.dds.subscription.SampleInfo;

import ice.SampleArray;

public class CapnoImpl implements Capno {
    private final CapnoModel parent;
    private final ice.SampleArray sampleArray = new ice.SampleArray();
    private final SampleInfo sampleInfo = new SampleInfo();
    
    public CapnoImpl(CapnoModel parent, ice.SampleArray sampleArray, SampleInfo sampleInfo) {
        this.parent = parent;
        this.sampleArray.copy_from(sampleArray);
        this.sampleInfo.copy_from(sampleInfo);
    }
    
    @Override
    public SampleInfo getSampleInfo() {
        return sampleInfo;
    }
    
    @Override
    public SampleArray getSampleArray() {
        return sampleArray;
    }
    
    @Override
    public CapnoModel getParent() {
        return parent;
    }
    
    @Override
    public String toString() {
        return sampleArray.unique_device_identifier;
    }
}
