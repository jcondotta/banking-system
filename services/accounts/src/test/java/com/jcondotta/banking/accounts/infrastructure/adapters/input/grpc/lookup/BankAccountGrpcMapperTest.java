package com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.lookup;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.AccountHolderSummary;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.AddressSummary;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.ContactInfoSummary;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.IdentityDocumentSummary;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.PersonalInfoSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.HolderType;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

class BankAccountGrpcMapperTest {

  private static final UUID BANK_ACCOUNT_ID = UUID.fromString("01920bff-1338-7efd-ade6-e9128debe5d4");
  private static final UUID HOLDER_ID = UUID.fromString("c6a4a1b2-0f8c-41e3-a622-98d66de824a9");
  private static final String IBAN = "ES3801283316232166447417";
  private static final Instant CREATED_AT = Instant.parse("2022-06-24T12:45:01.123456789Z");
  private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1988, 6, 24);

  private final BankAccountGrpcMapper mapper = new BankAccountGrpcMapper();

  @Test
  void shouldMapCompleteBankAccountSummary_whenAllValuesArePresent() {
    var response = mapper.toResponse(summary(IBAN, "3º - 1ª"));

    var softly = new SoftAssertions();
    softly.assertThat(response.getId()).isEqualTo(BANK_ACCOUNT_ID.toString());
    softly.assertThat(response.getAccountType()).isEqualTo("CHECKING");
    softly.assertThat(response.getCurrency()).isEqualTo("EUR");
    softly.assertThat(response.hasIban()).isTrue();
    softly.assertThat(response.getIban()).isEqualTo(IBAN);
    softly.assertThat(response.getStatus()).isEqualTo("ACTIVE");
    softly.assertThat(response.getCreatedAt().getSeconds()).isEqualTo(CREATED_AT.getEpochSecond());
    softly.assertThat(response.getCreatedAt().getNanos()).isEqualTo(CREATED_AT.getNano());
    softly.assertThat(response.getHoldersCount()).isOne();

    var holder = response.getHolders(0);
    softly.assertThat(holder.getId()).isEqualTo(HOLDER_ID.toString());
    softly.assertThat(holder.getType()).isEqualTo("PRIMARY");
    softly.assertThat(holder.getPersonalInfo().getFirstName()).isEqualTo("Jefferson");
    softly.assertThat(holder.getPersonalInfo().getLastName()).isEqualTo("Condotta");
    softly.assertThat(holder.getPersonalInfo().getIdentityDocument().getCountry()).isEqualTo("SPAIN");
    softly.assertThat(holder.getPersonalInfo().getIdentityDocument().getType()).isEqualTo("FOREIGNER_ID");
    softly.assertThat(holder.getPersonalInfo().getIdentityDocument().getNumber()).isEqualTo("X7566995H");
    softly.assertThat(holder.getPersonalInfo().getDateOfBirth().getYear()).isEqualTo(DATE_OF_BIRTH.getYear());
    softly.assertThat(holder.getPersonalInfo().getDateOfBirth().getMonth()).isEqualTo(DATE_OF_BIRTH.getMonthValue());
    softly.assertThat(holder.getPersonalInfo().getDateOfBirth().getDay()).isEqualTo(DATE_OF_BIRTH.getDayOfMonth());
    softly.assertThat(holder.getContactInfo().getEmail()).isEqualTo("jefferson.condotta@email.com");
    softly.assertThat(holder.getContactInfo().getPhoneNumber()).isEqualTo("+49123456789");
    softly.assertThat(holder.getAddress().getStreet()).isEqualTo("Carrer de Mallorca");
    softly.assertThat(holder.getAddress().getStreetNumber()).isEqualTo("01");
    softly.assertThat(holder.getAddress().hasComplement()).isTrue();
    softly.assertThat(holder.getAddress().getComplement()).isEqualTo("3º - 1ª");
    softly.assertThat(holder.getAddress().getPostalCode()).isEqualTo("08013");
    softly.assertThat(holder.getAddress().getCity()).isEqualTo("Barcelona");
    softly.assertThat(holder.getAddress().getCountry()).isEqualTo("ES");
    softly.assertAll();
  }

  @Test
  void shouldLeaveOptionalFieldsAbsent_whenIbanAndAddressComplementAreMissing() {
    var response = mapper.toResponse(summary(null, null));

    var softly = new SoftAssertions();
    softly.assertThat(response.hasIban()).isFalse();
    softly.assertThat(response.getStatus()).isEqualTo("PENDING");
    softly.assertThat(response.getHolders(0).getAddress().hasComplement()).isFalse();
    softly.assertAll();
  }

  private static BankAccountSummary summary(String iban, String addressComplement) {
    var identityDocument = new IdentityDocumentSummary("SPAIN", "FOREIGNER_ID", "X7566995H");
    var personalInfo = new PersonalInfoSummary("Jefferson", "Condotta", identityDocument, DATE_OF_BIRTH);
    var contactInfo = new ContactInfoSummary("jefferson.condotta@email.com", "+49123456789");
    var address = new AddressSummary("Carrer de Mallorca", "01", addressComplement, "08013", "Barcelona", "ES");
    var holder = new AccountHolderSummary(HOLDER_ID, personalInfo, contactInfo, address, HolderType.PRIMARY, CREATED_AT);

    return new BankAccountSummary(
      BANK_ACCOUNT_ID,
      AccountType.CHECKING,
      Currency.EUR,
      iban,
      iban == null ? AccountStatus.PENDING : AccountStatus.ACTIVE,
      CREATED_AT,
      List.of(holder)
    );
  }
}
