package ua.edu.ukma.springers.rezflix.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class RandomUtils {

    private static final Random RANDOM = new Random();

    public static String getRandomEmail(int prefixLength, int domainLength, int lastLength) {
        String res = getRandomString(prefixLength) +
                "@" +
                getRandomString(domainLength) +
                "." +
                getRandomString(lastLength);
        return res.toLowerCase();
    }

    public static String getRandomString(int length) {
        if (length == 0)
            return "";
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder res = new StringBuilder();
        while (res.length() < length) {
            int index = RANDOM.nextInt(chars.length()); // Виправлено для сумісності з java.util.Random
            res.append(chars.charAt(index));
        }
        return res.toString();
    }

    public static <E extends Enum<E>> E getRandomEnumValue(Class<E> clazz) {
        E[] values = clazz.getEnumConstants();
        return values[RANDOM.nextInt(values.length)];
    }

    public static int getRandomInt(int min, int max) {
        return RANDOM.nextInt(max - min + 1) + min;
    }

    public static long getRandomLong(long min, long max) {
        return min + (long) (RANDOM.nextDouble() * (max - min));
    }

    public static double getRandomDouble(double min, double max) {
        return min + (max - min) * RANDOM.nextDouble();
    }

    public static boolean getRandomBoolean() {
        return RANDOM.nextBoolean();
    }
}