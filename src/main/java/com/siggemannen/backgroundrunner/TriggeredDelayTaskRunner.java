package com.siggemannen.backgroundrunner;

import static java.time.LocalDateTime.now;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.siggemannen.functional.throwing.ThrowingRunnable;

/**
 * <P>
 * A runner that runs a task after it's "armed" and a certain delay has passed. <br>
 * A re-arming before task firing pushes its run further away.
 * </P>
 * <P>
 * It's possible to customize delay intervals depending on how many times the task was triggered.<br>
 * After task is completed, it will be reset to initial state and wait for next re-trigger
 */
public class TriggeredDelayTaskRunner
{
    private final ThrowingRunnable task;
    private final int misfireDelayIgnoreMs;
    private final List<Integer> delaySteps;
    private boolean cancel = false;
    private int currentStep = 0;
    private final AtomicBoolean started = new AtomicBoolean(false);
    private static final int MIN_RESOLUTION = 50;
    private final int resolution;
    private LocalDateTime triggerDate;
    private Thread t;

    /**
     * Creates a new delay task.
     *
     * @param task the runnable that will be performed
     * @param misfireDelayIgnoreMs threshold of how much time must pass since previous trigger to advance in the next step delay of a task. For
     *            example, if a task is triggered very quickly we want to "de-bounce" these actions into one
     * @param delaySteps list of how long delay should each trigger lead until the task fires. Every trigger, list is advanced one position and task
     *            waits that amount of milliseconds until firing. <br>
     *            Note: there must be at least one delay step
     */
    public TriggeredDelayTaskRunner(ThrowingRunnable task, int misfireDelayIgnoreMs, List<Integer> delaySteps)
    {
        if (delaySteps == null || delaySteps.size() == 0)
        {
            throw new IllegalArgumentException("At least one delay step is required to be set");
        }
        this.task = task;
        this.misfireDelayIgnoreMs = misfireDelayIgnoreMs;
        this.delaySteps = delaySteps;
        //Make some sort of guesstimate delay to cover the least delay step
        resolution = Math.max(MIN_RESOLUTION, (int) (delaySteps.stream().min(Integer::compareTo).get() / 10.0)); //Set a decent resolution for checking, we don't want it to occur for too often
    }

    /**
     * Cancel and cleans up the triggers
     */
    public void cancel()
    {
        cancel = true;
        if (t != null)
        {
            t.interrupt();
            t = null;
        }
    }

    /**
     * Arms the task to be run
     */
    public synchronized void trigger()
    {
        /**
         * If not started
         */
        if (started.compareAndSet(false, true))
        {
            if (t != null)  //If we restarted, interrupt the sleep and continue on
            {
                t.interrupt();
                return;
            }
            t = new Thread(this::startLoop);
            if (task.getName() != null)
            {
                t.setName(task.getName());
            }
            t.setDaemon(true);
            t.start();
            return;
        }
        /**
         * If started, check if previous call was within limit for misfire ignore. If not, then advance to next delay
         */

        //Check if last trigger was before, if we ignore misfires, then advance to next step
        if (misfireDelayIgnoreMs == 0 || triggerDate == null || now().minus(Duration.ofMillis(misfireDelayIgnoreMs)).compareTo(triggerDate) > 0)
        {
            triggerDate = now();
            currentStep++;
        }
        else
        {
            triggerDate = now();
        }
    }

    private void startLoop()
    {
        triggerDate = now();
        while (!cancel)
        {
            try
            {
                Thread.sleep(resolution);
            }
            catch (InterruptedException e)
            {
                break;
            }
            if (!checkIfWeShouldFire())
            {
                continue;
            }

            //Reset things
            reset();
            //Fire!
            task.run();

            try //Sleep forever until next iteration
            {
                Thread.sleep(Integer.MAX_VALUE);
            }
            catch (InterruptedException e)
            {
                //Our thread gets interrupted either by cancel or by a new triggering
            }
        }
    }

    private void reset()
    {
        currentStep = 0;
        cancel = false;
        triggerDate = now();
        started.set(false);

    }

    private boolean checkIfWeShouldFire()
    {
        if (currentStep >= delaySteps.size())
        {
            currentStep = delaySteps.size() - 1;
        }
        //Check if it's been enough time since last fire
        int delayStep = delaySteps.get(currentStep);
        return now().minus(Duration.ofMillis(delayStep)).compareTo(triggerDate) > 0;
    }

}