package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open;

import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.AccountTypeRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.CurrencyRequest;
//import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.OpenBankAccountRequest;
//import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store.OutboxEventStore;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.OpenBankAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/v1/bank-accounts/bulk")
@RequiredArgsConstructor
public class BulkBankAccountController {

    private final BankAccountsHttpClient client;

    @PostMapping("/{quantity}")
    public void createBulk(@PathVariable int quantity) {

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            IntStream.range(0, quantity)
              .forEach(i -> executor.submit(() -> {
                  var request = buildRequest(i);
                  client.create(request);
              }));

        }
    }

    private OpenBankAccountRequest buildRequest(int i) {
        return new OpenBankAccountRequest(
            AccountTypeRequest.CHECKING,
            CurrencyRequest.EUR,
            AccountHolderRequestFactory.random(i)
        );
    }
}