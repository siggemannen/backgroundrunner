package com.siggemannen.backgroundrunner;

import java.util.function.Supplier;

/**
 * A supplier interface that allows throwing exceptions but still possible to use in lambdas
 */
public interface ThrowingSupplier<T> extends Supplier<T>
{
    @Override
    default T get()
    {
        try
        {
            return get0();
        }
        catch (Throwable ex)
        {
            Throwing.sneakyThrow(ex);
        }
        return null;

    }

    T get0() throws Throwable;

}
