package com.jcondotta.banking.recipients.application.recipient.query.list;

import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.recipients.application.common.log.LogKey;
import com.jcondotta.banking.recipients.application.common.log.LogOutcome;
import com.jcondotta.banking.recipients.application.common.log.RecipientEventType;
import com.jcondotta.banking.recipients.domain.common.FailureReason;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ListRecipientsQueryHandler
  implements QueryHandler<ListRecipientsQuery, ListRecipientsQueryResult> {

  private static final Logger log = LoggerFactory.getLogger(ListRecipientsQueryHandler.class);

  private final RecipientQueryRepository queryRepository;

  public ListRecipientsQueryHandler(RecipientQueryRepository queryRepository) {
    this.queryRepository = queryRepository;
  }

  @Override
  @Observed(
    name = "recipient.list",
    contextualName = "listRecipients",
    lowCardinalityKeyValues = {
      "aggregate", "recipient",
      "operation", "list"
    }
  )
  public ListRecipientsQueryResult handle(ListRecipientsQuery query) {
    var startNs = System.nanoTime();

    log.atDebug()
      .setMessage("Recipient listing started")
      .addKeyValue(LogKey.EVENT_TYPE, RecipientEventType.LIST)
      .addKeyValue(LogKey.BANK_ACCOUNT_ID, query.bankAccountId().asString())
      .log();

    try {
      var result = new ListRecipientsQueryResult(
        queryRepository.findByBankAccountId(query.bankAccountId())
      );

      log.atInfo()
        .setMessage("Recipients listed")
        .addKeyValue(LogKey.EVENT_TYPE, RecipientEventType.LIST)
        .addKeyValue(LogKey.OUTCOME, LogOutcome.SUCCESS)
        .addKeyValue(LogKey.BANK_ACCOUNT_ID, query.bankAccountId().asString())
        .addKeyValue(LogKey.RESULT_COUNT, result.recipients().size())
        .addKeyValue(LogKey.DURATION_MS, durationMs(startNs))
        .log();

      return result;
    }
    catch (DomainException ex) {
      var reason = FailureReason.from(ex);
      log.atWarn()
        .setMessage("Recipient listing failed: " + reason.name())
        .addKeyValue(LogKey.EVENT_TYPE, RecipientEventType.LIST)
        .addKeyValue(LogKey.OUTCOME, LogOutcome.FAILURE)
        .addKeyValue(LogKey.REASON, reason.normalize())
        .addKeyValue(LogKey.BANK_ACCOUNT_ID, query.bankAccountId().asString())
        .addKeyValue(LogKey.DURATION_MS, durationMs(startNs))
        .log();

      throw ex;
    }
    catch (Exception ex) {
      log.atError()
        .setMessage("Unexpected error during recipient listing")
        .addKeyValue(LogKey.EVENT_TYPE, RecipientEventType.LIST)
        .addKeyValue(LogKey.OUTCOME, LogOutcome.FAILURE)
        .addKeyValue(LogKey.REASON, FailureReason.INTERNAL_ERROR.normalize())
        .addKeyValue(LogKey.BANK_ACCOUNT_ID, query.bankAccountId().asString())
        .addKeyValue(LogKey.DURATION_MS, durationMs(startNs))
        .setCause(ex)
        .log();

      throw ex;
    }
  }

  private static long durationMs(long startNs) {
    return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
  }
}
