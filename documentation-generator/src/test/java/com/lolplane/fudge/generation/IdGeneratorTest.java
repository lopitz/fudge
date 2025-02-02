package com.lolplane.fudge.generation;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class IdGeneratorTest {

    @Test
    @DisplayName("should generate sequential integers starting from seed")
    void shouldGenerateSequentialIntegersStartingFromSeed() {
        var generator = IdGenerator.ofInt(5);
        var iterator = generator.iterator();

        assertThat(iterator.next()).isEqualTo(5);
        assertThat(iterator.next()).isEqualTo(6);
        assertThat(iterator.next()).isEqualTo(7);
    }

    @Test
    @DisplayName("should throw NoSuchElementException if value exceeds the maximal value of an integer")
    void shouldThrowNoSuchElementExceptionIfValueExceedsTheMaximalValueOfAnInteger() {
        var generator = IdGenerator.ofInt(Integer.MAX_VALUE - 1);
        var iterator = generator.iterator();

        assertThat(iterator.next()).isEqualTo(Integer.MAX_VALUE - 1);
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(iterator::next);
    }

    @Test
    @DisplayName("should initialize generate with given seed")
    void shouldInitializeGenerateWithGivenSeed() {
        var generator = IdGenerator.ofInt(10);
        var iterator = generator.iterator();

        assertThat(iterator.next()).isEqualTo(10);
    }

    @Test
    @DisplayName("should handle multiple generators independently")
    void shouldHandleMultipleGeneratorsIndependently() {
        var generator1 = IdGenerator.ofInt(1);
        var generator2 = IdGenerator.ofInt(100);

        var iterator1 = generator1.iterator();
        var iterator2 = generator2.iterator();

        assertThat(iterator1.next()).isEqualTo(1);
        assertThat(iterator2.next()).isEqualTo(100);

        assertThat(iterator1.next()).isEqualTo(2);
        assertThat(iterator2.next()).isEqualTo(101);
    }
}
