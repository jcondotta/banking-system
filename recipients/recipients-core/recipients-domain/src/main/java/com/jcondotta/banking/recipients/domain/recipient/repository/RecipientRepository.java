package com.jcondotta.banking.recipients.domain.recipient.repository;

import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;

import java.util.Optional;

public interface RecipientRepository {

  Optional<Recipient> findById(RecipientId recipientId);

  void create(Recipient recipient);

  void update(Recipient recipient);

  void delete(Recipient recipient);
}
