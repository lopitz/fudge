package com.lolplane.fudge.exceptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MultipleIOExceptionTest {

    @Test
    @DisplayName("should store exceptions to retrieve causes accurately")
    void shouldStoreExceptionsToRetrieveCausesAccurately() {
        var exception1 = new IOException("Exception 1");
        var exception2 = new IOException("Exception 2");
        var exceptions = List.of(exception1, exception2);

        var multipleIOException = new MultipleIOException(exceptions);

        assertThat(multipleIOException.getCauses()).containsExactly(exception1, exception2);
    }

    @Test
    @DisplayName("should exclude null exceptions from causes to ensure consistency")
    void shouldExcludeNullExceptionsFromCausesToEnsureConsistency() {
        var exception1 = new IOException("Exception 1");
        var exceptions = new ArrayList<IOException>();
        exceptions.add(exception1);
        exceptions.add(null);

        var multipleIOException = new MultipleIOException(exceptions);

        assertThat(multipleIOException.getCauses()).containsExactly(exception1);
    }

    @Test
    @DisplayName("should summarize messages of all exceptions to create a proper message")
    void shouldSummarizeMessagesOfAllExceptionsToCreateAProperMessage() {
        var exception1 = new IOException("Exception 1");
        var exception2 = new IOException("Exception 2");
        var exceptions = List.of(exception1, exception2);

        var multipleIOException = new MultipleIOException(exceptions);

        assertThat(multipleIOException.getMessage())
            .isEqualTo("Multiple IO exceptions: Exception 1\nException 2");
    }

    @Test
    @DisplayName("should handle empty exception list gracefully to avoid errors")
    void shouldHandleEmptyExceptionListGracefullyToAvoidErrors() {
        var exceptions = List.<IOException>of();

        var multipleIOException = new MultipleIOException(exceptions);

        assertThat(multipleIOException.getCauses()).isEmpty();
        assertThat(multipleIOException.getMessage()).isEqualTo("Multiple IO exceptions: ");
    }

    @Test
    @DisplayName("should handle null messages gracefully to prevent null pointer exceptions")
    void shouldHandleNullMessagesGracefullyToPreventNullPointerExceptions() {
        var exception1 = new IOException((String) null);
        var exception2 = new IOException("Exception 2");
        var exceptions = List.of(exception1, exception2);

        var multipleIOException = new MultipleIOException(exceptions);

        assertThat(multipleIOException.getMessage())
            .isEqualTo("Multiple IO exceptions: null\nException 2");
    }

    @Test
    @DisplayName("should ensure causes list is immutable to maintain encapsulation")
    void shouldEnsureCausesListIsImmutableToMaintainEncapsulation() {
        var exception1 = new IOException("Exception 1");
        var exceptions = List.of(exception1);
        var multipleIOException = new MultipleIOException(exceptions);
        var actual = multipleIOException.getCauses();
        var newException = new IOException("New Exception");

        assertThatThrownBy(() -> actual.add(newException))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}
