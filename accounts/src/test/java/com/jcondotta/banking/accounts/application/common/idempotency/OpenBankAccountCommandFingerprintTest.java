package com.jcondotta.banking.accounts.application.common.idempotency;

import com.jcondotta.application.idempotency.identity.IdempotencyKey;
import com.jcondotta.application.idempotency.IdempotentCommandFingerprint;
import com.jcondotta.banking.accounts.application.bankaccount.command.open.model.OpenBankAccountCommand;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.infrastructure.adapters.config.jackson.IdempotencyJacksonConfig;
import com.jcondotta.banking.infrastructure.idempotency.JacksonIdempotentCommandFingerprint;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OpenBankAccountCommandFingerprintTest {

    private static final IdempotencyKey KEY = IdempotencyKey.of(UUID.fromString("a1000000-0000-0000-0000-000000000001"));

    private final IdempotentCommandFingerprint fingerprint =
        new JacksonIdempotentCommandFingerprint(new IdempotencyJacksonConfig().idempotencyJsonMapper());

    @Test
    void shouldReturnSameHash_whenCommandsHaveSameData() {
        var firstCommand = command(AccountHolderFixtures.JEFFERSON, Currency.EUR, KEY);
        var secondCommand = command(AccountHolderFixtures.JEFFERSON, Currency.EUR, KEY);

        assertThat(fingerprint.hash(firstCommand))
            .matches("[0-9a-f]{64}")
            .isEqualTo(fingerprint.hash(secondCommand));
    }

    @Test
    void shouldReturnDifferentHash_whenCurrencyChanges() {
        var original = command(AccountHolderFixtures.JEFFERSON, Currency.EUR, KEY);
        var changed = command(AccountHolderFixtures.JEFFERSON, Currency.USD, KEY);

        assertThat(fingerprint.hash(original)).isNotEqualTo(fingerprint.hash(changed));
    }

    @Test
    void shouldReturnDifferentHash_whenHolderDataChanges() {
        var original = command(AccountHolderFixtures.JEFFERSON, Currency.EUR, KEY);
        var changed = command(AccountHolderFixtures.PATRIZIO, Currency.EUR, KEY);

        assertThat(fingerprint.hash(original)).isNotEqualTo(fingerprint.hash(changed));
    }

    @Test
    void shouldReturnDifferentHash_whenIdempotencyKeyChanges() {
        var anotherKey = IdempotencyKey.of(UUID.fromString("a1000000-0000-0000-0000-000000000002"));
        var original = command(AccountHolderFixtures.JEFFERSON, Currency.EUR, KEY);
        var changed = command(AccountHolderFixtures.JEFFERSON, Currency.EUR, anotherKey);

        assertThat(fingerprint.hash(original)).isNotEqualTo(fingerprint.hash(changed));
    }

    private OpenBankAccountCommand command(AccountHolderFixtures holder, Currency currency, IdempotencyKey key) {
        return new OpenBankAccountCommand(
            holder.personalInfo(), holder.contactInfo(), holder.address(), AccountType.CHECKING, currency, key);
    }
}
