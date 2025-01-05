package de.tum.cit.ase.eos;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import de.tum.in.test.api.util.ReflectionTestUtils;

/**
 * Helper class for common reflection operations.
 */
public final class HelperClass {

    /**
     * Helper method to find specific constructor.
     *
     * @param className  - Name of the class, including package.
     * @param parameters - List of types taken by constructor.
     * @return Constructor object with desired parameters, if nothing found returns
     *         null.
     */
    private static Constructor<?> getConstructor(String className, Class<?>[] parameters) {
        try {
            List<Constructor<?>> constructorsList = Arrays.stream(ReflectionTestUtils.getClazz(className).getConstructors())
                    .filter(constructor -> Arrays.equals(constructor.getParameterTypes(), parameters)).toList();
            return constructorsList.isEmpty() || constructorsList.size() >= 2 ? null : constructorsList.get(0);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Creates new instance of requested class with specific constructor.
     *
     * @param className       - Name of the class, including package.
     * @param parameters      - List of types taken by constructor. Should be null
     *                            if empty (default) constructor.
     * @param constructorArgs -Parameter instances of the constructor of the class,
     *                            that it should use to get instantiated with. Should be
     *                            null if constructor doesn't have any parameters.
     * @return New instance of class, if specified constructor was not found returns
     *         null.
     */
    public static Object createInstance(String className, Class<?>[] parameters, Object... constructorArgs) {
        Constructor<?> constructor = parameters == null ? null : getConstructor(className, parameters);
        return parameters == null && className != null ? ReflectionTestUtils.newInstance(className)
                : constructor == null ? null : ReflectionTestUtils.newInstance(constructor, constructorArgs);
    }

    /**
     * @param instance    - Object which attribute we want to get.
     * @param name        - Name of the attribute.
     * @param forceAccess - If access modifier of the attribute is protected or
     *                        private should be true.
     * @return Value of the attribute of the given object.
     */
    public static Object getAttribute(Object instance, String name, boolean forceAccess) {
        return forceAccess ? ReflectionTestUtils.valueForNonPublicAttribute(instance, name) : ReflectionTestUtils.valueForAttribute(instance, name);
    }

    /**
     * @param instance         - Object which method we want to invoke.
     * @param name             - Name of the method.
     * @param forceAccess      - If access modifier of the method is protected or
     *                             private should be true.
     * @param parameterTypes   - Null if method doesn't have any parameters.
     * @param methodParameters - Null if method doesn't have any parameters,
     *                             parameters we will pass to the method.
     * @return Result of the method invocation.
     */
    public static Object invokeMethod(Object instance, String name, boolean forceAccess, Class<?>[] parameterTypes, Object... methodParameters) {
        return forceAccess
                ? parameterTypes == null ? ReflectionTestUtils.invokeNonPublicMethod(instance, name)
                        : ReflectionTestUtils.invokeNonPublicMethod(instance, ReflectionTestUtils.getMethod(instance, name, parameterTypes), methodParameters)
                : parameterTypes == null ? ReflectionTestUtils.invokeMethod(instance, name)
                        : ReflectionTestUtils.invokeMethod(instance, ReflectionTestUtils.getMethod(instance, name, parameterTypes), methodParameters);
    }
}
