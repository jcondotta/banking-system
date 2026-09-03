package com.jcondotta.banking.accounts.application.bankaccount.query.get;

import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.application.common.log.BankAccountEventType;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.accounts.application.common.log.BankAccountFailureReason;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetBankAccountByIbanQueryHandler
  implements QueryHandler<GetBankAccountByIbanQuery, BankAccountSummary> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetBankAccountByIbanQueryHandler.class);

  private final BankAccountQueryRepository bankAccountQueryRepository;

  @Override
  @Observed(
    name = "accounts.getByIban",
    contextualName = "getBankAccountByIban",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "getByIban"
    }
  )
  public BankAccountSummary handle(GetBankAccountByIbanQuery query) {
    var logContext = LogContext.timed(LOGGER, BankAccountEventType.GET_BY_IBAN)
      .with(BankAccountLogKey.MASKED_IBAN, query.iban().masked());

    try {
      var summary = bankAccountQueryRepository
        .findByIban(query.iban())
        .orElseThrow(() -> new BankAccountNotFoundException(query.iban()));

      logContext.info("Bank account retrieved by IBAN")
        .success()
        .log();

      return summary;
    }
    catch (DomainException ex) {
      var reason = BankAccountFailureReason.from(ex);

      logContext.warn("Bank account retrieval by IBAN failed")
        .failure()
        .with(LogKey.REASON, reason.normalize())
        .log();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during bank account retrieval by IBAN", ex)
        .failure()
        .with(LogKey.REASON, BankAccountFailureReason.INTERNAL_ERROR.normalize())
        .log();

      throw ex;
    }
  }
}
