import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Фильтрация доменных имён по RegExp
 * Play Framework routes
 * Тест регулярного выражения для разбора доменных имён
 */
public class DomainRegexpTest {
    @Test
    public void checkUrl() {
        String pattern = "(?!.*?ngk-info\\.com).*";

        assertTrue("test.ru".matches(pattern));
        assertTrue("ngk-info1.com".matches(pattern));
        assertTrue("ng-info.com".matches(pattern));

        assertFalse("ngk-info.com".matches(pattern));
        assertFalse("dev.ngk-info.com".matches(pattern));
        assertFalse("test.ngk-info.com".matches(pattern));
    }
}
