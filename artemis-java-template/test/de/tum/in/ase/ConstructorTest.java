package de.tum.in.ase;

import static org.junit.Assert.fail;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import org.json.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * @author Stephan Krusche (krusche@in.tum.de)
 * @version 3.0 (25.09.2019)
 * <br><br>
 * This test evaluates if the specified constructors in the structure oracle are correctly implemented with the expected parameter types and annotations,
 * based on its definition in the structure oracle (test.json).
 */
@RunWith(Parameterized.class)
public class ConstructorTest extends StructuralTest {

    public ConstructorTest(String expectedClassName, String expectedPackageName, JSONObject expectedClassJSON) {
        super(expectedClassName, expectedPackageName, expectedClassJSON);
    }

    /**
     * This method collects the classes in the structure oracle file for which constructors are specified.
     * These classes are packed into a list, which represents the test data.
     * @return A list of arrays containing each class' name, package and the respective JSON object defined in the structure oracle.
     */
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> findClasses() {
        List<Object[]> testData = new ArrayList<Object[]>();

        if (structureOracleJSON == null) {
            fail("The ConstructorTest can only run if the structural oracle (test.json) is present. If you do not provide it, delete ConstructorTest.java!");
        }

        for (int i = 0; i < structureOracleJSON.length(); i++) {
            JSONObject expectedClassJSON = structureOracleJSON.getJSONObject(i);

            // Only test the constructors if they are specified in the structure diff
            if (expectedClassJSON.has(JSON_PROPERTY_CLASS) && expectedClassJSON.has(JSON_PROPERTY_CONSTRUCTORS)) {
                JSONObject expectedClassPropertiesJSON = expectedClassJSON.getJSONObject(JSON_PROPERTY_CLASS);
                String expectedClassName = expectedClassPropertiesJSON.getString(JSON_PROPERTY_NAME);
                String expectedPackageName = expectedClassPropertiesJSON.getString(JSON_PROPERTY_PACKAGE);
                testData.add(new Object[] { expectedClassName, expectedPackageName, expectedClassJSON });
            }
        }
        if (testData.size() == 0) {
            fail("No tests for constructors available in the structural oracle (test.json). Either provide constructor information or delete ConstructorTest.java!");
        }
        return testData;
    }

    /**
     * This test loops over the list of the test data generated by the method findClasses(), checks if each class is found
     * at all in the assignment and then proceeds to check its constructors.
     */
    @Test(timeout = 1000)
    public void testConstructors() {
        Class<?> actualClass = findClassForTestType("constructor");

        if (expectedClassJSON.has(JSON_PROPERTY_CONSTRUCTORS)) {
            JSONArray expectedConstructors = expectedClassJSON.getJSONArray(JSON_PROPERTY_CONSTRUCTORS);
            checkConstructors(actualClass, expectedConstructors);
        }
    }

    /**
     * This method checks if a observed class' constructors match the expected ones defined in the structure oracle.
     * @param observedClass: The class that needs to be checked as a Class object.
     * @param expectedConstructors: The information on the expected constructors contained in a JSON array. This information consists
     * of the parameter types and the visibility modifiers.
     */
    private void checkConstructors(Class<?> observedClass, JSONArray expectedConstructors) {
        for (int i = 0; i < expectedConstructors.length(); i++) {
            JSONObject expectedConstructor = expectedConstructors.getJSONObject(i);
            JSONArray expectedParameters = expectedConstructor.has(JSON_PROPERTY_PARAMETERS) ? expectedConstructor.getJSONArray(JSON_PROPERTY_PARAMETERS) : new JSONArray();
            JSONArray expectedModifiers = expectedConstructor.has(JSON_PROPERTY_MODIFIERS) ? expectedConstructor.getJSONArray(JSON_PROPERTY_MODIFIERS) : new JSONArray();
            JSONArray expectedAnnotations = expectedConstructor.has(JSON_PROPERTY_ANNOTATIONS) ? expectedConstructor.getJSONArray(JSON_PROPERTY_ANNOTATIONS) : new JSONArray();

            boolean parametersAreRight = false;
            boolean modifiersAreRight = false;
            boolean annotationsAreRight = false;

            for (Constructor<?> observedConstructor : observedClass.getDeclaredConstructors()) {
                Class<?>[] observedParameters = observedConstructor.getParameterTypes();
                String[] observedModifiers = Modifier.toString(observedConstructor.getModifiers()).split(" ");
                Annotation[] observedAnnotations = observedConstructor.getAnnotations();

                parametersAreRight = checkParameters(observedParameters, expectedParameters);
                modifiersAreRight = checkModifiers(observedModifiers, expectedModifiers);
                annotationsAreRight = checkAnnotations(observedAnnotations, expectedAnnotations);

                // If both are correct, then we found our constructor and we can break the loop
                if (parametersAreRight && modifiersAreRight && annotationsAreRight) {
                    break;
                }
            }

            String expectedConstructorInformation = "the expected constructor of the class '" + expectedClassName + "' with "
                + ((expectedParameters.length() == 0) ? "no parameters" : "the parameters: " + expectedParameters.toString());

            if (!parametersAreRight) {
                fail("The parameters of " + expectedConstructorInformation + " are not implemented as expected.");
            }
            if (!modifiersAreRight) {
                fail("The access modifiers of " + expectedConstructorInformation + " are not implemented as expected.");
            }
            if (!annotationsAreRight) {
                fail("The annotation(s) of " + expectedConstructorInformation + " are not implemented as expected.");
            }
        }
    }
}
