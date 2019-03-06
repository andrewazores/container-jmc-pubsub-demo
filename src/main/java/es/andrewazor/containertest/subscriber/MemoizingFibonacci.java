package es.andrewazor.containertest.subscriber;

import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

class MemoizingFibonacci {

    // not the most optimal data structure for this task - but that's okay, it
    // makes the recordings more interesting
    private final Map<BigInteger, BigInteger> map = new TreeMap<>();
    {
        map.put(BigInteger.ZERO, BigInteger.ZERO);
        map.put(BigInteger.ONE, BigInteger.ONE);
    }

    private final AtomicBoolean cached = new AtomicBoolean(false);

    void setCached(boolean cached) {
        this.cached.set(cached);
    }

    boolean isCached() {
        return this.cached.get();
    }

    BigInteger compute(int num) {
        if (this.isCached()) {
            return fibCached(new BigInteger(Integer.toString(num)));
        }
        return fib(new BigInteger(new Integer(num).toString()));
    }

    private BigInteger fibCached(BigInteger num) {
        if (map.containsKey(num)) {
            return map.get(num);
        }
        BigInteger result = fibCached(num.subtract(BigInteger.ONE)).add(fibCached(num.subtract(BigInteger.TWO)));
        map.put(num, result);
        return result;
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