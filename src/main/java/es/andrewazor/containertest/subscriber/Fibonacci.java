package es.andrewazor.containertest.subscriber;

import java.math.BigInteger;

class Fibonacci {

    BigInteger compute(int num) {
        return fib(new BigInteger(new Integer(num).toString()));
    }

    private BigInteger fib(BigInteger num) {
        if (BigInteger.ZERO.equals(num)) {
            return new BigInteger("0");
        }
        if (BigInteger.ONE.equals(num)) {
            return new BigInteger("1");
        }
        return fib(num.subtract(new BigInteger("1"))).add(fib(num.subtract(new BigInteger("2"))));
    }

}