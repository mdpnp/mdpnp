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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeff Plourde
 *
 */
public class OrdinalEnum {
    public interface ShortType {
        short asShort();
    }

    public static final <T extends ShortType> Map<Short, T> buildShort(Class<T> cls) {
        Map<Short, T> map = new HashMap<Short, T>();
        for (T t : cls.getEnumConstants()) {
            if (map.containsKey(t.asShort())) {
                throw new IllegalStateException("" + t.asShort() + " is ordinal for " + t + " and " + map.get(t.asShort()));
            }
            map.put(t.asShort(), t);
        }
        return map;
    }

    public interface IntType {
        int asInt();
    }

    public static final <T extends IntType> Map<Integer, T> buildInt(Class<T> cls) {
        Map<Integer, T> map = new HashMap<Integer, T>();
        for (T t : cls.getEnumConstants()) {
            if (map.containsKey(t.asInt())) {
                throw new IllegalStateException("" + t.asInt() + " is ordinal for " + t + " and " + map.get(t.asInt()));
            }
            map.put(t.asInt(), t);
        }
        return map;
    }

    public interface LongType {
        long asLong();
    }

    public static final <T extends LongType> Map<Long, T> buildLong(Class<T> cls) {
        Map<Long, T> map = new HashMap<Long, T>();
        for (T t : cls.getEnumConstants()) {
            if (map.containsKey(t.asLong())) {
                throw new IllegalStateException("" + t.asLong() + " is ordinal for " + t + " and " + map.get(t.asLong()));
            }
            map.put(t.asLong(), t);
        }
        return map;
    }
}
