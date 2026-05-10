package com.jcondotta.banking.accounts.application.bankaccount.query.get;

import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.accounts.application.common.log.BankAccountEventType;
import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.domain.common.FailureReason;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetBankAccountByIdQueryHandler
  implements QueryHandler<GetBankAccountByIdQuery, BankAccountSummary> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetBankAccountByIdQueryHandler.class);

  private final BankAccountQueryRepository bankAccountQueryRepository;

  @Override
  @Observed(
    name = "accounts.getById",
    contextualName = "getBankAccountById",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "getById"
    }
  )
  public BankAccountSummary handle(GetBankAccountByIdQuery query) {
    var logContext = LogContext.timed(LOGGER, BankAccountEventType.GET_BY_ID)
      .with(BankAccountLogKey.BANK_ACCOUNT_ID, query.bankAccountId().value().toString());

    try {
      var summary = bankAccountQueryRepository
        .findById(query.bankAccountId())
        .orElseThrow(() -> new BankAccountNotFoundException(query.bankAccountId()));

      logContext.info("Bank account retrieved")
        .success()
        .log();

      return summary;
    }
    catch (DomainException ex) {
      var reason = FailureReason.from(ex);

      logContext.warn("Bank account retrieval failed")
        .failure()
        .with(LogKey.REASON, reason.normalize())
        .log();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during bank account retrieval", ex)
        .failure()
        .with(LogKey.REASON, FailureReason.INTERNAL_ERROR.normalize())
        .log();

      throw ex;
    }
  }
}
