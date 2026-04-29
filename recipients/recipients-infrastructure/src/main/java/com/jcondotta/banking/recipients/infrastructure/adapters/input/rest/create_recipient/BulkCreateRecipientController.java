package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.create_recipient;

import com.jcondotta.banking.infrastructure.adapters.output.rest.HttpHeadersConstants;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.properties.AccountRecipientsURIProperties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("${app.api.recipients.root-path}/bulk")
public class BulkCreateRecipientController {

  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);
  private static final String CONTENT_TYPE_JSON = "application/json";
  private static final String DUPLICATE_IBAN = "GB82WEST12345698765432";

  private final HttpClient httpClient = HttpClient.newHttpClient();
  private final AccountRecipientsURIProperties uriProperties;
  private final int serverPort;

  public BulkCreateRecipientController(
    AccountRecipientsURIProperties uriProperties,
    @Value("${server.port}") int serverPort
  ) {
    this.uriProperties = uriProperties;
    this.serverPort = serverPort;
  }

  @PostMapping(path = "/{quantity}", version = "1.0")
  public BulkCreateRecipientResponse createBulk(
    @PathVariable("bank-account-id") UUID bankAccountId,
    @PathVariable int quantity
  ) {
    if (quantity < 1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "quantity must be greater than zero");
    }

    var correlationId = MDC.get("correlationId");
    var recipientsUri = URI.create("http://localhost:" + serverPort + uriProperties.recipientsURI(bankAccountId));
    var plannedScenarios = plannedScenarios(quantity);
    var results = new ConcurrentLinkedQueue<ScenarioResult>();
    var duplicateSeed = new DuplicateSeed();

    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      for (int i = 0; i < plannedScenarios.size(); i++) {
        final int index = i;
        final BulkCreateScenario scenario = plannedScenarios.get(i);

        executor.submit(() -> results.add(executeScenario(
          scenario,
          bankAccountId,
          recipientsUri,
          correlationId,
          index,
          duplicateSeed
        )));
      }
    }

    return summarize(bankAccountId, quantity, results);
  }

  private ScenarioResult executeScenario(
    BulkCreateScenario scenario,
    UUID bankAccountId,
    URI recipientsUri,
    String correlationId,
    int index,
    DuplicateSeed duplicateSeed
  ) {
    return switch (scenario) {
      case VALID_CREATE -> new ScenarioResult(
        scenario,
        sendJson(recipientsUri, correlationId, validRequestBody(index + 1)),
        1
      );
      case MISSING_BODY -> new ScenarioResult(scenario, sendWithoutBody(recipientsUri, correlationId), 1);
      case MISSING_RECIPIENT_NAME -> new ScenarioResult(
        scenario,
        sendJson(recipientsUri, correlationId, "{\"iban\":\"" + validIban(index + 1) + "\"}"),
        1
      );
      case MISSING_IBAN -> new ScenarioResult(
        scenario,
        sendJson(recipientsUri, correlationId, "{\"recipientName\":\"" + validRecipientName(index + 1) + "\"}"),
        1
      );
      case INVALID_IBAN -> new ScenarioResult(
        scenario,
        sendJson(recipientsUri, correlationId, invalidIbanRequestBody(index + 1)),
        1
      );
      case DUPLICATE_IBAN -> createDuplicateIbanScenario(recipientsUri, correlationId, bankAccountId, duplicateSeed);
    };
  }

  private ScenarioResult createDuplicateIbanScenario(
    URI recipientsUri,
    String correlationId,
    UUID bankAccountId,
    DuplicateSeed duplicateSeed
  ) {
    var setupRequests = duplicateSeed.ensureSeed(recipientsUri, correlationId, bankAccountId);
    var status = sendJson(recipientsUri, correlationId, duplicateIbanRequestBody());

    return new ScenarioResult(BulkCreateScenario.DUPLICATE_IBAN, status, setupRequests + 1);
  }

  private int sendWithoutBody(URI recipientsUri, String correlationId) {
    var requestBuilder = HttpRequest.newBuilder(recipientsUri)
      .timeout(REQUEST_TIMEOUT)
      .header("Accept", CONTENT_TYPE_JSON)
      .header("Content-Type", CONTENT_TYPE_JSON)
      .POST(HttpRequest.BodyPublishers.noBody());

    if (correlationId != null) {
      requestBuilder.header(HttpHeadersConstants.CORRELATION_ID, correlationId);
    }

    return send(requestBuilder.build());
  }

  private int sendJson(URI recipientsUri, String correlationId, String body) {
    var requestBuilder = HttpRequest.newBuilder(recipientsUri)
      .timeout(REQUEST_TIMEOUT)
      .header("Accept", CONTENT_TYPE_JSON)
      .header("Content-Type", CONTENT_TYPE_JSON)
      .POST(HttpRequest.BodyPublishers.ofString(body));

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
      log.warn("Bulk recipient create request failed with I/O error", e);
      return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.warn("Bulk recipient create request was interrupted", e);
      return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
  }

  private List<BulkCreateScenario> plannedScenarios(int quantity) {
    var scenarios = new ArrayList<BulkCreateScenario>(quantity);
    var available = List.of(BulkCreateScenario.values());

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

  private BulkCreateScenario randomScenario() {
    var values = BulkCreateScenario.values();
    return values[ThreadLocalRandom.current().nextInt(values.length)];
  }

  private BulkCreateRecipientResponse summarize(
    UUID bankAccountId,
    int requestedScenarios,
    ConcurrentLinkedQueue<ScenarioResult> results
  ) {
    var byScenario = new LinkedHashMap<String, Integer>();
    var byStatus = new LinkedHashMap<Integer, Integer>();
    int executedHttpRequests = 0;

    for (var result : results) {
      byScenario.merge(result.scenario().code, 1, Integer::sum);
      byStatus.merge(result.httpStatus(), 1, Integer::sum);
      executedHttpRequests += result.executedHttpRequests();
    }

    return new BulkCreateRecipientResponse(
      bankAccountId,
      requestedScenarios,
      results.size(),
      executedHttpRequests,
      byScenario,
      byStatus
    );
  }

  private String validRequestBody(int index) {
    return "{\"recipientName\":\"" + validRecipientName(index) + "\",\"iban\":\"" + validIban(index) + "\"}";
  }

  private String invalidIbanRequestBody(int index) {
    return "{\"recipientName\":\"" + validRecipientName(index) + "\",\"iban\":\"INVALID123\"}";
  }

  private String duplicateIbanRequestBody() {
    return "{\"recipientName\":\"recipient-duplicate\",\"iban\":\"" + DUPLICATE_IBAN + "\"}";
  }

  private String validRecipientName(int index) {
    return "recipient-" + index;
  }

  private String validIban(int index) {
    var bban = "WEST" + String.format("%014d", index);
    var checkDigits = ibanCheckDigits("GB", bban);
    return "GB" + checkDigits + bban;
  }

  private String ibanCheckDigits(String countryCode, String bban) {
    var rearranged = bban + countryCode + "00";
    var remainder = 0;

    for (char current : rearranged.toCharArray()) {
      if (Character.isLetter(current)) {
        var value = Integer.toString(current - 'A' + 10);
        for (char digit : value.toCharArray()) {
          remainder = (remainder * 10 + (digit - '0')) % 97;
        }
        continue;
      }

      remainder = (remainder * 10 + (current - '0')) % 97;
    }

    return String.format("%02d", 98 - remainder);
  }

  private final class DuplicateSeed {
    private boolean created;

    private synchronized int ensureSeed(URI recipientsUri, String correlationId, UUID bankAccountId) {
      if (created) {
        return 0;
      }

      sendJson(
        recipientsUri,
        correlationId,
        "{\"recipientName\":\"recipient-duplicate-seed-" + bankAccountId + "\",\"iban\":\"" + DUPLICATE_IBAN + "\"}"
      );
      created = true;
      return 1;
    }
  }

  public record BulkCreateRecipientResponse(
    UUID bankAccountId,
    int requestedScenarios,
    int completedScenarios,
    int executedHttpRequests,
    Map<String, Integer> byScenario,
    Map<Integer, Integer> byStatus
  ) {
  }

  private record ScenarioResult(BulkCreateScenario scenario, int httpStatus, int executedHttpRequests) {
  }

  private enum BulkCreateScenario {
    VALID_CREATE("valid_create"),
    MISSING_BODY("missing_body"),
    MISSING_RECIPIENT_NAME("missing_recipient_name"),
    MISSING_IBAN("missing_iban"),
    INVALID_IBAN("invalid_iban"),
    DUPLICATE_IBAN("duplicate_iban");

    private final String code;

    BulkCreateScenario(String code) {
      this.code = code;
    }
  }
}
