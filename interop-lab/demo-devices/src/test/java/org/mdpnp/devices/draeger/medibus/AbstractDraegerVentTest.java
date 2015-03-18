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
package org.mdpnp.devices.draeger.medibus;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class AbstractDraegerVentTest {

    private static final Logger log = LoggerFactory.getLogger(AbstractDraegerVent.class);

    @Test
    public void testLoadMap() throws Exception {

        URL f = AbstractDraegerVent.class.getResource("draeger.map");

        Map<Enum<?>, String> numerics = new HashMap<>();
        Map<Enum<?>, String> waveforms = new HashMap<>();
        AbstractDraegerVent.loadMap(f, numerics, waveforms);
        log.info(numerics.toString());
        log.info(waveforms.toString());
        Assert.assertEquals("Failed to read numerics",  8, numerics.size());
        Assert.assertEquals("Failed to read waveforms", 5, waveforms.size());
    }

    @Test
    public void testClockImpl() throws Exception {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,        1986);
        cal.set(Calendar.MONTH,       Calendar.JANUARY);
        cal.set(Calendar.DATE,        28);
        cal.set(Calendar.HOUR_OF_DAY, 11);
        cal.set(Calendar.MINUTE,      39);
        cal.set(Calendar.SECOND,      13);
        cal.set(Calendar.MILLISECOND, 0);


        AbstractDraegerVent.DraegerVentClock deviceClock = new AbstractDraegerVent.DraegerVentClock() {
            @Override
            long systemCurrentTimeMillis() {
                return cal.getTimeInMillis();
            }
        };

        // start with the system clock being in sync with device
        long delta0 = deviceClock.receiveDateTime(new Date(cal.getTimeInMillis()));
        Assert.assertEquals("Invalid time delta",  0L, delta0);

        long dt0 = deviceClock.instant().getTime().toEpochMilli();
        Assert.assertEquals("Invalid device time",  new Date(cal.getTimeInMillis()), new Date(dt0));

        // pretend the device got faster than the system clock
        cal.add(Calendar.MILLISECOND, -20);

        long dt1 = deviceClock.instant().getTime().toEpochMilli();
        Assert.assertEquals("Invalid device time",  new Date(dt0), new Date(dt1));

        // device is still slower, but there is a drift.
        cal.add(Calendar.MILLISECOND, 200);
        long delta1 = deviceClock.receiveDateTime(new Date(cal.getTimeInMillis()-50));
        Assert.assertEquals("Invalid time delta", -50L, delta1);

        long dt3 = deviceClock.instant().getTime().toEpochMilli();
        Assert.assertEquals("Invalid device time",  new Date(cal.getTimeInMillis()-50L), new Date(dt3));

    }
}
