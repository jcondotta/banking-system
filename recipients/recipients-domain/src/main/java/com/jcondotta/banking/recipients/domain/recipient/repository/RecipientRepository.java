package com.jcondotta.banking.recipients.domain.recipient.repository;

import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;

import java.util.Optional;

public interface RecipientRepository {

  Optional<Recipient> findById(RecipientId id);
  void save(Recipient recipient);

}