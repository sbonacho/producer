package com.sbonacho.seda.load.producer.utils;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RandomString implements RandomMessage{

    /**
     * Generate a random string.
     */
    public String next() {
        char[] buf = this.getBuf();
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

    public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String lower = upper.toLowerCase(Locale.ROOT);

    public static final String digits = "0123456789";

    public static final String alphanum = upper + lower + digits;

    private final Random random;

    private final char[] symbols;

    private final int min;
    private final int max;

    private char[] buf;

    public RandomString(Random random, String symbols, Integer[] length) {
        if (symbols.length() < 2) throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        Arrays.sort(length, Collections.reverseOrder());
        if (length.length == 1 || length[0] == length[1]) {
            max = 0;
            min = length[0];
            this.buf = new char[min];
        } else {
            max = length[0];
            min = length[1];
        }
    }

    /**
     * Create an alphanumeric string generator.
     */
    public RandomString(Random random, Integer[] length) {
        this(random, alphanum, length);
    }

    /**
     * Random size
     */
    public RandomString(Integer[] length) {
        this(new SecureRandom(), length);
    }

    public char[] getBuf() {
        if (max == 0) {
            return buf;
        } else {
            int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
            return new char[randomNum];
        }
    }

}