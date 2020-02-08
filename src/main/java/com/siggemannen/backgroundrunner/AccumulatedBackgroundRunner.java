package com.siggemannen.backgroundrunner;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.siggemannen.functional.throwing.ThrowingRunnable;

/**
 * Runs tasks with a slight delay, allowing a new immediate task with same "tag" to override the previous one!
 */
public final class AccumulatedBackgroundRunner
{
    private static final Map<String, ScheduledFuture> JOB_MAP = new Hashtable<>();
    private static final ScheduledExecutorService THREAD_POOL = Executors.newScheduledThreadPool(1);

    private AccumulatedBackgroundRunner()
    {
    }

    /**
     * Schedules a task to perform after a certain delay. If there exists a task on schedule, cancel it, since new task supersedes it
     * 
     * @param taskId identifier of the task "group" for which we will replace existing task with a new incoming one
     * @param runnable runnable to run
     * @param delay delay until task will fire
     * @param delayUnit time unit for the delay parameter value
     */
    public static void schedule(String taskId, ThrowingRunnable runnable, int delay, TimeUnit delayUnit)
    {
        //Get previous future
        ScheduledFuture future = JOB_MAP.get(taskId);
        if (future != null)
        {
            future.cancel(false);
        }
        JOB_MAP.put(taskId, THREAD_POOL.schedule(() ->
        {
            runAndRemoveFromSchedule(taskId, runnable);
        }, delay, delayUnit));
    }

    private static void runAndRemoveFromSchedule(String taskId, ThrowingRunnable r)
    {
        try
        {
            //Set thread name to taskId so we know what we're doing
            Thread.currentThread().setName(Thread.currentThread().getName() + ", executing: " + taskId);
            r.run();
        }
        finally
        {
            JOB_MAP.remove(taskId);
        }
    }
}
