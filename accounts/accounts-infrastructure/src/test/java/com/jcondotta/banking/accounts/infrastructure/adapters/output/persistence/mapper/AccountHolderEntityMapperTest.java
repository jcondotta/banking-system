package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.mapper;

class AccountHolderEntityMapperTest {

//  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
//  private static final UUID ACCOUNT_HOLDER_UUID = UUID.randomUUID();
//
//  private static final AccountHolderName VALID_NAME = AccountHolderFixtures.JEFFERSON.getAccountHolderName();
//  private static final PassportNumber VALID_PASSPORT = AccountHolderFixtures.JEFFERSON.getPassportNumber();
//  private static final DateOfBirth VALID_DATE_OF_BIRTH = AccountHolderFixtures.JEFFERSON.getDateOfBirth();
//  private static final Email VALID_EMAIL = AccountHolderFixtures.JEFFERSON.getEmail();
//
//  private static final Instant NOW = Instant.now(ClockTestFactory.FIXED_CLOCK);
//
//  private AccountHolderEntityMapper mapper;
//
//  @BeforeEach
//  void setUp() {
//    mapper = Mappers.getMapper(AccountHolderEntityMapper.class);
//  }
//
//  @ParameterizedTest
//  @EnumSource(value = AccountHolderType.class)
//  void shouldMapToBankingEntity_whenAccountHolderIsValid(AccountHolderType type) {
//    BankAccountId id = BankAccountId.of(BANK_ACCOUNT_UUID);
//    AccountHolderId accountHolderId = AccountHolderId.of(ACCOUNT_HOLDER_UUID);
//
//    AccountHolder primaryHolder = BankAccount.restoreAccountHolder(
//      accountHolderId,
//      VALID_NAME,
//      VALID_PASSPORT,
//      VALID_DATE_OF_BIRTH,
//      VALID_EMAIL,
//      type,
//      NOW
//    );
//
//    BankingEntity entity = mapper.toAccountHolderEntity(id, primaryHolder);
//
//    assertThat(entity).isNotNull();
//    assertThat(entity.getPartitionKey()).isEqualTo(AccountHolderEntityKey.partitionKey(id));
//    assertThat(entity.getSortKey()).isEqualTo(AccountHolderEntityKey.sortKey(accountHolderId));
//
//    assertThat(entity.getEntityType()).isEqualTo(EntityType.ACCOUNT_HOLDER);
//    assertThat(entity.getBankAccountId()).isEqualTo(id.value());
//    assertThat(entity.getAccountHolderId()).isEqualTo(accountHolderId.value());
//    assertThat(entity.getAccountHolderName()).isEqualTo(VALID_NAME.value());
//    assertThat(entity.getPassportNumber()).isEqualTo(VALID_PASSPORT.value());
//    assertThat(entity.getDateOfBirth()).isEqualTo(VALID_DATE_OF_BIRTH.value());
//    assertThat(entity.getEmail()).isEqualTo(VALID_EMAIL.value());
//    assertThat(entity.getAccountHolderType()).isEqualTo(type);
//    assertThat(entity.getCreatedAt()).isEqualTo(NOW);
//  }
//
//  @ParameterizedTest
//  @EnumSource(value = AccountHolderType.class)
//  void shouldMapToDomain_whenBankingEntityIsValid(AccountHolderType type) {
//    BankAccountId id = BankAccountId.of(BANK_ACCOUNT_UUID);
//    AccountHolderId accountHolderId = AccountHolderId.of(ACCOUNT_HOLDER_UUID);
//
//    BankingEntity entity = BankingEntity.builder()
//      .partitionKey(AccountHolderEntityKey.partitionKey(id))
//      .sortKey(AccountHolderEntityKey.sortKey(accountHolderId))
//      .entityType(EntityType.ACCOUNT_HOLDER)
//      .id(id.value())
//      .accountHolderId(accountHolderId.value())
//      .accountHolderName(VALID_NAME.value())
//      .passportNumber(VALID_PASSPORT.value())
//      .dateOfBirth(VALID_DATE_OF_BIRTH.value())
//      .email(VALID_EMAIL.value())
//      .type(type)
//      .createdAt(NOW)
//      .build();
//
//    AccountHolder domain = mapper.toDomain(entity);
//
//    assertThat(domain).isNotNull();
//    assertThat(domain.id()).isEqualTo(accountHolderId);
//    assertThat(domain.holderName()).isEqualTo(VALID_NAME);
//    assertThat(domain.identityDocument()).isEqualTo(VALID_PASSPORT);
//    assertThat(domain.dateOfBirth()).isEqualTo(VALID_DATE_OF_BIRTH);
//    assertThat(domain.email()).isEqualTo(VALID_EMAIL);
//    assertThat(domain.type()).isEqualTo(type);
//    assertThat(domain.createdAt()).isEqualTo(NOW);
//  }
//
//  @ParameterizedTest
//  @EnumSource(value = AccountHolderType.class)
//  void shouldPreserveData_whenMappingToEntityAndBackToDomain(AccountHolderType type) {
//    BankAccountId id = BankAccountId.of(BANK_ACCOUNT_UUID);
//    AccountHolderId accountHolderId = AccountHolderId.of(ACCOUNT_HOLDER_UUID);
//
//    AccountHolder original = BankAccount.restoreAccountHolder(
//      accountHolderId,
//      VALID_NAME,
//      VALID_PASSPORT,
//      VALID_DATE_OF_BIRTH,
//      VALID_EMAIL,
//      type,
//      NOW
//    );
//
//    BankingEntity entity = mapper.toAccountHolderEntity(id, original);
//    AccountHolder restored = mapper.toDomain(entity);
//
//    assertThat(restored)
//      .usingRecursiveComparison()
//      .isEqualTo(original);
//  }
}