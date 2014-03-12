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

import java.nio.ByteBuffer;
import java.util.Calendar;

import org.mdpnp.devices.io.util.Bits;

/**
 * @author Jeff Plourde
 *
 */
public class AbsoluteTime implements Value {

    private final java.util.Date date = new java.util.Date();
    private final java.util.Calendar calendar = Calendar.getInstance();
    private boolean isValid = false;

    public AbsoluteTime() {

    }

    public void setNow() {
        setTimeInMillis(System.currentTimeMillis());
    }

    public void setTimeInMillis(long tm) {
        date.setTime(tm);
        isValid = true;
    }

    public void setDate(java.util.Date date) {
        if (null != date) {
            setTimeInMillis(date.getTime());
        } else {
            isValid = false;
        }
    }

    public java.util.Date getDate() {
        return isValid ? this.date : null;
    }

    @Override
    public void format(ByteBuffer bb) {
        if (isValid) {
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            BCD.put(bb, (short) (year / 100));
            BCD.put(bb, (short) (year % 100));
            BCD.put(bb, (short) (calendar.get(Calendar.MONTH) + 1));
            BCD.put(bb, (short) calendar.get(Calendar.DAY_OF_MONTH));
            BCD.put(bb, (short) calendar.get(Calendar.HOUR_OF_DAY));
            BCD.put(bb, (short) calendar.get(Calendar.MINUTE));
            BCD.put(bb, (short) calendar.get(Calendar.SECOND));
            // per spec sec_fractions is ignored
            // TODO that is probably Philips-specific!!!
            BCD.put(bb, (short) 0); // sec_fractions
        } else {
            Bits.putUnsignedInt(bb, 0xFFFFFFFFL);
            Bits.putUnsignedInt(bb, 0xFFFFFFFFL);
        }
    }

    @Override
    public void parse(ByteBuffer bb) {
        short century, year, month, day, hour, minute, second, sec_fractions;
        century = BCD.get(bb);
        year = BCD.get(bb);
        month = BCD.get(bb);
        day = BCD.get(bb);
        hour = BCD.get(bb);
        minute = BCD.get(bb);
        second = BCD.get(bb);
        sec_fractions = BCD.get(bb);

        if (century == 0xFF && year == 0xFF && month == 0XFF && day == 0xFF && hour == 0xFF && minute == 0xFF && second == 0xFF
                && sec_fractions == 0xFF) {
            isValid = false;
        } else {
            isValid = true;
            calendar.set(Calendar.YEAR, century * 100 + year);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, second);
            // per spec sec_fractions is ignored
            // TODO that is probably Philips-specific!!!
            date.setTime(calendar.getTimeInMillis());
        }
    }

    @Override
    public java.lang.String toString() {
        return isValid ? ("" + date) : "INVALID";
    }
}
