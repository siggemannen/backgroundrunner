package com.siggemannen.backgroundrunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;

/**
 * To verify {@link BackgroundRunner}
 */
public class BackgroundRunnerTest
{
    private AtomicBoolean ab;
    private String result;
    private Exception e;

    @Before
    public void setUp()
    {
        ab = new AtomicBoolean(false);
        result = null;
        e = null;
    }

    @Test
    public void test_background_runner() throws InterruptedException, ExecutionException
    {
        BackgroundRunner br = new BackgroundRunner(this::method_that_throws);
        br.execute();
        br.get();
        assertTrue("We expected our method to be triggered", ab.get());
    }

    @Test
    public void test_background_runner_supplier() throws InterruptedException, ExecutionException
    {
        BackgroundRunner<String> br = new BackgroundRunner<>(this::supply_method);
        br.execute();
        String result = br.get();
        assertEquals("We expect our test result", "test", result);
    }

    @Test
    public void test_background_runner_supplier_consumer() throws InterruptedException, ExecutionException
    {
        BackgroundRunner<String> br = new BackgroundRunner<>(this::supply_method, this::consume_result_method);
        br.execute();
        String result = br.get();
        
        assertEquals("Our result should also be consumed", "test", result);
        Thread.sleep(100);
        assertEquals("Our result should also be consumed", "test", this.result);
    }
    
    @Test
    public void test_background_runner_exception() throws InterruptedException
    {
        BackgroundRunner<String> br = new BackgroundRunner<>(this::throwing_supplier, this::consume_result_method);
        br.execute();
        //We shouldn't expect any supplier to be called
        Thread.sleep(200);
        assertEquals("Our result should also be consumed", null, this.result);
    }

    @Test
    public void test_exception_consumer_ok() throws InterruptedException
    {
        BackgroundRunner<String> br = new BackgroundRunner<>(this::supply_method, this::consume_result_method, this::consume_exception);
        br.execute();
        Thread.sleep(200);
        //We expect our result be null
        assertEquals("Our result should also be consumed", "test", this.result);
        //We expect exception to be consumed
        assertTrue("Our exception consumer should not have been called", e == null);
    }

    @Test
    public void test_exception_supplier_failed() throws InterruptedException
    {
        BackgroundRunner<String> br = new BackgroundRunner<>(this::throwing_supplier, this::consume_result_method, this::consume_exception);
        br.execute();
        Thread.sleep(200);
        //We expect our result be null
        assertEquals("Our result should also be consumed", null, this.result);
        //We expect exception to be consumed
        assertTrue("Our exception consumer should have been called", e != null);
    }
    
    @Test
    public void test_exception_consumer_failed() throws InterruptedException
    {
        BackgroundRunner<String> br = new BackgroundRunner<>(this::throwing_supplier, this::consume_result_method_failure, this::consume_exception);
        br.execute();
        Thread.sleep(200);
        //We expect our result be null
        assertEquals("Our result should not be consumed", null, this.result);
        //We expect exception to be consumed
        assertTrue("Our exception consumer should have been called", e != null);
    }
    
    @Test
    public void test_runner_runnable_and_exception_consumer() throws InterruptedException
    {
        BackgroundRunner<String> br = new BackgroundRunner<>(this::throwing_supplier, this::consume_exception);
        br.execute();
        Thread.sleep(200);
        //We expect our result be null
        assertEquals("Our result should not be consumed", null, this.result);
        //We expect exception to be consumed
        assertTrue("Our exception consumer should have been called", e != null);
    }

    @Test
    public void test_runner_runnable_and_exception_consumer_no_exceptions() throws InterruptedException
    {
        BackgroundRunner<String> br = new BackgroundRunner<>(this::supply_method, this::consume_exception);
        br.execute();
        Thread.sleep(200);
        //We expect our result be null
        assertEquals("Our result should not be consumed cause we have no consumer", null, this.result);
        //We expect exception to be consumed
        assertTrue("Our exception consumer should not have been called", e == null);
    }
    
    @Test
    public void test_result_consumer_throws_exception_that_should_be_accepted() throws InterruptedException
    {
        BackgroundRunner<String> br = new BackgroundRunner<>(this::supply_method, this::consume_result_method_failure, this::consume_exception);
        br.execute();
        Thread.sleep(200);
        //We expect our result be null
        assertEquals("Our result should not be consumed", null, this.result);
        //We expect exception to be consumed
        assertTrue("Our exception consumer should have been called", e != null);
    }
    
    public void consume_exception(Exception e)
    {
        this.e = e;
    }
    public String throwing_supplier() throws Exception
    {
        throw new Exception("Thrown");
    }
    
    public void method_that_throws() throws Exception
    {
        ab.set(true);
    }

    public String supply_method() throws Exception
    {
        return "test";
    }

    public void consume_result_method(String result) throws Exception
    {
        this.result = result;
    }

    public void consume_result_method_failure(String result) throws Exception
    {
        throw new Exception(result);
    }
}
