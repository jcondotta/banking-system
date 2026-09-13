package com.jcondotta.banking.ledger;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class LedgerArchitectureTest {

    private static final String LEDGER_ROOT = "com.jcondotta.banking.ledger";
    private static final String LEDGER_DOMAIN_PACKAGE = LEDGER_ROOT + ".domain..";
    private static final String LEDGER_APPLICATION_PACKAGE = LEDGER_ROOT + ".application..";
    private static final String LEDGER_INFRASTRUCTURE_PACKAGE = LEDGER_ROOT + ".infrastructure..";
    private static final String LEDGER_PERSISTENCE_PACKAGE = LEDGER_ROOT + ".infrastructure.adapters.output.persistence..";

    private static JavaClasses ledgerClasses;

    @BeforeAll
    static void setUp() {
        ledgerClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(LEDGER_ROOT);
    }

    @Test
    void ledgerDomainShouldRemainFrameworkFree() {
        noClasses()
            .that().resideInAPackage(LEDGER_DOMAIN_PACKAGE)
            .should().dependOnClassesThat().resideInAnyPackage(
                "org.springframework..",
                "jakarta.persistence..",
                LEDGER_APPLICATION_PACKAGE,
                LEDGER_INFRASTRUCTURE_PACKAGE
            )
            .because("ledger domain must remain framework-free and independent")
            .check(ledgerClasses);
    }

    @Test
    void ledgerApplicationShouldNotDependOnInfrastructure() {
        noClasses()
            .that().resideInAPackage(LEDGER_APPLICATION_PACKAGE)
            .should().dependOnClassesThat().resideInAPackage(LEDGER_INFRASTRUCTURE_PACKAGE)
            .because("ledger application must not depend on infrastructure adapters")
            .check(ledgerClasses);
    }

    @Test
    void ledgerApplicationShouldNotDependOnPersistence() {
        noClasses()
            .that().resideInAPackage(LEDGER_APPLICATION_PACKAGE)
            .should().dependOnClassesThat().resideInAPackage(LEDGER_PERSISTENCE_PACKAGE)
            .because("ledger application must not access persistence adapters directly")
            .check(ledgerClasses);
    }
}
