import junit.framework.Assert;
import org.junit.Test;

public class RandomTest {

    /**
     * Первый тест - проверяем первую функцию генерации случайных чисел
     */
    @Test
    public void randomWithoutParams() {
        int v1 = CheckRandom.genRandom1();
        int v2 = CheckRandom.genRandom1();
        Assert.assertNotSame(v1, v2);
    }

    /**
     * Второй тест - проверяем вторую функцию генерации случайных чисел
     */
    @Test
    public void randomWithoutParams2() {
        int v1 = CheckRandom.genRandom2();
        int v2 = CheckRandom.genRandom2();
        Assert.assertNotSame(v1, v2);
    }

}
