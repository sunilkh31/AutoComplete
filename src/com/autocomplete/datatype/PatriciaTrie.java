package com.autocomplete.datatype;

import java.util.ArrayList;
import java.util.List;

import com.autocomplete.comparators.KeyComparator;

public class PatriciaTrie<K> {

    private final TrieNode<K> root = new TrieNode<K>(null, -1);
    private final KeyComparator<K> keyComp;
    private int size = 0;

    public PatriciaTrie(KeyComparator<K> keyComp) {
        this.keyComp = keyComp;
    }

    public int size() {
        return size;
    }

    public K firstKey() {
        return firstNode().getKey();
    }

    public List<K> getSuggestions(K prefix, int numOfSuggestions) {
        ArrayList<K> suggestions = new ArrayList<>();
        TrieNode<K> subtree = subtree(prefix);
        TrieNode<K> curr = null;
        if (subtree != null) {
            curr = traverseLeft(subtree);
            suggestions.add(curr.getKey());
            numOfSuggestions--;
        }

        while (numOfSuggestions >= 0 && curr != null) {
            curr = nextKeyInSubtree(curr, subtree);
            if (curr != null) {
                suggestions.add(curr.getKey());
                numOfSuggestions--;
            }
        }
        return suggestions;
    }

    public void put(K key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        int lengthInBits = lengthInBits(key);

        // Key with zero bits have to be root node
        if (lengthInBits == 0) {
            if (root.isEmpty()) {
                incrementSize();
            }
            root.setKey(key);
            return;
        }

        TrieNode<K> nearest = getNearestNodeForKey(key);
        if (compareKeys(key, nearest.getKey())) {
            if (nearest.isEmpty()) {
                incrementSize();
            }
            nearest.setKey(key);
            return;
        }

        int bitIndex = bitIndex(key, nearest.getKey());
        if (!TrieUtil.isOutOfBoundsIndex(bitIndex)) {
            if (TrieUtil.isValidBitIndex(bitIndex)) {
                TrieNode<K> newEntry = new TrieNode<K>(key, bitIndex);
                addTrieNode(newEntry);
                incrementSize();
            } else if (TrieUtil.isNullBitKey(bitIndex)) {
                // Root key goes here
                if (root.isEmpty()) {
                    incrementSize();
                }
                root.setKey(key);
            }
        }
    }

    /**
     * It finds out the subtree with the given prefix if exist. We stop the lookup if h.bitIndex > lengthInBits.
     */
    private TrieNode<K> subtree(K prefix) {
        int lengthInBits = lengthInBits(prefix);

        TrieNode<K> current = root.getLeft();
        TrieNode<K> path = root;
        while (true) {
            if (current.getBitIndex() <= path.getBitIndex() || lengthInBits < current.getBitIndex()) {
                break;
            }

            path = current;
            if (!isBitSet(prefix, current.getBitIndex())) {
                current = current.getLeft();
            } else {
                current = current.getRight();
            }
        }

        // Make sure the entry is valid for a subtree.
        TrieNode<K> entry = current.isEmpty() ? path : current;

        // If entry is root, it can't be empty.
        if (entry.isEmpty()) {
            return null;
        }

        // if root && length of root is less than length of lookup,
        // there's nothing.
        // (this prevents returning the whole subtree if root has an empty
        // string and we want to lookup empty things)
        if (entry == root && lengthInBits(entry.getKey()) < lengthInBits) {
            return null;
        }

        // Found key's length-th bit differs from our key
        // which means it cannot be the prefix...
        if (isBitSet(prefix, lengthInBits) != isBitSet(entry.getKey(), lengthInBits)) {
            return null;
        }

        // ... or there are less than 'length' equal bits
        int bitIndex = bitIndex(prefix, entry.getKey());
        if (bitIndex >= 0 && bitIndex < lengthInBits) {
            return null;
        }

        return entry;
    }

    /**
     * Returns the entry lexicographically after the given entry. If the given entry is null, returns the first node.
     */
    private TrieNode<K> nextKeyInSubtree(TrieNode<K> node, TrieNode<K> subtreeParent) {
        if (node == null) {
            return firstNode();
        } else {
            return nextTrieNode(node.getPredecessor(), node, subtreeParent);
        }
    }

    private TrieNode<K> addTrieNode(TrieNode<K> node) {
        TrieNode<K> current = root.getLeft();
        TrieNode<K> path = root;
        while (true) {
            if (current.getBitIndex() >= node.getBitIndex() || current.getBitIndex() <= path.getBitIndex()) {
                node.setPredecessor(node);

                if (!isBitSet(node.getKey(), node.getBitIndex())) {
                    node.setLeft(node);
                    node.setRight(current);
                } else {
                    node.setLeft(current);
                    node.setRight(node);
                }

                node.setParent(path);
                if (current.getBitIndex() >= node.getBitIndex()) {
                    current.setParent(node);
                }

                // if we inserted an uplink, set the predecessor on it
                if (current.getBitIndex() <= path.getBitIndex()) {
                    current.setPredecessor(node);
                }

                if (path == root || !isBitSet(node.getKey(), path.getBitIndex())) {
                    path.setLeft(node);
                } else {
                    path.setRight(node);
                }

                return node;
            }

            path = current;

            if (!isBitSet(node.getKey(), current.getBitIndex())) {
                current = current.getLeft();
            } else {
                current = current.getRight();
            }
        }
    }

