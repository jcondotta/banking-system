package com.jcondotta.banking.recipients.infrastructure.config;

import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients.ListRecipientsQuery;
import com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients.ListRecipientsQueryHandler;
import com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients.ListRecipientsQueryResult;
import com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients.RecipientQueryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RecipientApplicationConfig {

  @Bean
  public QueryHandler<ListRecipientsQuery, ListRecipientsQueryResult> listRecipientsQueryHandler(
    RecipientQueryRepository recipientQueryRepository
  ) {
    return new ListRecipientsQueryHandler(recipientQueryRepository);
  }
}
