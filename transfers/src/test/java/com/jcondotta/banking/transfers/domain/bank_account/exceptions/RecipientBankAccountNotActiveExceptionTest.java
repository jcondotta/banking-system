package com.jcondotta.banking.transfers.domain.bank_account.exceptions;

import com.jcondotta.banking.transfers.domain.bank_account.enums.BankAccountStatus;
import com.jcondotta.domain.exception.DomainRuleViolationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class RecipientBankAccountNotActiveExceptionTest {

    @ParameterizedTest
    @EnumSource(value = BankAccountStatus.class, names = "ACTIVE", mode = EnumSource.Mode.EXCLUDE)
    void shouldCreateException_withCorrectMessageAndStatus(BankAccountStatus status) {
        var exception = new RecipientBankAccountNotActiveException(status);

        assertThat(exception)
            .isInstanceOf(DomainRuleViolationException.class)
            .hasMessage(RecipientBankAccountNotActiveException.MESSAGE);

        assertThat(exception.getStatus()).isEqualTo(status);
    }
}