    /**
     * Returns the nearest node for the given key
     */
    private TrieNode<K> getNearestNodeForKey(K key) {
        TrieNode<K> current = root.getLeft();
        TrieNode<K> path = root;
        while (true) {
            if (current.getBitIndex() <= path.getBitIndex()) {
                return current;
            }

            path = current;
            if (!isBitSet(key, current.getBitIndex())) {
                current = current.getLeft();
            } else {
                current = current.getRight();
            }
        }
    }

    private TrieNode<K> nextTrieNode(TrieNode<K> start, TrieNode<K> previous, TrieNode<K> tree) {

        TrieNode<K> current = start;

        // Only look at the left if this was a recursive or
        // the first check, otherwise we know we've already looked
        // at the left.
        if (previous == null || start != previous.getPredecessor()) {
            while (!current.getLeft().isEmpty()) {
                // stop traversing if we've already
                // returned the left of this node.
                if (previous == current.getLeft()) {
                    break;
                }

                if (isValidUplink(current.getLeft(), current)) {
                    return current.getLeft();
                }

                current = current.getLeft();
            }
        }

        // If there's no data at all, exit.
        if (current.isEmpty()) {
            return null;
        }

        // If we've already returned the left, and the immediate right is null,
        // there's only one entry in the Trie which is stored at the root.
        if (current.getRight() == null) {
            return null;
        }

        // If nothing is valid on the left, try the right.
        if (previous != current.getRight()) {
            // See if it immediately is valid.
            if (isValidUplink(current.getRight(), current)) {
                return current.getRight();
            }

            // Must search on the right's side if it wasn't initially valid.
            return nextTrieNode(current.getRight(), previous, tree);
        }

        // Neither left nor right are valid, find the first parent
        // whose child did not come from the right & traverse it.
        while (current == current.getParent().getRight()) {
            // If we're going to traverse to above the subtree, stop.
            if (current == tree) {
                return null;
            }

            current = current.getParent();
        }

        // If we're on the top of the subtree, we can't go any higher.
        if (current == tree) {
            return null;
        }

        // If there's no right, the parent must be root, so we're done.
        if (current.getParent().getRight() == null) {
            return null;
        }

        // If the parent's right points to itself, we've found one.
        if (previous != current.getParent().getRight()
                && isValidUplink(current.getParent().getRight(), current.getParent())) {
            return current.getParent().getRight();
        }

        // If the parent's right is itself, there can't be any more nodes.
        if (current.getParent().getRight() == current.getParent()) {
            return null;
        }

        // We need to traverse down the parent's right's path.
        return nextTrieNode(current.getParent().getRight(), previous, tree);
    }

    /**
     * Returns the first node.
     */
    private TrieNode<K> firstNode() {
        // if Trie is empty, no first node.
        if (size == 0) {
            return null;
        }
        return traverseLeft(root);
    }

    /**
     * Traverse left through the tree until it finds a valid node.
     */
    private TrieNode<K> traverseLeft(TrieNode<K> node) {
        while (true) {
            TrieNode<K> child = node.getLeft();
            // if we hit root and it didn't have a node, go right instead.
            if (child.isEmpty()) {
                child = node.getRight();
            }

            if (child.getBitIndex() <= node.getBitIndex()) {
                return child;
            }

            node = child;
        }
    }

    /**
     * Returns true if 'next' is a valid up link coming from 'from'.
     */
    private static boolean isValidUplink(TrieNode<?> next, TrieNode<?> from) {
        return next != null && next.getBitIndex() <= from.getBitIndex() && !next.isEmpty();
    }

    private int lengthInBits(K key) {
        return (key == null) ? 0 : keyComp.lengthInBits(key);
    }

    private boolean isBitSet(K key, int bitIndex) {
        return (key == null) ? false : keyComp.isBitSet(key, bitIndex);
    }

    private int bitIndex(K key, K otherKey) {
        if (key != null && otherKey != null) {
            return keyComp.bitIndex(key, otherKey);
        } else if (key != null && otherKey == null) {
            return bitIndex(key);
        } else if (key == null && otherKey != null) {
            return bitIndex(otherKey);
        }

        return KeyComparator.NULL_BIT_KEY;
    }

    private int bitIndex(K key) {
        int lengthInBits = lengthInBits(key);
        for (int i = 0; i < lengthInBits; i++) {
            if (isBitSet(key, i)) {
                return i;
            }
        }

        return KeyComparator.NULL_BIT_KEY;
    }

    private boolean compareKeys(K key, K other) {
        if (key == null) {
            return (other == null);
        } else if (other == null) {
            return (key == null);
        }

        return keyComp.compare(key, other) == 0;
    }

    private void incrementSize() {
        size++;
    }
}
