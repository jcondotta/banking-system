package com.jcondotta.banking.accounts.domain.bankaccount.aggregate;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountJointHolderAddedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotActiveException;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.MaxJointHoldersExceededException;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.BankAccountTestFixture;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountAddJointAccountHolderTest {

  private static final AccountHolderFixtures PRIMARY_ACCOUNT_HOLDER = AccountHolderFixtures.JEFFERSON;
  private static final AccountHolderFixtures JOINT_ACCOUNT_HOLDER_1 = AccountHolderFixtures.PATRIZIO;
  private static final AccountHolderFixtures JOINT_ACCOUNT_HOLDER_2 = AccountHolderFixtures.VIRGINIO;

  @Test
  void shouldAddJointAccountHolder_whenAccountIsActive() {
    var bankAccount = BankAccountTestFixture.openActiveAccount(PRIMARY_ACCOUNT_HOLDER);

    bankAccount.addJointAccountHolder(
      JOINT_ACCOUNT_HOLDER_1.personalInfo(),
      JOINT_ACCOUNT_HOLDER_1.contactInfo(),
      JOINT_ACCOUNT_HOLDER_1.address()
    );

    assertThat(bankAccount.getActiveHolders())
      .hasSize(2)
      .filteredOn(AccountHolder::isJoint)
      .hasSize(1);

    var jointHolder = bankAccount.getJointHolders().getFirst();

    assertThat(jointHolder.getPersonalInfo()).isEqualTo(JOINT_ACCOUNT_HOLDER_1.personalInfo());
    assertThat(jointHolder.getContactInfo()).isEqualTo(JOINT_ACCOUNT_HOLDER_1.contactInfo());
    assertThat(jointHolder.getAddress()).isEqualTo(JOINT_ACCOUNT_HOLDER_1.address());
    assertThat(jointHolder.getCreatedAt()).isNotNull();

    var events = bankAccount.pullEvents();

    assertThat(events)
      .hasSize(1)
      .singleElement()
      .isInstanceOfSatisfying(BankAccountJointHolderAddedEvent.class, event -> {
        assertThat(event.aggregateId()).isEqualTo(bankAccount.getId());
        assertThat(event.accountHolderId()).isEqualTo(jointHolder.getId());
        assertThat(event.occurredAt()).isNotNull();
      });

    assertThat(bankAccount.pullEvents()).isEmpty();
  }

  @Test
  void shouldThrowBankAccountNotActiveException_whenAccountIsNotActive() {
    var bankAccount = BankAccountTestFixture.openPendingAccount(PRIMARY_ACCOUNT_HOLDER);

    assertThatThrownBy(() ->
      bankAccount.addJointAccountHolder(
        JOINT_ACCOUNT_HOLDER_1.personalInfo(),
        JOINT_ACCOUNT_HOLDER_1.contactInfo(),
        JOINT_ACCOUNT_HOLDER_1.address()
      ))
      .isInstanceOf(BankAccountNotActiveException.class);
  }

  @Test
  void shouldThrowMaxJointAccountHoldersExceededException_whenJointLimitIsReached() {
    var bankAccount = BankAccountTestFixture.openActiveAccount(PRIMARY_ACCOUNT_HOLDER);

    bankAccount.addJointAccountHolder(
      JOINT_ACCOUNT_HOLDER_1.personalInfo(),
      JOINT_ACCOUNT_HOLDER_1.contactInfo(),
      JOINT_ACCOUNT_HOLDER_1.address()
    );

    assertThatThrownBy(() ->
      bankAccount.addJointAccountHolder(
        JOINT_ACCOUNT_HOLDER_2.personalInfo(),
        JOINT_ACCOUNT_HOLDER_2.contactInfo(),
        JOINT_ACCOUNT_HOLDER_2.address()
      ))
      .isInstanceOf(MaxJointHoldersExceededException.class);
  }
}
