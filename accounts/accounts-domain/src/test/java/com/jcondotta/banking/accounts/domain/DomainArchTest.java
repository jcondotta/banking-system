package com.jcondotta.banking.accounts.domain;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

class DomainArchTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
          .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
          .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
          .importPackages("com.jcondotta.banking.accounts.domain");
    }

    @Test
    @DisplayName("value objects must be immutable and enforce value semantics")
    void value_objects_should_be_valid() {
        classes()
          .that().resideInAPackage("..domain..value_objects..")
          .should(beImmutableValueObject())
          .because("Value Objects must be immutable and enforce value semantics")
          .allowEmptyShould(false)
          .check(importedClasses);
    }

    private ArchCondition<JavaClass> beImmutableValueObject() {
        return new ArchCondition<>("be a immutable Value Object") {
            @Override
            public void check(JavaClass clazz, ConditionEvents events) {
                boolean isRecord = clazz.isRecord();

                boolean hasSetter = clazz.getMethods().stream()
                  .anyMatch(m -> m.getName().startsWith("set")
                    && m.getRawParameterTypes().size() == 1
                    && m.getRawReturnType().getName().equals("void"));

                if (hasSetter) {
                    events.add(SimpleConditionEvent.violated(clazz, clazz.getName() + " has setter methods"));
                }

                if (!isRecord) {
                    boolean isFinalClass = clazz.getModifiers().contains(JavaModifier.FINAL);

                    boolean allFieldsFinal = clazz.getFields().stream()
                      .filter(f -> !f.getModifiers().contains(JavaModifier.STATIC))
                      .allMatch(f -> f.getModifiers().contains(JavaModifier.FINAL));

                    boolean hasEquals = clazz.getMethods().stream()
                      .anyMatch(m -> m.getName().equals("equals")
                        && m.getRawParameterTypes().size() == 1);

                    boolean hasHashCode = clazz.getMethods().stream()
                      .anyMatch(m -> m.getName().equals("hashCode")
                        && m.getRawParameterTypes().isEmpty());

                    if (!isFinalClass)
                        events.add(SimpleConditionEvent.violated(
                          clazz, clazz.getName() + " should be final"));

                    if (!allFieldsFinal)
                        events.add(SimpleConditionEvent.violated(
                          clazz, clazz.getName() + " has non-final instance fields"));

                    if (!hasEquals || !hasHashCode)
                        events.add(SimpleConditionEvent.violated(
                          clazz, clazz.getName() + " must override equals() and hashCode()"));
                }
            }
        };
    }
}