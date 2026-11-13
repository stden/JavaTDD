import java.util.Random;

public class CheckRandom {
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            genRandom1();
        }
    }

    public static int genRandom1() {
        Random random = new Random();
        int value = random.nextInt(100);
        System.out.println(" random = " + value);
        return value;
    }

    public static int genRandom2() {
        Random random = new Random(System.currentTimeMillis());
        int value = random.nextInt(100);
        System.out.println(" random = " + value);
        return value;
    }

}
