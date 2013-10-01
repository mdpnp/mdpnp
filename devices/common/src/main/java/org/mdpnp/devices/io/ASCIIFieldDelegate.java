/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ASCIIFieldDelegate implements Runnable {
    private final Object target;
    private final LineInfo[] lineInfo;

    private static final Logger log = LoggerFactory.getLogger(ASCIIFieldDelegate.class);

    private Class<?> componentType(Class<?> cls) {
        if(cls.isArray()) {
            return componentType(cls.getComponentType());
        } else {
            return cls;
        }
    }

    public ASCIIFieldDelegate(URL properties) throws NoSuchFieldException, SecurityException, IOException {
        this(null, properties);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ASCIIFieldDelegate(Object target, URL properties) throws IOException, NoSuchFieldException, SecurityException {
        target = null == target ? this : target;
        this.target = target;
        InputStream is = properties.openStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;

        List<LineInfo> lineInfo = new ArrayList<LineInfo>();
        Class<?> targetType = target.getClass();

        while(null != (line = br.readLine())) {
            line = line.replaceAll("\\#.*$", "");
            if(line.startsWith("\t\t")) {
                LineInfo.Field f = lineInfo.get(lineInfo.size()-1).getCurrentField();
                line = line.substring(2, line.length());
                Class<?> type = componentType(f.getField().getType());
                if(type.isEnum()) {
                    String [] vals = line.split("\t");
                    f.putEnumValue(vals[0], Enum.valueOf( (Class<? extends Enum>) type, vals[1]));
                } else if(Date.class.equals(type)) {
                    f.setDateFormat(new SimpleDateFormat(line));
                } else {
                    log.warn("Not parsing:"+line);
                }
            } else if(line.startsWith("\t")) {
                line = line.substring(1, line.length());
                if(!lineInfo.get(lineInfo.size()-1).setFireMethod(targetType, line)) {
                    lineInfo.get(lineInfo.size()-1).addField(targetType, line);
                }
            } else if(line.length() > 0){
                lineInfo.add(new LineInfo(line));
            }
        }

        br.close();
        is.close();
        this.lineInfo = lineInfo.toArray(new LineInfo[0]);
    }

    protected final static java.lang.reflect.Field fieldIfAvailable(Class<?> type, String fieldName) {
        try {
            return type.getDeclaredField(fieldName);
        } catch (NoSuchFieldException nsfe) {

        }
        Class<?> parent = type.getSuperclass();
        if(null != parent) {
            return fieldIfAvailable(parent, fieldName);
        } else {
            return null;
        }
    }

    protected final static Method methodIfAvailable(Class<?> type, String name, Class<?> [] params, Class<?> returnType) {
        try {
            java.lang.reflect.Method method = type.getDeclaredMethod(name, params);
            if(null != returnType) {
                if(!returnType.equals(method.getReturnType())) {
                    return null;
                }
            }
            return method;
        } catch(Throwable t) {
            return null;
        }
    }

    private static class LineInfo {
        private final Pattern pattern;
        private Method fireMethod;

        public static class Field {
            private final java.lang.reflect.Field field;
            private final java.lang.reflect.Method method;
            private final java.lang.reflect.Method filter;
            private java.util.Map<String, Object> enumValues;
            private DateFormat dateFormat;



            public Field(Class<?> type, String fieldName) throws NoSuchFieldException, SecurityException {
                String[] fieldFilter = fieldName.split("\t");
                if(fieldFilter.length>1) {
                    this.filter = methodIfAvailable(type, fieldFilter[1], new Class<?>[] {String.class}, String.class);
                } else {
                    this.filter = null;
                }
                fieldName = fieldFilter[0];

                this.method = methodIfAvailable(type, fieldName, new Class<?>[] {String.class}, null);
                if(null == this.method) {
                    this.field = fieldIfAvailable(type, fieldName);
                    if(null != this.field) {
                        this.field.setAccessible(true);
                    }
                } else {
                    this.method.setAccessible(true);
                    this.field = null;
                }
            }

            public String applyFilter(Object target, String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if(null != filter) {
                    return (String) filter.invoke(target, val);
                } else {
                    return val;
                }
            }

//			public java.lang.reflect.Method getFilter() {
//				return filter;
//			}

            public DateFormat getDateFormat() {
                return dateFormat;
            }

            public void setDateFormat(DateFormat dateFormat) {
                this.dateFormat = dateFormat;
            }

            public Object getEnumValue(String s) {
                if(null == enumValues) {
                    return null;
                } else {
                    return enumValues.get(s);
                }
            }

            public void putEnumValue(String s, Object o) {
                if(null == enumValues) {
                    enumValues = new HashMap<String, Object>();
                }
                enumValues.put(s, o);
            }
            public java.lang.reflect.Field getField() {
                return field;
            }
            public java.lang.reflect.Method getMethod() {
                return method;
            }

        }

        private final List<Field> fields = new ArrayList<Field>();

        public LineInfo(String pattern) {
            this.pattern = Pattern.compile(pattern);
        }

        public final boolean setFireMethod(Class<?> type, String name) {
            Method fireMethod = methodIfAvailable(type, name, new Class<?>[0], null);
            if(null == fireMethod) {
                fireMethod = methodIfAvailable(type, name, new Class<?>[] {type}, null);
            }
            if(null != fireMethod) {
                fireMethod.setAccessible(true);
                this.fireMethod = fireMethod;
                return true;
            } else {
                return false;
            }
        }

        public final void addField(Class<?> type, String name) throws NoSuchFieldException, SecurityException {
            fields.add(new Field(type, name));
        }

        public final Field getCurrentField() {
            return fields.isEmpty() ? null : fields.get(fields.size() - 1);
        }

        private static final Object map(String val, Class<?> fieldType, Object enumValue, DateFormat dateFormat) throws ParseException {
            if(null == val) {
                return null;
            } else {
                if(String.class.equals(fieldType)) {
                    return val;
                } else if(Integer.class.equals(fieldType)) {
                    return Integer.parseInt(val);
                } else if(Long.class.equals(fieldType)) {
                    return Long.parseLong(val);
                } else if(Short.class.equals(fieldType)) {
                    return Short.parseShort(val);
                } else if(Character.class.equals(fieldType)) {
                    if(val.length()>0) {
                        return val.charAt(0);
                    } else {
                        return null;
                    }
                } else if(Byte.class.equals(fieldType)) {
                    return Byte.parseByte(val);
                } else if(Double.class.equals(fieldType)) {
                    return Double.parseDouble(val);
                } else if(Float.class.equals(fieldType)) {
                    return Float.parseFloat(val);
                } else if(fieldType.isEnum()) {
                    return enumValue;
                } else if(Date.class.equals(fieldType)) {
                    return dateFormat.parse(val);
                } else {
                    log.warn("Unsupported field type:"+fieldType);
                    return null;
                }
            }
        }
        private static final Object[] NULL = new Object[] {null};
        private static final void setNullField(Object target, Field field) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
            if(null != field.getMethod()) {
                field.getMethod().invoke(target, NULL);
            } else if(field.getField() != null) {
                field.getField().set(target, null);
            }
        }

        private static final void setField(String val, Field field, Object target) throws IllegalArgumentException, IllegalAccessException, ParseException, InvocationTargetException {
            java.lang.reflect.Field f = field.getField();
            java.lang.reflect.Method m = field.getMethod();
            if(null != m) {
                m.invoke(target,  map(val, m.getParameterTypes()[0], field.getEnumValue(val), field.getDateFormat()));
            } else if(null != f) {
                f.set(target, map(val, field.getField().getType(), field.getEnumValue(val), field.getDateFormat()));
            }
        }

        public boolean parseLine(String line, Object target) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
            Matcher m;
            if( (m=pattern.matcher(line)).matches()) {
                for(int i = 0; i < fields.size(); i++) {
                    try {
                        String val = m.group(i+1);
                        val = fields.get(i).applyFilter(target, val);
                        if(fields.get(i).getField() != null && fields.get(i).getField().getType().isArray()) {
                            Class<?> arrayType = fields.get(i).getField().getType();
                            Object array = fields.get(i).getField().get(target);
                            int len = java.lang.reflect.Array.getLength(array);
                            for(int j = 0; j < len; j++) {
                                if(m.groupCount()>=(i+1+j)) {
                                    val = fields.get(i).applyFilter(target, m.group(i+1+j));
                                    java.lang.reflect.Array.set(array, j, map(val, arrayType.getComponentType(), fields.get(i).getEnumValue(val), fields.get(i).getDateFormat()));
                                } else {
                                    java.lang.reflect.Array.set(array, j, null);
                                }
                            }
                        } else {
                            setField(val, fields.get(i), target);
                        }
                    } catch (Throwable t) {
                        setNullField(target, fields.get(i));
//						t.printStackTrace();
                    }
                }
                if(null != fireMethod) {
                    try {
                        if(fireMethod.getParameterTypes().length == 1) {
                            fireMethod.invoke(target, target);
                        } else if(fireMethod.getParameterTypes().length == 0) {
                            fireMethod.invoke(target);
                        }
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean parseLine(String line) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        for(LineInfo li : lineInfo) {
            if(li.parseLine(line, target)) {
                return true;
            }
        }
        return false;
    }

    private InputStream inputStream;

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line = null;
        try {
            while(null != (line = reader.readLine())) {
                if(!parseLine(line)) {
                    log.info("Unknown line:"+line);
                }
            }

        } catch(Throwable t) {
            t.printStackTrace();
        }
    }
}
