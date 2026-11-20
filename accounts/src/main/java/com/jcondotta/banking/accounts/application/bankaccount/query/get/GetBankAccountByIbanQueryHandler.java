package com.jcondotta.banking.accounts.application.bankaccount.query.get;

import com.jcondotta.scopedlogging.LogScope;
import com.jcondotta.scopedlogging.application.logscope.LogContext;
import com.jcondotta.scopedlogging.domain.logging.LogKey;
import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.application.common.log.BankAccountOperation;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.accounts.application.common.log.BankAccountFailureReason;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetBankAccountByIbanQueryHandler
  implements QueryHandler<GetBankAccountByIbanQuery, BankAccountSummary> {

  private final BankAccountQueryRepository bankAccountQueryRepository;
  private final LogContext logContext;

  @Override
  @LogScope(BankAccountOperation.GET_BY_IBAN)
  @Observed(
    name = "accounts.getByIban",
    contextualName = "getBankAccountByIban",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "getByIban"
    }
  )
  public BankAccountSummary handle(GetBankAccountByIbanQuery query) {
    logContext.with(BankAccountLogKey.MASKED_IBAN, query.iban().masked());

    try {
      var summary = bankAccountQueryRepository
        .findByIban(query.iban())
        .orElseThrow(() -> new BankAccountNotFoundException(query.iban()));

      logContext.info("Bank account retrieved by IBAN")
        .with(BankAccountLogKey.BANK_ACCOUNT_ID, summary.id())
        .success();

      return summary;
    }
    catch (DomainException ex) {
      var reason = BankAccountFailureReason.from(ex);

      logContext.warn("Bank account retrieval by IBAN failed")
        .with(LogKey.REASON, reason.normalize())
        .failure();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during bank account retrieval by IBAN", ex)
        .with(LogKey.REASON, BankAccountFailureReason.INTERNAL_ERROR.normalize())
        .failure();

      throw ex;
    }
  }
}
