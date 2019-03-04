package es.andrewazor.containertest.subscriber;

import static org.junit.Assert.assertThat;

import java.math.BigInteger;

import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.Before;
import org.junit.Test;

public class MemoizingFactorialTest {

    private MemoizingFactorial factorial;

    @Before
    public void setup() {
        factorial = new MemoizingFactorial();
    }

    @Test
    public void testNegative() {
        assertThat("negative is handled by returning 0", factorial.compute(BigInteger.valueOf(-1L)), equalTo(BigInteger.ZERO));
    }

    @Test
    public void testZero() {
        testImpl(0, 1);
    }

    @Test
    public void testOne() {
        testImpl(1, 1);
    }

    @Test
    public void testTwo() {
        testImpl(2, 2);
    }

    @Test
    public void testThree() {
        testImpl(3, 6);
    }

    @Test
    public void testFour() {
        testImpl(4, 24);
    }

    private void testImpl(int num, int expected) {
        assertThat(String.format("%d! is %d", num, expected), factorial.compute(BigInteger.valueOf(num)), equalTo(BigInteger.valueOf(expected)));
    }

}