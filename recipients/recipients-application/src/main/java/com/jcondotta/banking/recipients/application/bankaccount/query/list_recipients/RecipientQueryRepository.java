package com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients;

import com.jcondotta.banking.recipients.application.bankaccount.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;

import java.util.List;

public interface RecipientQueryRepository {

  List<RecipientSummary> findActiveByBankAccountId(BankAccountId bankAccountId);

}