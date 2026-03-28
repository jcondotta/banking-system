package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.mapper;

import com.jcondotta.banking.accounts.infrastructure.config.ClockTestFactory;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.UUID;

class BankAccountEntityMapperImplTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
  private static final UUID ACCOUNT_HOLDER_UUID = UUID.randomUUID();

  private static final Iban VALID_IBAN = Iban.of("ES3801283316232166447417");

  private static final Instant NOW = Instant.now(ClockTestFactory.FIXED_CLOCK);

  private AccountHolderEntityMapper accountHolderEntityMapper;
  private BankAccountEntityMapperImpl mapper;

  private BankAccountId bankAccountId;
  private AccountHolderId accountHolderId;

  @BeforeEach
  void setUp() {
    accountHolderEntityMapper = Mockito.mock(AccountHolderEntityMapper.class);
    mapper = new BankAccountEntityMapperImpl(accountHolderEntityMapper);

    bankAccountId = BankAccountId.of(BANK_ACCOUNT_UUID);
    accountHolderId = AccountHolderId.of(ACCOUNT_HOLDER_UUID);
  }

  //TODO
//  @ParameterizedTest
//  @ArgumentsSource(AccountTypeAndCurrencyArgumentsProvider.class)
//  void shouldMapToBankingEntities_whenBankAccountIsValid(AccountType accountType, Currency currency) {
//    AccountHolder primaryHolder = BankAccount.restoreAccountHolder(
//      accountHolderId,
//      AccountHolderFixtures.JEFFERSON.getAccountHolderName(),
//      AccountHolderFixtures.JEFFERSON.getPassportNumber(),
//      AccountHolderFixtures.JEFFERSON.getDateOfBirth(),
//      AccountHolderFixtures.JEFFERSON.getEmail(),
//      AccountHolderType.PRIMARY,
//      NOW
//    );
//
//    BankAccount bankAccount = BankAccount.restore(
//      id,
//      accountType,
//      currency,
//      VALID_IBAN,
//      AccountStatus.ACTIVE,
//      NOW,
//      List.of(primaryHolder)
//    );
//
//    BankingEntity mockedAccountHolderEntity = BankingEntity.builder()
//      .entityType(EntityType.ACCOUNT_HOLDER)
//      .accountHolderId(accountHolderId.value())
//      .build();
//
//    when(accountHolderEntityMapper.toAccountHolderEntity(id, primaryHolder))
//      .thenReturn(mockedAccountHolderEntity);
//
//    List<BankingEntity> entities = mapper.toBankingEntities(bankAccount);
//
//    assertThat(entities).hasSize(2);
//
//    BankingEntity bankAccountEntity = entities.getFirst();
//
//    assertThat(bankAccountEntity.getEntityType()).isEqualTo(EntityType.BANK_ACCOUNT);
//    assertThat(bankAccountEntity.getPartitionKey()).isEqualTo(BankAccountEntityKey.partitionKey(id));
//    assertThat(bankAccountEntity.getSortKey()).isEqualTo(BankAccountEntityKey.sortKey(id));
//    assertThat(bankAccountEntity.getBankAccountId()).isEqualTo(id.value());
//    assertThat(bankAccountEntity.getAccountType()).isEqualTo(accountType);
//    assertThat(bankAccountEntity.getCurrency()).isEqualTo(currency);
//    assertThat(bankAccountEntity.getIban()).isEqualTo(VALID_IBAN.value());
//    assertThat(bankAccountEntity.getStatus()).isEqualTo(AccountStatus.ACTIVE);
//    assertThat(bankAccountEntity.getCreatedAt()).isEqualTo(NOW);
//
//    verify(accountHolderEntityMapper).toAccountHolderEntity(id, primaryHolder);
//  }
//
//  @ParameterizedTest
//  @ArgumentsSource(AccountTypeAndCurrencyArgumentsProvider.class)
//  void shouldMapToDomain_whenEntityAndAccountHoldersAreValid(AccountType accountType, Currency currency) {
//    BankingEntity bankAccountEntity = BankingEntity.builder()
//      .id(id.value())
//      .accountType(accountType)
//      .currency(currency)
//      .iban(VALID_IBAN.value())
//      .status(AccountStatus.ACTIVE)
//      .createdAt(NOW)
//      .build();
//
//    BankingEntity accountHolderEntity = BankingEntity.builder()
//      .entityType(EntityType.ACCOUNT_HOLDER)
//      .accountHolderName(AccountHolderFixtures.JEFFERSON.getAccountHolderName().value())
//      .passportNumber(AccountHolderFixtures.JEFFERSON.getPassportNumber().value())
//      .dateOfBirth(AccountHolderFixtures.JEFFERSON.getDateOfBirth().value())
//      .email(AccountHolderFixtures.JEFFERSON.getEmail().value())
//      .type(AccountHolderType.PRIMARY)
//      .createdAt(NOW)
//      .build();
//
//    AccountHolder mockedAccountHolder = mock(AccountHolder.class);
//
//    when(accountHolderEntityMapper.toDomain(accountHolderEntity))
//      .thenReturn(mockedAccountHolder);
//
//    BankAccount result = mapper.toDomain(
//      bankAccountEntity,
//      List.of(accountHolderEntity)
//    );
//
//    assertThat(result.id()).isEqualTo(id);
//    assertThat(result.accountType()).isEqualTo(accountType);
//    assertThat(result.currency()).isEqualTo(currency);
//    assertThat(result.iban()).isEqualTo(VALID_IBAN);
//    assertThat(result.accountStatus()).isEqualTo(AccountStatus.ACTIVE);
//    assertThat(result.createdAt()).isEqualTo(NOW);
//    assertThat(result.holders()).containsExactly(mockedAccountHolder);
//
//    verify(accountHolderEntityMapper)
//      .toDomain(accountHolderEntity);
//  }
}