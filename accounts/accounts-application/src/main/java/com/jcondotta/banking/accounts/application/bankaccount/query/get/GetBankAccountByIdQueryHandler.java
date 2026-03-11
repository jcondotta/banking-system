package com.jcondotta.banking.accounts.application.bankaccount.query.get;

import com.jcondotta.application.core.query.QueryHandler;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetBankAccountByIdQueryHandler
  implements QueryHandler<GetBankAccountByIdQuery, BankAccountSummary> {

  private final BankAccountQueryRepository bankAccountQueryRepository;

  @Override
  @Observed(
    name = "bankaccount.getById",
    contextualName = "getBankAccountById",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "getById"
    }
  )
  public BankAccountSummary handle(GetBankAccountByIdQuery query) {
    return bankAccountQueryRepository
      .findById(query.bankAccountId())
      .orElseThrow(() -> new BankAccountNotFoundException(query.bankAccountId()));
  }
}