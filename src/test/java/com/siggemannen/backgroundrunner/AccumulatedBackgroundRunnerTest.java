package com.siggemannen.backgroundrunner;

import static java.lang.Math.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.siggemannen.functional.throwing.ThrowingRunnable;

/**
 * Tests {@link AccumulatedBackgroundRunner}
 */
public class AccumulatedBackgroundRunnerTest
{
    @Test
    public void test_runner_simple()
    {
        RunnableHolder rh = new RunnableHolder();
        AccumulatedBackgroundRunner.schedule("test", rh, 0, TimeUnit.SECONDS);
        sleep();
        assertTrue(rh.hasRun == 1);
        AccumulatedBackgroundRunner.schedule("test", rh, 0, TimeUnit.SECONDS);
        sleep();
        assertTrue(rh.hasRun == 2);
    }

    @Test
    public void test_runner_queued()
    {
        RunnableHolder rh = new RunnableHolder();
        RunnableHolder rh2 = new RunnableHolder();
        AccumulatedBackgroundRunner.schedule("test", rh, 200, TimeUnit.MILLISECONDS);
        AccumulatedBackgroundRunner.schedule("test", rh2, 0, TimeUnit.SECONDS);
        AccumulatedBackgroundRunner.schedule("test", rh2, 0, TimeUnit.SECONDS);
        AccumulatedBackgroundRunner.schedule("test", rh2, 0, TimeUnit.SECONDS);
        AccumulatedBackgroundRunner.schedule("test", rh2, 0, TimeUnit.SECONDS);
        sleep();
        assertTrue(rh.hasRun == 0);
        assertTrue(rh2.hasRun == 1);
    }

    @Test
    public void test_class() throws Exception
    {
        assertUtilityClassWellDefined(AccumulatedBackgroundRunner.class);
    }

    @Test
    public void test_runner_exceptions()
    {
        RunnableHolderException rh = new RunnableHolderException();
        AccumulatedBackgroundRunner.schedule("test", rh, 0, TimeUnit.SECONDS);
        sleep();
        AccumulatedBackgroundRunner.schedule("test", rh, 0, TimeUnit.SECONDS);
        sleep();
        assertTrue(rh.hasRun == 2);
    }
    
    @Test
    public void test_runner_different_tasks()
    {
        RunnableHolderException rh = new RunnableHolderException();
        AccumulatedBackgroundRunner.schedule("test", rh, 100, TimeUnit.MILLISECONDS);
        AccumulatedBackgroundRunner.schedule("test2", rh, 50, TimeUnit.MILLISECONDS);
        AccumulatedBackgroundRunner.schedule("test3", rh, 0, TimeUnit.MILLISECONDS);
        sleep(500);
        assertTrue(rh.hasRun == 3);
    }
    
    @Test
    public void test_load_test()
    {
        
        RunnableHolder rd = new RunnableHolder();
        RunnableHolderException rhe = new RunnableHolderException();
        for (int i = 0; i<1000; i++)
        {
            AccumulatedBackgroundRunner.schedule("test" + (int)(random() * 5), i % 3 == 0 ? rhe : rd, 100 * (int)(random() * 5 - 1), TimeUnit.MILLISECONDS);
            sleep(100 * (int)(random() * 2 - 1));
        }
        sleep(500);
        assertTrue("We expected total run to be at least 100 for " + rd.hasRun + " and " + rhe.hasRun, rd.hasRun + rhe.hasRun > 100);
    }

    public static void assertUtilityClassWellDefined(final Class<?> clazz) throws Exception
    {
        assertTrue("class must be final", Modifier.isFinal(clazz.getModifiers()));
        assertEquals("There must be only one constructor", 1, clazz.getDeclaredConstructors().length);
        final Constructor<?> constructor = clazz.getDeclaredConstructor();
        if (constructor.isAccessible() || !Modifier.isPrivate(constructor.getModifiers()))
        {
            fail("constructor is not private");
        }
        constructor.setAccessible(true);
        constructor.newInstance();
        constructor.setAccessible(false);
        for (final Method method: clazz.getMethods())
        {
            if (!Modifier.isStatic(method.getModifiers()) && method.getDeclaringClass().equals(clazz))
            {
                fail("there exists a non-static method:" + method);
            }
        }
    }

    private void sleep()
    {
        sleep(100);
    }

    private void sleep(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException e)
        {
        }
    }
    private class RunnableHolderException implements ThrowingRunnable
    {
        private int hasRun;

        @Override
        public void run0() throws Throwable
        {
            hasRun++;
            throw new Exception();
        }
    }

    private class RunnableHolder implements ThrowingRunnable
    {
        private int hasRun;

        @Override
        public void run0() throws Throwable
        {
            hasRun++;
        }
    }
}
