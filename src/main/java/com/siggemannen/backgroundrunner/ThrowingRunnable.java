package com.siggemannen.backgroundrunner;

/**
 * A runnable that can throw exceptions
 */
public interface ThrowingRunnable extends Runnable
{

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

    void run0() throws Throwable;

}