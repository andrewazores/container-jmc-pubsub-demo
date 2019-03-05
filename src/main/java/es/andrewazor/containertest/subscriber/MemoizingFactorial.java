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
        if (num.compareTo(new BigInteger("0")) < 0) {
            return new BigInteger("0");
        }
        if (map.containsKey(num)) {
            return map.get(num);
        }
        BigInteger result = num.multiply(compute(num.subtract(new BigInteger("1"))));
        if (cached.get()) {
            map.put(num, result);
        }
        return result;
    }

    void setCached(boolean cached) {
        this.cached.compareAndSet(!cached, cached);
    }

    boolean isCached() {
        return this.cached.get();
    }

}