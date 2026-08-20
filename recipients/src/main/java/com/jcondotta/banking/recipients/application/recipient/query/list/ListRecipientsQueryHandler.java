package com.jcondotta.banking.recipients.application.recipient.query.list;

import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.recipients.application.common.log.RecipientLogKey;
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
      .with(RecipientLogKey.BANK_ACCOUNT_ID, query.bankAccountId().asString());

    try {
      var result = new ListRecipientsQueryResult(
        queryRepository.findByBankAccountId(query.bankAccountId(), query.pageRequest(), query.filter())
      );

      logContext.info("Recipients listed")
        .success()
        .with(RecipientLogKey.FILTER_NAME_PRESENT, query.filter().hasName())
        .with(RecipientLogKey.RESULT_COUNT, result.page().content().size())
        .with(RecipientLogKey.PAGE, result.page().page())
        .with(RecipientLogKey.SIZE, result.page().size())
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
