package com.jcondotta.banking.accounts.domain.bankaccount.validation;

public final class BankAccountErrors {

  private BankAccountErrors() {
  }

  public static final String ID_MUST_BE_PROVIDED = "id must be provided";
  public static final String ACCOUNT_TYPE_MUST_BE_PROVIDED = "accountType must be provided";
  public static final String CURRENCY_MUST_BE_PROVIDED = "currency must be provided";
  public static final String ACCOUNT_STATUS_MUST_BE_PROVIDED = "account status must be provided";
  public static final String IBAN_MUST_BE_PROVIDED = "iban must be provided";
  public static final String ACCOUNT_HOLDERS_MUST_BE_PROVIDED = "accountHolders must be provided";
  public static final String CREATED_AT_MUST_BE_PROVIDED = "created at must be provided";
}