package com.jcondotta.banking.transfers.domain.bank_transfer.aggregate;

import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_transfer.enums.TransferStatus;
import com.jcondotta.banking.transfers.domain.bank_transfer.enums.TransferType;
import com.jcondotta.banking.transfers.domain.bank_transfer.events.InternalTransferCompletedEvent;
import com.jcondotta.banking.transfers.domain.bank_transfer.events.InternalTransferRequestedEvent;
import com.jcondotta.banking.transfers.domain.bank_transfer.exceptions.InvalidTransferStatusTransitionException;
import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.banking.transfers.domain.bank_transfer.validation.BankTransferErrors;
import com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.transfer_entry.InternalTransferEntry;
import com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.transfer_entry.TransferEntry;
import com.jcondotta.banking.transfers.domain.monetary_movement.value_objects.MonetaryAmount;
import com.jcondotta.domain.core.AggregateRoot;
import com.jcondotta.domain.identity.EventId;

import java.time.Instant;
import java.util.List;

import static com.jcondotta.domain.support.Preconditions.required;

public final class BankTransfer extends AggregateRoot<BankTransferId> {

  public static final TransferStatus STATUS_ON_REQUEST = TransferStatus.PENDING;

  private final List<TransferEntry> transferEntries;
  private final TransferType transferType;
  private final String reference;
  private final Instant createdAt;

  private TransferStatus transferStatus;

  private BankTransfer(
      BankTransferId bankTransferId,
      List<TransferEntry> transferEntries,
      TransferType transferType,
      String reference,
      TransferStatus transferStatus,
      Instant createdAt
  ) {
    super(required(bankTransferId, BankTransferErrors.ID_MUST_BE_PROVIDED));
    this.transferEntries = required(transferEntries, BankTransferErrors.TRANSFER_ENTRIES_MUST_BE_PROVIDED);
    this.transferType = required(transferType, BankTransferErrors.TRANSFER_TYPE_MUST_BE_PROVIDED);
    this.reference = reference;
    this.transferStatus = required(transferStatus, BankTransferErrors.TRANSFER_STATUS_MUST_BE_PROVIDED);
    this.createdAt = required(createdAt, BankTransferErrors.CREATED_AT_MUST_BE_PROVIDED);
  }

  public static BankTransfer requestInternalTransfer(
      BankTransferId bankTransferId,
      BankAccountId senderAccountId,
      BankAccountId recipientAccountId,
      MonetaryAmount amount,
      String reference,
      Instant requestedAt
  ) {
    required(senderAccountId, BankTransferErrors.SENDER_ACCOUNT_ID_MUST_BE_PROVIDED);
    required(recipientAccountId, BankTransferErrors.RECIPIENT_ACCOUNT_ID_MUST_BE_PROVIDED);
    required(amount, BankTransferErrors.MONETARY_AMOUNT_MUST_BE_PROVIDED);
    required(requestedAt, BankTransferErrors.REQUESTED_AT_MUST_BE_PROVIDED);

    var entryDebit = InternalTransferEntry.ofDebit(senderAccountId, recipientAccountId, amount);
    var entryCredit = InternalTransferEntry.ofCredit(senderAccountId, recipientAccountId, amount);

    var transfer = new BankTransfer(
        bankTransferId,
        List.of(entryDebit, entryCredit),
        TransferType.INTERNAL,
        reference,
        STATUS_ON_REQUEST,
        requestedAt
    );

    transfer.registerEvent(new InternalTransferRequestedEvent(
        EventId.newId(),
        bankTransferId,
        senderAccountId,
        recipientAccountId,
        amount,
        reference,
        requestedAt
    ));

    return transfer;
  }

  public static BankTransfer restore(
      BankTransferId bankTransferId,
      List<TransferEntry> transferEntries,
      TransferType transferType,
      String reference,
      TransferStatus transferStatus,
      Instant createdAt
  ) {
    return new BankTransfer(bankTransferId, transferEntries, transferType, reference, transferStatus, createdAt);
  }

  public void complete(Instant completedAt) {
    required(completedAt, BankTransferErrors.COMPLETED_AT_MUST_BE_PROVIDED);

    if (transferStatus == TransferStatus.COMPLETED) {
      return;
    }

    if (transferStatus != TransferStatus.PENDING) {
      throw new InvalidTransferStatusTransitionException(transferStatus, TransferStatus.COMPLETED);
    }

    transferStatus = TransferStatus.COMPLETED;
    registerEvent(new InternalTransferCompletedEvent(EventId.newId(), getId(), completedAt));
  }

  public void fail() {
    if (transferStatus == TransferStatus.FAILED) {
      return;
    }

    if (transferStatus != TransferStatus.PENDING) {
      throw new InvalidTransferStatusTransitionException(transferStatus, TransferStatus.FAILED);
    }

    transferStatus = TransferStatus.FAILED;
  }

  public List<TransferEntry> getTransferEntries() {
    return transferEntries;
  }

  public TransferType getTransferType() {
    return transferType;
  }

  public String getReference() {
    return reference;
  }

  public TransferStatus getTransferStatus() {
    return transferStatus;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
