import org.junit.Test;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static junit.framework.Assert.*;
import static util.Dumper.*;

/**
 * Testing convert (dump) any object to JSON and to Initialization code
 */
public class DumperTest {

    @Test
    public void dumpToJSON() {
        A a = new A();

        assertEquals("{\"a\":1,\"b\":2}", toJSON(a));
        assertEquals("null", toJSON(null));
        assertEquals("\"test\"", toJSON("test"));
        assertEquals("\"ERROR\"", toJSON(RequestState.ERROR));
        assertEquals("1", toJSON(1));
        assertEquals("1.1", toJSON(1.1));
        assertEquals("1.0E10", toJSON(1e10));
        assertEquals("\"c\"", toJSON('c'));
    }

    @Test
    public void dumpToString() {
        assertEquals("1", dump(1));
        assertEquals("2", dump(2));
        assertEquals("2.2", dump(2.2));
        assertEquals("1.0E10", dump(1e10));
        assertEquals("1.1f", dump(1.1f));
        assertEquals("\"test\"", dump("test"));
        assertEquals("RequestState.ERROR", dump(RequestState.ERROR));
        assertEquals("true", dump(true));
        assertEquals("false", dump(false));
        assertEquals("'c'", dump('c'));
        assertEquals("\"\\\"\"", dump("\""));
        assertEquals("\"\\\"\\\"\"", dump("\"\""));
        assertEquals("Calendar.getInstance()", dump(Calendar.getInstance()));
        assertEquals("Calendar.getInstance().getTime()", dump(Calendar.getInstance().getTime()));
    }

    @Test
    public void primitiveTypes() {
        byte bb = 23;
        assertEquals("byte", type(bb));
        assertEquals("byte bb = 23;\n", init("bb", bb));
        assertEquals(23, bb);
        assertEquals("assertEquals(23, bb);\n", toAssert("bb", bb));
        short s = 4;
        assertEquals("short", type(s));
        assertEquals("short s = 4;\n", init("s", s));
        assertEquals(4, s);
        assertEquals("assertEquals(4, s);\n", toAssert("s", s));
        int x = 1;
        assertTrue(int.class.isPrimitive());
        assertFalse(Integer.class.isPrimitive());
        assertEquals("int", type(x));
        assertEquals("int x = 1;\n", init("x", x));
        assertEquals(1, x);
        assertEquals("assertEquals(1, x);\n", toAssert("x", x));
        long l = 3;
        assertEquals("long", type(l));
        assertEquals("long l = 3;\n", init("l", l));
        assertEquals(3, l);
        assertEquals("assertEquals(3, l);\n", toAssert("l", l));
        float f = 1.1f;
        assertEquals("float", type(f));
        assertEquals("float f = 1.1f;\n", init("f", f));
        assertEquals(1.1f, f);
        assertEquals("assertEquals(1.1f, f);\n", toAssert("f", f));
        double d = 1.0;
        assertEquals("double", type(d));
        assertEquals("double d = 1.0;\n", init("d", d));
        assertEquals("assertEquals(1.0, d);\n", toAssert("d", d));
        boolean b = false;
        assertEquals("boolean", type(b));
        assertEquals("boolean b = false;\n", init("b", b));
        assertFalse(b);
        assertEquals("assertFalse(b);\n", toAssert("b", b));
        b = true;
        assertEquals("boolean b = true;\n", init("b", b));
        assertTrue(b);
        assertEquals("assertTrue(b);\n", toAssert("b", b));
        char c = 'a';
        assertEquals("char", type(c));
        assertEquals("char c = 'a';\n", init("c", c));
        assertEquals('a', c);
        assertEquals("assertEquals('a', c);\n", toAssert("c", c));
    }

    @Test
    public void boxingUnboxing() throws NoSuchFieldException {
        Integer i = 23;
        assertEquals(Integer.class, i.getClass());
        assertEquals(23, i.intValue());
        assertEquals("int", type(i));
        int ii = 2;
        assertEquals(Integer.class, ((Object) ii).getClass());
        //assertEquals("int", type(ii)); TODO: Сделать, чтобы работало

        class Local {
            public int i;
            public Integer i2;
        }

        Local local = new Local();
        local.i = 2;
        local.i2 = 22;
        Field[] f = local.getClass().getFields();
        assertEquals(int.class, f[0].getType());
        assertEquals(Integer.class, f[1].getType());
    }

