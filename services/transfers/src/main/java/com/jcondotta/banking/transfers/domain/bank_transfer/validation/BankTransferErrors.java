package com.jcondotta.banking.transfers.domain.bank_transfer.validation;

public final class BankTransferErrors {

  private BankTransferErrors() {}

  public static final String ID_MUST_BE_PROVIDED = "bank transfer id must be provided";
  public static final String TRANSFER_ENTRIES_MUST_BE_PROVIDED = "transfer entries must be provided";
  public static final String TRANSFER_TYPE_MUST_BE_PROVIDED = "transfer type must be provided";
  public static final String TRANSFER_STATUS_MUST_BE_PROVIDED = "transfer status must be provided";
  public static final String CREATED_AT_MUST_BE_PROVIDED = "created at must be provided";
  public static final String REQUESTED_AT_MUST_BE_PROVIDED = "requested at must be provided";
  public static final String COMPLETED_AT_MUST_BE_PROVIDED = "completed at must be provided";
  public static final String FAILED_AT_MUST_BE_PROVIDED = "failed at must be provided";
  public static final String SENDER_ACCOUNT_ID_MUST_BE_PROVIDED = "sender account id must be provided";
  public static final String RECIPIENT_ACCOUNT_ID_MUST_BE_PROVIDED = "recipient account id must be provided";

  public static final String MOVEMENT_AMOUNT_MUST_BE_PROVIDED = "movement amount must be provided";
  public static final String AMOUNT_MUST_BE_PROVIDED = "amount must be provided";
  public static final String CURRENCY_MUST_BE_PROVIDED = "currency must be provided";
}
