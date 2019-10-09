# backgroundrunner

This class allows to perform background operations that can throw exceptions and handle results by callbacks and consumers.

It also simplifies working with lambdas that can throw exceptions.

## Usage

  * To schedule a simple runnable in a new thread
```
BackgroundRunner br = new BackgroundRunner(this::method); //Initializes a new runner with a runnable that can throw exceptions
br.execute(); //Creates a new thread and executes the method

public void method() throws Exception
{
    System.out.println("Do things");
}

```

  * To create a background thread that processes and returns result that is passed to a result consumer
  
```

BackgroundRunner<String> br2 = new BackgroundRunner<>(() -> "test", e ->
{
    System.out.println(e);
});
br2.execute();


```

  * Finally, creates a background worker that can handle results and accept exceptions that have occurred during result processing

```
BackgroundRunner<String> br3 = new BackgroundRunner<>(this::generateData, this::processData, this::processException);
br3.execute();

public String generateData() throws Exception
{
    throw new Exception("Error occurred");
}

public void processData(String data) throws Exception
{
    System.out.println(data);
}

public void processException(Exception e) throws Exception
{
    e.printStackTrace();
}
	
```