    @Test
    public void arraysReflectionAPI() {
        Object obj = new int[][]{{1, 2}, {3, 4, 5}};
        assertEquals(int[][].class, obj.getClass());
        assertTrue(obj.getClass().isArray());
        assertEquals(int[].class, obj.getClass().getComponentType());
        assertTrue(obj.getClass().getComponentType().isArray());
        assertEquals(int.class, obj.getClass().getComponentType().getComponentType());
        assertFalse(obj.getClass().getComponentType().getComponentType().isArray());
        assertEquals(int.class, arrayElementClass(obj));
    }

    @Test
    public void arraysAndCollections() {
        int[] a = {1, 2, 3};
        assertEquals("int[]", type(a));
        assertEquals("{1, 2, 3}", dump(a));
        assertEquals("int[] a = {1, 2, 3};\n", init("a", a));
        assertEquals(int.class, arrayElementClass(a));
        String[] s = new String[]{"test", "array"};
        assertEquals("String[]", type(s));
        assertEquals("{\"test\", \"array\"}", dump(s));
        assertEquals("String[] s = {\"test\", \"array\"};\n", init("s", s));

        // Массив Enum
        RequestTypeX[] r = {RequestTypeX.AD, RequestTypeX.CASHBACK};
        assertEquals("RequestTypeX[]", type(r));
        assertEquals("{RequestTypeX.AD, RequestTypeX.CASHBACK}", dump(r));
        assertEquals("RequestTypeX[] r = {RequestTypeX.AD, RequestTypeX.CASHBACK};\n", init("r", r));
        // Массив сложных объектов
        RequestX requestX0 = new RequestX();
        requestX0.setVer("0.1");
        assertEquals("RequestX requestX0 = new RequestX();\n" +
                "requestX0.setVer(\"0.1\");\n", init("requestX0", requestX0));
        RequestX requestX1 = new RequestX();
        requestX1.setType(RequestTypeX.AD);
        assertEquals("RequestX requestX1 = new RequestX();\n" +
                "requestX1.setType(RequestTypeX.AD);\n", init("requestX1", requestX1));
        RequestX[] requests = {requestX0, requestX1};
        assertEquals("RequestX[]", type(requests));
        assertEquals("new RequestX()", dump(requestX0));
        assertEquals("{new RequestX(), new RequestX()}", dump(requests));
        assertEquals("RequestX requestX0 = new RequestX();\n" +
                "requestX0.setVer(\"0.1\");\n" +
                "RequestX requestX1 = new RequestX();\n" +
                "requestX1.setType(RequestTypeX.AD);\n" +
                "RequestX[] requests = {requestX0, requestX1};\n", init("requests", requests));
    }

    /**
     * Многомерные массивы
     */
    @Test
    public void multidimensionalArrays() {
        int[][] aa = {{1, 2}, {3, 4, 5}};
        assertEquals("int[][]", type(aa));
        assertEquals("{{1, 2}, {3, 4, 5}}", dump(aa));
        assertEquals("int[][] aa = {{1, 2}, {3, 4, 5}};\n", init("aa", aa));
    }

    /**
     * Работа с коллекциями
     */
    @Test
    public void arrayLists() {
        ArrayList<String> list = new ArrayList<String>();
        assertEquals("ArrayList<String> list = new ArrayList<String>();\n", init("list", list));
        list.add("Test");
        assertEquals("ArrayList<E>", type(list));
        assertEquals("ArrayList<String> list = new ArrayList<String>();\n" +
                "list.add(\"Test\");\n", init("list", list));

        assertEquals(1, list.size());
        assertEquals("Test", list.get(0));
        assertEquals("assertEquals(1, list.size());\n" +
                "assertEquals(\"Test\", list.get(0));\n",
                toAssert("list", list));

        ArrayList<Integer> ints = new ArrayList<Integer>();
        ints.add(2);
        ints.add(5);
        assertEquals(2, ints.size());
        assertEquals(2, ints.get(0).intValue());
        assertEquals(5, ints.get(1).intValue());
        assertEquals("assertEquals(2, ints.size());\n" +
                "assertEquals(2, ints.get(0));\n" +
                "assertEquals(5, ints.get(1));\n",
                toAssert("ints", ints));
    }

