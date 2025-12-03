package de.orat.math.gacasadi.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.SequencedCollection;
import java.util.SequencedSet;

public final class ComposableImmutableBinaryTree<T> {

    private static sealed interface Node<T> permits Branch, Leaf {
    }

    private static record Branch<T>(Node<T> left, Node<T> right) implements Node<T> {

    }

    private static record Leaf<T>(T value) implements Node<T> {

    }

    private final Node<T> root;

    private ComposableImmutableBinaryTree(Node<T> root) {
        this.root = root;
    }

    public ComposableImmutableBinaryTree(T value) {
        this.root = new Leaf<>(value);
    }

    /**
     * This is a cheap operation.
     */
    public ComposableImmutableBinaryTree<T> append(ComposableImmutableBinaryTree<T> other) {
        var branch = new Branch<>(this.root, other.root);
        return new ComposableImmutableBinaryTree<>(branch);
    }

    private SequencedCollection<T> dfsPostorderIterative() {
        Deque<T> values = new ArrayDeque<>();
        Deque<Node<T>> stack = new ArrayDeque<>();
        stack.addLast(root);
        while (!stack.isEmpty()) {
            var top = stack.pollLast(); // Process tail first.
            switch (top) {
                case Branch<T> branch -> {
                    stack.addLast(branch.right);
                    stack.addLast(branch.left); // Leftmost node at the tail.
                }
                case Leaf<T> leaf ->
                    values.addLast(leaf.value); // Rightmost leaf at the tail.
            }
        }
        return values;
    }

    /**
     * This is an expensive operation.
     */
    public SequencedSet<T> computeUniqueLeafs() {
        var leafs = this.dfsPostorderIterative();
        SequencedSet<T> uniqueLeafs = new LinkedHashSet<>(leafs);
        return uniqueLeafs;
    }
}
