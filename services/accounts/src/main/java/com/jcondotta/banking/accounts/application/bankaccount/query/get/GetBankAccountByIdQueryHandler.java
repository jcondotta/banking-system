package com.jcondotta.banking.accounts.application.bankaccount.query.get;

import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.accounts.application.common.log.BankAccountOperation;
import com.jcondotta.scopedlogging.LogScope;
import com.jcondotta.scopedlogging.application.logscope.LogContext;
import com.jcondotta.scopedlogging.domain.logging.LogKey;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.application.common.log.BankAccountFailureReason;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetBankAccountByIdQueryHandler
  implements QueryHandler<GetBankAccountByIdQuery, BankAccountSummary> {

  private final BankAccountQueryRepository bankAccountQueryRepository;
  private final LogContext logContext;

  @Override
  @LogScope(BankAccountOperation.GET_BY_ID)
  @Observed(
    name = "accounts.getById",
    contextualName = "getBankAccountById",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "getById"
    }
  )
  public BankAccountSummary handle(GetBankAccountByIdQuery query) {
    logContext.with(BankAccountLogKey.BANK_ACCOUNT_ID, query.bankAccountId().value().toString());

    try {
      var summary = bankAccountQueryRepository
        .findById(query.bankAccountId())
        .orElseThrow(() -> new BankAccountNotFoundException(query.bankAccountId()));

      logContext.info("Bank account retrieved")
        .success();

      return summary;
    }
    catch (DomainException ex) {
      var reason = BankAccountFailureReason.from(ex);

      logContext.warn("Bank account retrieval failed")
        .with(LogKey.REASON, reason.normalize())
        .failure();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during bank account retrieval", ex)
        .with(LogKey.REASON, BankAccountFailureReason.INTERNAL_ERROR.normalize())
        .failure();

      throw ex;
    }
  }
}
