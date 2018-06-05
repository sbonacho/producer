package com.sbonacho.seda.load.producer.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RandomStringTest {

    private void tester(Integer[] rank, Integer max, Integer min){
        RandomString random = new RandomString(rank);
        assertThat(random.next().length()).isBetween(min, max);
    }

    @Test
    public void randomString() throws Exception {
        int max = 1;
        int min = 0;
        Integer[] rank = {max, min};
        tester(rank, max, min);
    }


    @Test
    public void randomStringReverse() throws Exception {
        int max = 10;
        int min = 0;
        Integer[] rank = {min, max};
        tester(rank, max, min);
    }

    @Test
    public void randomStringBig() throws Exception {
        int max = 1110;
        int min = 1100;
        Integer[] rank = {min, max};
        tester(rank, max, min);
    }

    @Test
    public void randomStringEquals() throws Exception {
        int max = 10;
        int min = 10;
        Integer[] rank = {min, max};
        tester(rank, max, min);
    }

    @Test
    public void randomStringZero() throws Exception {
        int max = 0;
        int min = 0;
        Integer[] rank = {min, max};
        tester(rank, max, min);
    }
}