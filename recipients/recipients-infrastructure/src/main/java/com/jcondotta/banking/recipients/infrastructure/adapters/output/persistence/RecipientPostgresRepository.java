package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence;

import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.DuplicateRecipientIbanException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientAlreadyExistsException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientOptimisticLockException;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.banking.recipients.infrastructure.adapters.metrics.persistence.RecipientPersistenceMetrics;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.mapper.RecipientEntityMapper;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.repository.RecipientEntityRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.jcondotta.banking.recipients.infrastructure.adapters.metrics.persistence.RecipientPersistenceConstraint.BANK_ACCOUNT_IBAN;
import static com.jcondotta.banking.recipients.infrastructure.adapters.metrics.persistence.RecipientPersistenceOperation.CREATE;
import static com.jcondotta.banking.recipients.infrastructure.adapters.metrics.persistence.RecipientPersistenceOperation.DELETE;
import static com.jcondotta.banking.recipients.infrastructure.adapters.metrics.persistence.RecipientPersistenceOperation.UPDATE;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RecipientPostgresRepository implements RecipientRepository {

    private static final String CONSTRAINT_DUPLICATE_IBAN = "uq_recipient_bank_account_iban";

    private final RecipientEntityRepository repository;
    private final RecipientEntityMapper mapper;
    private final RecipientPersistenceMetrics persistenceMetrics;

    @Override
    @Transactional(readOnly = true)
    public Optional<Recipient> findById(RecipientId recipientId) {
        return repository.findById(recipientId.value())
            .map(mapper::toDomain);
    }

    @Override
    @Transactional
    @Observed(
        name = "recipient.persistence.create",
        contextualName = "createRecipientInPostgres",
        lowCardinalityKeyValues = {
            "repository", "postgres",
            "operation", "create"
        }
    )
    public void create(Recipient recipient) {
        try {
            repository.saveAndFlush(mapper.toEntity(recipient));
        }
        catch (DataIntegrityViolationException e) {
            if (isDuplicateIban(e)) {
                persistenceMetrics.recordUniqueConstraintViolation(CREATE, BANK_ACCOUNT_IBAN);
                throw new DuplicateRecipientIbanException(recipient.getIban(), recipient.getBankAccountId());
            }
            throw new RecipientAlreadyExistsException(recipient.getId());
        }
    }

    @Override
    @Transactional
    public void update(Recipient recipient) {
        try {
            repository.saveAndFlush(mapper.toEntity(recipient));
        }
        catch (ObjectOptimisticLockingFailureException e) {
            persistenceMetrics.recordOptimisticLockConflict(UPDATE);
            throw new RecipientOptimisticLockException(recipient.getId());
        }
    }

    @Override
    @Transactional
    public void delete(Recipient recipient) {
        var recipientId = recipient.getId();
        var version = recipient.getVersion();

        var deleted = repository.deleteIfVersionMatches(recipientId.value(), version);
        if (deleted > 0 || !repository.existsById(recipientId.value())) {
            return;
        }

        persistenceMetrics.recordDeleteVersionMiss();
        persistenceMetrics.recordOptimisticLockConflict(DELETE);
        throw new RecipientOptimisticLockException(recipientId);
    }

    private static boolean isDuplicateIban(DataIntegrityViolationException e) {
        var msg = e.getMostSpecificCause().getMessage();
        return msg != null && msg.contains(CONSTRAINT_DUPLICATE_IBAN);
    }
}
