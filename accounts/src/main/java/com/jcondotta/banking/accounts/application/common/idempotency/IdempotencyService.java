package com.jcondotta.banking.accounts.application.common.idempotency;

import com.jcondotta.application.idempotency.repository.IdempotencyRepository;
import com.jcondotta.application.idempotency.IdempotencyRecord;
import com.jcondotta.application.idempotency.exceptions.IdempotencyKeyConflictException;
import com.jcondotta.application.command.IdempotentCommand;
import com.jcondotta.application.idempotency.IdempotentCommandFingerprint;
import com.jcondotta.application.idempotency.IdempotentSummary;
import com.jcondotta.scopedlogging.domain.logging.LogKey;
import com.jcondotta.scopedlogging.application.logscope.LogContext;
import com.jcondotta.banking.accounts.application.common.log.BankAccountFailureReason;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRepository<IdempotencyRecord<BankAccountId>> idempotencyRepository;
    private final IdempotentCommandFingerprint fingerprint;
    private final LogContext logContext;

    public IdempotentSummary<BankAccountId> check(IdempotentCommand<?> command) {
        var requestHash = fingerprint.hash(command);

        var existingRecord = idempotencyRepository.find(command.idempotencyKey());

        if (existingRecord.isEmpty()) {
            return IdempotentSummary.newOperation(command.idempotencyKey(), requestHash);
        }

        var idempotentRecord = existingRecord.get();
        if (!idempotentRecord.requestHash().equals(requestHash)) {
            logContext.warn("Idempotency key reused with different request data")
              .with(LogKey.REASON, BankAccountFailureReason.IDEMPOTENCY_CONFLICT.normalize())
              .failure();
            throw new IdempotencyKeyConflictException(command.idempotencyKey());
        }

        return IdempotentSummary.replay(idempotentRecord);
    }
}
