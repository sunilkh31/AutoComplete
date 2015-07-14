package com.autocomplete.comparators;

public class StringKeyComparator implements KeyComparator<String> {

    public static final StringKeyComparator CHAR = new StringKeyComparator(Character.SIZE);

    private final int size;
    private final int msb;

    private StringKeyComparator(int size) {
        this(size, 1 << size - 1);
    }

    private StringKeyComparator(int size, int msb) {
        this.size = size;
        this.msb = msb;
    }

    @Override
    public int lengthInBits(String key) {
        return key.length() * size;
    }

    @Override
    public boolean isBitSet(String key, int bitIndex) {
        if (bitIndex >= lengthInBits(key)) {
            return false;
        }

        int index = bitIndex / size;
        int bit = bitIndex % size;

        return (key.charAt(index) & mask(bit)) != 0;
    }

    @Override
    public int bitIndex(String key, String otherKey) {

        boolean allNull = true;
        int length = Math.max(key.length(), otherKey.length());

        for (int i = 0; i < length; i++) {

            char ch1 = valueAt(key, i);
            char ch2 = valueAt(otherKey, i);

            if (ch1 != ch2) {
                int xor = ch1 ^ ch2;
                for (int j = 0; j < size; j++) {
                    if ((xor & mask(j)) != 0) {
                        return (i * size) + j;
                    }
                }
            }

            if (ch1 != 0) {
                allNull = false;
            }
        }

        // All bits are 0
        if (allNull) {
            return KeyComparator.NULL_BIT_KEY;
        }

        // Both keys are equal
        return KeyComparator.EQUAL_BIT_KEY;
    }

    @Override
    public int compare(String o1, String o2) {
        return ((Comparable<String>) o1).compareTo(o2);
    }

    /**
     * Returns a bit mask where the given bit is set
     */
    private int mask(int bit) {
        return msb >>> bit;
    }

    /**
     * Returns the character at the given index.
     */
    private char valueAt(String value, int index) {
        if (index < value.length()) {
            char ch = value.charAt(index);
            if (size == Byte.SIZE) {
                ch &= 0xFF;
            }
            return ch;
        }
        return 0;
    }
}
