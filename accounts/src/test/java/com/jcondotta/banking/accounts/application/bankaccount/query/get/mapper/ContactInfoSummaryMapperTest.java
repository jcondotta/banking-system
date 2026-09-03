package com.jcondotta.banking.accounts.application.bankaccount.query.get.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.ContactInfoSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.ContactInfo;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.Email;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.PhoneNumber;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContactInfoSummaryMapperTest {

  private static final Email EMAIL = Email.of("jefferson.condotta@email.com");
  private static final PhoneNumber PHONE = PhoneNumber.of("+34600111222");

  private final ContactInfoSummaryMapper mapper = new ContactInfoSummaryMapperImpl();

  @Test
  void shouldMapContactInfoDetails_whenEmailAndPhoneArePresent() {
    var contactInfo = ContactInfo.of(EMAIL, PHONE);

    ContactInfoSummary details = mapper.toSummary(contactInfo);

    assertThat(details.email()).isEqualTo(EMAIL.value());
    assertThat(details.phoneNumber()).isEqualTo(PHONE.value());
  }

  @Test
  void shouldReturnNull_whenContactInfoIsNull() {
    ContactInfoSummary details = mapper.toSummary(null);

    assertThat(details).isNull();
  }

  @Test
  void shouldReturnNull_whenContactInfoValueObjectsAreNull() {
    assertThat(mapper.map((Email) null)).isNull();
    assertThat(mapper.map((PhoneNumber) null)).isNull();
  }
}
