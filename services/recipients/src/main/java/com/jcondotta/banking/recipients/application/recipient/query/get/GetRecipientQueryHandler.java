package com.jcondotta.banking.recipients.application.recipient.query.get;

import com.jcondotta.scopedlogging.application.logscope.LogContext;
import com.jcondotta.scopedlogging.LogScope;
import com.jcondotta.scopedlogging.domain.logging.LogKey;
import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.recipients.application.common.log.RecipientOperation;
import com.jcondotta.banking.recipients.application.common.log.RecipientLogKey;
import com.jcondotta.banking.recipients.application.recipient.query.RecipientQueryRepository;
import com.jcondotta.banking.recipients.application.common.log.RecipientFailureReason;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Component;

@Component
public class GetRecipientQueryHandler implements QueryHandler<GetRecipientQuery, GetRecipientQueryResult> {

  private final LogContext logContext;

  private final RecipientQueryRepository queryRepository;

  public GetRecipientQueryHandler(RecipientQueryRepository queryRepository, LogContext logContext) {
    this.logContext = logContext;
    this.queryRepository = queryRepository;
  }

  @Override
  @LogScope(RecipientOperation.GET)
  @Observed(
    name = "recipients.get",
    contextualName = "getRecipient",
    lowCardinalityKeyValues = {
      "aggregate", "recipient",
      "operation", "get"
    }
  )
  public GetRecipientQueryResult handle(GetRecipientQuery query) {
    logContext.with(RecipientLogKey.BANK_ACCOUNT_ID, query.bankAccountId().asString())
      .with(RecipientLogKey.RECIPIENT_ID, query.recipientId().asString());

    try {
      var recipient = queryRepository.findByBankAccountIdAndRecipientId(query.bankAccountId(), query.recipientId())
        .orElseThrow(() -> new RecipientNotFoundException(query.recipientId(), query.bankAccountId()));

      logContext.info("Recipient found")
        .success();

      return new GetRecipientQueryResult(recipient);
    }
    catch (DomainException ex) {
      var reason = RecipientFailureReason.from(ex);

      logContext.warn("Recipient retrieval failed")
        .with(LogKey.REASON, reason.normalize())
        .failure();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during recipient retrieval", ex)
        .with(LogKey.REASON, RecipientFailureReason.INTERNAL_ERROR.normalize())
        .failure();

      throw ex;
    }
  }
}
