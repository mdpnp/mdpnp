package org.mdpnp.apps.fxbeans;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Generator {
    
    public static void writeField(Field f, PrintStream out, String prefix, List<String> setters) {
        Class<?> type = f.getType();
        String typeName = null, initialValue = null, templateName = null;
        
        if(boolean.class.equals(type)) {
            typeName = "Boolean";
            initialValue = "false";
        } else if(byte.class.equals(type)) {
            typeName = "Byte";
            initialValue = "0";
        } else if(short.class.equals(type)) {
            typeName = "Short";
            initialValue = "0";
        } else if(int.class.equals(type)) {
            typeName = "Integer";
            initialValue = "0";
        } else if(long.class.equals(type)) {
            typeName = "Long";
            initialValue = "0";
        } else if(float.class.equals(type)) {
            typeName = "Float";
            initialValue = "0f";
        } else if(double.class.equals(type)) {
            typeName = "Double";
            initialValue = "0.0";
        } else if(String.class.equals(type)) {
            typeName = "String";
            initialValue ="\"\"";
        } else if(ice.Time_t.class.equals(type)) {
            typeName = "Object";
            initialValue = "null";
            templateName = "java.util.Date";
        } else {
            typeName = "Object";
            initialValue = "null";
            templateName = type.getCanonicalName();
        }
        
        if(typeName != null) {
            out.append(prefix).append("private ").append(typeName).append("Property").append(templateName!=null?("<"+templateName+">"):"").append(" ").append(f.getName()).append(";\n\n");
            
            out.append(prefix).append("public ").append(typeName).append("Property").append(templateName!=null?("<"+templateName+">"):"").append(" ").append(f.getName()).append("Property() {\n");
            out.append(prefix).append("    if(null == this.").append(f.getName()).append(") {\n");
            out.append(prefix).append("        this.").append(f.getName()).append(" = new Simple").append(typeName).append("Property").append(templateName!=null?("<"+templateName+">"):"").append("(this, \"").append(f.getName()).append("\");\n");
            out.append(prefix).append("    }\n");
            out.append(prefix).append("    ").append("return ").append(f.getName()).append(";\n");
            out.append(prefix).append("}\n\n");
            
            out.append(prefix).append("public ").append(templateName != null ? templateName : type.getSimpleName()).append(" get").append(Character.toUpperCase(f.getName().charAt(0))).append(f.getName().substring(1)).append("() {\n");
            out.append(prefix).append("    return ").append(f.getName()).append("Property().get();\n");
            out.append(prefix).append("}\n\n");
            
            out.append(prefix).append("public void set").append(Character.toUpperCase(f.getName().charAt(0))).append(f.getName().substring(1)).append("(").append(templateName!=null?templateName:type.getSimpleName()).append(" ").append(f.getName()).append(") {\n");
            out.append(prefix).append("    this.").append(f.getName()).append("Property().set(").append(f.getName()).append(");\n");
            out.append(prefix).append("}\n\n");
            
            
            StringBuffer sb = new StringBuffer();
            if(ice.Time_t.class.equals(type)) {
                sb.append("set").append(Character.toUpperCase(f.getName().charAt(0))).append(f.getName().substring(1)).append("(new java.util.Date(v.").append(f.getName()).append(".sec * 1000L + v.").append(f.getName()).append(".nanosec / 1000000L").append("));");
            } else if(ice.Values.class.equals(type)) {
                sb.append("Number[] values = new Number[v.").append(f.getName()).append(".userData.size()];");
                setters.add(sb.toString());
                sb.delete(0, sb.length());
                sb.append("for(int i = 0; i < values.length; i++) {");
                setters.add(sb.toString());
                sb.delete(0, sb.length());
                sb.append("    values[i] = v.").append(f.getName()).append(".userData.getFloat(i);");
                setters.add(sb.toString());
                sb.delete(0, sb.length());
                sb.append("}");
                setters.add(sb.toString());
                sb.delete(0, sb.length());
                sb.append(f.getName()+"Property().set(values);");
            } else {
                sb.append("set").append(Character.toUpperCase(f.getName().charAt(0))).append(f.getName().substring(1)).append("(v.").append(f.getName()).append(");");
            }
            setters.add(sb.toString());
        }
    }
    
    public static void generate(Class<?> cls, PrintStream out) {
        out.append("package ").append(cls.getPackage().getName()).append(";\n\n");
        out.append("import javafx.beans.property.*;\n");
        out.append("import org.mdpnp.apps.fxbeans.AbstractFx;\n");
        out.append("import com.rti.dds.subscription.SampleInfo;\n");
        out.append("import org.mdpnp.apps.fxbeans.Updatable;\n\n");
        
        out.append("public class ").append(cls.getSimpleName()).append("Fx extends AbstractFx<").append(cls.getCanonicalName()).append("> implements Updatable<").append(cls.getCanonicalName()).append("> {\n");
        
        out.append("    public ").append(cls.getSimpleName()).append("Fx() {\n");
        out.append("    }\n\n");
        
        List<String> setters = new ArrayList<String>();
        
        for(Field f : cls.getFields()) {
            writeField(f, out, "    ", setters);
        }
        
        out.append("    @Override\n");
        out.append("    public void update(").append(cls.getCanonicalName()).append(" v, SampleInfo s) {\n");
        for(String set : setters) {
            out.append("        ").append(set).append("\n");
        }
        out.append("        super.update(v, s);\n");
        out.append("    }\n");
        
        
        out.append("}\n");
    }
    
    public static void main(String[] args) throws ClassNotFoundException {
        generate(Class.forName(args[0]), System.out);
    }
}
