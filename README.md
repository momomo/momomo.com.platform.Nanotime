<!---
-->

##### Allows for nanosecond time resolution when asking for time from Java Runtime in contrast with System.currentTimeMillis().

##### Maven dependencies available on maven central [search.maven.org](https://search.maven.org/search?q=com.momomo)
##### Dependency   
```xml
<dependency>
  <groupId>com.momomo</groupId>
  <artifactId>momomo.com.platform.Nanotime</artifactId>
  <version>5.0.2</version>
</dependency>                                                      
```                         
##### Repository
```xml
<repository>
    <id>maven-central</id>
    <url>http://repo1.maven.org/maven2</url>
</repository>
```

##### Our other, highlighted [repositories](https://github.com/momomo?tab=repositories)

* **[`momomo.com.platform.Core`](https://github.com/momomo/momomo.com.platform.Core)** Is essentially what makes the our the core of several of momomo.com's public releases and contains a bunch of Java utility.

* **[`momomo.com.platform.Lambda`](https://github.com/momomo/momomo.com.platform.Lambda)** Contains a bunch of `functional interfaces` similar to `Runnable`, `Supplier`, `Function`, `BiFunction`, `Consumer` `...` and so forth all packed in a easily accessed and understood intuitive pattern that are used plenty in our libraries. **`Lambda.V1E`**, **`Lambda.V2E`**, **`Lambda.R1E`**, **`Lambda.R2E`**, ...

* **[`momomo.com.platform.Return`](https://github.com/momomo/momomo.com.platform.Return)** Intuitive library that makes it easier for you to return multiple, fully defined objects on the fly from any method, any time rather than being limited to the default maximum of one. 

* **[`momomo.com.platform.db.transactional.Hibernate`](https://github.com/momomo/momomo.com.platform.db.transactional.Hibernate)** A library to execute database commands in transactions without  having to use annotations based on Hibernate libraries. No Spring!

* **[`momomo.com.platform.db.transactional.Spring`](https://github.com/momomo/momomo.com.platform.db.transactional.Spring)** A library to execute database commands in transactions without  having to use annotations based on Spring libraries.

### Background

First, know that `System.nanoTime()` is elapsed nanos since an arbitrary origin, usually *the start of the JVM* and can usually only be used to measure elapsed time between two invocations. 

What this implementation does is provide you with a way of getting higher precision when asking for time, with *nanosecond* precision.

Normally, you can get the time from your system using `System.currentTimeMillis(`) with millisecond precision but when invoked twice right after each other, calls 
to `System.currentTimeMillis()` will usually return the same value.

This library provides you with nanosecond precision similar to `System.currentTimeMillis()` by calibrating `System.nanoTime()` with `System.currentTimeMillis()`.  

When calibrating the two, our code will:  
   1. Ask `System.currentTimeMillis()` as quickly as we possibly can up until we detect the `1ms` flip.
   2. Once detected, we ask `System.nanoTime()` what time it has and record the difference between the two. 
   3. Repeat this process `100` times (cheap operation) which tests have found is more than enough. 
   4. Also, we subtract the cost of operation `System.nanoTime()` slightly past `System.currentTimeMillis()` which is usually around `30ns` but we calculate the actual once we detect a flip.

The total time for the calibration for `100` times, is as you guessed it, around `100ms` since we are waiting for `100` flips to occur.

## Getting started

There's basically only one class, **`Nanotime.java`** containing the implementation of our concept but we've provided another one due to API call looking better through **`Nano`** since **`Nanotime`** methods are not static.
 
 * **[`Nano.java`](src/momomo/com/Nano.java)**   
    A class utilizing what should only ever be, one instance of **`Nanotime`**.
    
    We have the following static methods currently
    ```java
    Nano.time()       : long                      : 1616615287382000605 
    Nano.timestamp()  : java.sql.Timestamp        : toString() -> 2021-03-25 22:15:28.986068681 
    Nano.datetime()   : java.time.LocalDateTime   : toString() -> 2021-03-25T21:15:28.989876426 
    Nano.localtime()  : java.time.LocalTime       : toString() -> 21:18:34.260363177 
    Nano.instant()    : java.time.Instant         : toString() -> 2021-03-25T21:18:49.431440982Z
    Nano.zonedtime()  : java.time.ZonedDateTime   : toString() -> 2021-03-25T21:18:49.434488996Z 
    Nano.offsettime() : java.time.OffsetDateTime  : toString() -> 2021-03-25T21:18:49.434622190Z
    ```   
   
    For all of these types, we will set up the relevant **nano bits** for you.   
       
 * **[`Nanotime.java`](src/momomo/com/sources/Nanotime.java)**  
    Is the instance class with similarly named instance methods.
 
For normal use, you'd just call **`Nano.time()`**, **`Nano.timestamp()`**, **`Nano.datetime()`**, **`Nano.localtime()`**, **`Nano.instant()`** ... 

#### Thats' it!

### Configuration

To configure **`Nanotime.java`** just call **`Nanotime.setInstance( new Nanotime(...) )`** prior to any use of **`Nano.time()`**. You can also create your own instance that can be accessed separately. 

### How accurate is this?  
* Is this a *100% accurate* record of current time in nanos?   
No, but is there even *such a definition*? What is time? Time always have a reference point. Even atomic clocks do not give a 100% accurate definition of time at any given moment.
 
* Can two machines that make use of this reliably record time of invocation and could a third party reliably tell which came first?   
No, we can not state that either since each machine will generate a different set of calibrated values against it's own `System.currentTimeMillis()`. There will be slight differences. But given that two machines could synchronize their time and reference point, it is possible we could say an invocation occurred before the other using the generated timestamp but that is after such synchronization has been perfomed.   
 &nbsp;  
 This library provides no means to perform such synchronization of reference points across several machines but we've left the implementation open for such possibility if we ever need it. We believe a master machine could be made to send out its recorded `System.currentTimeMillis()` and `System.nanoTime()` to slave machines for them to synchronize with. Such time requests is possible to ask a running Redis instance as an example. 
 
##### Measuring the *error size* is possible but very difficult since:
 
   1. We can not issue both commands at the exact same time, but only one after the other.
   
   3. Cost of call to `System.nanoTime()` as well as to `System.currentTimeMillis()` is not constant and linear, and can vary greatly with a call to `System.nanoTime()` followed by a call to `System.currentTimeMillis()`, and followed by a call to `System.nanoTime()` might at times take `30ns` between each and at times a wopping `0.4ms`. The JVM sometimes generates big diffs between these calls and when we compare their numbers, the difference might be very large between two calls despite the `min` being as close as `30ns`.
     
   3. `System.currentTimeMillis()` is not reliable to compare to in the first place as it might report a millisecond flip `±0.1ms` off.  
       
   4. Acccuracy depends much on your computers ability to calibrate better. A slow computer is likely to yield less accurate results.
   
##### Question

If we plot `System.nanoTime()` over the most accurate clock ever devised, will we get a `100%` perfectly linear graph? How about `System.currentTimeMillis()`? What if plot the ratio between both?

No, because `System.currentTimeMillis()` is not linear, nor consistent in reporting ***time on time***, which is to be expected as `System.currentTimeMillis()` can not be `100%` consistent against `System.nanoTime()` where it would flip a `ms` on the exact end of a `ms` on the `1000 000ns` because it is only millisecond precision. It **can not time** a `nanosecond` flip that precisely.      

***It should be highlighted that once calibrated our reference point stays constant, always remains the same, and never changes.***

The `Java API` has some info on the accuracy of **[`System.currentTimeMillis()`](https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/lang/System.html#currentTimeMillis())** where we find:  

> Note that while the unit of time of the return value is a millisecond, the **granularity** of the value depends on the underlying operating system and may be larger. For example, many operating systems measure time in units of **tens of milliseconds**.

That means an error size of up to `100 0000ns * 0.1 = 100 000ns`. This means a millisecond might be reported earlier or later of up to `0.1 milliseconds` off and is what we've noticed in our generated data as well but rarely to those extremes but when we generate `100 million` data points we could see larger discrepancies close to those extremes occur!

On the contrary a call to `System.nanoTime()` is very expensive at times, with recordings between two calls to `System.nanoTime()` taking as much as up to `0.4ms = 400000ns` and other times only about `~30ns`. 

So to think you can measure the performance of two invocations using `System.nanoTime()` reliably is also wrong as the cost of the second call might actually get you a very delayed answer.   

### Final comments
  
For us, what is most important *is not* being as close to the most accurate clock ever devices, nor to be as close to `System.currenTimeMillis()` as possible which we've proven is not an exact science anyway but for us, it is only important to get close enough. and should only be seen as a higher precision version of the existing `System.currentTimeMillis()` as it will often prove useless when invoked tightly while `System.nanoTime()` will almost always show a **`diff`** and now so will **`Nano.time()`**.  

Our code just synchronizes the two and allows you to map `System.nanoTime()` to one based on a sane and constant reference frame rather than the randomness of when the JVM turned on.

In the end, we only call **`System.nanoTime() + diff`** where **`diff`** is **`final`** after being calculated in the constructor of **`Nanotime`**.    

### Sample run & results    

A sample test run **on our example code within** **[`Nanotime`](src/momomo/com/platform/sources/Nanotime.java)** will output the following which also shows the **rounding** of `System.currentTimeMillis()` **fits extremely well** within bounds.

```java
// We generate the data as quick as we can and then generate the strings in the output below

array[++i] = Long[]{Nano.time(), System.currentTimeMillis(), Nano.time(), Nano.time(), System.currentTimeMillis()}
``` 

You can **[view or download the 100 000 rows of output here (15MB)](https://github.com/momomo/momomo.com.yz.github.statics/blob/master/momomo.com.platform.Nanotime/generated/output.txt?raw=true)**.   
Just scroll through it and ***try to detect & expect*** the flips to occur. 

Some *random highlights* from that file:

```java

index  : 601
nanos  : 1616615287358999926    <----- 58 : nanos
millis : 1616615287359          <----- 59 : millis      ! System.currentTimeInMillis() FLIPS
nanos  : 1616615287359000035    <----- 59 : nanos       ! Nano.time() FLIPS
nanos  : 1616615287359000090    <----- 59 : nanos
millis : 1616615287359          <----- 59 : millis

index  : 602
nanos  : 1616615287359000251    <----- 59 : nanos
millis : 1616615287359          ...
nanos  : 1616615287359000355
nanos  : 1616615287359000405
millis : 1616615287359           


...


index  : 72680
nanos  : 1616615287381999301
millis : 1616615287381
nanos  : 1616615287381999363
nanos  : 1616615287381999396    <----- 81 : nanos
millis : 1616615287381          <----- 81 : millis

index  : 72681
nanos  : 1616615287381999464    <----- 81 : nanos
millis : 1616615287381          <----- 81 : millis
nanos  : 1616615287381999527    <----- 81 : nanos
nanos  : 1616615287382000411    <----- 82 : nanos        ! Nano.time() FLIPS 
millis : 1616615287382          <----- 82 : millis       ! System.currentTimeInMillis() FLIPS

index  : 72682
nanos  : 1616615287382000471    <----- 82 : nanos
millis : 1616615287382          ...
nanos  : 1616615287382000521
nanos  : 1616615287382000548
millis : 1616615287382

```
 
Here are some highlights from a *different run* where we calculate `min` and `max` diffs from what we expect at switches. Calculation is not perfect and actually quite complex to get right.   


```java
---------------------- LARGE --------------------
index     : 29917
nanos  0  : 1616623610040999916
millis 0  : 1616623610041
nanos  1  : 1616623610041000009
nanos  2  : 1616623610041000054
millis 1  : 1616623610041
nano cost : 45
diff      : 9
-------------------------------------------------

---------------------- SMALL --------------------
index     : 41948
nanos  0  : 1616623610044999909
millis 0  : 1616623610045
nanos  1  : 1616623610045000000
nanos  2  : 1616623610045000047
millis 1  : 1616623610045
nano cost : 47
diff      : 0
-------------------------------------------------

---------------------- LARGE --------------------
index     : 74341
nanos  0  : 1616623610053999918
millis 0  : 1616623610054
nanos  1  : 1616623610053999969
nanos  2  : 1616623610053999996
millis 1  : 1616623610054
nano cost : 27
diff      : 31
-------------------------------------------------

---------------------- LARGE --------------------
index     : 79042
nanos  0  : 1616623610054999874
millis 0  : 1616623610055
nanos  1  : 1616623610054999926
nanos  2  : 1616623610054999953
millis 1  : 1616623610055
nano cost : 27
diff      : 74
-------------------------------------------------
```

```java
=================================================
Smallest: 0.0
Largest : 74.0
=================================================
```

### Contribute
Send an email to `opensource{at}momomo.com` if you would like to contribute in any way, make changes or otherwise have thoughts and/or ideas on things to improve.
