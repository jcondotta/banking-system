package com.jcondotta.banking.transfers.ledger.infrastructure.adapters.output.persistence.mapper;

import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.aggregate.LedgerAccount;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.identity.LedgerAccountId;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.value_objects.AccountBalance;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.value_objects.AccountReference;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.value_objects.Balance;
import com.jcondotta.banking.transfers.ledger.infrastructure.adapters.output.persistence.entity.AccountBalanceEntity;
import com.jcondotta.banking.transfers.ledger.infrastructure.adapters.output.persistence.entity.LedgerAccountEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class LedgerAccountEntityMapper {

    public LedgerAccountEntity toEntity(LedgerAccount account) {
        return LedgerAccountEntity.builder()
                .id(account.getId().value())
                .accountReference(account.getAccountReference().value())
                .accountType(account.getAccountType())
                .currency(account.getCurrency())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .build();
    }

    public AccountBalanceEntity toBalanceEntity(LedgerAccount account) {
        return AccountBalanceEntity.builder()
                .accountId(account.getId().value())
                .bookedAmount(account.getBalance().booked().amount())
                .heldAmount(account.getBalance().held().amount())
                .version(0L)
                .updatedAt(account.getCreatedAt())
                .build();
    }

    public LedgerAccount toDomain(LedgerAccountEntity entity, AccountBalanceEntity balanceEntity) {
        Currency currency = entity.getCurrency();
        return LedgerAccount.restore(
                LedgerAccountId.of(entity.getId()),
                AccountReference.of(entity.getAccountReference()),
                entity.getAccountType(),
                currency,
                entity.getStatus(),
                entity.getCreatedAt(),
                new AccountBalance(
                    new Balance(balanceEntity.getBookedAmount(), currency),
                    new Balance(balanceEntity.getHeldAmount(), currency)
                )
        );
    }
}
