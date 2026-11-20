package com.jcondotta.application.idempotency;

import com.jcondotta.application.command.IdempotentCommand;

public interface IdempotentCommandFingerprint {

    String hash(IdempotentCommand<?> command);
}
