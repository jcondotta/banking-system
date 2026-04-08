package com.jcondotta.banking.accounts.application.bankaccount.query.get.model;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public record BankAccountSummary(
  UUID id,
  AccountType accountType,
  Currency currency,
  String iban,
  AccountStatus accountStatus,
  Instant createdAt,
  List<AccountHolderSummary> holders
) {

  static final String ID_REQUIRED = "id must be provided";
  static final String ACCOUNT_TYPE_REQUIRED = "accountType must be provided";
  static final String CURRENCY_REQUIRED = "currency must be provided";
  static final String IBAN_REQUIRED = "iban must be provided";
  static final String ACCOUNT_STATUS_REQUIRED = "accountStatus must be provided";
  static final String CREATED_AT_REQUIRED = "createdAt must be provided";
  static final String ACCOUNT_HOLDERS_REQUIRED = "holders must be provided";
  static final String ACCOUNT_HOLDERS_NULL_ELEMENT = "holders must not contain null elements";

  public BankAccountSummary {
    requireNonNull(id, ID_REQUIRED);
    requireNonNull(accountType, ACCOUNT_TYPE_REQUIRED);
    requireNonNull(currency, CURRENCY_REQUIRED);
    requireNonNull(iban, IBAN_REQUIRED);
    requireNonNull(accountStatus, ACCOUNT_STATUS_REQUIRED);
    requireNonNull(createdAt, CREATED_AT_REQUIRED);

    requireNonNull(holders, ACCOUNT_HOLDERS_REQUIRED);
    holders.forEach(holder -> requireNonNull(holder, ACCOUNT_HOLDERS_NULL_ELEMENT));

    holders = List.copyOf(holders);
  }
}