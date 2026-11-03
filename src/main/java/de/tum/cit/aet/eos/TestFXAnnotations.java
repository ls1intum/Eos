package de.tum.cit.aet.eos;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import de.tum.cit.ase.ares.api.MirrorOutput;
import de.tum.cit.ase.ares.api.StrictTimeout;
import de.tum.cit.ase.ares.api.jupiter.Public;

/**
 * Ares annotations for TestFX tests.
 * <p>
 * This class provides the minimum set of annotations required for TestFX tests to be executed with Ares 2.
 * <p>
 * In contrast to Ares 1, Ares 2 is configured by a security policy instead of annotations. The security policy is
 * specific to the exercise, therefore the concrete test class has to reference it with the
 * {@link de.tum.cit.ase.ares.api.Policy} annotation, e.g.
 * {@code @Policy(value = "test/SecurityPolicy.yaml", withinPath = "classes/java/main/org/example")}. The policy has
 * to list the concrete test class in {@code theFollowingClassesAreTestClasses} and permit the import of the
 * {@code javafx} package in {@code regardingPackageImports}.
 */
@StrictTimeout(10)
@MirrorOutput
@Public
@Inherited
@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE })
public @interface TestFXAnnotations {
}
