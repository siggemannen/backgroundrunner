package com.siggemannen.backgroundrunner;

/**
 * Sneaky throwing class to handle exceptions in non-exception interfaces (like Runnable, Supplier)
 */
public final class Throwing
{

    private Throwing()
    {
    }

    /**
     * The compiler sees the signature with the throws T inferred to a RuntimeException type, so it allows the unchecked exception to propagate.
     * http://www.baeldung.com/java-sneaky-throws
     */
    @SuppressWarnings("unchecked")
    public static <E extends Throwable> void sneakyThrow(Throwable ex) throws E
    {
        throw (E) ex;
    }
}