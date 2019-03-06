package es.andrewazor.containertest.subscriber;

import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

class MemoizingFactorial {

    // not the most optimal data structure for this task - but that's okay, it
    // makes the recordings more interesting
    private final Map<BigInteger, BigInteger> map = new TreeMap<>();
    {
        map.put(BigInteger.ZERO, BigInteger.ONE);
        map.put(BigInteger.ONE, BigInteger.ONE);
    }

    private final AtomicBoolean cached = new AtomicBoolean(false);

    BigInteger compute(BigInteger num) {
        if (cached.get()) {
            return computeCached(num);
        }
        return computeNonCached(num);
    }

    private BigInteger computeCached(BigInteger num) {
        if (num.compareTo(BigInteger.ZERO) < 0) {
            return BigInteger.ZERO;
        }
        if (map.containsKey(num)) {
            return map.get(num);
        }
        BigInteger result = num.multiply(compute(num.subtract(BigInteger.ONE)));
        map.put(num, result);
        return result;
    }

    private BigInteger computeNonCached(BigInteger num) {
        if (num.compareTo(new BigInteger("0")) < 0) {
            return new BigInteger("0");
        }
        if (num.compareTo(new BigInteger("0")) == 0) {
            return new BigInteger("1");
        }
        if (num.compareTo(new BigInteger("1")) == 0) {
            return new BigInteger("1");
        }
        return num.multiply(compute(num.subtract(new BigInteger("1"))));
    }

    void setCached(boolean cached) {
        this.cached.compareAndSet(!cached, cached);
    }

    boolean isCached() {
        return this.cached.get();
    }

}