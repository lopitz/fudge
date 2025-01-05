package com.lolplane.fudge.tools;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Either<L, R> {

    /**
     * Factory method to create a Left (representing an error or failure).
     */
    public static <L, R> Either<L, R> left(L value) {
        return new Left<>(value);
    }

    /**
     * Factory method to create a Right (representing a success or result).
     */
    public static <L, R> Either<L, R> right(R value) {
        return new Right<>(value);
    }

    /**
     * Checks if the value is Left (error).
     */
    public abstract boolean isLeft();

    /**
     * Checks if the value is Right (success).
     */
    public abstract boolean isRight();

    /**
     * Maps the Right (success) value using a mapping function.
     * <p>
     * If the value is Left, it returns the same Left without applying the function.
     */
    public abstract <T> Either<L, T> map(Function<? super R, ? extends T> mapper);

    /**
     * Maps the Left (error) value using a mapping function.
     * <p>
     * If the value is Right, it returns the same Right without applying the function.
     */
    public abstract <T> Either<T, R> mapLeft(Function<? super L, ? extends T> mapper);

    /**
     * Executes the provided Consumer if this is a Right (success).
     */
    public abstract void ifRight(Consumer<? super R> action);

    /**
     * Executes the provided Consumer if this is a Left (error).
     */
    public abstract void ifLeft(Consumer<? super L> action);

    /**
     * Returns the Right value if present, or an empty Optional if it's a Left.
     */
    public abstract Optional<R> right();

    /**
     * Returns the Left value if present, or an empty Optional if it's a Right.
     */
    public abstract Optional<L> left();

    static final class Left<L, R> extends Either<L, R> {
        private final L value;

        private Left(L value) {
            this.value = value;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public <T> Either<L, T> map(Function<? super R, ? extends T> mapper) {
            return Either.left(value);
        }

        @Override
        public <T> Either<T, R> mapLeft(Function<? super L, ? extends T> mapper) {
            return Either.left(mapper.apply(value));
        }

        @Override
        public void ifRight(Consumer<? super R> action) {
            // Do nothing since it's a Left
        }

        @Override
        public void ifLeft(Consumer<? super L> action) {
            action.accept(value);
        }

        @Override
        public Optional<R> right() {
            return Optional.empty();
        }

        @Override
        public Optional<L> left() {
            return Optional.of(value);
        }

        @Override
        public String toString() {
            return "Left[" + value + "]";
        }
    }

    private static final class Right<L, R> extends Either<L, R> {
        private final R value;

        private Right(R value) {
            this.value = value;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public <T> Either<L, T> map(Function<? super R, ? extends T> mapper) {
            return Either.right(mapper.apply(value));
        }

        @Override
        public <T> Either<T, R> mapLeft(Function<? super L, ? extends T> mapper) {
            return Either.right(value);
        }

        @Override
        public void ifRight(Consumer<? super R> action) {
            action.accept(value);
        }

        @Override
        public void ifLeft(Consumer<? super L> action) {
            // Do nothing since it's a Right
        }

        @Override
        public Optional<R> right() {
            return Optional.of(value);
        }

        @Override
        public Optional<L> left() {
            return Optional.empty();
        }

        @Override
        public String toString() {
            return "Right[" + value + "]";
        }
    }
}
