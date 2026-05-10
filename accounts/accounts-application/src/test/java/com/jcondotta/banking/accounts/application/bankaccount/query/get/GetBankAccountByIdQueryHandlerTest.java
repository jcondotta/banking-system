package com.jcondotta.banking.accounts.application.bankaccount.query.get;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.accounts.application.common.log.BankAccountEventType;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.application.logging.LogOutcome;
import com.jcondotta.application.logging.StructuredLogEventSupport;
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

  private ListAppender<ILoggingEvent> logAppender;

  private QueryHandler<GetBankAccountByIdQuery, BankAccountSummary> queryHandler;

  @BeforeEach
  void setUp() {
    queryHandler = new GetBankAccountByIdQueryHandler(bankAccountQueryRepository);
    logAppender = StructuredLogEventSupport.attachAppender(GetBankAccountByIdQueryHandler.class);
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    StructuredLogEventSupport.detachAppender(GetBankAccountByIdQueryHandler.class, logAppender);
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

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.INFO);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, BankAccountEventType.GET_BY_ID)
      .containsEntry(LogKey.OUTCOME, LogOutcome.SUCCESS)
      .containsEntry(BankAccountLogKey.BANK_ACCOUNT_ID, bankAccountId.value().toString());
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
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

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.WARN);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, BankAccountEventType.GET_BY_ID)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE)
      .containsEntry(LogKey.REASON, "not_found");
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
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

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.ERROR);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, BankAccountEventType.GET_BY_ID)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE)
      .containsEntry(LogKey.REASON, "internal_error");
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }
}
