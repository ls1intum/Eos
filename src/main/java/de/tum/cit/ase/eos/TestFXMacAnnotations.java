package de.tum.cit.ase.eos;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import de.tum.in.test.api.*;

/**
 * Ares annotations for TestFX tests when running on macOS.
 * <p>
 * When running on macOS, additional annotations are required in order to execute the tests successfully.
 * Depending on the macOS version, the paths may vary. It might be necessary to add additional paths in the concrete test class
 * using the {@link WhitelistPath} annotation of Ares.
 * <p>
 * If you want a reproducible test execution, you should run the tests in Docker with the {@link TestFXAnnotations} annotation.
 */
// Whitelisted paths for macOS
@WhitelistPath(value = "/Users/[^/]+/\\.gradle(/.*)?", type = PathType.REGEX_ABSOLUTE)
@WhitelistPath(value = "/Users/[^/]+/\\.openjfx(/.*)?", type = PathType.REGEX_ABSOLUTE)
@WhitelistPath("/System/Library/Fonts")
@WhitelistPath(value = "/Users/[^/]+/Library/Java/Extensions/libglass.dylib", type = PathType.REGEX_ABSOLUTE)
@WhitelistPath(value = "/Users/[^/]+/Library/Java/JavaVirtualMachines(/.*)?", type = PathType.REGEX_ABSOLUTE)

@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE })
public @interface TestFXMacAnnotations {
}
