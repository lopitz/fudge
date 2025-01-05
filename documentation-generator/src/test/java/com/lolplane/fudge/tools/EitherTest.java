package com.lolplane.fudge.tools;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EitherTest {

    @Test
    @DisplayName("should map the right value to a new value when Right")
    void shouldMapRightValueToNewValueWhenRight() {
        var rightMappingActual = new AtomicInteger();
        var leftMappingActual = new AtomicInteger();

        Either<String, Integer> result = Either.right(10);
        result.map(r -> rightMappingActual.getAndIncrement());
        result.mapLeft(r -> leftMappingActual.getAndIncrement());
        result.ifRight(r -> rightMappingActual.getAndIncrement());
        result.ifLeft(r -> leftMappingActual.getAndIncrement());

        assertThat(result.isLeft()).isFalse();
        assertThat(result.isRight()).isTrue();
        assertThat(result.right()).contains(10);
        assertThat(result.left()).isEmpty();
        assertThat(rightMappingActual.get()).isEqualTo(2);
        assertThat(leftMappingActual.get()).isZero();
        assertThat(result).hasToString("Right[10]");
    }

    @Test
    @DisplayName("should not apply map and return Left when Left")
    void shouldNotApplyMapAndReturnLeftWhenLeft() {
        var rightMappingActual = new AtomicInteger();
        var leftMappingActual = new AtomicInteger();

        Either<String, Integer> result = Either.left("Error");
        result.map(r -> rightMappingActual.getAndIncrement());
        result.mapLeft(r -> leftMappingActual.getAndIncrement());
        result.ifRight(r -> rightMappingActual.getAndIncrement());
        result.ifLeft(r -> leftMappingActual.getAndIncrement());


        assertThat(result.isLeft()).isTrue();
        assertThat(result.isRight()).isFalse();
        assertThat(result.left()).contains("Error");
        assertThat(result.right()).isEmpty();
        assertThat(rightMappingActual.get()).isZero();
        assertThat(leftMappingActual.get()).isEqualTo(2);
        assertThat(result).hasToString("Left[Error]");
    }
}
