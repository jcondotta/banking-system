package com.jcondotta.banking.recipients.application.recipient.query.list;

import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.recipients.application.common.log.LogContext;
import com.jcondotta.banking.recipients.application.common.log.LogKey;
import com.jcondotta.banking.recipients.application.common.log.RecipientEventType;
import com.jcondotta.banking.recipients.domain.common.FailureReason;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ListRecipientsQueryHandler
  implements QueryHandler<ListRecipientsQuery, ListRecipientsQueryResult> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ListRecipientsQueryHandler.class);

  private final RecipientQueryRepository queryRepository;

  public ListRecipientsQueryHandler(RecipientQueryRepository queryRepository) {
    this.queryRepository = queryRepository;
  }

  @Override
  @Observed(
      name = "recipients.list",
      contextualName = "listRecipients",
      lowCardinalityKeyValues = {
          "aggregate", "recipient",
          "operation", "list"
      }
  )
  public ListRecipientsQueryResult handle(ListRecipientsQuery query) {
    var logContext = LogContext.timed(LOGGER, RecipientEventType.LIST)
      .with(LogKey.BANK_ACCOUNT_ID, query.bankAccountId().asString());

    try {
      var result = new ListRecipientsQueryResult(
        queryRepository.findByBankAccountId(query.bankAccountId(), query.pageRequest())
      );

      logContext.info("Recipients listed")
        .success()
        .with(LogKey.RESULT_COUNT, result.page().content().size())
        .with(LogKey.PAGE, result.page().page())
        .with(LogKey.SIZE, result.page().size())
        .log();

      return result;
    }
    catch (DomainException ex) {
      var reason = FailureReason.from(ex);

      logContext.warn("Recipient listing failed: " + reason.name())
        .failure()
        .with(LogKey.REASON, reason.normalize())
        .log();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during recipient listing", ex)
        .failure()
        .with(LogKey.REASON, FailureReason.INTERNAL_ERROR.normalize())
        .log();

      throw ex;
    }
  }

}
