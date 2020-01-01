package com.siggemannen.backgroundrunner;

import java.util.function.Supplier;

/**
 * A supplier interface that allows throwing exceptions but still possible to use in lambdas
 */
public interface ThrowingSupplier<T> extends Supplier<T>
{
    /**
     * This is the actual method that supplies data and should be overridden
     * @throws Throwable exception that is thrown
     */
    T get0() throws Throwable;
    
    /**
     * Overrides {@link Supplier#get()} to be able to handle the exceptions in <code>get0</code>
     */
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
}