package com.jcondotta.banking.transfers.ledger;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class LedgerArchitectureTest {

    private static final String TRANSFERS_ROOT = "com.jcondotta.banking.transfers";

    private static final String LEDGER_PACKAGE = TRANSFERS_ROOT + ".ledger..";
    private static final String LEDGER_DOMAIN_PACKAGE = TRANSFERS_ROOT + ".ledger.domain..";
    private static final String LEDGER_APPLICATION_PACKAGE = TRANSFERS_ROOT + ".ledger.application..";
    private static final String LEDGER_PERSISTENCE_PACKAGE =
        TRANSFERS_ROOT + ".ledger.infrastructure.adapters.output.persistence..";

    private static final String BANK_TRANSFER_DOMAIN_PACKAGE = TRANSFERS_ROOT + ".domain.bank_transfer..";
    private static final String BANK_TRANSFER_APPLICATION_PACKAGE = TRANSFERS_ROOT + ".application.bank_transfer..";
    private static final String BANK_TRANSFER_INFRASTRUCTURE_PACKAGE = TRANSFERS_ROOT + ".infrastructure..";

    private static JavaClasses transfersClasses;

    @BeforeAll
    static void setUp() {
        transfersClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(TRANSFERS_ROOT);
    }

    @Test
    void ledgerDomainAndApplicationShouldNotDependOnBankTransferPackages() {
        noClasses()
            .that().resideInAnyPackage(LEDGER_DOMAIN_PACKAGE, LEDGER_APPLICATION_PACKAGE)
            .should().dependOnClassesThat().resideInAnyPackage(
                BANK_TRANSFER_DOMAIN_PACKAGE,
                BANK_TRANSFER_APPLICATION_PACKAGE,
                BANK_TRANSFER_INFRASTRUCTURE_PACKAGE
            )
            .because("ledger domain and application must not depend on bank_transfer packages")
            .check(transfersClasses);
    }

    @Test
    void bankTransferDomainAndApplicationShouldNotAccessLedgerPersistence() {
        noClasses()
            .that().resideInAnyPackage(BANK_TRANSFER_DOMAIN_PACKAGE, BANK_TRANSFER_APPLICATION_PACKAGE)
            .should().dependOnClassesThat().resideInAPackage(LEDGER_PERSISTENCE_PACKAGE)
            .because("bank_transfer domain and application must not access ledger persistence adapters")
            .check(transfersClasses);
    }

    @Test
    void ledgerDomainShouldRemainFrameworkFree() {
        noClasses()
            .that().resideInAPackage(LEDGER_DOMAIN_PACKAGE)
            .should().dependOnClassesThat().resideInAnyPackage(
                "org.springframework..",
                "jakarta.persistence..",
                LEDGER_APPLICATION_PACKAGE,
                TRANSFERS_ROOT + ".ledger.infrastructure.."
            )
            .because("ledger domain must remain framework-free and independent")
            .check(transfersClasses);
    }
}