    @Test
    public void datesAndTimes() throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        cal.setTime(sdf.parse("06.04.2013 18:43:56"));
        assertEquals(6, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.APRIL, cal.get(Calendar.MONTH));
        assertEquals(2013, cal.get(Calendar.YEAR));
        assertEquals(18, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(43, cal.get(Calendar.MINUTE));
        assertEquals(56, cal.get(Calendar.SECOND));
        assertEquals("06.04.2013 18:43:56", sdf.format(cal.getTime()));

        assertEquals("GregorianCalendar cal = Calendar.getInstance();\n", init("cal", Calendar.getInstance()));
        assertNotNull(cal);
        assertEquals("assertNotNull(cal);\n", toAssert("cal", Calendar.getInstance()));
    }

    @Test
    public void typeAndInit() {
        RequestTypeX requestTypeX = RequestTypeX.AD;
        assertEquals("RequestTypeX", type(requestTypeX));
        assertEquals("RequestTypeX requestTypeX = RequestTypeX.AD;\n", init("requestTypeX", requestTypeX));
        requestTypeX = RequestTypeX.CASHBACK;
        assertEquals("RequestTypeX", type(requestTypeX));
        assertEquals("RequestTypeX requestTypeX = RequestTypeX.CASHBACK;\n", init("requestTypeX", requestTypeX));
        Object obj = null;
        assertEquals("Object", type(obj));
        assertEquals("Object obj = null;\n", init("obj", obj));

        RequestX requestX = new RequestX();
        assertEquals("RequestX", type(requestX));
        assertEquals("RequestX requestX = new RequestX();\n", init("requestX", requestX));

        requestX.setSign("SIGN");
        assertEquals("RequestX requestX = new RequestX();\n" +
                "requestX.setSign(\"SIGN\");\n", init("requestX", requestX));
        assertEquals("SIGN", requestX.getSign());
        assertEquals("assertEquals(\"SIGN\", requestX.getSign());\n", toAssert("requestX", requestX));

        requestX.setType(RequestTypeX.ACTIVATION);
        assertEquals("RequestX requestX = new RequestX();\n" +
                "requestX.setSign(\"SIGN\");\n" +
                "requestX.setType(RequestTypeX.ACTIVATION);\n", init("requestX", requestX));
        assertEquals(RequestTypeX.ACTIVATION, requestX.getType());
        assertEquals("assertEquals(\"SIGN\", requestX.getSign());\n" +
                "assertEquals(RequestTypeX.ACTIVATION, requestX.getType());\n", toAssert("requestX", requestX));

        requestX.setVer("\"");
        assertEquals("RequestX requestX = new RequestX();\n" +
                "requestX.setSign(\"SIGN\");\n" +
                "requestX.setType(RequestTypeX.ACTIVATION);\n" +
                "requestX.setVer(\"\\\"\");\n", init("requestX", requestX));
        assertEquals("\"", requestX.getVer());
        assertEquals("assertEquals(\"SIGN\", requestX.getSign());\n" +
                "assertEquals(RequestTypeX.ACTIVATION, requestX.getType());\n" +
                "assertEquals(\"\\\"\", requestX.getVer());\n", toAssert("requestX", requestX));

        requestX.setOffline(true);
        assertEquals("RequestX requestX = new RequestX();\n" +
                "requestX.setOffline(true);\n" +
                "requestX.setSign(\"SIGN\");\n" +
                "requestX.setType(RequestTypeX.ACTIVATION);\n" +
                "requestX.setVer(\"\\\"\");\n", init("requestX", requestX));
        assertTrue(requestX.isOffline());
        assertEquals("assertTrue(requestX.isOffline());\n" +
                "assertEquals(\"SIGN\", requestX.getSign());\n" +
                "assertEquals(RequestTypeX.ACTIVATION, requestX.getType());\n" +
                "assertEquals(\"\\\"\", requestX.getVer());\n", toAssert("requestX", requestX));

        requestX.setRequestDate(Calendar.getInstance());
        assertEquals("assertTrue(requestX.isOffline());\n" +
                "assertNotNull(requestX.getRequestDate());\n" +
                "assertEquals(\"SIGN\", requestX.getSign());\n" +
                "assertEquals(RequestTypeX.ACTIVATION, requestX.getType());\n" +
                "assertEquals(\"\\\"\", requestX.getVer());\n", toAssert("requestX", requestX));
    }

