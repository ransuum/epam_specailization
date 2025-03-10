package org.epam.util.own_pair;

import lombok.Getter;

import java.util.Objects;

@Getter
public final class EpamPair<L, R> {
    private static final EpamPair<Object, Object> EMPTY = new EpamPair<>(null, null);
    private final L left;
    private final R right;

    public static <L, R> EpamPair<L, R> createLeft(L left) {
        return left == null ? empty() : new EpamPair<>(left, null);
    }

    @SuppressWarnings("unchecked")
    public static <L, R> EpamPair<L, R> empty() {
        return (EpamPair<L, R>) EMPTY;
    }

    public static <L, R> EpamPair<L, R> createRight(R right) {
        return right == null ? empty() : new EpamPair<>(null, right);
    }

    public static <L, R> EpamPair<L, R> create(L left, R right) {
        return right == null && left == null ? empty() : new EpamPair<>(left, right);
    }

    private EpamPair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.left) + 31 * Objects.hashCode(this.right);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        else if (!(obj instanceof EpamPair<?, ?> pair)) return false;
        else return Objects.equals(this.left, pair.left)
                    && Objects.equals(this.right, pair.right);

    }

    @Override
    public String toString() {
        return "EpamPair{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
