package de.tum.in.test.api.structural;

import static de.tum.in.test.api.localization.Messages.formatLocalized;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.json.JSONObject;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;

/**
 * This test evaluates the hierarchy of the class, i.e. if the class is abstract
 * or an interface or an enum and also if the class extends another superclass
 * and if it implements the interfaces and annotations, based on its definition
 * in the structure oracle (test.json).
 *
 * @author Stephan Krusche (krusche@in.tum.de)
 * @version 5.1 (2022-03-30)
 */
@API(status = Status.STABLE)
public abstract class ClassTestProvider extends StructuralTestProvider {

	/**
	 * This method collects the classes in the structure oracle file for which at
	 * least one class property is specified. These classes are then transformed
	 * into JUnit 5 dynamic tests.
	 *
	 * @return A dynamic test container containing the test for each class which is
	 *         then executed by JUnit.
	 * @throws URISyntaxException an exception if the URI of the class name cannot
	 *                            be generated (which seems to be unlikely)
	 */
	protected DynamicContainer generateTestsForAllClasses() throws URISyntaxException {
		List<DynamicNode> tests = new ArrayList<>();
		if (structureOracleJSON == null)
			throw failure(
					"The ClassTest test can only run if the structural oracle (test.json) is present. If you do not provide it, delete ClassTest.java!"); //$NON-NLS-1$
		for (var i = 0; i < structureOracleJSON.length(); i++) {
			var expectedClassJSON = structureOracleJSON.getJSONObject(i);
			var expectedClassPropertiesJSON = expectedClassJSON.getJSONObject(JSON_PROPERTY_CLASS);
			/*
			 * Only test the classes that have additional properties (except name and
			 * package) defined in the structure oracle.
			 */
			if (expectedClassPropertiesJSON.has(JSON_PROPERTY_NAME)
					&& expectedClassPropertiesJSON.has(JSON_PROPERTY_PACKAGE)
					&& hasAdditionalProperties(expectedClassPropertiesJSON)) {
				var expectedClassName = expectedClassPropertiesJSON.getString(JSON_PROPERTY_NAME);
				var expectedPackageName = expectedClassPropertiesJSON.getString(JSON_PROPERTY_PACKAGE);
				var expectedClassStructure = new ExpectedClassStructure(expectedClassName, expectedPackageName,
						expectedClassJSON);
				tests.add(dynamicTest("testClass[" + expectedClassName + "]", () -> testClass(expectedClassStructure))); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		if (tests.isEmpty())
			throw failure(
					"No tests for classes available in the structural oracle (test.json). Either provide attributes information or delete ClassTest.java!"); //$NON-NLS-1$
		/*
		 * Using a custom URI here to workaround surefire rendering the JUnit XML
		 * without the correct test names.
		 */
		return dynamicContainer(getClass().getName(), new URI(getClass().getName()), tests.stream());
	}

	protected static boolean hasAdditionalProperties(JSONObject jsonObject) {
		List<String> keys = new ArrayList<>(jsonObject.keySet());
		keys.remove(JSON_PROPERTY_NAME);
		keys.remove(JSON_PROPERTY_PACKAGE);
		return !keys.isEmpty();
	}

	/**
	 * This method gets passed the expected class structure generated by the method
	 * {@link #generateTestsForAllClasses()}, checks if the class is found at all in
	 * the assignment and then proceeds to check its properties.
	 *
	 * @param expectedClassStructure The class structure that we expect to find and
	 *                               test against.
	 */
	protected static void testClass(ExpectedClassStructure expectedClassStructure) {
		var expectedClassName = expectedClassStructure.getExpectedClassName();
		var observedClass = findClassForTestType(expectedClassStructure, "class"); //$NON-NLS-1$
		var expectedClassPropertiesJSON = expectedClassStructure.getPropertyAsJsonObject(JSON_PROPERTY_CLASS);
		checkBasicClassProperties(expectedClassName, observedClass, expectedClassPropertiesJSON);
		checkSuperclass(expectedClassName, observedClass, expectedClassPropertiesJSON);
		checkInterfaces(expectedClassName, observedClass, expectedClassPropertiesJSON);
		checkAnnotations(expectedClassName, observedClass, expectedClassPropertiesJSON);
	}

	private static void checkBasicClassProperties(String expectedClassName, Class<?> observedClass,
			JSONObject expectedClassPropertiesJSON) {
		if (checkBooleanOf(expectedClassPropertiesJSON, "isAbstract") //$NON-NLS-1$
				&& !Modifier.isAbstract(observedClass.getModifiers()))
			throw failure(formatLocalized("structural.class.abstract", expectedClassName)); //$NON-NLS-1$
		if (checkBooleanOf(expectedClassPropertiesJSON, "isEnum") && !observedClass.isEnum()) //$NON-NLS-1$
			throw failure(formatLocalized("structural.class.enum", expectedClassName)); //$NON-NLS-1$
		if (checkBooleanOf(expectedClassPropertiesJSON, "isInterface") //$NON-NLS-1$
				&& !Modifier.isInterface(observedClass.getModifiers()))
			throw failure(formatLocalized("structural.class.interface", expectedClassName)); //$NON-NLS-1$
		if (expectedClassPropertiesJSON.has(JSON_PROPERTY_MODIFIERS)) {
			var expectedModifiers = getExpectedJsonProperty(expectedClassPropertiesJSON, JSON_PROPERTY_MODIFIERS);
			var modifiersAreCorrect = checkModifiers(Modifier.toString(observedClass.getModifiers()).split(" "), //$NON-NLS-1$
					expectedModifiers);
			if (!modifiersAreCorrect)
				throw failure(formatLocalized("structural.class.modifiers", expectedClassName)); //$NON-NLS-1$
		}
	}

	private static boolean checkBooleanOf(JSONObject expectedClassPropertiesJSON, String booleanProperty) {
		return expectedClassPropertiesJSON.has(booleanProperty)
				&& expectedClassPropertiesJSON.getBoolean(booleanProperty);
	}

	private static void checkSuperclass(String expectedClassName, Class<?> observedClass,
			JSONObject expectedClassPropertiesJSON) {
		// Filter out the enums, since there is a separate test for them
		if (expectedClassPropertiesJSON.has(JSON_PROPERTY_SUPERCLASS)
				&& !"Enum".equals(expectedClassPropertiesJSON.getString(JSON_PROPERTY_SUPERCLASS))) { //$NON-NLS-1$
			var expectedSuperClassName = expectedClassPropertiesJSON.getString(JSON_PROPERTY_SUPERCLASS);
			if (!checkExpectedType(observedClass.getSuperclass(), observedClass.getGenericSuperclass(),
					expectedSuperClassName)) {
				var failMessage = formatLocalized("structural.class.extends", expectedClassName, //$NON-NLS-1$
						expectedSuperClassName);
				throw failure(failMessage);
			}
		}
	}

	private static void checkInterfaces(String expectedClassName, Class<?> observedClass,
			JSONObject expectedClassPropertiesJSON) {
		if (expectedClassPropertiesJSON.has(JSON_PROPERTY_INTERFACES)) {
			var expectedInterfaces = expectedClassPropertiesJSON.getJSONArray(JSON_PROPERTY_INTERFACES);
			var observedInterfaces = observedClass.getInterfaces();
			var observedGenericInterfaceTypes = observedClass.getGenericInterfaces();
			for (var i = 0; i < expectedInterfaces.length(); i++) {
				var expectedInterface = expectedInterfaces.getString(i);
				var implementsInterface = false;
				for (var j = 0; j < observedInterfaces.length; j++) {
					var observedInterface = observedInterfaces[j];
					var observedGenericInterfaceType = observedGenericInterfaceTypes[j];
					if (checkExpectedType(observedInterface, observedGenericInterfaceType, expectedInterface)) {
						implementsInterface = true;
						break;
					}
				}
				if (!implementsInterface)
					throw failure(formatLocalized("structural.class.implements", expectedClassName, expectedInterface)); //$NON-NLS-1$
			}
		}
	}

	private static void checkAnnotations(String expectedClassName, Class<?> observedClass,
			JSONObject expectedClassPropertiesJSON) {
		if (expectedClassPropertiesJSON.has(JSON_PROPERTY_ANNOTATIONS)) {
			var expectedAnnotations = expectedClassPropertiesJSON.getJSONArray(JSON_PROPERTY_ANNOTATIONS);
			var observedAnnotations = observedClass.getAnnotations();
			var annotationsAreRight = checkAnnotations(observedAnnotations, expectedAnnotations);
			if (!annotationsAreRight)
				throw failure(formatLocalized("structural.class.annotations", expectedClassName)); //$NON-NLS-1$
		}
	}
}
