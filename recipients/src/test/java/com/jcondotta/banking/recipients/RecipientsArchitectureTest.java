package com.jcondotta.banking.recipients;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class RecipientsArchitectureTest {

  private static final String ROOT_PACKAGE = "com.jcondotta.banking.recipients";
  private static final String DOMAIN_PACKAGE = ROOT_PACKAGE + ".domain..";
  private static final String APPLICATION_PACKAGE = ROOT_PACKAGE + ".application..";
  private static final String INFRASTRUCTURE_PACKAGE = ROOT_PACKAGE + ".infrastructure..";
  private static final String INPUT_ADAPTER_PACKAGE = ROOT_PACKAGE + ".infrastructure.adapters.input..";
  private static final String OUTPUT_ADAPTER_PACKAGE = ROOT_PACKAGE + ".infrastructure.adapters.output..";
  private static final String REST_ADAPTER_PACKAGE = ROOT_PACKAGE + ".infrastructure.adapters.input.rest..";
  private static final String PERSISTENCE_ENTITY_PACKAGE =
    ROOT_PACKAGE + ".infrastructure.adapters.output.persistence.entity..";

  private static JavaClasses recipientsClasses;

  @BeforeAll
  static void setUp() {
    recipientsClasses = new ClassFileImporter()
      .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
      .importPackages(ROOT_PACKAGE);
  }

  @Test
  void domainShouldRemainFrameworkFreeAndIndependent() {
    noClasses()
      .that().resideInAPackage(DOMAIN_PACKAGE)
      .should().dependOnClassesThat().resideInAnyPackage(
        APPLICATION_PACKAGE,
        INFRASTRUCTURE_PACKAGE,
        "org.springframework..",
        "jakarta.persistence..",
        "org.slf4j..",
        "io.micrometer.."
      )
      .because("the domain must remain framework-free and independent")
      .check(recipientsClasses);
  }

  @Test
  void applicationShouldNotDependOnInfrastructure() {
    noClasses()
      .that().resideInAPackage(APPLICATION_PACKAGE)
      .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE_PACKAGE)
      .because("application use cases must depend on ports rather than adapters")
      .check(recipientsClasses);
  }

  @Test
  void inputAndOutputAdaptersShouldNotDependOnEachOther() {
    noClasses()
      .that().resideInAPackage(INPUT_ADAPTER_PACKAGE)
      .should().dependOnClassesThat().resideInAPackage(OUTPUT_ADAPTER_PACKAGE)
      .because("input adapters must invoke use cases rather than output adapters")
      .check(recipientsClasses);

    noClasses()
      .that().resideInAPackage(OUTPUT_ADAPTER_PACKAGE)
      .should().dependOnClassesThat().resideInAPackage(INPUT_ADAPTER_PACKAGE)
      .because("output adapters must not depend on delivery mechanisms")
      .check(recipientsClasses);
  }

  @Test
  void jpaAndRestTypesShouldRemainInsideInfrastructure() {
    noClasses()
      .that().resideOutsideOfPackage(INFRASTRUCTURE_PACKAGE)
      .should().dependOnClassesThat().resideInAnyPackage(
        "jakarta.persistence..",
        "org.springframework.data..",
        REST_ADAPTER_PACKAGE,
        PERSISTENCE_ENTITY_PACKAGE
      )
      .because("JPA entities, Spring Data, and REST contracts belong to infrastructure")
      .check(recipientsClasses);
  }

  @Test
  void architecturalLayersShouldBeFreeOfCycles() {
    slices()
      .matching(ROOT_PACKAGE + ".(*)..")
      .should().beFreeOfCycles()
      .check(recipientsClasses);
  }
}
