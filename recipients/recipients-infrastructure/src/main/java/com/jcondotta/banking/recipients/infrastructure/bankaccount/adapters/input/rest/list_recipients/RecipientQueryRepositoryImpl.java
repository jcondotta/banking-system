package com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.input.rest.list_recipients;

import com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients.RecipientQueryRepository;
import com.jcondotta.banking.recipients.application.bankaccount.query.mapper.RecipientSummaryMapper;
import com.jcondotta.banking.recipients.application.bankaccount.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class RecipientQueryRepositoryImpl implements RecipientQueryRepository {

  private final BankAccountRepository bankAccountRepository;
  private final RecipientSummaryMapper recipientSummaryMapper;

  @Override
  public List<RecipientSummary> findActiveByBankAccountId(BankAccountId bankAccountId) {
    return bankAccountRepository.findById(bankAccountId)
      .map(bankAccount -> recipientSummaryMapper.toSummaryList(bankAccount.getActiveRecipients()))
      .orElseThrow(() -> new BankAccountNotFoundException(bankAccountId));
  }
}