package com.siggemannen.backgroundrunner;

import java.util.function.Function;

/**
 * Throwing function version of {@link Function}
 */
public interface ThrowingFunction<T, R> extends Function<T, R>
{
    /**
     * The actual method that performs work
     * @param t argument to function
     * @return applied value
     * @throws Throwable exception that can be thrown from the function
     */
    R apply0(T t) throws Throwable;
    @Override
    
    /**
     * Implementation that "sneaks" in exception handling and re-throws exceptions from apply0 method, shouldn't be overridden
     */
    default R apply(T t)
    {
        try
        {
            return apply0(t);
        }
        catch (Throwable ex)
        {
            Throwing.sneakyThrow(ex);
        }
        return null;
    }
}
