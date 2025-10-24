package com.siggemannen.backgroundrunner;

import static java.util.Arrays.asList;

import org.junit.After;
import org.junit.Test;

/**
 * Tests {@link TriggeredDelayTaskRunner}
 */
public class TriggeredDelayTaskRunnerTest
{
    private TriggeredDelayTaskRunner dt;

    @Test
    public void test_triggers()
    {
        VerifiableRunnable vr = new VerifiableRunnable("test_triggers");
        System.out.println("===========================" + vr.getName() + "===========================");
        dt = new TriggeredDelayTaskRunner(vr, 0, asList(1000));
        dt.trigger();
        vr.verify(false, 200);
        vr.verify(false, 200);
        sleep(1000);
        vr.verify(true, 0);
    }

    @Test
    public void test_triggers_retriggering()
    {
        VerifiableRunnable vr = new VerifiableRunnable("test_triggers_retriggering");
        System.out.println("===========================" + vr.getName() + "===========================");
        dt = new TriggeredDelayTaskRunner(vr, 0, asList(1000));
        dt.trigger();
        vr.verify(false, 200);
        dt.trigger();
        vr.verify(false, 200);
        dt.trigger();
        sleep(500);
        vr.verify(false, 200);
    }

    @Test
    public void test_triggers_restart()
    {
        VerifiableRunnable vr = new VerifiableRunnable("test_triggers_restart");
        System.out.println("===========================" + vr.getName() + "===========================");
        dt = new TriggeredDelayTaskRunner(vr, 0, asList(500));
        dt.trigger();
        vr.verify(false, 200);
        vr.verify(true, 600);
        //Now re-trigger it so we can start over
        vr.reset();
        vr.verify(false, 100);
        dt.trigger();
        vr.verify(false, 200);
        vr.verify(false, 100);
        vr.verify(true, 200);
    }

    @Test
    public void test_triggers_within_misfire()
    {
        VerifiableRunnable vr = new VerifiableRunnable("test_triggers_within_misfire");
        System.out.println("===========================" + vr.getName() + "===========================");
        dt = new TriggeredDelayTaskRunner(vr, 100, asList(500, 300));
        dt.trigger();
        vr.verify(false, 50);
        dt.trigger();
        vr.verify(false, 200);
        vr.verify(true, 150);
        vr.verify(true, 300);
    }

    @Test
    public void test_triggers_outside_misfire()
    {
        VerifiableRunnable vr = new VerifiableRunnable("test_triggers_outside_misfire");
        System.out.println("===========================" + vr.getName() + "===========================");
        dt = new TriggeredDelayTaskRunner(vr, 100, asList(500, 300));
        dt.trigger();
        vr.verify(false, 150);
        dt.trigger(); //This is outside of misfire and therefore stepping is increased
        vr.verify(false, 200);
        vr.verify(true, 200);
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

    @After
    public void tearDown()
    {
        dt.cancel();
    }
}
