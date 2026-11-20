package com.jcondotta.banking.infrastructure.idempotency;

import com.jcondotta.application.idempotency.identity.IdempotencyKey;
import com.jcondotta.application.command.IdempotentCommand;
import com.jcondotta.application.idempotency.IdempotentCommandFingerprint;
import com.jcondotta.banking.infrastructure.adapters.config.jackson.IdempotencyJacksonConfig;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import tools.jackson.databind.json.JsonMapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class JacksonIdempotentCommandFingerprintTest {

    private static final IdempotencyKey KEY = IdempotencyKey.of(UUID.fromString("a1000000-0000-0000-0000-000000000001"));

    private final IdempotentCommandFingerprint fingerprint =
        new JacksonIdempotentCommandFingerprint(new IdempotencyJacksonConfig().idempotencyJsonMapper());

    @Test
    void shouldReturnSameHash_whenMapInsertionOrderChanges() {
        var firstAttributes = new LinkedHashMap<String, String>();
        firstAttributes.put("name", "Ana");
        firstAttributes.put("currency", "EUR");

        var secondAttributes = new LinkedHashMap<String, String>();
        secondAttributes.put("currency", "EUR");
        secondAttributes.put("name", "Ana");

        assertThat(fingerprint.hash(new TestCommand(KEY, firstAttributes)))
            .matches("[0-9a-f]{64}")
            .isEqualTo(fingerprint.hash(new TestCommand(KEY, secondAttributes)));
    }

    @Test
    void shouldReturnDifferentHash_whenCommandDataChanges() {
        var original = new TestCommand(KEY, Map.of("name", "Ana"));
        var changed = new TestCommand(KEY, Map.of("name", "Maria"));

        assertThat(fingerprint.hash(original)).isNotEqualTo(fingerprint.hash(changed));
    }

    @Test
    void shouldUseDedicatedMapper_whenDefaultMapperAlsoExists() {
        var defaultMapper = mock(JsonMapper.class);
        try (var context = new AnnotationConfigApplicationContext()) {
            context.registerBean("jsonMapper", JsonMapper.class, () -> defaultMapper);
            context.register(IdempotencyJacksonConfig.class, JacksonIdempotentCommandFingerprint.class);
            context.refresh();

            var command = new TestCommand(KEY, Map.of("name", "Ana"));
            var sharedFingerprint = context.getBean(IdempotentCommandFingerprint.class);

            assertSoftly(softly -> {
                softly.assertThat(context.getBean(JsonMapper.class)).isSameAs(defaultMapper);
                softly.assertThat(sharedFingerprint.hash(command)).isEqualTo(fingerprint.hash(command));
            });
            verifyNoInteractions(defaultMapper);
        }
    }

    record TestCommand(IdempotencyKey idempotencyKey, Map<String, String> attributes) implements IdempotentCommand<Void> {
    }
}
