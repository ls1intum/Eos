package de.tum.cit.ase.eos;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.hamcrest.TypeSafeMatcher;

import de.tum.in.test.api.*;
import de.tum.in.test.api.jupiter.Public;

/**
 * Ares annotations for TestFX tests.
 * <p>
 * This class provides the minimum set of annotations required for TestFX tests to be executed in a Docker container with Ares.
 */
// Whitelisted paths for build
@WhitelistPath("build/classes/java/main")
@WhitelistPath("build/resources/main")

// Whitelisted paths for screenshots
@WhitelistPath(value = "screenshots", level = PathActionLevel.WRITE)
@WhitelistPath(value = "screenshots", level = PathActionLevel.DELETE)
@WhitelistPath(value = "screenshots", level = PathActionLevel.READ)

// Whitelisted paths for Linux and when running in Docker
@WhitelistPath(value = "/root/\\.gradle(/.*)?", type = PathType.REGEX_ABSOLUTE)
@WhitelistPath(value = "/root/\\.openjfx(/.*)?", type = PathType.REGEX_ABSOLUTE, level = PathActionLevel.READ)
@WhitelistPath(value = "/root/\\.openjfx(/.*)?", type = PathType.REGEX_ABSOLUTE, level = PathActionLevel.EXECUTE)
@WhitelistPath("/usr/share/fonts")
@WhitelistPath("/opt/java/openjdk/lib")
@WhitelistPath("/usr/java/packages/lib")

// Whitelisted packages
@AddTrustedPackages({ @AddTrustedPackage("com.sun.prism.**"), @AddTrustedPackage("com.sun.glass.ui.**"), @AddTrustedPackage("com.sun.javafx.**"), @AddTrustedPackage("javafx.**"),
        @AddTrustedPackage("java.awt.**"), @AddTrustedPackage("sun.awt.**"), @AddTrustedPackage("org.testfx.**"), })
@WhitelistClass(TypeSafeMatcher.class)

// Allow all threads and trust all threads
@TrustedThreads(value = TrustedThreads.TrustScope.ALL_THREADS)
@AllowThreads
// TODO: @DisableThreadGroupCheck

// Whitelisted test classes
@WhitelistClass(JavaFXTest.class)
@WhitelistClass(HelperClass.class)

// General test annotations
@StrictTimeout(10)
@MirrorOutput
@Public
@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE })
public @interface TestFXAnnotations {
}
