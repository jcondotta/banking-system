package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.repository;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.mapper.BankAccountSummaryMapper;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import com.jcondotta.banking.recipients.domain.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.recipients.domain.testsupport.BankAccountTestFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountQueryDynamoDbRepositoryTest {

  private static final AccountHolderFixtures PRIMARY_FIXTURE = AccountHolderFixtures.JEFFERSON;

  @Mock
  private BankAccountRepository bankAccountRepository;

  @Mock
  private BankAccountSummaryMapper bankAccountSummaryMapper;

  @InjectMocks
  private BankAccountQueryDynamoDbRepository repository;

  @Test
  void shouldReturnSummary_whenBankAccountIsFound() {
    BankAccount bankAccount = BankAccountTestFactory.withPrimary(PRIMARY_FIXTURE);
    BankAccountSummary summary = mock(BankAccountSummary.class);

    when(bankAccountRepository.findById(bankAccount.getId())).thenReturn(Optional.of(bankAccount));
    when(bankAccountSummaryMapper.toSummary(bankAccount)).thenReturn(summary);

    assertThat(repository.findById(bankAccount.getId())).hasValue(summary);

    verify(bankAccountRepository).findById(bankAccount.getId());
    verify(bankAccountSummaryMapper).toSummary(bankAccount);
  }

  @Test
  void shouldReturnEmpty_whenBankAccountIsNotFound() {
    BankAccountId bankAccountId = BankAccountId.newId();

    when(bankAccountRepository.findById(bankAccountId)).thenReturn(Optional.empty());

    assertThat(repository.findById(bankAccountId)).isEmpty();

    verify(bankAccountRepository).findById(bankAccountId);
    verifyNoInteractions(bankAccountSummaryMapper);
  }
}