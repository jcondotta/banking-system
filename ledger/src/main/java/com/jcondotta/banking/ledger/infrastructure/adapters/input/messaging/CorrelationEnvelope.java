package com.jcondotta.banking.ledger.infrastructure.adapters.input.messaging;

import java.util.UUID;

record CorrelationEnvelope(UUID correlationId) {}
