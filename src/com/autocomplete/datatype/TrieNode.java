package com.autocomplete.datatype;



public class TrieNode<K> {

    private K key;
    private int bitIndex;
    private TrieNode<K> parent;
    private TrieNode<K> left;
    private TrieNode<K> right;
    private TrieNode<K> predecessor;

    public TrieNode(K key, int bitIndex) {
        this.setBitIndex(bitIndex);
        this.setKey(key);
        this.setParent(null);
        this.setLeft(this);
        this.setRight(null);
        this.setPredecessor(this);
    }

    public boolean isEmpty() {
        return getKey() == null;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    /**
     * Neither the left nor right child is a loop back
     */
    public boolean isInternalNode() {
        return getLeft() != this && getRight() != this;
    }

    /**
     * Either the left or right child is a loop back
     */
    public boolean isExternalNode() {
        return !isInternalNode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof TrieNode<?>)) {
            return false;
        }

        TrieNode<?> other = (TrieNode<?>) o;
        if (TrieUtil.areEqual(getKey(), other.getKey())) {
            return true;
        }
        return false;
    }

    /**
     * @return the parent
     */
    public TrieNode<K> getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(TrieNode<K> parent) {
        this.parent = parent;
    }

    /**
     * @return the left
     */
    public TrieNode<K> getLeft() {
        return left;
    }

    /**
     * @param left the left to set
     */
    public void setLeft(TrieNode<K> left) {
        this.left = left;
    }

    /**
     * @return the right
     */
    public TrieNode<K> getRight() {
        return right;
    }

    /**
     * @param right the right to set
     */
    public void setRight(TrieNode<K> right) {
        this.right = right;
    }

    /**
     * @return the predecessor
     */
    public TrieNode<K> getPredecessor() {
        return predecessor;
    }

    /**
     * @param predecessor the predecessor to set
     */
    public void setPredecessor(TrieNode<K> predecessor) {
        this.predecessor = predecessor;
    }

    /**
     * @return the bitIndex
     */
    public int getBitIndex() {
        return bitIndex;
    }

    /**
     * @param bitIndex the bitIndex to set
     */
    public void setBitIndex(int bitIndex) {
        this.bitIndex = bitIndex;
    }
}