    @Test
    public void dumpAssert() {
        int i = 1;
        assertEquals(1, i);
        assertEquals("assertEquals(1, i);\n", toAssert("i", i));
        assertEquals("int", type(i));
        assertEquals("int i = 1;\n", init("i", i));
        assertEquals("i", defaultVarName(i));
        i = 2;
        assertEquals(2, i);
        assertEquals("assertEquals(2, i);\n", toAssert("i", i));
        assertEquals("int i = 2;\n", init("i", i));
        assertEquals("assertEquals(3, varName);\n", toAssert("varName", 3));
        assertEquals("int i = 3;\n", init("i", 3));
        assertEquals("int i = 239;\n", init("i", 239));
        String s = "test";
        assertEquals("test", s);
        assertEquals("String", type(s));
        assertEquals("assertEquals(\"test\", s);\n", toAssert("s", s));
        assertEquals("String s = \"test\";\n", init("s", s));
        assertEquals("s", defaultVarName(s));
        Object o = null;
        assertEquals("Object", type(o));
        assertNull(o);
        assertEquals("assertNull(o);\n", toAssert("o", o));
        assertEquals("Object o = null;\n", init("o", o));
        assertEquals("o", defaultVarName(o));

        A a = new A();
        assertEquals("A", type(a));
        assertEquals(1, a.a);
        assertEquals(2, a.b);
        assertEquals("assertEquals(1, a.a);\n" +
                "assertEquals(2, a.b);\n", toAssert("a", a));
        assertEquals("A a = new A();\na.a = 1;\na.b = 2;\n", init("a", a));
        assertEquals("a", defaultVarName(a));

        B b = new B();
        assertEquals("B", type(b));
        assertEquals(1, b.a);
        assertEquals(2, b.b);
        assertEquals("assertEquals(1, b.a);\n" +
                "assertEquals(2, b.b);\n", toAssert("b", b));
        assertEquals("b", defaultVarName(b));

        RequestTypeX type = RequestTypeX.CASHBACK;
        assertEquals(RequestTypeX.CASHBACK, type);
        assertEquals("assertEquals(RequestTypeX.CASHBACK, type);\n", toAssert("type", type));
        assertEquals("assertEquals(RequestTypeX.ACTIVATION, type);\n", toAssert("type", RequestTypeX.ACTIVATION));
        assertEquals("requestTypeX", defaultVarName(type));

        RequestX requestX = new RequestX();
        assertEquals("", toAssert("requestX", requestX));
        assertEquals("RequestX requestX = new RequestX();\n", init("requestX", requestX));
        assertEquals("requestX", defaultVarName(requestX));
    }

    public enum RequestState {
        ERROR
    }

    public enum RequestTypeX {
        ACTIVATION, PAYMENT_AND_CONFIRM, CASHBACK, AD
    }

    public class A {
        public int a = 1;
        public int b = 2;
    }

    public class B {
        public int a = 1;
        public int b = 2;
        private int privateField = 3;
    }

    public class RequestX implements Serializable {
        public static final String IGNORED_CONST = "IGNORED_CONST";
        protected String ver;
        protected RequestTypeX type;
        protected String requestId;
        protected String terminalId;
        protected Calendar requestDate;
        protected String employeeId;
        protected String cardNum;
        protected boolean isOffline;
        protected String shiftId;
        protected String sign;

        public String getVer() {
            return ver;
        }

        public void setVer(String value) {
            this.ver = value;
        }

        public RequestTypeX getType() {
            return type;
        }

        public void setType(RequestTypeX value) {
            this.type = value;
        }

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String value) {
            this.requestId = value;
        }

        public String getTerminalCode() {
            return terminalId;
        }

        public void setTerminalCode(String value) {
            this.terminalId = value;
        }

        public Calendar getRequestDate() {
            return requestDate;
        }

        public void setRequestDate(Calendar value) {
            this.requestDate = value;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(String value) {
            this.employeeId = value;
        }

        public String getCardNum() {
            return cardNum;
        }

        public void setCardNum(String value) {
            this.cardNum = value;
        }

        public boolean isOffline() {
            return isOffline;
        }

        public void setOffline(boolean value) {
            this.isOffline = value;
        }

        public String getShiftId() {
            return shiftId;
        }

        public void setShiftId(String value) {
            this.shiftId = value;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }
    }
}
