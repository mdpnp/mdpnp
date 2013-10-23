package org.mdpnp.devices.philips.intellivue.util;

import static org.mdpnp.devices.io.util.Bits.getUnsignedShort;
import static org.mdpnp.devices.io.util.Bits.putUnsignedShort;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;

public class Util {
    private Util() {

    }





    public static  class PrefixLengthShort {
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
            if(clear) {
                list.clear();
            }
            try {
                int count = getUnsignedShort(bb);
                int length = getUnsignedShort(bb);
                for(int i = 0; i < count; i++) {
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
            if(clear) {
                list.clear();
            }

            int count = getUnsignedShort(bb);
            int length = getUnsignedShort(bb);
            for(int i = 0; i < count; i++) {
                T p = b.build();
                p.parse(bb);
                list.add(p);
            }
        }

        public static void write(ByteBuffer bb, Collection<? extends Formatable> list) {
            if(list.isEmpty()) {
                putUnsignedShort(bb, 0);
                putUnsignedShort(bb, 0);
            } else {
                putUnsignedShort(bb, list.size());
                putUnsignedShort(bb, 0);
                int pos = bb.position();
                for(Formatable f : list) {
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
