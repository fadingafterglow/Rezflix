package ua.edu.ukma.springers.rezflix.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class RandomUtils {

    private static final Random RANDOM = new Random();

    public String getRandomEmail(int prefixLength, int domainLength, int lastLength) {
        String res = getRandomString(prefixLength) +
                "@" +
                getRandomString(domainLength) +
                "." +
                getRandomString(lastLength);
        return res.toLowerCase();
    }

    public String getRandomString(int length) {
        if (length == 0)
            return "";
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder res = new StringBuilder();
        while (res.length() < length) {
            int index = RANDOM.nextInt(0, chars.length());
            res.append(chars.charAt(index));
        }
        return res.toString();
    }

    public <E extends Enum<E>> E getRandomEnumValue(Class<E> clazz) {
        E[] values = clazz.getEnumConstants();
        return values[RANDOM.nextInt(values.length)];
    }

    public int getRandomInt(int min, int max) {
        return RANDOM.nextInt(min, max + 1);
    }

    public long getRandomLong(long min, long max) {
        return RANDOM.nextLong(min, max + 1);
    }

    public double getRandomDouble(double min, double max) {
        return RANDOM.nextDouble(min, max);
    }

    public boolean getRandomBoolean() {
        return RANDOM.nextBoolean();
    }

}
