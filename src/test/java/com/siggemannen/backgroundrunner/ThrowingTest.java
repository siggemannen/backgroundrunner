package com.siggemannen.backgroundrunner;

import static com.siggemannen.backgroundrunner.Throwing.sneakyThrow;

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
    
    @Test
    public void test_throwing_clazz() throws Exception
    {
        TestClassUtils.assertUtilityClassWellDefined(Throwing.class);
    }
}