package com.siggemannen.backgroundrunner;

import java.util.function.Function;

import org.junit.Test;

/**
 * Verifies {@link ThrowingSupplier}
 */
public class ThrowingFunctionTest
{
    @Test(expected = Exception.class)
    public void test_throwing_supplier_exception()
    {
        Function<String, Integer> s = new FailureThrowingFunction<>();
        s.apply("testFailed");
    }

    @Test
    public void test_throwing_supplier_ok()
    {
        Function<String, Integer> s = new OKThrowingFunction<>();
        s.apply("testOK");
    }

    @Test(expected = Exception.class)
    public void test_throwing_supplier_throws_method_error()
    {
        ThrowingFunction<Integer, String> s = this::throwingFunction;
        s.apply(5);
    }

    private String throwingFunction(@SuppressWarnings("unused") int arg) throws Exception
    {
        throw new Exception();
    }

    class FailureThrowingFunction<T, R> implements ThrowingFunction<T, R>
    {
        @Override
        public R apply0(T arg) throws Throwable
        {
            throw new Exception();
        }
    }

    class OKThrowingFunction<T, R> implements ThrowingFunction<T, R>
    {
        @Override
        public R apply0(T arg) throws Throwable
        {
            return null;
        }
    }
}
