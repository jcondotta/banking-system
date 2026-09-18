package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.mapper;

import com.jcondotta.banking.recipients.domain.bank_account.BankAccount;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.entity.BankAccountEntity;
import org.springframework.stereotype.Component;

@Component
public class BankAccountEntityMapper {

  public BankAccountEntity toEntity(BankAccount bankAccount) {
    return BankAccountEntity.builder()
      .id(bankAccount.id().value())
      .status(bankAccount.status())
      .build();
  }

  public BankAccount toDomain(BankAccountEntity entity) {
    return new BankAccount(BankAccountId.of(entity.getId()), entity.getStatus());
  }
}
