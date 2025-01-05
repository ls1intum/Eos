package de.tum.in.test.api.io;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.junit.jupiter.api.extension.*;

import de.tum.in.test.api.WithIOManager;

/**
 * Manages how IO testing is performed in the Ares test extension for IO.
 * <p>
 * Ares does not make any guarantee whether instances are reused across
 * different tests or if a new manager instance is created for each test.
 * <p>
 * Implementations are highly encouraged to respect/reuse the user settings in
 * the given {@link AresIOContext}, if feasible.
 *
 * @param <T> the type of the controller object, that is an object that can be
 *                used by testers to control IO testing inside the test method. See
 *                e.g. {@link IOTester}
 * @author Christian Femers
 */
@API(status = Status.EXPERIMENTAL)
public interface IOManager<T> {

    /**
     * Invoked before each test is executed.
     *
     * @param context the current Ares IO context
     * @see BeforeEachCallback
     */
    void beforeTestExecution(AresIOContext context);

    /**
     * Invoked each the test is executed.
     *
     * @param context the current Ares IO context
     * @see AfterEachCallback
     */
    void afterTestExecution(AresIOContext context);

    /**
     * Provides an instance of an object to control IO testing that is available as
     * parameter in the test method.
     *
     * @param context the current Ares IO context
     * @return a tester instance. This should only be null if
     *         {@link #getControllerClass()} returns null as well or no test is
     *         currently running. May be a subclass of
     *         {@link #getControllerClass()}.
     */
    T getControllerInstance(AresIOContext context);

    /**
     * The class of the type provided by
     * {@link #getControllerInstance(AresIOContext)} such that Ares can register a
     * parameter provider.
     *
     * @return the tester class, or null if no such controller object exists. This
     *         must not be {@link Object}.
     */
    Class<T> getControllerClass();
}
