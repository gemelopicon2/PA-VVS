package es.udc.paproject.backend.test.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import com.tngtech.archunit.lang.syntax.elements.ClassesShould;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

public class ArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("es.udc.paproject.backend");
    }

    @Test
    @DisplayName("Estrutural: O modelo de dominio non debe depender do paquete REST")
    void modelShouldNotDependOnRestPackage() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..model..")
                .should().dependOnClassesThat().resideInAPackage("..rest..");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Estrutural: Os DAOs/Repositorios só poden ser accedidos pola capa de servizos ou entidades")
    void repositoriesShouldOnlyBeAccessedByServices() {
        ArchRule rule = classes()
                .that().resideInAPackage("..model.entities..")
                .and().haveSimpleNameEndingWith("Dao")
                .or().haveSimpleNameEndingWith("Repository")
                .should().onlyHaveDependentClassesThat()
                .resideInAnyPackage("..model.services..", "..model.entities..");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Nomenclatura: Todas as clases de excepción deben rematar co sufixo 'Exception'")
    void exceptionClassesShouldHaveExceptionSuffix() {
        ArchRule rule = classes()
                .that().areAssignableTo(Throwable.class)
                .should().haveSimpleNameEndingWith("Exception");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Boas Prácticas: Prohibida a inyección de dependencias por campo (@Autowired nos atributos)")
    void noFieldInjectionAllowed() {
        ArchRule rule = noFields()
                .should().beAnnotatedWith(Autowired.class)
                .because("Debe usarse inxección por constructor para favorecer a testabilidade");

        rule.check(importedClasses);
    }
}