package com.jcondotta.application.idempotency;

import com.jcondotta.application.command.Command;

public interface IdempotentCommand<R> extends Command<R> {
    IdempotencyKey idempotencyKey();
}
