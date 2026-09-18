package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.mapper;

import com.jcondotta.banking.recipients.domain.bank_account.BankAccount;
import com.jcondotta.banking.recipients.domain.bank_account.enums.BankAccountStatus;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.entity.BankAccountEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class BankAccountEntityMapperTest {

  private final BankAccountEntityMapper mapper = new BankAccountEntityMapper();

  @Test
  void shouldMapBankAccountToEntity() {
    var bankAccount = new BankAccount(BankAccountId.of(UUID.randomUUID()), BankAccountStatus.BLOCKED);

    var entity = mapper.toEntity(bankAccount);

    assertSoftly(softly -> {
      softly.assertThat(entity.getId()).isEqualTo(bankAccount.id().value());
      softly.assertThat(entity.getStatus()).isEqualTo(bankAccount.status());
    });
  }

  @Test
  void shouldMapEntityToBankAccount() {
    var id = UUID.randomUUID();
    var entity = BankAccountEntity.builder()
      .id(id)
      .status(BankAccountStatus.CLOSED)
      .build();

    var bankAccount = mapper.toDomain(entity);

    assertSoftly(softly -> {
      softly.assertThat(bankAccount.id()).isEqualTo(BankAccountId.of(id));
      softly.assertThat(bankAccount.status()).isEqualTo(BankAccountStatus.CLOSED);
    });
  }
}
