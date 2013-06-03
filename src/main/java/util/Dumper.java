package util;

import org.codehaus.jackson.map.ObjectMapper;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;

/**
 * Returns objects converted to string (in JSON, in Initialization code)
 */
public class Dumper {

    private static ObjectMapper mapper = new ObjectMapper();
    private static Map<Class, String> types = new HashMap<Class, String>();
    private static HashSet<Class> inlineTypes = new HashSet<Class>();

    /**
     * Primitive Data Types
     */
    static {
        types.put(byte.class, "byte");
        types.put(short.class, "short");
        types.put(int.class, "int");

        types.put(Byte.class, "byte");
        types.put(Short.class, "short");
        types.put(Integer.class, "int");
        types.put(Long.class, "long");
        types.put(Float.class, "float");
        types.put(Double.class, "double");
        types.put(Boolean.class, "boolean");
        types.put(Character.class, "char");
        types.put(Void.class, "void");

        inlineTypes.addAll(types.keySet());
        inlineTypes.add(String.class);
        inlineTypes.add(Calendar.class);
        inlineTypes.add(Date.class);
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
     * @param varName variable name
     * @param o       object with a value for generation
     * @return String with set of assertions
     */
    public static String toAssert(String varName, Object o) {
        Set<Object> objects = new HashSet<Object>();
        return toAssertImpl(varName, o, objects);
    }

    private static String toAssertImpl(String var, Object o, Set<Object> objects) {
        if (objects.contains(o))
            return "";
        objects.add(o);
        if (o == null)
            return String.format("assertNull(%s);\n", var);
        if (o instanceof Boolean)
            return o.equals(true) ? String.format("assertTrue(%s);\n", var) : String.format("assertFalse(%s);\n", var);
        if (o instanceof Calendar)
            return String.format("assertNotNull(%s);\n", var);
        if (hasSimpleType(o))
            return String.format("assertEquals(%s, %s);\n", dump(o), var);
        if (o instanceof ArrayList) {
            return assertArrayList(var, (ArrayList) o);
        }
        StringBuilder res = new StringBuilder();
        for (Field field : o.getClass().getFields()) {
            try {
                if (isConstant(field)) continue;
                String fieldName = String.format("%s.%s", var, field.getName());
                res.append(toAssertImpl(fieldName, field.get(o), objects));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(o.getClass()).getPropertyDescriptors()) {
                // propertyEditor.getReadMethod() exposes the getter
                // btw, this may be null if you have a write-only property
                Method writeMethod = pd.getWriteMethod();
                if (writeMethod == null) continue;
                Method readMethod = pd.getReadMethod();
                Object value = readMethod.invoke(o);
                if (value == null || (value.equals(false)))
                    continue;
                if (hasSimpleType(value)) {
                    res.append(toAssertImpl(String.format("%s.%s()", var, readMethod.getName()), value, objects));
                } else {
                    String name = defaultVarName(value);
                    res.append(String.format("%s %s = %s.%s();\n", type(value), name, var, readMethod.getName()));
                    res.append(toAssertImpl(name, value, objects));
                }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return res.toString();
    }

    private static String assertArrayList(String var, ArrayList list) {
        StringBuilder res = new StringBuilder();
        res.append(String.format("assertEquals(%d, %s.size());\n", list.size(), var));
        for (int i = 0; i < list.size(); i++) {
            res.append(toAssert(var + ".get(" + i + ")", list.get(i)));
        }
        return res.toString();
    }

    public static String init(String var, Object o) {
        if (o == null)
            return String.format("Object %s = null;\n", var);
        if (hasSimpleType(o))
            return String.format("%s %s = %s;\n", type(o), var, dump(o));
        if (o.getClass().isArray()) {
            Class<?> elementType = arrayElementClass(o);
            if (inlineTypes.contains(elementType) || elementType.isEnum())
                return String.format("%s %s = %s;\n", type(o), var, dump(o));
            else {
                StringBuilder res = new StringBuilder();
                StringBuilder list = new StringBuilder(String.format("%s %s = {", type(o), var));
                int length = Array.getLength(o);
                for (int i = 0; i < length; i++) {
                    if (i > 0) list.append(", ");
                    Object value = Array.get(o, i);
                    String name = defaultVarName(value) + i;
                    res.append(init(name, value));
                    list.append(name);
                }
                list.append("};\n");
                res.append(list);
                return res.toString();
            }
        }
        if (o instanceof ArrayList) {
            ArrayList<?> arrayList = (ArrayList) o;

            String type = "String";
            if (arrayList.size() > 0) {
                Object vv = arrayList.get(0);
                type = type(vv);
            }

            StringBuilder res = new StringBuilder("ArrayList<" + type + "> " + var + " = new ArrayList<" + type + ">();\n");
            int index = 0;
            for (Object value : arrayList) {
                if (hasSimpleType(value)) {
                    res.append(var).append(".add(").append(dump(value)).append(");\n");
                } else {
                    String name = defaultVarName(value) + index++;
                    res.append(init(name, value));
                    res.append(var).append(".add(").append(name).append(");\n");
                }
            }
            return res.toString();
        }
        StringBuilder res = new StringBuilder(String.format("%s %s = new %s();\n", type(o), var, type(o)));
        for (Field field : o.getClass().getFields()) {
            try {
                if (isConstant(field)) continue;

                String fieldName = String.format("%s.%s", var, field.getName());
                res.append(toInit2(fieldName, field.get(o)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(o.getClass()).getPropertyDescriptors()) {
                // propertyEditor.getReadMethod() exposes the getter
                // btw, this may be null if you have a write-only property
                Method writeMethod = pd.getWriteMethod();
                if (writeMethod == null) continue;
                Method readMethod = pd.getReadMethod();
                Object value = readMethod.invoke(o);
                if (value == null || (value.equals(false)))
                    continue;
                if (!hasSimpleType(value)) {
                    String name = defaultVarName(value);
                    res.append(init(name, value));
                    res.append(String.format("%s.%s(%s);\n", var, writeMethod.getName(), name));
                } else {
                    res.append(String.format("%s.%s(%s);\n", var, writeMethod.getName(), dump(value)));
                }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return res.toString();
    }

    public static Class<?> arrayElementClass(Object o) {
        Class<?> type = o.getClass();
        return getElementType(type);
    }

    private static Class<?> getElementType(Class<?> type) {
        return type.isArray() ? getElementType(type.getComponentType()) : type;
    }

    private static boolean hasSimpleType(Object o) {
        return types.containsKey(o.getClass()) || o instanceof Enum || o instanceof String || o instanceof Calendar;
    }

    private static boolean isConstant(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
    }

    private static String toInit2(String varName, Object o) {
        if (o == null)
            return String.format("Object %s = null;\n", varName);
        if (hasSimpleType(o))
            return String.format("%s = %s;\n", varName, dump(o));
        String res = String.format("%s = new %s();\n", varName, type(o));
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
            return String.format("\"%s\"", ((String) o).replace("\"", "\\\""));
        if (o instanceof Enum)
            return String.format("%s.%s", o.getClass().getSimpleName(), o);
        if (o instanceof Float)
            return String.format("%sf", o);
        if (o instanceof Character)
            return String.format("'%s'", o);
        if (o instanceof Calendar)
            return "Calendar.getInstance()";
        if (o instanceof Date)
            return "Calendar.getInstance().getTime()";
        if (o.getClass().isArray()) {
            StringBuilder res = new StringBuilder("{");
            int length = Array.getLength(o);
            for (int i = 0; i < length; i++) {
                if (i > 0) res.append(", ");
                res.append(dump(Array.get(o, i)));
            }
            res.append("}");
            return res.toString();
        }
        if (hasSimpleType(o))
            return o.toString();
        else
            return "new " + type(o) + "()";
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
        Class<?> cls = o.getClass();
        if (types.containsKey(cls))
            return types.get(cls);
        if (o instanceof Enum) {
            return cls.getSimpleName();
        }
        Type genericSuperclass = cls.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            Type[] types = {((ParameterizedType) genericSuperclass).getActualTypeArguments()[0]};
            StringBuilder res = new StringBuilder(cls.getSimpleName());
            res.append("<");
            boolean first = true;
            for (Type type : types) {
                if (first)
                    first = false;
                else
                    res.append(", ");
                res.append(type);
            }
            res.append(">");
            return res.toString();
        }
        return cls.getSimpleName();
    }

    /**
     * @param obj Обьект
     * @return Имя переменной по-умолчанию для данного типа (например: Random random)
     */
    public static String defaultVarName(Object obj) {
        if (obj == null)
            return "o";
        String s = obj.getClass().getSimpleName();
        if (types.containsKey(obj.getClass()) || obj instanceof String)
            return String.format("%c", Character.toLowerCase(s.charAt(0)));
        return String.format("%s%s", Character.toLowerCase(s.charAt(0)), s.substring(1));
    }
}
