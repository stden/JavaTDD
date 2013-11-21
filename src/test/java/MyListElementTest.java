import lists.MyListElement;
import org.junit.Test;

import java.util.HashMap;
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
        // Одинаковые Хеш-коды - коллизия хеш-кодов
        assertEquals(a.hashCode(), b.hashCode());
        // Но сами объекты - разные
        assertFalse(a.equals(b));

        // Множество
        Set<MyListElement> set = new HashSet<MyListElement>();
        set.add(a);
        assertTrue(set.contains(a));
        assertFalse(set.contains(b));

        set.add(b);
        assertTrue(set.contains(a));
        assertTrue(set.contains(b));

        // Хеш-таблица
        HashMap<MyListElement, String> map = new HashMap<MyListElement, String>();
        map.put(a, "A");
        map.put(b, "B");
        assertEquals("A", map.get(a));
        assertEquals("B", map.get(b));
    }
}
