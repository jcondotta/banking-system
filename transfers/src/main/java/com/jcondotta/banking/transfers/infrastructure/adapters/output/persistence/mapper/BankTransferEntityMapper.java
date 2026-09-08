package com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.mapper;

import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_transfer.aggregate.BankTransfer;
import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.transfer_entry.InternalTransferEntry;
import com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.transfer_entry.TransferEntry;
import com.jcondotta.banking.transfers.domain.movement.MovementAmount;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.entity.BankTransferEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BankTransferEntityMapper {

  public BankTransferEntity toEntity(BankTransfer bankTransfer) {
    var entry = internalTransferEntry(bankTransfer);
    var movementAmount = entry.movement().movementAmount();

    return BankTransferEntity.builder()
      .id(bankTransfer.getId().value())
      .senderAccountId(entry.partySender().bankAccountId().value())
      .recipientAccountId(entry.partyRecipient().bankAccountId().value())
      .amount(movementAmount.amount())
      .currency(movementAmount.currency())
      .transferType(bankTransfer.getTransferType())
      .transferStatus(bankTransfer.getTransferStatus())
      .reference(bankTransfer.getReference())
      .createdAt(bankTransfer.getCreatedAt())
      .build();
  }

  public BankTransfer toDomain(BankTransferEntity entity) {
    var senderAccountId = BankAccountId.of(entity.getSenderAccountId());
    var recipientAccountId = BankAccountId.of(entity.getRecipientAccountId());
    var movementAmount = MovementAmount.of(entity.getAmount(), entity.getCurrency());

    return BankTransfer.restore(
      BankTransferId.of(entity.getId()),
      List.of(
        InternalTransferEntry.ofDebit(senderAccountId, recipientAccountId, movementAmount),
        InternalTransferEntry.ofCredit(senderAccountId, recipientAccountId, movementAmount)
      ),
      entity.getTransferType(),
      entity.getReference(),
      entity.getTransferStatus(),
      entity.getCreatedAt()
    );
  }

  private static InternalTransferEntry internalTransferEntry(BankTransfer bankTransfer) {
    return bankTransfer.getTransferEntries().stream()
      .filter(InternalTransferEntry.class::isInstance)
      .map(InternalTransferEntry.class::cast)
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("BankTransfer must contain an internal transfer entry"));
  }
}
