package com.siggemannen.backgroundrunner;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.SwingWorker;

public class BackgroundRunner<E> extends SwingWorker<E, Object>
{

    private final Supplier<E> task;
    private final Consumer<E> result;
    private final Consumer<Exception> exceptionConsumer;
    boolean triggeredExceptionInSupplier;

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
     * Performs a task supplying E and runs result with the supplied value
     */

    public BackgroundRunner(ThrowingSupplier<E> task, ThrowingConsumer<E> result, ThrowingConsumer<Exception> exceptionConsumer)
    {
        this.task = task;
        this.result = result;
        this.exceptionConsumer = exceptionConsumer;

    }

    @Override
    protected E doInBackground() throws Exception
    {
        try
        {
            return task.get();
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
                throw e;
            }
        }
        return null;
    }

    @Override
    protected void done()
    {
        try
        {
            if (result != null && !triggeredExceptionInSupplier)
            {
                result.accept(get());
            }
        }
        catch (Exception e)
        {
            if (exceptionConsumer != null)
            {
                exceptionConsumer.accept(e);
            }
        }
    }

}
