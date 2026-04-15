package com.jcondotta.banking.recipients.application.bankaccount.command.create_recipient;

import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.repository.BankAccountRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateRecipientCommandHandler implements CommandHandlerWithResult<CreateRecipientCommand, RecipientId> {

  private final BankAccountRepository bankAccountRepository;
  private final Clock clock;

  @Override
  @Observed(
    name = "bankaccount.recipient.create",
    contextualName = "createRecipient",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "create"
    })
  public RecipientId handle(CreateRecipientCommand command) {
    var bankAccount = bankAccountRepository.findById(command.bankAccountId())
      .orElseThrow(() -> new BankAccountNotFoundException(command.bankAccountId()));

    Recipient recipient = bankAccount.createRecipient(
      command.recipientName(),
      command.iban(),
      Instant.now(clock)
    );

    bankAccountRepository.save(bankAccount);

    log.info(
      "Recipient created successfully [recipientId={}, bankAccountId={}]",
      recipient.getId().value(),
      command.bankAccountId().value()
    );

    return recipient.getId();
  }
}
