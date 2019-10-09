package com.sigge.backgroundrunner;

import java.util.function.Supplier;

import org.junit.Test;

/**
 * Verifies {@link ThrowingSupplier}
 */
public class ThrowingSupplierTest
{
    @Test(expected=Exception.class)
    public void test_throwing_supplier_exception()
    {
        //@formatter:off
        Supplier<String> s = new FailureThrowingSupplier<>();
        s.get();
    }
    
    @Test
    public void test_throwing_supplier_ok()
    {
        Supplier<String> s = new OKThrowingSupplier<>();
        s.get();
    }
    
    class FailureThrowingSupplier<E> implements ThrowingSupplier<E>
    {
        @Override
        public E get0() throws Throwable
        {
            throw new Exception();
        }
    }

    class OKThrowingSupplier<E> implements ThrowingSupplier<E>
    {
        @Override
        public E get0() throws Throwable
        {
            return null;
        }
    }

}
