package com.jcondotta.banking.transfers.domain.bank_transfer.exceptions;

import com.jcondotta.domain.exception.DomainRuleViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdenticalInternalPartiesExceptionTest {

    @Test
    void shouldCreateException_withCorrectMessage() {
        var exception = new IdenticalInternalPartiesException();

        assertThat(exception)
            .isInstanceOf(DomainRuleViolationException.class)
            .hasMessage(IdenticalInternalPartiesException.MESSAGE);
    }
}
