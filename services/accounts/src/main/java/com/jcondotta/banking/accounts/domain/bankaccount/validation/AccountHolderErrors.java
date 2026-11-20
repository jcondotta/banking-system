package com.jcondotta.banking.accounts.domain.bankaccount.validation;

public final class AccountHolderErrors {

  private AccountHolderErrors() {
  }

  public static final String ID_MUST_BE_PROVIDED = "account holder id must be provided";
  public static final String PERSONAL_INFO_MUST_BE_PROVIDED = "account holder personal info must be provided";
  public static final String CONTACT_INFO_MUST_BE_PROVIDED = "account holder contact info must be provided";
  public static final String ADDRESS_MUST_BE_PROVIDED = "account holder address must be provided";
  public static final String ACCOUNT_HOLDER_TYPE_MUST_BE_PROVIDED = "account holder type must be provided";
  public static final String CREATED_AT_MUST_BE_PROVIDED = "created at must be provided";
}