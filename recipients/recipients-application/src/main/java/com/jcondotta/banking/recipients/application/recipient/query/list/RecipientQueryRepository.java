package com.jcondotta.banking.recipients.application.recipient.query.list;

import com.jcondotta.application.query.PageRequest;
import com.jcondotta.application.query.PageResult;
import com.jcondotta.banking.recipients.application.recipient.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;

public interface RecipientQueryRepository {

  PageResult<RecipientSummary> findByBankAccountId(
    BankAccountId bankAccountId,
    PageRequest pageRequest,
    ListRecipientsFilter filter
  );

}
