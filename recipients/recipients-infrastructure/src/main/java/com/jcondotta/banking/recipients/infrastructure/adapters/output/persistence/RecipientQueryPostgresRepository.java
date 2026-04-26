package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence;

import com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients.RecipientQueryRepository;
import com.jcondotta.banking.recipients.application.bankaccount.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.infrastructure.adapters.metrics.RecipientMetrics;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.mapper.RecipientSummaryMapper;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.repository.RecipientEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecipientQueryPostgresRepository implements RecipientQueryRepository {

    private final RecipientEntityRepository repository;
    private final RecipientSummaryMapper summaryMapper;
    private final RecipientMetrics recipientMetrics;

    @Override
    public List<RecipientSummary> findByBankAccountId(BankAccountId bankAccountId) {
        var recipients = repository.findByBankAccountIdOrderByNameAsc(bankAccountId.value())
            .stream()
            .map(summaryMapper::fromEntity)
            .toList();

        recipientMetrics.recordListResultSize(recipients.size());

        return recipients;
    }
}
