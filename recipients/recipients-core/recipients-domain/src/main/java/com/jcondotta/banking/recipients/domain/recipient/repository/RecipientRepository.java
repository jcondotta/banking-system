package com.jcondotta.banking.recipients.domain.recipient.repository;

import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.domain.core.repository.AggregateRepository;

public interface RecipientRepository extends AggregateRepository<Recipient, RecipientId> {

  void delete(Recipient recipient);
}
