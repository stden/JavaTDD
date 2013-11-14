import lists.MyListElement;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Тестирование работы hashcode и equals
 */
public class MyListElementTest {

    @Test
    public void test() {
        MyListElement a = new MyListElement("A");
        MyListElement b = new MyListElement("B");
        assertEquals(a.hashCode(), b.hashCode());
        assertFalse(a.equals(b));

        Set<MyListElement> set = new HashSet<MyListElement>();
        set.add(a);
        assertTrue(set.contains(a));
        assertFalse(set.contains(b));

        set.add(b);
        assertTrue(set.contains(a));
        assertTrue(set.contains(b));
    }


}
