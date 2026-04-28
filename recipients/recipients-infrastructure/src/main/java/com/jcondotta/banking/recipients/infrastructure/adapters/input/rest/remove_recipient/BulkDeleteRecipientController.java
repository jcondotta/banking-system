package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.remove_recipient;

import com.jcondotta.banking.infrastructure.adapters.output.rest.HttpHeadersConstants;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.properties.AccountRecipientsURIProperties;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.repository.RecipientEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Profile("local")
@RestController
@RequestMapping("/api/v1/recipients/bulk")
public class BulkDeleteRecipientController {

  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);
  private static final String CONTENT_TYPE_JSON = "application/json";

  private final HttpClient httpClient = HttpClient.newHttpClient();
  private final AccountRecipientsURIProperties uriProperties;
  private final RecipientEntityRepository recipientRepository;
  private final int serverPort;

  public BulkDeleteRecipientController(
    AccountRecipientsURIProperties uriProperties,
    RecipientEntityRepository recipientRepository,
    @Value("${server.port}") int serverPort
  ) {
    this.uriProperties = uriProperties;
    this.recipientRepository = recipientRepository;
    this.serverPort = serverPort;
  }

  @DeleteMapping
  public BulkDeleteRecipientResponse deleteBulk() {
    var recipients = recipientRepository.findAll().stream()
      .map(recipient -> new RecipientTarget(recipient.getBankAccountId(), recipient.getId()))
      .toList();

    if (recipients.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "there are no recipients available to delete");
    }

    var correlationId = MDC.get("correlationId");
    var plannedScenarios = plannedScenarios(recipients.size());
    var results = new ConcurrentLinkedQueue<ScenarioResult>();

    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      for (int i = 0; i < recipients.size(); i++) {
        final var target = recipients.get(i);
        final var scenario = plannedScenarios.get(i);

        executor.submit(() -> results.add(executeScenario(target, scenario, correlationId)));
      }
    }

    return summarize(recipients.size(), results);
  }

  private ScenarioResult executeScenario(
    RecipientTarget target,
    BulkDeleteScenario scenario,
    String correlationId
  ) {
    return switch (scenario) {
      case DIRECT_DELETE -> new ScenarioResult(
        scenario,
        List.of(sendDelete(target.bankAccountId(), target.recipientId(), correlationId))
      );
      case NON_EXISTING_BANK_ACCOUNT_THEN_DELETE -> new ScenarioResult(
        scenario,
        List.of(
          sendDelete(UUID.randomUUID(), target.recipientId(), correlationId),
          sendDelete(target.bankAccountId(), target.recipientId(), correlationId)
        )
      );
      case NON_EXISTING_RECIPIENT_THEN_DELETE -> new ScenarioResult(
        scenario,
        List.of(
          sendDelete(target.bankAccountId(), UUID.randomUUID(), correlationId),
          sendDelete(target.bankAccountId(), target.recipientId(), correlationId)
        )
      );
    };
  }

  private int sendDelete(UUID bankAccountId, UUID recipientId, String correlationId) {
    var requestBuilder = HttpRequest.newBuilder(URI.create(
        "http://localhost:" + serverPort + uriProperties.recipientURI(bankAccountId, recipientId)
      ))
      .timeout(REQUEST_TIMEOUT)
      .header("Accept", CONTENT_TYPE_JSON)
      .DELETE();

    if (correlationId != null) {
      requestBuilder.header(HttpHeadersConstants.CORRELATION_ID, correlationId);
    }

    return send(requestBuilder.build());
  }

  private int send(HttpRequest request) {
    try {
      return httpClient.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
    }
    catch (IOException e) {
      log.warn("Bulk recipient delete request failed with I/O error", e);
      return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.warn("Bulk recipient delete request was interrupted", e);
      return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
  }

  private List<BulkDeleteScenario> plannedScenarios(int quantity) {
    var scenarios = new ArrayList<BulkDeleteScenario>(quantity);
    var available = List.of(BulkDeleteScenario.values());

    if (quantity >= available.size()) {
      scenarios.addAll(available);
      for (int i = available.size(); i < quantity; i++) {
        scenarios.add(randomScenario());
      }
      Collections.shuffle(scenarios);
      return scenarios;
    }

    for (int i = 0; i < quantity; i++) {
      scenarios.add(randomScenario());
    }
    return scenarios;
  }

  private BulkDeleteScenario randomScenario() {
    var values = BulkDeleteScenario.values();
    return values[ThreadLocalRandom.current().nextInt(values.length)];
  }

  private BulkDeleteRecipientResponse summarize(
    int totalRecipients,
    ConcurrentLinkedQueue<ScenarioResult> results
  ) {
    var byScenario = new LinkedHashMap<String, Integer>();
    var byStatus = new LinkedHashMap<Integer, Integer>();
    int executedHttpRequests = 0;

    for (var result : results) {
      byScenario.merge(result.scenario().code, 1, Integer::sum);
      for (var status : result.statuses()) {
        byStatus.merge(status, 1, Integer::sum);
        executedHttpRequests++;
      }
    }

    return new BulkDeleteRecipientResponse(
      totalRecipients,
      results.size(),
      executedHttpRequests,
      byScenario,
      byStatus
    );
  }

  public record BulkDeleteRecipientResponse(
    int totalRecipients,
    int completedRecipients,
    int executedHttpRequests,
    Map<String, Integer> byScenario,
    Map<Integer, Integer> byStatus
  ) {
  }

  private record RecipientTarget(UUID bankAccountId, UUID recipientId) {
  }

  private record ScenarioResult(BulkDeleteScenario scenario, List<Integer> statuses) {
  }

  private enum BulkDeleteScenario {
    DIRECT_DELETE("direct_delete"),
    NON_EXISTING_BANK_ACCOUNT_THEN_DELETE("non_existing_bank_account_then_delete"),
    NON_EXISTING_RECIPIENT_THEN_DELETE("non_existing_recipient_then_delete");

    private final String code;

    BulkDeleteScenario(String code) {
      this.code = code;
    }
  }
}
