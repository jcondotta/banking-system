package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.mapper;

import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.AccountHolder;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.HolderType;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.recipients.domain.testsupport.AccountHolderTestFactory;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address.Address;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.AccountHolderEntityKey;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.BankingEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.enums.EntityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountHolderEntityMapperTest {

    private static final UUID BANK_ACCOUNT_UUID = UUID.fromString("a1000000-0000-0000-0000-000000000001");
    private static final Instant CREATED_AT = Instant.parse("2022-06-24T12:45:01Z");

    private AccountHolderEntityMapper mapper;
    private BankAccountId bankAccountId;

    @BeforeEach
    void setUp() {
        mapper = new AccountHolderEntityMapper() {};
        bankAccountId = BankAccountId.of(BANK_ACCOUNT_UUID);
    }

    @Nested
    class DomainToEntity {

        @ParameterizedTest
        @EnumSource(HolderType.class)
        void shouldMapToEntity_whenAccountHolderHasAllFields(HolderType holderType) {
            var fixture = AccountHolderFixtures.JEFFERSON;
            AccountHolder holder = AccountHolderTestFactory.build(fixture, holderType, CREATED_AT);

            BankingEntity entity = mapper.toAccountHolderEntity(bankAccountId, holder);

            assertThat(entity.getEntityType()).isEqualTo(EntityType.ACCOUNT_HOLDER);
            assertThat(entity.getPartitionKey()).isEqualTo(AccountHolderEntityKey.partitionKey(bankAccountId));
            assertThat(entity.getSortKey()).isEqualTo(AccountHolderEntityKey.sortKey(holder.getId()));
            assertThat(entity.getBankAccountId()).isEqualTo(BANK_ACCOUNT_UUID);
            assertThat(entity.getAccountHolderId()).isEqualTo(holder.getId().value());
            assertThat(entity.getHolderType()).isEqualTo(holderType.name());
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);

            assertPersonalInfoEntity(entity, fixture);
            assertContactInfoEntity(entity, fixture);
            assertAddressEntity(entity, fixture);
        }

        @Test
        void shouldMapToEntity_whenAddressComplementIsNull() {
            var fixture = AccountHolderFixtures.PATRIZIO;
            AccountHolder holder = AccountHolderTestFactory.build(fixture, HolderType.PRIMARY, CREATED_AT);

            BankingEntity entity = mapper.toAccountHolderEntity(bankAccountId, holder);

            assertThat(entity.getAddressComplement()).isNull();
            assertThat(entity.getStreet()).isEqualTo(fixture.address().street().value());
            assertThat(entity.getStreetNumber()).isEqualTo(fixture.address().streetNumber().value());
            assertThat(entity.getPostalCode()).isEqualTo(fixture.address().postalCode().value());
            assertThat(entity.getCity()).isEqualTo(fixture.address().city().value());
        }
    }

    @Nested
    class EntityToDomain {

        @ParameterizedTest
        @EnumSource(HolderType.class)
        void shouldMapToDomain_whenEntityHasAllFields(HolderType holderType) {
            var fixture = AccountHolderFixtures.JEFFERSON;
            AccountHolder original = AccountHolderTestFactory.build(fixture, holderType, CREATED_AT);
            BankingEntity entity = mapper.toAccountHolderEntity(bankAccountId, original);

            AccountHolder domain = mapper.toDomain(entity);

            assertThat(domain.getId()).isEqualTo(original.getId());
            assertThat(domain.getAccountHolderType()).isEqualTo(holderType);
            assertThat(domain.getCreatedAt()).isEqualTo(CREATED_AT);

            assertPersonalInfoDomain(domain, fixture);
            assertContactInfoDomain(domain, fixture);
            assertAddressDomain(domain.getAddress(), fixture);
        }

        @Test
        void shouldMapToDomain_whenAddressComplementIsNull() {
            var fixture = AccountHolderFixtures.PATRIZIO;
            AccountHolder original = AccountHolderTestFactory.build(fixture, HolderType.PRIMARY, CREATED_AT);
            BankingEntity entity = mapper.toAccountHolderEntity(bankAccountId, original);

            AccountHolder domain = mapper.toDomain(entity);

            assertThat(domain.getAddress().addressComplement()).isNull();
            assertThat(domain.getAddress().street().value()).isEqualTo(fixture.address().street().value());
            assertThat(domain.getAddress().streetNumber().value()).isEqualTo(fixture.address().streetNumber().value());
            assertThat(domain.getAddress().postalCode().value()).isEqualTo(fixture.address().postalCode().value());
            assertThat(domain.getAddress().city().value()).isEqualTo(fixture.address().city().value());
        }
    }

    @Nested
    class RoundTrip {

        @ParameterizedTest
        @EnumSource(HolderType.class)
        void shouldPreserveAllFields_whenMappingToEntityAndRestoringToDomain(HolderType holderType) {
            var fixture = AccountHolderFixtures.JEFFERSON;
            AccountHolder original = AccountHolderTestFactory.build(fixture, holderType, CREATED_AT);

            BankingEntity entity = mapper.toAccountHolderEntity(bankAccountId, original);
            AccountHolder restored = mapper.toDomain(entity);

            assertThat(restored.getId()).isEqualTo(original.getId());
            assertThat(restored.getAccountHolderType()).isEqualTo(original.getAccountHolderType());
            assertThat(restored.getCreatedAt()).isEqualTo(original.getCreatedAt());
            assertThat(restored.getPersonalInfo()).isEqualTo(original.getPersonalInfo());
            assertThat(restored.getContactInfo()).isEqualTo(original.getContactInfo());
            assertThat(restored.getAddress()).isEqualTo(original.getAddress());
        }
    }

    private void assertPersonalInfoEntity(BankingEntity entity, AccountHolderFixtures fixture) {
        var personalInfo = fixture.personalInfo();
        assertThat(entity.getHolderFirstName()).isEqualTo(personalInfo.holderName().firstName());
        assertThat(entity.getHolderLastName()).isEqualTo(personalInfo.holderName().lastName());
        assertThat(entity.getDocumentType()).isEqualTo(personalInfo.identityDocument().type().name());
        assertThat(entity.getDocumentCountry()).isEqualTo(personalInfo.identityDocument().country().name());
        assertThat(entity.getDocumentNumber()).isEqualTo(personalInfo.identityDocument().number().value());
        assertThat(entity.getDateOfBirth()).isEqualTo(personalInfo.dateOfBirth().value());
    }

    private void assertContactInfoEntity(BankingEntity entity, AccountHolderFixtures fixture) {
        var contactInfo = fixture.contactInfo();
        assertThat(entity.getEmail()).isEqualTo(contactInfo.email().value());
        assertThat(entity.getPhoneNumber()).isEqualTo(contactInfo.phoneNumber().value());
    }

    private void assertAddressEntity(BankingEntity entity, AccountHolderFixtures fixture) {
        var address = fixture.address();
        assertThat(entity.getStreet()).isEqualTo(address.street().value());
        assertThat(entity.getStreetNumber()).isEqualTo(address.streetNumber().value());
        assertThat(entity.getAddressComplement()).isEqualTo(address.addressComplement().value());
        assertThat(entity.getPostalCode()).isEqualTo(address.postalCode().value());
        assertThat(entity.getCity()).isEqualTo(address.city().value());
    }

    private void assertPersonalInfoDomain(AccountHolder domain, AccountHolderFixtures fixture) {
        assertThat(domain.getPersonalInfo()).isEqualTo(fixture.personalInfo());
    }

    private void assertContactInfoDomain(AccountHolder domain, AccountHolderFixtures fixture) {
        assertThat(domain.getContactInfo()).isEqualTo(fixture.contactInfo());
    }

    private void assertAddressDomain(Address address, AccountHolderFixtures fixture) {
        assertThat(address).isEqualTo(fixture.address());
    }
}