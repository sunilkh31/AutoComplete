package com.autocomplete.datatype;

import com.autocomplete.comparators.KeyComparator;

public class TrieUtil {

    static boolean isOutOfBoundsIndex(int bitIndex) {
        return bitIndex == KeyComparator.OUT_OF_BOUNDS_BIT_KEY;
    }

    static boolean isEqualBitKey(int bitIndex) {
        return bitIndex == KeyComparator.EQUAL_BIT_KEY;
    }

    static boolean isNullBitKey(int bitIndex) {
        return bitIndex == KeyComparator.NULL_BIT_KEY;
    }

    static boolean isValidBitIndex(int bitIndex) {
        return 0 <= bitIndex && bitIndex <= Integer.MAX_VALUE;
    }

    static boolean areEqual(Object a, Object b) {
        return (a == null ? b == null : a.equals(b));
    }

    static <T> T notNull(T o, String message) {
        if (o == null) {
            throw new NullPointerException(message);
        }
        return o;
    }

    @SuppressWarnings("unchecked")
    static <K> K cast(Object key) {
        return (K) key;
    }

    private TrieUtil() {
    }
}
