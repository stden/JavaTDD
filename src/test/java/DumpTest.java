import org.junit.Test;

import java.io.Serializable;
import java.util.Calendar;

import static junit.framework.Assert.*;

/**
 * Testing convert (dump) any object to JSON and to Initialization code
 */
public class DumpTest {

    @Test
    public void dumpJson() {
        A a = new A();

        assertEquals("{\"a\":1,\"b\":2}", Dumper.toJSON(a));
        assertEquals("null", Dumper.toJSON(null));
        assertEquals("\"test\"", Dumper.toJSON("test"));
        assertEquals("\"ERROR\"", Dumper.toJSON(RequestState.ERROR));
        assertEquals("1", Dumper.toJSON(1));
        assertEquals("1.1", Dumper.toJSON(1.1));
        assertEquals("1.0E10", Dumper.toJSON(1e10));
    }

    @Test
    public void dumpString() {
        assertEquals("1", Dumper.dump(1));
        assertEquals("2", Dumper.dump(2));
        assertEquals("2.2", Dumper.dump(2.2));
        assertEquals("1.0E10", Dumper.dump(1e10));
        assertEquals("1.1f", Dumper.dump(1.1f));
        assertEquals("\"test\"", Dumper.dump("test"));
        assertEquals("RequestState.ERROR", Dumper.dump(RequestState.ERROR));
        assertEquals("true", Dumper.dump(true));
        assertEquals("false", Dumper.dump(false));
    }

    @Test
    public void types() {
        int x = 1;
        assertTrue(int.class.isPrimitive());
        assertFalse(Integer.class.isPrimitive());
        assertEquals("int", Dumper.type(x));
        assertEquals("int x = 1;\n", Dumper.init("x", x));
        long l = 3;
        assertEquals("long", Dumper.type(l));
        assertEquals("long l = 3;\n", Dumper.init("l", l));
        boolean b = false;
        assertEquals("boolean", Dumper.type(b));
        assertEquals("boolean b = false;\n", Dumper.init("b", b));
        double d = 1.0;
        assertEquals("double", Dumper.type(d));
        assertEquals("double d = 1.0;\n", Dumper.init("d", d));
        float f = 1.1f;
        assertEquals("float", Dumper.type(f));
        assertEquals("float f = 1.1f;\n", Dumper.init("f", f));
        char c = 'a';
        assertEquals("char", Dumper.type(c));
        byte bb = 23;
        assertEquals("byte", Dumper.type(bb));
        RequestTypeX requestTypeX = RequestTypeX.AD;
        assertEquals("RequestTypeX", Dumper.type(requestTypeX));
        Object obj = null;
        assertEquals("Object", Dumper.type(obj));
    }

    @Test
    public void dumpAssert() {
        int x = 1;
        assertEquals(1, x);
        assertEquals("assertEquals(1, x);\n", Dumper.toAssert("x", x));
        assertEquals("int", Dumper.type(x));
        assertEquals("int x = 1;\n", Dumper.init("x", x));
        x = 2;
        assertEquals(2, x);
        assertEquals("assertEquals(2, x);\n", Dumper.toAssert("x", x));
        assertEquals("int x = 2;\n", Dumper.init("x", x));
        assertEquals("assertEquals(3, varName);\n", Dumper.toAssert("varName", 3));
        assertEquals("int x = 3;\n", Dumper.init("x", 3));
        assertEquals("int x = 239;\n", Dumper.init("x", 239));
        String s = "test";
        assertEquals("test", s);
        assertEquals("String", Dumper.type(s));
        assertEquals("assertEquals(\"test\", s);\n", Dumper.toAssert("s", s));
        assertEquals("String s = \"test\";\n", Dumper.init("s", s));
        Object obj = null;
        assertEquals("Object", Dumper.type(obj));
        assertNull(obj);
        assertEquals("assertNull(obj);\n", Dumper.toAssert("obj", obj));
        assertEquals("Object obj = null;\n", Dumper.init("obj", obj));

        A xx = new A();
        assertEquals("A", Dumper.type(xx));
        assertEquals(1, xx.a);
        assertEquals(2, xx.b);
        assertEquals("assertEquals(1, xx.a);\n" +
                "assertEquals(2, xx.b);\n", Dumper.toAssert("xx", xx));
        assertEquals("A xx = new A();\nxx.a = 1;\nxx.b = 2;\n", Dumper.init("xx", xx));

        B b1 = new B();
        assertEquals("B", Dumper.type(b1));
        assertEquals(1, b1.a);
        assertEquals(2, b1.b);
        assertEquals("assertEquals(1, b1.a);\n" +
                "assertEquals(2, b1.b);\n", Dumper.toAssert("b1", b1));

        RequestTypeX type = RequestTypeX.CASHBACK;
        assertEquals(RequestTypeX.CASHBACK, type);
        assertEquals("assertEquals(RequestTypeX.CASHBACK, type);\n", Dumper.toAssert("type", type));
        assertEquals("assertEquals(RequestTypeX.ACTIVATION, type);\n", Dumper.toAssert("type", RequestTypeX.ACTIVATION));

        RequestX requestX = new RequestX();
        assertEquals("", Dumper.toAssert("requestX", requestX));
        assertEquals("RequestX requestX = new RequestX();\n", Dumper.init("requestX", requestX));
    }

    public enum RequestState {
        ERROR
    }

    public enum RequestTypeX {
        ACTIVATION, PAYMENT, PAYMENT_AND_CONFIRM, PAYMENT_CONFIRM, BALANCE,
        CASHBACK, LINK, EXCHANGE, DEPOSIT, LAST_RRN, AD, INFO, EQUALIZING, AUTH
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
        protected String clientType;
        protected String clientVersion;

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

        public String getClientType() {
            return clientType;
        }

        public void setClientType(String clientType) {
            this.clientType = clientType;
        }

        public String getClientVersion() {
            return clientVersion;
        }

        public void setClientVersion(String clientVersion) {
            this.clientVersion = clientVersion;
        }
    }
}
