package com.lolplane.fudge.tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EitherTest {

    @Test
    @DisplayName("should map the right value to a new value when Right")
    void shouldMapRightValueToNewValueWhenRight() {
        Either<String, Integer> right = Either.right(10);

        Either<String, String> result = right.map(Object::toString);

        assertThat(result.right()).contains("10");
    }

    @Test
    @DisplayName("should not apply map and return Left when Left")
    void shouldNotApplyMapAndReturnLeftWhenLeft() {
        Either<String, Integer> left = Either.left("Error");

        Either<String, String> result = left.map(Object::toString);

        assertThat(result.left()).contains("Error");
    }
}
