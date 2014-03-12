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
package org.mdpnp.devices.philips.intellivue.data;

import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

/**
 * @author Jeff Plourde
 *
 */
public enum MetricCategory implements OrdinalEnum.IntType {
    /**
     * not specified
     */
    MCAT_UNSPEC(0),
    /**
     * automatic measurement
     */
    AUTO_MEASUREMENT(1),
    /**
     * manual measurement
     */
    MANUAL_MEASUREMENT(2),
    /**
     * automatic setting
     */
    AUTO_SETTING(3),
    /**
     * manual setting
     */
    MANUAL_SETTING(4),
    /**
     * automatic calculation, e.g. differential temperature
     */
    AUTO_CALCULATION(5),
    /**
     * manual calculation
     */
    MANUAL_CALCULATION(6),
    /**
     * this measurement may change its category during operation or may be used
     * in various modes.
     */
    MULTI_DYNAMIC_CAPABILITIES(50),
    /**
     * measurement is automatically adjusted for patient temperature
     */
    AUTO_ADJUST_PAT_TEMP(128),
    /**
     * measurement manually adjusted for patient temperature
     */
    MANUAL_ADJUST_PAT_TEMP(129),
    /**
     * this is not a measurement, but an alarm limit setting
     */
    AUTO_ALARM_LIMIT_SETTING(130);

    private final int x;

    private static final Map<Integer, MetricCategory> map = OrdinalEnum.buildInt(MetricCategory.class);

    private MetricCategory(int x) {
        this.x = x;
    }

    public static final MetricCategory valueOf(int x) {
        return map.get(x);
    }

    public int asInt() {
        return x;
    }
}
