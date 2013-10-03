package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;
import org.mdpnp.devices.philips.intellivue.attribute.Attribute;
import org.mdpnp.devices.philips.intellivue.attribute.AttributeFactory;
import org.mdpnp.devices.philips.intellivue.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeValueList implements Parseable, Formatable, Util.PrefixLengthShort.Builder<Attribute<?>> {
    private final List<Attribute<?>> list = new java.util.ArrayList<Attribute<?>>();
    private final List<AttributeValueAssertion> recycle = new java.util.ArrayList<AttributeValueAssertion>();
    private final Map<OIDType, Attribute<?>> map = new java.util.HashMap<OIDType, Attribute<?>>();

    @Override
    public Attribute<?> build() {
        return newAVA();
    }

    private AttributeValueAssertion newAVA() {
        if(recycle.isEmpty()) {
            return new AttributeValueAssertion();
        } else {
            return recycle.remove(0);
        }
    }

    public void reset() {
        for(Formatable f : list) {
            if(f instanceof AttributeValueAssertion) {
                recycle.add((AttributeValueAssertion) f);
            }
        }
        list.clear();
        map.clear();
    }

    @Override
    public void format(ByteBuffer bb) {
        Util.PrefixLengthShort.write(bb, list);
    }



    @Override
    public void parse(ByteBuffer bb) {
        parse(bb, true);
    }

    public void parseMore(ByteBuffer bb) {
        parse(bb, false);
    }

    private void parse(ByteBuffer bb, boolean clear) {
        Util.PrefixLengthShort.read(bb, list, clear, this);


        for(Attribute<?> a : list) {
            map.put(a.getOid(), a);
        }

    }

    public void put(OIDType type, Attribute<?> a) {
        list.add(a);
        map.put(type, a);


        // TODO this is ugly
//		ByteBuffer bb = ByteBuffer.allocate(5000);
//		bb.order(ByteOrder.BIG_ENDIAN);
//		f.format(bb);
//		bb.flip();
//		byte[] buf = new byte[bb.remaining()];
//		bb.get(buf);
//		add(type, buf);
    }

    public boolean remove(OIDType type) {
        Attribute<?> a = map.remove(type);
        if(null != a) {
            list.remove(a);
            if(a instanceof AttributeValueAssertion) {
                recycle.add((AttributeValueAssertion)a);
            }
            return true;
        } else {
            return false;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(AttributeValueList.class);

    public <T extends Value> Attribute<T> getAttribute(AttributeId attrId, Class<T> valueClass) {
        return getAttribute(attrId, valueClass, null);
    }

    @SuppressWarnings("unchecked")
    public <T extends Value> Attribute<T> getAttribute(OIDType oid, Class<T> valueClass, Attribute<T> attr) {
        Attribute<?> a = get(oid);
        if(a != null && valueClass.isInstance(a.getValue())) {
            return (Attribute<T>) a;
        } else {
            attr = null == attr ? AttributeFactory.getAttribute(oid, valueClass) : attr;
            return get(oid, attr) ? attr : null;
        }
    }

    public <T extends Value> Attribute<T> getAttribute(AttributeId attrId, Class<T> valueClass, Attribute<T> attr) {
        return getAttribute(attrId.asOid(), valueClass, attr);
    }

    @SuppressWarnings("unchecked")
    public <T extends Value> Attribute<T> getAttribute(Attribute<T> attr) {
        return getAttribute(attr.getOid(), (Class<T>)attr.getValue().getClass(), attr);
    }


    public Attribute<?> get(OIDType type) {
        return map.get(type);
    }

    public boolean get(Attribute<?> p) {
        return get(p.getOid(), p);
    }

    public boolean get(OIDType type, Attribute<?> p) {
        Attribute<?> a = map.get(type);
        if(a == null) {
            return false;
        } else if(a instanceof AttributeValueAssertion) {
            if(p == null) {
                return false;
            } else {
                int idx = list.indexOf(a);
                ByteBuffer bb = ByteBuffer.wrap(((AttributeValueAssertion)a).getValue().getArray());
                bb.order(ByteOrder.BIG_ENDIAN);
                p.parse(bb);
                list.set(idx, p);
                map.put(type, p);
                recycle.add((AttributeValueAssertion) a);
                return true;
            }
        } else if(a.getValue() instanceof ByteArray) {
            int idx = list.indexOf(a);
            ByteArray ba = (ByteArray) a.getValue();
            p.parse(ByteBuffer.wrap(ba.getArray()).order(ByteOrder.BIG_ENDIAN));
            list.set(idx, p);
            map.put(type, p);
            return true;
        } else {
            return false;
        }
    }

    public void add(OIDType type, byte[] value) {
        AttributeValueAssertion ava = newAVA();
        ava.setValue(value);
        ava.setOid(type);
        add(ava);
    }

    public void add(AttributeValueAssertion ava) {
        list.add(ava);
        map.put(ava.getOid(), ava);
    }

    public void add(Attribute<?> attr) {
        put(attr.getOid(), attr);
    }

    public Collection<Attribute<?>> getList() {
        return list;
    }
    public Map<OIDType, Attribute<?>> getMap() {
        return map;
    }

    @Override
    public java.lang.String toString() {
        StringBuilder sb = new StringBuilder("{");
        for(Attribute<?> a : list) {
            if(null == a) {
                sb.append(a).append(",");
            } else {
                if(a instanceof AttributeValueAssertion) {
                    Class<?> cls = AttributeFactory.valueType(a.getOid());
                    if(!ByteArray.class.equals(cls)) {
                        Attribute<?> _a = AttributeFactory.getAttribute(a.getOid());
                        if(null != _a) {
                            if(get(_a)) {
                                a = _a;
                            }
                        }
                    }
                }
                sb.append(AttributeId.valueOf(a.getOid().getType()));
                sb.append("=").append(a);
                sb.append(",");
            }
        }
        if(sb.length() > 1) {
            sb.delete(sb.length() - 1, sb.length());
        }
        sb.append("}");
        return sb.toString();
    }



}
