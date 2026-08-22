package com.jcondotta.banking.recipients.domain.recipient.repository;

import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.domain.core.repository.AggregateRepository;

import java.util.Optional;

public interface RecipientRepository extends AggregateRepository<Recipient, RecipientId> {

  Optional<Recipient> findByBankAccountIdAndId(BankAccountId bankAccountId, RecipientId recipientId);

  void delete(Recipient recipient);
}
