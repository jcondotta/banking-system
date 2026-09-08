package com.jcondotta.banking.transfers.ledger.application.common.log;

import com.jcondotta.banking.transfers.ledger.domain.ledger_account.exceptions.LedgerAccountNotFoundException;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.exceptions.LedgerAccountNotYetProvisionedException;
import com.jcondotta.domain.exception.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class LedgerFailureReasonTest {

    @ParameterizedTest
    @CsvSource({
        "NOT_FOUND, not_found",
        "DOMAIN_ERROR, domain_error",
        "INTERNAL_ERROR, internal_error"
    })
    void shouldNormalizeFailureReason_whenConvertingToLogFriendlyValue(
        LedgerFailureReason failureReason,
        String expected
    ) {
        assertThat(failureReason.normalize()).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("domainExceptions")
    void shouldReturnExpectedFailureReason_whenExceptionTypeIsKnown(
        DomainException exception,
        LedgerFailureReason expected
    ) {
        assertThat(LedgerFailureReason.from(exception)).isEqualTo(expected);
    }

    @Test
    void shouldReturnDomainError_whenExceptionTypeIsUnknown() {
        assertThat(LedgerFailureReason.from(new TestDomainException()))
            .isEqualTo(LedgerFailureReason.DOMAIN_ERROR);
    }

    @Test
    void shouldReturnDomainError_whenExceptionIsNull() {
        assertThat(LedgerFailureReason.from(null)).isEqualTo(LedgerFailureReason.DOMAIN_ERROR);
    }

    static Stream<Arguments> domainExceptions() {
        var accountReference = UUID.randomUUID();

        return Stream.of(
            Arguments.of(new LedgerAccountNotFoundException(accountReference), LedgerFailureReason.NOT_FOUND),
            Arguments.of(new LedgerAccountNotYetProvisionedException(accountReference), LedgerFailureReason.NOT_FOUND)
        );
    }

    private static final class TestDomainException extends DomainException {
        private TestDomainException() {
            super("domain error");
        }
    }
}
