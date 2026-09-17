package com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.lookup;

import com.google.protobuf.Timestamp;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.AccountHolderSummary;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.AddressSummary;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.ContactInfoSummary;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.IdentityDocumentSummary;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.PersonalInfoSummary;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.generated.v1.AccountHolder;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.generated.v1.Address;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.generated.v1.BankAccount;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.generated.v1.ContactInfo;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.generated.v1.IdentityDocument;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.generated.v1.PersonalInfo;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;

@Component
public class BankAccountGrpcMapper {

  public BankAccount toResponse(BankAccountSummary summary) {
    var builder = BankAccount.newBuilder()
      .setId(summary.id().toString())
      .setAccountType(summary.accountType().name())
      .setCurrency(summary.currency().name())
      .setStatus(summary.accountStatus().name())
      .setCreatedAt(toTimestamp(summary.createdAt()))
      .addAllHolders(summary.holders().stream().map(this::toAccountHolder).toList());

    if (summary.iban() != null) {
      builder.setIban(summary.iban());
    }

    return builder.build();
  }

  private AccountHolder toAccountHolder(AccountHolderSummary summary) {
    return AccountHolder.newBuilder()
      .setId(summary.id().toString())
      .setType(summary.type().name())
      .setPersonalInfo(toPersonalInfo(summary.personalInfo()))
      .setContactInfo(toContactInfo(summary.contactInfo()))
      .setAddress(toAddress(summary.address()))
      .setCreatedAt(toTimestamp(summary.createdAt()))
      .build();
  }

  private PersonalInfo toPersonalInfo(PersonalInfoSummary summary) {
    return PersonalInfo.newBuilder()
      .setFirstName(summary.firstName())
      .setLastName(summary.lastName())
      .setIdentityDocument(toIdentityDocument(summary.identityDocument()))
      .setDateOfBirth(toDate(summary.dateOfBirth()))
      .build();
  }

  private IdentityDocument toIdentityDocument(IdentityDocumentSummary summary) {
    return IdentityDocument.newBuilder()
      .setCountry(summary.country())
      .setType(summary.type())
      .setNumber(summary.number())
      .build();
  }

  private ContactInfo toContactInfo(ContactInfoSummary summary) {
    return ContactInfo.newBuilder()
      .setEmail(summary.email())
      .setPhoneNumber(summary.phoneNumber())
      .build();
  }

  private Address toAddress(AddressSummary summary) {
    var builder = Address.newBuilder()
      .setStreet(summary.street())
      .setStreetNumber(summary.streetNumber())
      .setPostalCode(summary.postalCode())
      .setCity(summary.city())
      .setCountry(summary.country());

    if (summary.addressComplement() != null) {
      builder.setComplement(summary.addressComplement());
    }

    return builder.build();
  }

  private static Timestamp toTimestamp(Instant instant) {
    return Timestamp.newBuilder()
      .setSeconds(instant.getEpochSecond())
      .setNanos(instant.getNano())
      .build();
  }

  private static com.google.type.Date toDate(LocalDate date) {
    return com.google.type.Date.newBuilder()
      .setYear(date.getYear())
      .setMonth(date.getMonthValue())
      .setDay(date.getDayOfMonth())
      .build();
  }
}
