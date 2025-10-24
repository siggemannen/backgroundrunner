package com.siggemannen.backgroundrunner;

import com.siggemannen.functional.throwing.ThrowingRunnable;

/**
 * A runnable that can be verified to be run
 */
public class VerifiableRunnable implements ThrowingRunnable
{
    private static final int MILLIS = 100;
    private boolean ran;
    private String name;

    VerifiableRunnable()
    {

    }

    VerifiableRunnable(String name)
    {
        this.name = name;
    }

    @Override
    public void run0()
    {
        ran = true;
    }

    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Verify result of running
     *
     * @param hasRun do we want to verify that it has ran (true) or not ran (false)
     * @param msVerifyUntil number of milliseconds until we give up verification and raise error
     * @throws Exception
     */
    public void verify(boolean hasRun, int msVerifyUntil)
    {
        int delay = 0;
        while (delay < msVerifyUntil)
        {
            if (ran && hasRun)
            {
                return;
            }
            try
            {
                Thread.sleep(MILLIS);
            }
            catch (InterruptedException e)
            {
            }
            delay += MILLIS;
        }
        if (ran != hasRun)
        {
            throw new RuntimeException("We wanted thread to have hasRun = " + hasRun + " but it was " + !hasRun);
        }
    }

    public void reset()
    {
        ran = false;
    }
}
