package com.sigge.backgroundrunner;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.SwingWorker;

public class BackgroundRunner<E> extends SwingWorker<E, Object>
{

    private final Supplier<E> task;
    private final Consumer<E> result;
    private final Consumer<Exception> exceptionConsumer;

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
     * Performs a task supplying E and runs result with the supplied value
     */

    public BackgroundRunner(ThrowingSupplier<E> task, ThrowingConsumer<E> result, Consumer<Exception> exceptionConsumer)
    {
        this.task = task;
        this.result = result;
        this.exceptionConsumer = exceptionConsumer;
    }

    @Override
    protected E doInBackground() throws Exception
    {
        return task.get();
    }

    @Override
    protected void done()
    {
        try
        {
            if (result != null)
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
