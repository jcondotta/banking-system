package com.jcondotta.banking.recipients.application.recipient.query.list;

import com.jcondotta.banking.recipients.application.recipient.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;

import java.util.List;

public interface RecipientQueryRepository {

  List<RecipientSummary> findByBankAccountId(BankAccountId bankAccountId);

}
