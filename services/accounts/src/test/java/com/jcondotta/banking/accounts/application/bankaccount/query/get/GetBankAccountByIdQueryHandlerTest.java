package com.jcondotta.banking.accounts.application.bankaccount.query.get;

import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.scopedlogging.LogScopeAspect;
import com.jcondotta.scopedlogging.application.logscope.LogScopeExecutor;
import com.jcondotta.scopedlogging.application.logscope.ScopedLogContext;
import com.jcondotta.scopedlogging.infrastructure.slf4j.Slf4jLogEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

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
    var logContext = new ScopedLogContext();
    var proxy = new AspectJProxyFactory(new GetBankAccountByIdQueryHandler(bankAccountQueryRepository, logContext));
    proxy.setProxyTargetClass(true);
    proxy.addAspect(new LogScopeAspect(new LogScopeExecutor(logContext, new Slf4jLogEventPublisher())));
    queryHandler = proxy.getProxy();
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

  @Test
  void shouldThrowUnexpectedException_whenRepositoryThrowsUnexpectedException() {

    var bankAccountId = BankAccountId.newId();
    var query = new GetBankAccountByIdQuery(bankAccountId);
    var exception = new IllegalStateException("database unavailable");

    when(bankAccountQueryRepository.findById(bankAccountId))
      .thenThrow(exception);

    assertThatThrownBy(() -> queryHandler.handle(query))
      .isSameAs(exception);

    verify(bankAccountQueryRepository).findById(bankAccountId);
    verifyNoMoreInteractions(bankAccountQueryRepository);
  }
}
