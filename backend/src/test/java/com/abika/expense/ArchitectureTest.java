package com.abika.expense;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.abika.expense", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    private static final String TRANSACTION_ANNOTATION_PACKAGE = "org.springframework.transaction.annotation";

    @ArchTest
    static final ArchRule domain_is_pure = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..", "jakarta..",
                    "..application..", "..adapter..", "..config..");

    // @Transactional (with its Propagation/Isolation members) is the one Spring piece allowed in the application layer.
    @ArchTest
    static final ArchRule application_depends_only_on_domain = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage("..adapter..", "..config..", "jakarta..")
            .orShould().dependOnClassesThat(
                    resideInAPackage("org.springframework..")
                            .and(not(resideInAPackage(TRANSACTION_ANNOTATION_PACKAGE))));

    @ArchTest
    static final ArchRule adapters_are_independent = slices()
            .matching("com.abika.expense.adapter.(*).(*)..")
            .should().notDependOnEachOther();
}
