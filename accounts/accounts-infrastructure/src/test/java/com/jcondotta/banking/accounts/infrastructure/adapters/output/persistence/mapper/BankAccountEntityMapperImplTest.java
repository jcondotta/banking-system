package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.mapper;

import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.AccountHolder;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.HolderType;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.bankaccount.testsupport.AccountHolderTestFactory;
import com.jcondotta.banking.accounts.domain.bankaccount.testsupport.BankAccountTestFactory;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.BankAccountEntityKey;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.BankingEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.enums.EntityType;
import com.jcondotta.banking.accounts.infrastructure.arguments_provider.AccountTypeAndCurrencyArgumentsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountEntityMapperImplTest {

    private static final AccountHolderFixtures PRIMARY_FIXTURE = AccountHolderFixtures.JEFFERSON;
    private static final AccountHolderFixtures JOINT_FIXTURE = AccountHolderFixtures.VIRGINIO;

    private BankAccountEntityMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new BankAccountEntityMapperImpl(new AccountHolderEntityMapperImpl());
    }

    @Nested
    class ToEntities {

        @ParameterizedTest
        @ArgumentsSource(AccountTypeAndCurrencyArgumentsProvider.class)
        void shouldReturnTwoEntities_whenBankAccountHasOnlyPrimaryHolder(AccountType accountType, Currency currency) {
            BankAccount bankAccount = BankAccountTestFactory.withPrimary(BankAccountId.newId(), accountType, currency, PRIMARY_FIXTURE);

            List<BankingEntity> entities = mapper.toEntities(bankAccount);

            assertThat(entities).hasSize(2);
            assertThat(entities.get(0).getEntityType()).isEqualTo(EntityType.BANK_ACCOUNT);
            assertThat(entities.get(1).getEntityType()).isEqualTo(EntityType.ACCOUNT_HOLDER);
        }

        @Test
        void shouldReturnThreeEntities_whenBankAccountHasPrimaryAndJointHolder() {
            BankAccount bankAccount = BankAccountTestFactory.withPrimaryAndJoint(PRIMARY_FIXTURE, JOINT_FIXTURE);

            List<BankingEntity> entities = mapper.toEntities(bankAccount);

            assertThat(entities).hasSize(3);
            assertThat(entities.get(0).getEntityType()).isEqualTo(EntityType.BANK_ACCOUNT);
            assertThat(entities.get(1).getEntityType()).isEqualTo(EntityType.ACCOUNT_HOLDER);
            assertThat(entities.get(2).getEntityType()).isEqualTo(EntityType.ACCOUNT_HOLDER);
        }

        @ParameterizedTest
        @ArgumentsSource(AccountTypeAndCurrencyArgumentsProvider.class)
        void shouldPlaceBankAccountEntityFirst_whenMappingToEntities(AccountType accountType, Currency currency) {
            BankAccount bankAccount = BankAccountTestFactory.withPrimary(BankAccountId.newId(), accountType, currency, PRIMARY_FIXTURE);

            List<BankingEntity> entities = mapper.toEntities(bankAccount);

            assertThat(entities.getFirst().isBankAccount()).isTrue();
        }

        @ParameterizedTest
        @ArgumentsSource(AccountTypeAndCurrencyArgumentsProvider.class)
        void shouldMapBankAccountEntityFields_whenMappingToEntities(AccountType accountType, Currency currency) {
            BankAccount bankAccount = BankAccountTestFactory.withPrimary(BankAccountId.newId(), accountType, currency, PRIMARY_FIXTURE);

            BankingEntity entity = mapper.toEntities(bankAccount).getFirst();

            assertThat(entity.getPartitionKey()).isEqualTo(BankAccountEntityKey.partitionKey(bankAccount.getId()));
            assertThat(entity.getSortKey()).isEqualTo(BankAccountEntityKey.sortKey(bankAccount.getId()));
            assertThat(entity.getEntityType()).isEqualTo(EntityType.BANK_ACCOUNT);
            assertThat(entity.getBankAccountId()).isEqualTo(bankAccount.getId().value());
            assertThat(entity.getAccountType()).isEqualTo(accountType.name());
            assertThat(entity.getCurrency()).isEqualTo(currency.name());
            assertThat(entity.getIban()).isEqualTo(bankAccount.getIban().value());
            assertThat(entity.getAccountStatus()).isEqualTo(bankAccount.getAccountStatus().name());
            assertThat(entity.getCreatedAt()).isEqualTo(bankAccount.getCreatedAt());
        }

        @ParameterizedTest
        @ArgumentsSource(AccountTypeAndCurrencyArgumentsProvider.class)
        void shouldMapAllAccountStatuses_whenMappingToEntities(AccountType accountType, Currency currency) {
            for (AccountStatus status : AccountStatus.values()) {
                BankAccount bankAccount = BankAccountTestFactory.build(BankAccountId.newId(), accountType, currency, status,
                  AccountHolderTestFactory.primary(PRIMARY_FIXTURE));

                BankingEntity entity = mapper.toEntities(bankAccount).getFirst();
                assertThat(entity.getAccountStatus()).isEqualTo(status.name());
            }
        }
    }

    @Nested
    class Restore {

        @ParameterizedTest
        @ArgumentsSource(AccountTypeAndCurrencyArgumentsProvider.class)
        void shouldRestoreBankAccount_whenMappingToEntitiesAndRestoringBack(AccountType accountType, Currency currency) {
            BankAccount original = BankAccountTestFactory.withPrimary(BankAccountId.newId(), accountType, currency, PRIMARY_FIXTURE);

            BankAccount restored = mapper.restore(mapper.toEntities(original));

            assertThat(restored.getId()).isEqualTo(original.getId());
            assertThat(restored.getAccountType()).isEqualTo(original.getAccountType());
            assertThat(restored.getCurrency()).isEqualTo(original.getCurrency());
            assertThat(restored.getIban()).isEqualTo(original.getIban());
            assertThat(restored.getAccountStatus()).isEqualTo(original.getAccountStatus());
            assertThat(restored.getCreatedAt()).isEqualTo(original.getCreatedAt());
            assertThat(restored.getActiveHolders()).hasSize(1);
        }

        @Test
        void shouldRestorePrimaryHolderFields_whenRestoringFromEntities() {
            BankAccount original = BankAccountTestFactory.withPrimary(PRIMARY_FIXTURE);

            BankAccount restored = mapper.restore(mapper.toEntities(original));

            AccountHolder primaryHolder = restored.getPrimaryHolder();
            assertThat(primaryHolder.getAccountHolderType()).isEqualTo(HolderType.PRIMARY);
            assertThat(primaryHolder.getPersonalInfo()).isEqualTo(PRIMARY_FIXTURE.personalInfo());
            assertThat(primaryHolder.getContactInfo()).isEqualTo(PRIMARY_FIXTURE.contactInfo());
            assertThat(primaryHolder.getAddress()).isEqualTo(PRIMARY_FIXTURE.address());
        }

        @Test
        void shouldRestorePrimaryAndJointHolders_whenEntitiesContainBothHolderTypes() {
            BankAccount original = BankAccountTestFactory.withPrimaryAndJoint(PRIMARY_FIXTURE, JOINT_FIXTURE);

            BankAccount restored = mapper.restore(mapper.toEntities(original));

            assertThat(restored.getActiveHolders()).hasSize(2);
            assertThat(restored.getPrimaryHolder().getPersonalInfo()).isEqualTo(PRIMARY_FIXTURE.personalInfo());
            assertThat(restored.getJointHolders()).hasSize(1);
            assertThat(restored.getJointHolders().getFirst().getPersonalInfo()).isEqualTo(JOINT_FIXTURE.personalInfo());
        }

        @Test
        void shouldThrowIllegalStateException_whenNoBankAccountEntityInList() {
            BankingEntity holderEntity = mapper.toEntities(BankAccountTestFactory.withPrimary(PRIMARY_FIXTURE)).get(1);

            assertThatThrownBy(() -> mapper.restore(List.of(holderEntity)))
              .isInstanceOf(IllegalStateException.class)
              .hasMessage("Bank account entity not found");
        }

        @Test
        void shouldThrowIllegalStateException_whenListIsEmpty() {
            assertThatThrownBy(() -> mapper.restore(List.of()))
              .isInstanceOf(IllegalStateException.class)
              .hasMessage("Bank account entity not found");
        }
    }

    @Nested
    class RoundTrip {

        @ParameterizedTest
        @ArgumentsSource(AccountTypeAndCurrencyArgumentsProvider.class)
        void shouldPreserveAllBankAccountFields_whenMappingToEntitiesAndRestoringToDomain(AccountType accountType, Currency currency) {
            BankAccount original = BankAccountTestFactory.withPrimaryAndJoint(
              BankAccountId.newId(),
              accountType,
              currency,
              PRIMARY_FIXTURE,
              JOINT_FIXTURE
            );

            BankAccount restored = mapper.restore(mapper.toEntities(original));

            assertThat(restored.getId()).isEqualTo(original.getId());
            assertThat(restored.getAccountType()).isEqualTo(original.getAccountType());
            assertThat(restored.getCurrency()).isEqualTo(original.getCurrency());
            assertThat(restored.getIban()).isEqualTo(original.getIban());
            assertThat(restored.getAccountStatus()).isEqualTo(original.getAccountStatus());
            assertThat(restored.getCreatedAt()).isEqualTo(original.getCreatedAt());
            assertThat(restored.getPrimaryHolder().getPersonalInfo()).isEqualTo(PRIMARY_FIXTURE.personalInfo());
            assertThat(restored.getJointHolders().getFirst().getPersonalInfo()).isEqualTo(JOINT_FIXTURE.personalInfo());
        }
    }
}