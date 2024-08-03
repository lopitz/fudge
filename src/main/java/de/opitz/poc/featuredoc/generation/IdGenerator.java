package de.opitz.poc.featuredoc.generation;

import java.util.Iterator;
import java.util.NoSuchElementException;

public record IdGenerator<T>(Iterator<T> iterator) {

    public static IdGenerator<Integer> ofInt(int seed) {
        return new IdGenerator<>(new IntIterator(seed));
    }

    private static class IntIterator implements Iterator<Integer> {
        private int currentValue;

        public IntIterator() {
            this(1);
        }

        public IntIterator(int seed) {
            this.currentValue = seed;
        }

        @Override
        public boolean hasNext() {
            return currentValue < Integer.MAX_VALUE;
        }

        @Override
        public Integer next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return currentValue++;
        }
    }
}
