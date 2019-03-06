package es.andrewazor.containertest.subscriber;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class MemoizingFibonacciTest {

    private MemoizingFibonacci fibonacci;

    @Before
    public void setup() {
        fibonacci = new MemoizingFibonacci();
    }

    @Test
    public void testZero() {
        testImpl(0, 0);
    }

    @Test
    public void testOne() {
        testImpl(1, 1);
    }

    @Test
    public void testTwo() {
        testImpl(2, 1);
    }

    @Test
    public void testThree() {
        testImpl(3, 2);
    }

    @Test
    public void testFour() {
        testImpl(4, 3);
    }

    @Test
    public void testFive() {
        testImpl(5, 5);
    }

    @Test
    public void testSix() {
        testImpl(6, 8);
    }

    @Test
    public void testSeven() {
        testImpl(7, 13);
    }

    private void testImpl(int num, int expected) {
        assertEquals(String.format("fib(%d) is %d", num, expected), fibonacci.compute(num).intValue(), expected);
        fibonacci.setCached(true);
        assertEquals(String.format("cached fib(%d) is %d", num, expected), fibonacci.compute(num).intValue(), expected);
    }

}