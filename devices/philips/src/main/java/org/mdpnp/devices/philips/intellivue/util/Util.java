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
package org.mdpnp.devices.philips.intellivue.util;

import static org.mdpnp.devices.io.util.Bits.getUnsignedShort;
import static org.mdpnp.devices.io.util.Bits.putUnsignedShort;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;

/**
 * @author Jeff Plourde
 *
 */
public class Util {
    private Util() {

    }

    public static class PrefixLengthShort {
        public interface Builder<T extends Parseable> {
            T build();
        }

        public void begin(ByteBuffer bb) {
            putUnsignedShort(bb, 0);
        }

        public static <T extends Parseable> void read(ByteBuffer bb, Collection<T> list, Class<? extends T> clazz) {
            read(bb, list, true, clazz);
        }

        @SuppressWarnings("unused")
        public static <T extends Parseable> void read(ByteBuffer bb, Collection<T> list, boolean clear, Class<? extends T> clazz) {
            if (clear) {
                list.clear();
            }
            try {
                int count = getUnsignedShort(bb);
                int length = getUnsignedShort(bb);
                for (int i = 0; i < count; i++) {
                    T p = clazz.newInstance();
                    p.parse(bb);
                    list.add(p);
                }
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            }

        }

        public static <T extends Parseable> void read(ByteBuffer bb, Collection<T> list, Builder<T> b) {
            read(bb, list, true, b);
        }

        @SuppressWarnings("unused")
        public static <T extends Parseable> void read(ByteBuffer bb, Collection<T> list, boolean clear, Builder<T> b) {
            if (clear) {
                list.clear();
            }

            int count = getUnsignedShort(bb);
            int length = getUnsignedShort(bb);
            for (int i = 0; i < count; i++) {
                T p = b.build();
                p.parse(bb);
                list.add(p);
            }
        }

        public static void write(ByteBuffer bb, Collection<? extends Formatable> list) {
            if (list.isEmpty()) {
                putUnsignedShort(bb, 0);
                putUnsignedShort(bb, 0);
            } else {
                putUnsignedShort(bb, list.size());
                putUnsignedShort(bb, 0);
                int pos = bb.position();
                for (Formatable f : list) {
                    f.format(bb);
                }
                int length = bb.position() - pos;
                bb.position(pos - 2);
                putUnsignedShort(bb, length);
                bb.position(bb.position() + length);
            }
        }

        public static void write(ByteBuffer bb, Formatable f) {
            putUnsignedShort(bb, 0);
            int pos = bb.position();
            f.format(bb);
            int length = bb.position() - pos;
            bb.position(pos - 2);
            putUnsignedShort(bb, length);
            bb.position(bb.position() + length);
        }
    }
}
