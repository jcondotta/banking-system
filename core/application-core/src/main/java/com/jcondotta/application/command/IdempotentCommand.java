package com.jcondotta.application.command;

import com.jcondotta.application.idempotency.identity.IdempotencyKey;

public interface IdempotentCommand<R> extends Command<R> {

    IdempotencyKey idempotencyKey();
}
