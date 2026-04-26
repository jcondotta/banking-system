package com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients;

import com.jcondotta.application.query.QueryHandler;
import io.micrometer.observation.annotation.Observed;

public class ListRecipientsQueryHandler
  implements QueryHandler<ListRecipientsQuery, ListRecipientsQueryResult> {

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
    return new ListRecipientsQueryResult(
      queryRepository.findByBankAccountId(query.bankAccountId())
    );
  }
}
