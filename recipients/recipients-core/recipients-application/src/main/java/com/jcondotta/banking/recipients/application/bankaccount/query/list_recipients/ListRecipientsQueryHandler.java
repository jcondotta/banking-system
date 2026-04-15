package com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients;

import com.jcondotta.application.query.QueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListRecipientsQueryHandler
  implements QueryHandler<ListRecipientsQuery, ListRecipientsQueryResult> {

  private final RecipientQueryRepository queryRepository;

  @Override
  public ListRecipientsQueryResult handle(ListRecipientsQuery query) {
    return new ListRecipientsQueryResult(
      queryRepository.findActiveByBankAccountId(query.bankAccountId())
    );
  }
}
