package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence;

import com.jcondotta.application.query.PageRequest;
import com.jcondotta.application.query.PageResult;
import com.jcondotta.banking.recipients.application.recipient.query.list.RecipientQueryRepository;
import com.jcondotta.banking.recipients.application.recipient.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.infrastructure.adapters.metrics.RecipientMetrics;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.mapper.RecipientSummaryMapper;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.repository.RecipientEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecipientQueryPostgresRepository implements RecipientQueryRepository {

    private final RecipientEntityRepository repository;
    private final RecipientSummaryMapper summaryMapper;

    @Override
    public PageResult<RecipientSummary> findByBankAccountId(BankAccountId bankAccountId, PageRequest pageRequest) {
        var pageable = org.springframework.data.domain.PageRequest.of(
            pageRequest.page(),
            pageRequest.size(),
            Sort.by("name").ascending()
        );

        var page = repository.findByBankAccountId(bankAccountId.value(), pageable);
        var recipients = page.getContent().stream()
            .map(summaryMapper::fromEntity)
            .toList();

        return new PageResult<>(
            recipients,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }
}
