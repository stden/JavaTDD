import org.codehaus.jackson.map.ObjectMapper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Returns objects converted to string (in JSON, in Initialization code)
 */
public class Dumper {

    private static ObjectMapper mapper = new ObjectMapper();
    private static Map<Class, String> types = new HashMap<Class, String>();

    /**
     * Primitive Data Types
     */
    static {
        types.put(Byte.class, "byte");
        types.put(Short.class, "short");
        types.put(Integer.class, "int");
        types.put(Long.class, "long");
        types.put(Float.class, "float");
        types.put(Double.class, "double");
        types.put(Boolean.class, "boolean");
        types.put(Character.class, "char");
        types.put(Void.class, "void");
    }

    /**
     * Converting object to string in JSON-format
     *
     * @param o Object to convert
     * @return JSON-string
     */
    public static String toJSON(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * Converting to assert'y or sequence of asserts
     *
     * @param varName varia ble name
     * @param o       object with a value for generation
     * @return String with set of assertions
     */
    public static String toAssert(String varName, Object o) {
        if (o == null)
            return "assertNull(" + varName + ");\n";
        if (o instanceof Integer || o instanceof Enum || o instanceof Double || o instanceof String)
            return "assertEquals(" + dump(o) + ", " + varName + ");\n";
        String res = "";
        for (Field field : o.getClass().getFields()) {
            try {
                String fieldName = varName + "." + field.getName();
                Object value = field.get(o);
                res += toAssert(fieldName, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public static String init(String varName, Object o) {
        if (o == null)
            return "Object " + varName + " = null;\n";
        if (types.containsKey(o.getClass()) || o instanceof Enum || o instanceof String)
            return type(o) + " " + varName + " = " + dump(o) + ";\n";
        String res = type(o) + " " + varName + " = new " + type(o) + "();\n";
        for (Field field : o.getClass().getFields()) {
            try {
                String fieldName = varName + "." + field.getName();
                Object value = field.get(o);
                res += toInit2(fieldName, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    private static String toInit2(String varName, Object o) {
        if (o == null)
            return "Object " + varName + " = null;\n";
        if (types.containsKey(o.getClass()) || o instanceof Enum || o instanceof String)
            return varName + " = " + dump(o) + ";\n";
        String res = varName + " = new " + type(o) + "();\n";
        for (Field field : o.getClass().getFields()) {
            try {
                String fieldName = varName + "." + field.getName();
                Object value = field.get(o);
                res += toInit2(fieldName, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * @return string to initialize value of this type
     */
    public static String dump(Object o) {
        if (o == null)
            return "null";
        if (o instanceof String)
            return String.format("\"%s\"", o);
        if (o instanceof Enum)
            return String.format("%s.%s", o.getClass().getSimpleName(), o);
        if (o instanceof Float)
            return o + "f";
        if (o instanceof Character)
            return String.format("'%s'", o);
        return o.toString();
    }

    /**
     * Название типа по переменной
     *
     * @param o Переменная любого типа
     * @return Название типа
     */
    public static String type(Object o) {
        if (o == null)
            return "Object";
        if (types.containsKey(o.getClass()))
            return types.get(o.getClass());
        return o.getClass().getSimpleName();
    }
}
