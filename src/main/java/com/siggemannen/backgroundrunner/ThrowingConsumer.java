package com.siggemannen.backgroundrunner;

import java.util.function.Consumer;

/**
 * This is a consumer that CAN throw an exception, mostly used as compiler workaround
 *
 * @param <T>
 */
@FunctionalInterface
public interface ThrowingConsumer<T> extends Consumer<T>
{

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

    void accept0(T e) throws Throwable;

}
