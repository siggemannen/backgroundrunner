package com.siggemannen.backgroundrunner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.siggemannen.functional.throwing.ThrowingConsumer;
import com.siggemannen.functional.throwing.ThrowingRunnable;
import com.siggemannen.functional.throwing.ThrowingSupplier;

/**
 * Performs a task in a background thread and returns result on AWT thread
 */
public class BackgroundRunner<E>
{
    private final Supplier<E> task;
    private final Consumer<E> result;
    private final Consumer<Exception> exceptionConsumer;
    boolean triggeredExceptionInSupplier;
    private final static ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private E computation;
    private Future<E> future;
    
    /**
     * Performs a task supplying E and runs result with the supplied value task might throw an exception
     */
    public BackgroundRunner(ThrowingSupplier<E> task)
    {
        this(task, null, null);
    }

    public BackgroundRunner(ThrowingSupplier<E> task, ThrowingConsumer<E> result)
    {
        this(task, result, null);
    }

    /**
     * Create a runner and perform a task that doesn't supply a return value Runnable might throw exception
     */
    public BackgroundRunner(ThrowingRunnable runnable)
    {
        this(() ->
        {
            runnable.run();
            return null;
        });
    }

    /**
     * Create a runner and perform a task that doesn't supply a return value
     * @param runnable Runnable to run that might throw exception
     * @param exceptionConsumer consumes exception occurred in the runnable
     */
    public BackgroundRunner(ThrowingRunnable runnable, ThrowingConsumer<Exception> exceptionConsumer)
    {
        this(() ->
        {
            runnable.run();
            return null;
        }, null, exceptionConsumer);
    }

    /**
     * Create a runner and perform a task that doesn't supply a return value
     * @param runnable Runnable to run that might throw exception
     * @param exceptionConsumer consumes exception occurred in the runnable
     */
    public BackgroundRunner(ThrowingRunnable runnable, ThrowingRunnable callback, ThrowingConsumer<Exception> exceptionConsumer)
    {
        this(() ->
        {
            runnable.run();
            return null;
        }, e -> callback.run(), exceptionConsumer);
    }
    /**
     * Performs a task supplying E and runs result with the supplied value
     */

    public BackgroundRunner(ThrowingSupplier<E> task, ThrowingConsumer<E> result, ThrowingConsumer<Exception> exceptionConsumer)
    {
        this.task = task;
        this.result = result;
        this.exceptionConsumer = exceptionConsumer;
    }
    
    /**
     * Cancels currently running task
     * @return
     */
    public boolean cancel()
    {
        if (future != null)
        {
            try{
                return future.cancel(true);
            }
            catch (Exception e)
            {
                
            }
        }
        return false;
    }
    
    /**
     * Submits execution to run in a new thread
     */
    public void execute()
    {
        future = EXECUTOR.submit(this::doInBackground);
    }
    public E doInBackground()
    {
        try
        {
            computation = task.get();
        }
        catch (Exception e)
        {
            if (exceptionConsumer != null)
            {
                triggeredExceptionInSupplier = true;
                exceptionConsumer.accept(e);
            }
            else
            {
                e.printStackTrace();
                throw e;
            }
        }
        try
        {
            if (result != null && !triggeredExceptionInSupplier)
            {
                result.accept(computation);
            }
        }
        catch (Exception e)
        {
            if (exceptionConsumer != null)
            {
                exceptionConsumer.accept(e);
            }
            e.printStackTrace();
        }
        return computation;
    }
    
    public E get() throws InterruptedException, ExecutionException
    {
        if (future != null)
        {
            return future.get();
        }
        return null;
    }
}
