package com.jcondotta.banking.infrastructure.idempotency;

import com.jcondotta.application.command.IdempotentCommand;
import com.jcondotta.application.idempotency.IdempotentCommandFingerprint;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import static java.util.Objects.requireNonNull;

/**
 * SHA-256 of the complete command, including its key, serialized as UTF-8 JSON
 * using the dedicated idempotency mapper.
 */
@Component
public final class JacksonIdempotentCommandFingerprint implements IdempotentCommandFingerprint {

    private final JsonMapper mapper;

    public JacksonIdempotentCommandFingerprint(@Qualifier("idempotencyJsonMapper") JsonMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String hash(IdempotentCommand<?> command) {
        byte[] serializedCommand = mapper.writeValueAsBytes(requireNonNull(command, "command must be provided"));
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(serializedCommand);
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 unavailable", ex);
        }
    }
}
