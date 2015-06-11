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
package org.mdpnp.devices.philips.intellivue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.devices.philips.intellivue.data.Label;
import org.mdpnp.devices.philips.intellivue.data.ObservedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DemoIntellivueTest {

    private final Logger log = LoggerFactory.getLogger(DemoIntellivueTest.class);

    @Test
    public void testLoadMap() throws Exception {
        Map<ObservedValue, String> numericMetricIds = new HashMap<ObservedValue, String>();
        Map<ObservedValue, Label> numericLabels = new HashMap<ObservedValue, Label>();
        Map<ObservedValue, String> sampleArrayMetricIds = new HashMap<ObservedValue, String>();
        Map<ObservedValue, Label> sampleArrayLabels = new HashMap<ObservedValue, Label>();
        DemoEthernetIntellivue.loadMap(numericMetricIds, numericLabels, sampleArrayMetricIds, sampleArrayLabels);

        URL url = DemoEthernetIntellivue.class.getResource("intellivue.map");
        if(url==null)
            throw new IllegalArgumentException("Cannot locate intellivue.map");
        try {
            InputStream is = url.openStream();
            DemoEthernetIntellivue.loadMap(is, numericMetricIds, numericLabels, sampleArrayMetricIds, sampleArrayLabels);
            is.close();
        }
        catch (IOException ex) {
            throw new IllegalStateException("Failed to load maps", ex);
        }

        log.info("numericMetricIds" + numericMetricIds);
        log.info("numericLabels" + numericLabels);
        log.info("sampleArrayMetricIds" + sampleArrayMetricIds);
        log.info("sampleArrayLabels" + sampleArrayLabels);

//        Assert.assertEquals("Failed to load numerics ids",    15, numericMetricIds.size());
//        Assert.assertEquals("Failed to load numerics labels", 15, numericLabels.size());
//        Assert.assertEquals("Failed to load array metrics",    7, sampleArrayMetricIds.size());
//        Assert.assertEquals("Failed to load array labels ",    7, sampleArrayLabels.size());
        Assert.assertEquals("Unequal count of numeric labels and nomenclature codes", numericMetricIds.size(), numericLabels.size());
        Assert.assertEquals("Unequal count of sample array labels and nomenclature codes", sampleArrayMetricIds.size(), sampleArrayLabels.size());
    }
}
