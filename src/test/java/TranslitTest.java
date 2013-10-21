import org.junit.Test;
import service.Translit;

import static junit.framework.Assert.assertEquals;

/**
 * Тестирование придумывания имён
 */
public class TranslitTest {

    @Test
    public void testMakeFileName() {
        assertEquals("Это_просто!", Translit.makeFileName("Это просто!"));
    }

    @Test
    public void testTranslit() {
        assertEquals("Eto_prosto!", Translit.translit("Это просто!"));
    }

    @Test
    public void testSuggestName() {
        assertEquals("eto_prosto!", Translit.suggestUrl(" Это просто! "));
        assertEquals("about", Translit.suggestUrl("О компании"));
        assertEquals("index", Translit.suggestUrl("Главная"));
        assertEquals("manufacturing_capacities", Translit.suggestUrl("Производственные мощности"));
        assertEquals("human_resources", Translit.suggestUrl("Персонал"));
        assertEquals("services", Translit.suggestUrl("Услуги"));

        String tests[][] = {
                {"services", "Услуги"},
                {"production", "Продукция"},
                {"manufacturing_equipments", "Производственное оборудование"},
                {"technical_equipment", "Техническое оснащение"},
                {"works", "Объём выполненных работ"},
                {"contacts", "Контакты"},
        };
        for (String[] test : tests) {
            assertEquals(test[0], Translit.suggestUrl(test[1]));
        }
    }
}
