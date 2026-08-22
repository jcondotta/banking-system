package com.jcondotta.banking.recipients.application.recipient.query;

import com.jcondotta.application.query.PageRequest;
import com.jcondotta.application.query.PageResult;
import com.jcondotta.banking.recipients.application.recipient.query.list.ListRecipientsFilter;
import com.jcondotta.banking.recipients.application.recipient.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;

import java.util.Optional;

public interface RecipientQueryRepository {

  PageResult<RecipientSummary> findByBankAccountId(
    BankAccountId bankAccountId,
    PageRequest pageRequest,
    ListRecipientsFilter filter
  );

  Optional<RecipientSummary> findByBankAccountIdAndRecipientId(BankAccountId bankAccountId, RecipientId recipientId);

}
