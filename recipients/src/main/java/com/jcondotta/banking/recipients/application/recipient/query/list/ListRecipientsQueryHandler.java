package com.jcondotta.banking.recipients.application.recipient.query.list;

import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.scopedlogging.application.logscope.LogContext;
import com.jcondotta.scopedlogging.LogScope;
import com.jcondotta.scopedlogging.domain.logging.LogKey;
import com.jcondotta.banking.recipients.application.common.log.RecipientLogKey;
import com.jcondotta.banking.recipients.application.common.log.RecipientOperation;
import com.jcondotta.banking.recipients.application.recipient.query.RecipientQueryRepository;
import com.jcondotta.banking.recipients.application.common.log.RecipientFailureReason;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Component;

@Component
public class ListRecipientsQueryHandler
  implements QueryHandler<ListRecipientsQuery, ListRecipientsQueryResult> {

  private final LogContext logContext;

  private final RecipientQueryRepository queryRepository;

  public ListRecipientsQueryHandler(RecipientQueryRepository queryRepository, LogContext logContext) {
    this.logContext = logContext;
    this.queryRepository = queryRepository;
  }

  @Override
  @LogScope(RecipientOperation.LIST)
  @Observed(
      name = "recipients.list",
      contextualName = "listRecipients",
      lowCardinalityKeyValues = {
          "aggregate", "recipient",
          "operation", "list"
      }
  )
  public ListRecipientsQueryResult handle(ListRecipientsQuery query) {
    logContext.with(RecipientLogKey.BANK_ACCOUNT_ID, query.bankAccountId().asString());

    try {
      var result = new ListRecipientsQueryResult(
        queryRepository.findByBankAccountId(query.bankAccountId(), query.pageRequest(), query.filter())
      );

      logContext.info("Recipients listed")
        .with(RecipientLogKey.FILTER_NAME_PRESENT, query.filter().hasName())
        .with(RecipientLogKey.RESULT_COUNT, result.page().content().size())
        .with(RecipientLogKey.PAGE, result.page().page())
        .with(RecipientLogKey.SIZE, result.page().size())
        .success();

      return result;
    }
    catch (DomainException ex) {
      var reason = RecipientFailureReason.from(ex);

      logContext.warn("Recipient listing failed: " + reason.name())
        .with(LogKey.REASON, reason.normalize())
        .failure();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during recipient listing", ex)
        .with(LogKey.REASON, RecipientFailureReason.INTERNAL_ERROR.normalize())
        .failure();

      throw ex;
    }
  }

}
