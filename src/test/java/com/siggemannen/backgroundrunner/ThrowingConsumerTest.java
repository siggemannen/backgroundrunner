package com.siggemannen.backgroundrunner;

import java.util.function.Consumer;

import org.junit.Test;

/**
 * Verifies {@link ThrowingConsumer}
 */
public class ThrowingConsumerTest
{
    @Test(expected = Exception.class)
    public void test_throwing_consumer_with_exception()
    {
        Consumer<String> tc = new FailureThrowingConsumer<>();
        tc.accept("testFailure");
    }

    @Test
    public void test_throwing_consumer_without_exception()
    {
        Consumer<String> tc = new OKThrowingConsumer<>();
        tc.accept("testOK");
    }
    
    @Test(expected=Exception.class)
    public void test_accept_exception_exception()
    {
        ThrowingConsumer<String> tc = this::consume;
        tc.accept("test");
    }
    
    private void consume(String string) throws Exception
    {
        throw new Exception(string);
    }

    class FailureThrowingConsumer<E> implements ThrowingConsumer<E>
    {
        @Override
        public void accept0(E e) throws Throwable
        {
            throw new Exception();
        }
    }

    class OKThrowingConsumer<E> implements ThrowingConsumer<E>
    {

        @Override
        public void accept0(E e) throws Throwable
        {
        }
    }
}