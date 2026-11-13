package service;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * Перевод url страницы в транслит
 */
public class Translit {

    final static int NEUTRAL = 0;

    final static int UPPER = 1;

    final static int LOWER = 2;

    final static Hashtable map = makeTranslitMap();

    private static Hashtable makeTranslitMap() {
        Hashtable<Character, String> map = new Hashtable<Character, String>();
        map.put('а', "a");
        map.put('б', "b");
        map.put('в', "v");
        map.put('г', "g");
        map.put('д', "d");
        map.put('е', "e");
        map.put('ё', "yo");
        map.put('ж', "zh");
        map.put('з', "z");
        map.put('и', "i");
        map.put('й', "j");
        map.put('к', "k");
        map.put('л', "l");
        map.put('м', "m");
        map.put('н', "n");
        map.put('о', "o");
        map.put('п', "p");
        map.put('р', "r");
        map.put('с', "s");
        map.put('т', "t");
        map.put('у', "u");
        map.put('ф', "f");
        map.put('х', "h");
        map.put('ц', "ts");
        map.put('ч', "ch");
        map.put('ш', "sh");
        map.put('щ', "sh'");
        map.put('ъ', "`");
        map.put('ы', "y");
        map.put('ь', "'");
        map.put('э', "e");
        map.put('ю', "yu");
        map.put('я', "ya");
        map.put('«', "\"");
        map.put('»', "\"");
        map.put('№', "No");
        map.put(' ', "_"); // Замена пробела
        return map;
    }

    private static int charClass(char c) {
        if (Character.isLowerCase(c))
            return LOWER;
        if (Character.isUpperCase(c))
            return UPPER;
        return NEUTRAL;
    }

    public static String translit(String text) {
        int len = text.length();
        if (len == 0)
            return text;
        StringBuilder sb = new StringBuilder();
        int pc = NEUTRAL;
        char c = text.charAt(0);
        int cc = charClass(c);
        for (int i = 1; i <= len; i++) {
            char nextChar = (i < len ? text.charAt(i) : ' ');
            int nc = charClass(nextChar);
            Character co = Character.toLowerCase(c);
            String tr = (String) map.get(co);
            if (tr == null) {
                sb.append(c);
            } else {
                switch (cc) {
                    case LOWER:
                    case NEUTRAL:
                        sb.append(tr);
                        break;
                    case UPPER:
                        if (nc == LOWER || (nc == NEUTRAL && pc != UPPER)) {
                            sb.append(Character.toUpperCase(tr.charAt(0)));
                            if (tr.length() > 0) {
                                sb.append(tr.substring(1));
                            }
                        } else {
                            sb.append(tr.toUpperCase());
                        }
                }
            }
            c = nextChar;
            pc = cc;
            cc = nc;
        }
        return sb.toString();
    }

    public static String makeFileName(String text) {
        int len = text.length();
        if (len == 0)
            return text;
        StringBuilder sb = new StringBuilder();
        char lastAppended = 0;
        int count = 0;
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            if ((c & 0xFFFF) > 0x7F) {
                // keep non-ASCII as is
            } else if (c <= ' ' || c == '/' || c == '\\' || c == ':' || c == '~' || c == '"' || c == '.') {
                c = '_';
            }
            if (c == '_' && lastAppended == '_')
                continue;
            sb.append(c);
            if (++count > 50)
                break;
            lastAppended = c;
        }
        return sb.toString();
    }

    // Стандартные URL-ы для страниц с заданными названиями
    private static final HashMap<String, String> names = new HashMap<String, String>();

    static {
        addName("о компании", "about");
        addName("главная", "index");
        addName("продукция", "catalogue");
        addName("фото", "photo");
        addName("Производственные мощности", "manufacturing_capacities");
        addName("Персонал", "human_resources");
        addName("Услуги", "services");
        addName("Услуги", "services");
        addName("Продукция", "production");
        addName("Производственное оборудование", "manufacturing_equipments");
        addName("Техническое оснащение", "technical_equipment");
        addName("Объём выполненных работ", "works");
        addName("Контакты", "contacts");
    }

    private static void addName(String key, String value) {
        names.put(key.toLowerCase(), value);
    }

    /**
     * Составить подходящий URL для страницы или сайта
     *
     * @param text Заголовок (название) страницы/сайта
     * @return результат
     */
    public static String suggestUrl(String text) {
        String title = text.trim().toLowerCase();
        // Если это какое-то заранее определённое сочетание, то выводим его
        if (names.containsKey(title))
            return names.get(title);
        return makeFileName(translit(title));
    }
}