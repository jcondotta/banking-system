package com.jcondotta.banking.recipients.application.recipient.query.get;

import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.recipients.application.common.log.RecipientEventType;
import com.jcondotta.banking.recipients.application.common.log.RecipientLogKey;
import com.jcondotta.banking.recipients.application.recipient.query.RecipientQueryRepository;
import com.jcondotta.banking.recipients.domain.common.FailureReason;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GetRecipientQueryHandler implements QueryHandler<GetRecipientQuery, GetRecipientQueryResult> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetRecipientQueryHandler.class);

  private final RecipientQueryRepository queryRepository;

  public GetRecipientQueryHandler(RecipientQueryRepository queryRepository) {
    this.queryRepository = queryRepository;
  }

  @Override
  @Observed(
    name = "recipients.get",
    contextualName = "getRecipient",
    lowCardinalityKeyValues = {
      "aggregate", "recipient",
      "operation", "get"
    }
  )
  public GetRecipientQueryResult handle(GetRecipientQuery query) {
    var logContext = LogContext.timed(LOGGER, RecipientEventType.GET)
      .with(RecipientLogKey.BANK_ACCOUNT_ID, query.bankAccountId().asString())
      .with(RecipientLogKey.RECIPIENT_ID, query.recipientId().asString());

    try {
      var recipient = queryRepository.findByBankAccountIdAndRecipientId(query.bankAccountId(), query.recipientId())
        .orElseThrow(() -> new RecipientNotFoundException(query.recipientId(), query.bankAccountId()));

      logContext.info("Recipient found")
        .success()
        .log();

      return new GetRecipientQueryResult(recipient);
    }
    catch (DomainException ex) {
      var reason = FailureReason.from(ex);

      logContext.warn("Recipient retrieval failed")
        .failure()
        .with(LogKey.REASON, reason.normalize())
        .log();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during recipient retrieval", ex)
        .failure()
        .with(LogKey.REASON, FailureReason.INTERNAL_ERROR.normalize())
        .log();

      throw ex;
    }
  }
}
