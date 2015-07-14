package com.autocomplete.comparators;

import java.util.Comparator;

public interface KeyComparator<K> extends Comparator<K> {

    /**
     * Returned if a key's bits were all zero (0).
     */
    public static final int NULL_BIT_KEY = -1;

    /**
     * Returned if the bits of two keys were all equal.
     */
    public static final int EQUAL_BIT_KEY = -2;

    /**
     * Returned if keys indices are out of bounds.
     */
    public static final int OUT_OF_BOUNDS_BIT_KEY = -3;

    /**
     * Returns the key's length in bits.
     */
    public int lengthInBits(K key);

    /**
     * Returns true if a key's bit it set at the given index.
     */
    public boolean isBitSet(K key, int bitIndex);

    /**
     * Returns the index of the first bit that is different in the two keys.
     */
    public int bitIndex(K key, K otherKey);

}
