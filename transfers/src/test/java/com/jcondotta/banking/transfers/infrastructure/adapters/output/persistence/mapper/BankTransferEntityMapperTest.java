package com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.mapper;

import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_transfer.aggregate.BankTransfer;
import com.jcondotta.banking.transfers.domain.bank_transfer.enums.TransferStatus;
import com.jcondotta.banking.transfers.domain.bank_transfer.enums.TransferType;
import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.transfer_entry.InternalTransferEntry;
import com.jcondotta.banking.transfers.domain.monetary_movement.enums.MovementType;
import com.jcondotta.banking.transfers.domain.monetary_movement.value_objects.MonetaryAmount;
import com.jcondotta.banking.transfers.domain.shared.value_objects.Currency;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.entity.BankTransferEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BankTransferEntityMapperTest {

  private final BankTransferEntityMapper mapper = new BankTransferEntityMapper();

  @Test
  void shouldMapToEntity_whenBankTransferIsProvided() {
    var bankTransferId = BankTransferId.newId();
    var senderAccountId = BankAccountId.of(UUID.randomUUID());
    var recipientAccountId = BankAccountId.of(UUID.randomUUID());
    var requestedAt = Instant.parse("2026-05-16T12:00:00Z");
    var monetaryAmount = MonetaryAmount.of(new BigDecimal("25.50"), Currency.EUR);

    var bankTransfer = BankTransfer.requestInternalTransfer(
      bankTransferId,
      senderAccountId,
      recipientAccountId,
      monetaryAmount,
      "teste postman",
      requestedAt
    );

    var entity = mapper.toEntity(bankTransfer);

    assertThat(entity.getId()).isEqualTo(bankTransferId.value());
    assertThat(entity.getSenderAccountId()).isEqualTo(senderAccountId.value());
    assertThat(entity.getRecipientAccountId()).isEqualTo(recipientAccountId.value());
    assertThat(entity.getAmount()).isEqualByComparingTo("25.50");
    assertThat(entity.getCurrency()).isEqualTo(Currency.EUR);
    assertThat(entity.getTransferType()).isEqualTo(TransferType.INTERNAL);
    assertThat(entity.getTransferStatus()).isEqualTo(TransferStatus.PENDING);
    assertThat(entity.getReference()).isEqualTo("teste postman");
    assertThat(entity.getCreatedAt()).isEqualTo(requestedAt);
  }

  @Test
  void shouldMapToDomain_whenEntityIsProvided() {
    var bankTransferId = UUID.randomUUID();
    var senderAccountId = UUID.randomUUID();
    var recipientAccountId = UUID.randomUUID();
    var createdAt = Instant.parse("2026-05-16T12:00:00Z");

    var entity = BankTransferEntity.builder()
      .id(bankTransferId)
      .senderAccountId(senderAccountId)
      .recipientAccountId(recipientAccountId)
      .amount(new BigDecimal("25.50"))
      .currency(Currency.EUR)
      .transferType(TransferType.INTERNAL)
      .transferStatus(TransferStatus.PENDING)
      .reference("teste postman")
      .createdAt(createdAt)
      .version(0L)
      .build();

    var bankTransfer = mapper.toDomain(entity);

    assertThat(bankTransfer.getId().value()).isEqualTo(bankTransferId);
    assertThat(bankTransfer.getTransferType()).isEqualTo(TransferType.INTERNAL);
    assertThat(bankTransfer.getTransferStatus()).isEqualTo(TransferStatus.PENDING);
    assertThat(bankTransfer.getReference()).isEqualTo("teste postman");
    assertThat(bankTransfer.getCreatedAt()).isEqualTo(createdAt);
    assertThat(bankTransfer.getTransferEntries()).hasSize(2);

    var debitEntry = (InternalTransferEntry) bankTransfer.getTransferEntries().getFirst();
    var creditEntry = (InternalTransferEntry) bankTransfer.getTransferEntries().getLast();

    assertThat(debitEntry.partySender().bankAccountId().value()).isEqualTo(senderAccountId);
    assertThat(debitEntry.partyRecipient().bankAccountId().value()).isEqualTo(recipientAccountId);
    assertThat(debitEntry.monetaryMovement().movementType()).isEqualTo(MovementType.DEBIT);
    assertThat(debitEntry.monetaryMovement().amount()).isEqualByComparingTo("25.50");
    assertThat(debitEntry.monetaryMovement().currency()).isEqualTo(Currency.EUR);

    assertThat(creditEntry.partySender().bankAccountId().value()).isEqualTo(senderAccountId);
    assertThat(creditEntry.partyRecipient().bankAccountId().value()).isEqualTo(recipientAccountId);
    assertThat(creditEntry.monetaryMovement().movementType()).isEqualTo(MovementType.CREDIT);
    assertThat(creditEntry.monetaryMovement().amount()).isEqualByComparingTo("25.50");
    assertThat(creditEntry.monetaryMovement().currency()).isEqualTo(Currency.EUR);
  }
}
