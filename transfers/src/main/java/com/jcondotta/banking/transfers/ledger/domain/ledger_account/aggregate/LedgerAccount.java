package com.jcondotta.banking.transfers.ledger.domain.ledger_account.aggregate;

import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.transfers.domain.movement.Movement;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.enums.LedgerAccountStatus;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.enums.LedgerAccountType;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.identity.LedgerAccountId;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.value_objects.AccountBalance;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.value_objects.AccountReference;
import com.jcondotta.domain.core.AggregateRoot;

import java.time.Instant;

import static com.jcondotta.domain.support.Preconditions.required;

public final class LedgerAccount extends AggregateRoot<LedgerAccountId> {

    private static final String ACCOUNT_REFERENCE_REQUIRED = "Account reference must be provided";
    private static final String ACCOUNT_TYPE_REQUIRED = "Account type must be provided";
    private static final String CURRENCY_REQUIRED = "Currency must be provided";
    private static final String STATUS_REQUIRED = "Status must be provided";
    private static final String CREATED_AT_REQUIRED = "Created at must be provided";
    private static final String BALANCE_REQUIRED = "Balance must be provided";

    private final AccountReference accountReference;
    private final LedgerAccountType accountType;
    private final Currency currency;
    private LedgerAccountStatus status;
    private final Instant createdAt;
    private AccountBalance balance;

    private LedgerAccount(
            LedgerAccountId id,
            AccountReference accountReference,
            LedgerAccountType accountType,
            Currency currency,
            LedgerAccountStatus status,
            Instant createdAt,
            AccountBalance balance
    ) {
        super(required(id, "Ledger account id must be provided"));
        this.accountReference = required(accountReference, ACCOUNT_REFERENCE_REQUIRED);
        this.accountType = required(accountType, ACCOUNT_TYPE_REQUIRED);
        this.currency = required(currency, CURRENCY_REQUIRED);
        this.status = required(status, STATUS_REQUIRED);
        this.createdAt = required(createdAt, CREATED_AT_REQUIRED);
        this.balance = required(balance, BALANCE_REQUIRED);
    }

    public static LedgerAccount provision(
            AccountReference accountReference,
            Currency currency,
            Instant createdAt
    ) {
        return new LedgerAccount(
                LedgerAccountId.newId(),
                accountReference,
                LedgerAccountType.CUSTOMER,
                currency,
                LedgerAccountStatus.ACTIVE,
                createdAt,
                AccountBalance.zero(currency)
        );
    }

    public static LedgerAccount restore(
            LedgerAccountId id,
            AccountReference accountReference,
            LedgerAccountType accountType,
            Currency currency,
            LedgerAccountStatus status,
            Instant createdAt,
            AccountBalance balance
    ) {
        return new LedgerAccount(id, accountReference, accountType, currency, status, createdAt, balance);
    }

    public AccountReference getAccountReference() {
        return accountReference;
    }

    public LedgerAccountType getAccountType() {
        return accountType;
    }

    public Currency getCurrency() {
        return currency;
    }

    public LedgerAccountStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public AccountBalance getBalance() {
        return balance;
    }

    public void applyMovement(Movement movement) {
        var updatedBooked = balance.booked().apply(movement);
        balance = new AccountBalance(updatedBooked, balance.held());
    }
}
