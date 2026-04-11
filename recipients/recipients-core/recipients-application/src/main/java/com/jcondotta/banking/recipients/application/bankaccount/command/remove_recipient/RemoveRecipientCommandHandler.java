package com.jcondotta.banking.recipients.application.bankaccount.command.remove_recipient;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.repository.BankAccountRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveRecipientCommandHandler implements CommandHandler<RemoveRecipientCommand> {

  private final BankAccountRepository bankAccountRepository;

  @Override
  @Observed(
    name = "bankaccount.recipient.remove",
    contextualName = "removeRecipient",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "remove"
    })
  public void handle(RemoveRecipientCommand command) {
    var bankAccount = bankAccountRepository.findById(command.bankAccountId())
      .orElseThrow(() -> new BankAccountNotFoundException(command.bankAccountId()));

    bankAccount.removeRecipient(command.recipientId());
    bankAccountRepository.save(bankAccount);

    log.info(
      "Recipient removed successfully [recipientId={}, bankAccountId={}]",
      command.recipientId().value(),
      command.bankAccountId().value()
    );
  }
}
