package com.jcondotta.banking.transfers.domain.bank_transfer.repository;

import com.jcondotta.banking.transfers.domain.bank_transfer.aggregate.BankTransfer;
import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.domain.core.repository.AggregateRepository;

public interface BankTransferRepository extends AggregateRepository<BankTransfer, BankTransferId> {

}