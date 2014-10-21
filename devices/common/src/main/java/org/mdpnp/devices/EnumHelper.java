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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Jeff Plourde
 * 
 */
public class EnumHelper {
    private static final Logger log = LoggerFactory.getLogger(EnumHelper.class);

    public static final <T extends Enum<T>> Map<Byte, T> build(Class<T> cls, String resourceName, int[] lineNumber) throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, IOException {
        Map<Byte, T> fromByte = new HashMap<Byte, T>();
        Field byteField = cls.getDeclaredField("b");
        Field unitField = null;
        try {
            unitField = cls.getDeclaredField("u");
            unitField.setAccessible(true);
        } catch (NoSuchFieldException nsfe) {
            unitField = null;
        }
        byteField.setAccessible(true);

        InputStream is = cls.getResource(resourceName).openStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        lineNumber[0] = 0;
        
        while (null != (line = br.readLine())) {
            lineNumber[0]++;
            String[] arr = line.split("\t");
            java.lang.Byte b = (byte) (0xFF & Integer.decode(arr[0]));
            T t = Enum.valueOf(cls, arr[1]);
            if (fromByte.containsKey(b)) {
                throw new ExceptionInInitializerError("Multiple " + cls.getSimpleName() + "s  (" + fromByte.get(b) + "," + t + ") for hex code "
                        + Integer.toHexString(b));
            }

            fromByte.put(b, t);
            byteField.setByte(t, b);
            if (unitField != null) {
                if (arr.length > 2) {
                    unitField.set(t, Unit.valueOf(arr[2]));
                } else {
                    log.warn("no units for " + t);
                }
            }
        }
        br.close();
        is.close();
        return fromByte;
    }
}
