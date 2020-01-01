package com.siggemannen.backgroundrunner;

/**
 * A runnable that can throw exceptions
 */
public interface ThrowingRunnable extends Runnable
{
    /**
     * This is the actual method that performs work and should be overridden
     * @throws Throwable exception that is thrown
     */
    void run0() throws Throwable;

    /**
     * Overrides {@link Runnable#run()} to be able to handle the exceptions in <code>run0</code>
     */
    @Override
    default void run()
    {
        try
        {
            run0();
        }
        catch (Throwable ex)
        {
            Throwing.sneakyThrow(ex);
        }
    }
}