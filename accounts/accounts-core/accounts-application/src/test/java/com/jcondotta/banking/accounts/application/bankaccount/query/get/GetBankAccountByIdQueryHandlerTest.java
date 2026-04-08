package com.jcondotta.banking.accounts.application.bankaccount.query.get;

import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetBankAccountByIdQueryHandlerTest {

  @Mock
  private BankAccountQueryRepository bankAccountQueryRepository;

  private QueryHandler<GetBankAccountByIdQuery, BankAccountSummary> queryHandler;

  @BeforeEach
  void setUp() {
    queryHandler = new GetBankAccountByIdQueryHandler(bankAccountQueryRepository);
  }

  @Test
  void shouldReturnBankAccountSummary_whenBankAccountExists() {

    var bankAccountId = BankAccountId.newId();
    var query = new GetBankAccountByIdQuery(bankAccountId);

    var expectedSummary = mock(BankAccountSummary.class);

    when(bankAccountQueryRepository.findById(bankAccountId))
      .thenReturn(Optional.of(expectedSummary));

    var result = queryHandler.handle(query);

    assertThat(result).isEqualTo(expectedSummary);

    verify(bankAccountQueryRepository).findById(bankAccountId);
    verifyNoMoreInteractions(bankAccountQueryRepository);
  }

  @Test
  void shouldThrowBankAccountNotFoundException_whenBankAccountDoesNotExist() {

    var bankAccountId = BankAccountId.newId();
    var query = new GetBankAccountByIdQuery(bankAccountId);

    when(bankAccountQueryRepository.findById(bankAccountId))
      .thenReturn(Optional.empty());

    assertThatThrownBy(() -> queryHandler.handle(query))
      .isInstanceOf(BankAccountNotFoundException.class)
      .hasMessageContaining(bankAccountId.value().toString());

    verify(bankAccountQueryRepository).findById(bankAccountId);
    verifyNoMoreInteractions(bankAccountQueryRepository);
  }
}