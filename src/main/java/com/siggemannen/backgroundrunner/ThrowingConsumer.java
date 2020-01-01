package com.siggemannen.backgroundrunner;

import java.util.function.Consumer;

/**
 * This is a consumer that CAN throw an exception, mostly used as compiler workaround
 *
 * @param <T> type of value it can consume
 */
@FunctionalInterface
public interface ThrowingConsumer<T> extends Consumer<T>
{

    /**
     * This is the actual method that consumes value and should be overridden
     * @param e value to consume
     * @throws Throwable exception that is thrown
     */
    void accept0(T e) throws Throwable;
    
    /**
     * Overrides {@link Consumer#accept()} to be able to handle the exceptions in <code>accept0</code>
     */
    @Override
    default void accept(final T e)
    {
        try
        {
            accept0(e);
        }
        catch (Throwable ex)
        {
            Throwing.sneakyThrow(ex);
        }
    }
}
