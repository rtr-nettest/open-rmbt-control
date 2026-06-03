package at.rtr.rmbt.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ShutdownThreadCleaner}.
 *
 * <p>The cleaner stops library background threads on shutdown by reflectively invoking known
 * cleanup entry points. These tests verify the two contract guarantees that make it safe to run
 * during context shutdown:
 * <ol>
 *   <li>when a target exists it is actually invoked, and</li>
 *   <li>when a class/field/method is absent it silently no-ops instead of throwing.</li>
 * </ol>
 * The reflection targets are exercised against fixtures in this test class so the assertions are
 * deterministic and don't depend on dnsjava/GeoTools thread state.
 */
class ShutdownThreadCleanerTest {

    private static final String SELF = ShutdownThreadCleanerTest.class.getName();

    static boolean staticInvoked;
    static boolean fieldMethodInvoked;

    /** Reflection target for {@code invokeStatic}. */
    public static void markStaticInvoked() {
        staticInvoked = true;
    }

    /** Public static field whose value carries the instance method for {@code invokeOnStaticField}. */
    public static final ShutdownThreadCleanerTest INSTANCE = new ShutdownThreadCleanerTest();

    /** Reflection target for {@code invokeOnStaticField}. */
    public void markFieldMethodInvoked() {
        fieldMethodInvoked = true;
    }

    @Test
    void invokeStatic_callsExistingStaticMethod() {
        staticInvoked = false;
        ShutdownThreadCleaner.invokeStatic(SELF, "markStaticInvoked");
        assertTrue(staticInvoked, "the existing static method should have been invoked");
    }

    @Test
    void invokeOnStaticField_callsMethodOnFieldValue() {
        fieldMethodInvoked = false;
        ShutdownThreadCleaner.invokeOnStaticField(SELF, "INSTANCE", "markFieldMethodInvoked");
        assertTrue(fieldMethodInvoked, "the static field value's method should have been invoked");
    }

    @Test
    void invokeStatic_isSilentWhenClassOrMethodMissing() {
        assertDoesNotThrow(() -> ShutdownThreadCleaner.invokeStatic("no.such.ClassName", "whatever"));
        assertDoesNotThrow(() -> ShutdownThreadCleaner.invokeStatic(SELF, "noSuchMethod"));
    }

    @Test
    void invokeOnStaticField_isSilentWhenFieldOrMethodMissing() {
        assertDoesNotThrow(() -> ShutdownThreadCleaner.invokeOnStaticField(SELF, "NO_SUCH_FIELD", "x"));
        assertDoesNotThrow(() -> ShutdownThreadCleaner.invokeOnStaticField(SELF, "INSTANCE", "noSuchMethod"));
    }

    @Test
    void stopLibraryThreads_completesWithoutThrowingAndIsRepeatable() {
        // Runs the real cleanup against whatever dnsjava/GeoTools state exists; the contract is that
        // it must never throw (shutdown must not be broken) and must be safe to call more than once.
        final ShutdownThreadCleaner cleaner = new ShutdownThreadCleaner();
        assertDoesNotThrow(cleaner::stopLibraryThreads);
        assertDoesNotThrow(cleaner::stopLibraryThreads);
    }
}
