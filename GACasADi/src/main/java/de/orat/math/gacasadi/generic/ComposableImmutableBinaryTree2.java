package de.orat.math.gacasadi.generic;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.SequencedCollection;
import java.util.SequencedSet;

public sealed interface ComposableImmutableBinaryTree2<T> permits ComposableImmutableBinaryTree2.Branch, ComposableImmutableBinaryTree2.Leaf {

    public static final record Branch<T>(ComposableImmutableBinaryTree2<T> left, ComposableImmutableBinaryTree2<T> right) implements ComposableImmutableBinaryTree2<T> {

    }

    public static final record Leaf<T>(T value) implements ComposableImmutableBinaryTree2<T> {

    }

    public static <T> ComposableImmutableBinaryTree2<T> create(T value) {
        return new Leaf<>(value);
    }

    /**
     * This is a cheap operation.
     */
    public default ComposableImmutableBinaryTree2<T> append(ComposableImmutableBinaryTree2<T> other) {
        return new Branch<>(this, other);
    }

    private SequencedCollection<T> dfsPostorderIterative() {
        Deque<T> values = new ArrayDeque<>();
        Deque<ComposableImmutableBinaryTree2<T>> stack = new ArrayDeque<>();
        stack.addLast(this);
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
    public default SequencedSet<T> computeUniqueLeafs() {
        var leafs = this.dfsPostorderIterative();
        SequencedSet<T> uniqueLeafs = new LinkedHashSet<>(leafs);
        return uniqueLeafs;
    }
}
