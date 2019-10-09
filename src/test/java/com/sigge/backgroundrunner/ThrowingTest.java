package com.sigge.backgroundrunner;

import static com.sigge.backgroundrunner.Throwing.sneakyThrow;

import org.junit.Test;

/**
 * Verifies {@link Throwing}
 */
public class ThrowingTest
{
    @Test(expected=Exception.class)
    public void test_throwing()
    {
        sneakyThrow(new Exception());
    }

    @Test(expected=RuntimeException.class)
    public void test_throwing_runtime()
    {
        sneakyThrow(new RuntimeException());
    }

}
