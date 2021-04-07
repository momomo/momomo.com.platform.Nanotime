<!---
-->

##### Allows for nanosecond time resolution when asking for time from Java Runtime in contrast with System.currentTimeMillis().

##### Maven dependencies available on maven central [search.maven.org](https://search.maven.org/search?q=com.momomo)
##### Dependency   
```xml
<dependency>
  <groupId>com.momomo</groupId>
  <artifactId>momomo.com.platform.Nanotime</artifactId>
  <version>1.4.2</version>
</dependency>                                                      
```                         
##### Repository
```xml
<repository>
    <id>maven-central</id>
    <url>http://repo1.maven.org/maven2</url>
</repository>
```

##### Our significant repositories

* **[`momomo.com.platform.Core`](https://github.com/momomo/momomo.com.platform.Core)**  
Is essentially what makes the our the core of several of momomo.com's public releases and contains a bunch of Java utility.

* **[`momomo.com.platform.Lambda`](https://github.com/momomo/momomo.com.platform.Lambda)**  
Contains a bunch of `functional interfaces` similar to `Runnable`, `Supplier`, `Function`, `BiFunction`, `Consumer` `...` and so forth all packed in a easily accessed and understood intuitive pattern that are used plenty in our libraries. **`Lambda.V1E`**, **`Lambda.V2E`**, **`Lambda.R1E`**, **`Lambda.R2E`**, ...

* **[`momomo.com.platform.Return`](https://github.com/momomo/momomo.com.platform.Return)**  
An intuitive library that allows you to return multiple return values with defined types on the fly from any method rather than being limited to the default maximum of one.

* **[`momomo.com.platform.db.transactional.Hibernate`](https://github.com/momomo/momomo.com.platform.db.transactional.Hibernate)**  
A library to execute database command in transactions without having to use annotations based on Hibernate libraries. No Spring!

### Background

First, know that `System.nanoTime()` is elapsed nanos since an arbitrary origin, usually *the start of the JVM* and can usually only be used to measure elapsed time between two invocations. 

What this implementation does is provide you with a way of getting higher precision when asking for the time, with *nanosecond* precision.

Normally, you can get the time from your system using `System.currentTimeMillis(`) with millisecond precision but when invoked twice right after each other, calls 
to `System.currentTimeMillis()` will usually return the same value.

This library provides you with nanosecond precision similar to `System.currentTimeMillis()` by calibrating `System.nanoTime()` with `System.currentTimeMillis()`.  

When calibrating the two, our code will:  
   1. Ask `System.currentTimeMillis()` as quickly as we can, until we detect the `1ms` flip.
   2. Once detected, we ask `System.nanoTime()` what time it has.   
   3. Record the difference between the two. 
   4. Repeat this process `100` times (cheap operation), which tests have found is reasonably. 
   5. Also, we subtract the cost of operation `System.nanoTime()` slightly past `System.currentTimeMillis()` which is usually around `30ns` but we calculate once we detect a flip.

The total time for the calibration for 100 times, is as you guessed it, around `100ms` since we are waiting for 100 flips to occur.

### Getting started

There's basically only one class, `Nanotime.java` containing the implementation of our concept but we've provided another one due to API call looking better through `Nano` since `Nanotime` methods are not static.
 
 * [Nano.java](src/momomo/com/Nano.java)   
    A class utilizing what should only ever be, one instance of `Nanotime`.
    
    We have the following static methods currently
    ```java
    Nano.time()       : long                      :  
    Nano.timestamp()  : java.sql.Timestamp        : toString() -> 2021-03-25 22:15:28.986068681 
    Nano.datetime()   : java.time.LocalDateTime   : toString() -> 2021-03-25T21:15:28.989876426 
    Nano.localtime()  : java.time.LocalTime       : toString() -> 21:18:34.260363177 
    Nano.instant()    : java.time.Instant         : toString() -> 2021-03-25T21:18:49.431440982Z
    Nano.zonedtime()  : java.time.ZonedDateTime   : toString() -> 2021-03-25T21:18:49.434488996Z 
    Nano.offsettime() : java.time.OffsetDateTime  : toString() -> 2021-03-25T21:18:49.434622190Z
    ```   
   
    For all of these we will set up the relevant **nano bits** for you.   
       
 * [Nanotime.java](src/momomo/com/Nanotime.java)  
    Is the instance class with similarly named instance methods.
 
For normal use, you'd just call `Nano.time()`, `Nano.timestamp()`, `Nano.datetime()`, `Nano.localtime()`, `Nano.instant()` ... 

#### Thats' it!

### Configuration

To configure `Nanotime.java` just call `Nanotime.setInstance( new Nanotime() )` prior to any use of `Nano.time()`. You can also create your own instance that can be accessed separately. 

### How accurate is this?  
* Is this a *100% accurate* record of current time in nanos?   
No, but is there even *such a definition*? What is time? Time always have a reference point. Even atomic clocks do not give a 100% accurate definition of time at any given moment.
 
* Can two machines that make use of this reliably record time of invocation and could a third party reliably tell which came first?   
No, we can not state that either since each machine will generate a different set of calibrated values against it's own `System.currentTimeMillis()`. There will be slight variances. But given that two machines could synchronize their time and reference point, it is possible we could say an invocation occurred before the other using the generated timestamp.

Measuring the *error size* is possible but very difficult since:
 
   1. We can not issue both commands at the exact same time, but only one after the other.
   
   2. A call to `System.nanoTime()` followed by a call to `System.currentTimeMillis()` followed by a call to `System.nanoTime()` might at times take `30ns` between each and at times a wopping `0.4ms`. The JVM sometimes generates big diffs between these calls and when we compare their numbers, the difference might be very large between two calls despite the min being as close as `30ns`.
     
   3. `System.currentTimeMillis()` is not reliable to compare to in the first place as it might report a millisecond switch `±0.1ms` off. 
       
   4. Acccuracy depends much on your computers ability to calibrate better. A slow computer is likely to yield less accurate results.
   
##### Question

If we plot `System.nanoTime()` over time, will we get a `100%` perfectly linear graph? If we do so for `System.currentTimeMillis()` do we as well?

No, because `System.currentTimeMillis()` is not linear, nor consistent in reporting time on time, which is to be expected as `System.currentTimeMillis()` can not be 100% consistent against `System.nanoTime()` where it would flip a `ms` on the exact end of a `ms` on the `1000 000ns` because it is only millisecond precision. It **can not time** a `nanosecond` switch that precisely.      

***It should be highlighted that once calibrated our reference point stays constant, always remains the same, and never changes.***

The `Java API` has some info on the accuracy of **[`System.currentTimeMillis()`](https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/lang/System.html#currentTimeMillis())** where we find:  

> Note that while the unit of time of the return value is a millisecond, the **granularity** of the value depends on the underlying operating system and may be larger. For example, many operating systems measure time in units of **tens of milliseconds**.

That means an error size of up to `100 0000ns * 0.1 = 100 000ns` which means a millisecond might be reported earlier or later of up to `0.1 milliseconds` off. 

This is what we've noticed in our generated data as well but rarely to those extremes but when we generate 100 million data points we could see larger discrepancies occur!

On the contrary a call to `System.nanoTime()` is very expensive at time up to `0.4ms = 400000ns` and other times only about `~30ns`. So to think you can measure the performance of two invocations using `System.nanoTime()` reliably is also wrong as the cost of the second call might actually get you a very delayed answer.  

### Final comments
  
For us, what is most important *is not* being as close to any `System.currentTimeMillis()` as possible, which is not an exact science anyway, but to get close enough and should only be seen as a higher precision version of the existing `System.currentTimeMillis()` as `System.currentTimeMillis()` will often prove useless when invoked tightly, while `System.nanoTime()` will almost always show a diff and now so will `Nano.time()`.  

Our code just synchronizes the two and allows you to map `System.nanoTime()` to one based on a sane and constant reference frame rather than the randomness of when the JVM turned on. 

### Sample run and results    

A sample test run on **our example code within [`Nanotime`](src/momomo/com/platform/sources/Nanotime.java)** will output the following which also shows the **rounding** of `System.currentTimeMillis()` **fits extremely well** within bounds.

You can **[view or download the 100 000 rows of output here](https://github.com/momomo/momomo.com.github.statics/blob/master/momomo.com.platform.Nanotime/generated/output.txt?raw=true)**. Just scroll through it and ***try to detect and expect*** the flips to occur. 

Some *random highlights* from that file:

```java
index  : 599
nanos  : 1616615287358999285
millis : 1616615287358
nanos  : 1616615287358999394
nanos  : 1616615287358999450
millis : 1616615287358


index  : 600
nanos  : 1616615287358999611
millis : 1616615287358          <-----
nanos  : 1616615287358999713
nanos  : 1616615287358999762
millis : 1616615287359


index  : 601
nanos  : 1616615287358999926
millis : 1616615287359          <-----
nanos  : 1616615287359000035
nanos  : 1616615287359000090
millis : 1616615287359


index  : 602
nanos  : 1616615287359000251
millis : 1616615287359
nanos  : 1616615287359000355
nanos  : 1616615287359000405
millis : 1616615287359           


...


index  : 72680
nanos  : 1616615287381999301
millis : 1616615287381
nanos  : 1616615287381999363
nanos  : 1616615287381999396
millis : 1616615287381


index  : 72681
nanos  : 1616615287381999464
millis : 1616615287381          <-----
nanos  : 1616615287381999527
nanos  : 1616615287382000411
millis : 1616615287382


index  : 72682
nanos  : 1616615287382000471
millis : 1616615287382          <-----
nanos  : 1616615287382000521
nanos  : 1616615287382000548
millis : 1616615287382


index  : 72683
nanos  : 1616615287382000605
millis : 1616615287382
nanos  : 1616615287382000654
nanos  : 1616615287382000681
millis : 1616615287382
```
 
Here are some highlights from a different smaller sample run that analyzes data similar found in the file where we calculate min and max diffs from what we expect at switches. Calculation is not perfect and actually quite complex to get right.   


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