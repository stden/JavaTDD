package lists;

/**
 * Элемент списка
 */
public class MyListElement {

    private final String a;

    public MyListElement(String a) {
        this.a = a;
    }

    /**
     * @return Всё время возвращает 0, чтобы сбить с толку коллекции ;)
     */
    public int hashCode() {
        return 0;
    }

    public boolean equals(Object obj) {
        return obj instanceof MyListElement && a.equals(((MyListElement) obj).a);
    }
}
