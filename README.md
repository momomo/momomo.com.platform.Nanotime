<!---
-->

## momomo.com.platform.Nanotime

###### Allows for nanosecond time resolution when asking for time from Java Runtime in contrast with System.currentTimeMillis().

##### Maven dependencies available on maven central [search.maven.org](https://search.maven.org/search?q=com.momomo)
##### Dependency   
```xml
<dependency>
  <groupId>com.momomo</groupId>
  <artifactId>momomo.com.platform.Nanotime</artifactId>
  <version>1.3.9</version>
</dependency>                                                      
```                         
##### Repository
```xml
<repository>
    <id>maven-central</id>
    <url>http://repo1.maven.org/maven2</url>
</repository>
```

##### Our other repositories

* [`momomo.com.platform.Core`](https://github.com/momomo/momomo.com.platform.Lambda)  
Is essentially what makes the our the core of several of momomo.com's public releases and contains a bunch of `Java` utility.

* [`momomo.com.platform.Lambda`](https://github.com/momomo/momomo.com.platform.Lambda)  
Contains a bunch of `functional interfaces` similar to `Runnable`, `Supplier`, `Function`, `BiFunction`, `Consumer` `...` and so forth all packed in a easily accessed 
and understood intuitive pattern.  
`Lambda.V1E`, `Lambda.V2E`, `Lambda.R1E`, `Lambda.R2E` are used plenty in examples below.

* [`momomo.com.platform.Return`](https://github.com/momomo/momomo.com.platform.Return)  
An intuitive library that allows you to return multiple return values with defined types on the fly from any method rather than being limited to the default maximum of one.

* [`momomo.com.platform.db.transactional.Hibernate`](https://github.com/momomo/momomo.com.platform.db.transactional.Hibernate)  
A library to execute database command in transactions without having to use annotations based on Hibernate libraries. No Spring!

### Background

First, know that `System.nanoTime()` is elapsed nanos since an arbitrary origin, usually *the start of the JVM* and can usually only be used to measure elapsed time 
between two invocations. 

What this implementation does is provide you with a way of getting higher precision when asking for the time, with *nanosecond* precision.

Normally, you can get the time from your system using `System.currentTimeMillis(`) with millisecond precision but when invoked twice right after each other, calls 
to `System.currentTimeMillis()` will usually return the same value.

This library provides you with nanosecond precision similar to `System.currentTimeMillis()` by calibrating `System.nanoTime()` with `System.currentTimeMillis()`.  

When calibrating the two, our code will:  
   1. Ask `System.currentTimeMillis()` as quickly as we can, until we detect a the `1ms` flip.
   2. Once detected, we ask `System.nanoTime()` what time it has.   
   3. Record the difference between the two. 
   4. Repeat this process `100` times (cheap operation), which tests have found is reasonably. 
   5. Also, we subtract the cost of operation `System.nanoTime()` slightly past `System.currentTimeMillis()` which is usually around `30ns` but we calculate once we detect a flip.

The total time for the calibration for 100 times, is as you guessed it, around `100ms` since we are waiting for 100 flips to occur.

##### Is this a *100% accurate* record of current time in nanos? 
* No, but is there even **such a definition**? What is time? Time always have a reference point. Even atomic clocks do not give a 100% accurate definition of time at any given moment.

Measuring the error size is possible but very difficult. 
   1. It is hard to measure both since we can not issue both commands at the exact same time, but only one after the other.
   2. A call to `System.nanoTime()` followed by a call to `System.currentTimeMillis()`, followed by a call to `System.nanoTime()` might at times take `30ns` between each, and at times 0.4ms. The JVM sometimes generates big diffs between calls at times, and when we compare the numbers the difference might be very large between two calls despite the min being as close as `30ns`.  
   3. `System.currentTimeMillis()` is not reliable to compare to in the first place. See proofs below.  
   4. Acccuracy depends much on your computers ability to calibrate better. A slow computer is likely to yield worse results.
   
Likely we would need a better clock to use to compare it to than `System.currenTimeMillis()`. Read more on    
   
#### Proving number 2

Take this as an example, where `Nano.time()` is called first, with `System.currentTimeMillis()` directly after, and then `Nano.time()` again once more after.

```java
// Can you do help us do this in a quicker way? 
array[i] = new Long[]{ Nano.time(), System.currentTimeMillis(), Nano.time() };
``` 

```java
nanos  0  : 161661301453 6997599
millis 0  : 161661301453 7
nanos  1  : 161661301453 7003893
```
                                                                                                                                                                  
This would show an error size of `2401ns` and `3893ns`. Yet what we can see is that we are both before and after and that the cost of calling `Nano.time()` from the preceeding `System.currentTimeMillis()` is a bit too large for us to make any real determination, but when we look at `all three`, we can see we are in the money.

But the diff between the calls are `~3000ns` at times. And sometimes much much more! 

#### Proving number 3

When we repeat the above code enough times, say a `1000 000` times, we can calculate the `largest diff` and the `smallest diff` around a switch. We look for the last `ms` and when it changes we compare `millis 0` currently to the next `Nano.time()` call `nanos 1`, not the previous one `nanos 0`.     

What we've seen is that we can get **smallest diff** down to `0ns` or `1ns` given enough iterations. **Zero**. 

The **largest diff** as we already showed in *Proving number 2* is not reliable to use for making a determination. 

Here is two other examples genereated from the **exact** same test run of a `1000 000` generated. We've added the first `100 000 numbers` as a file for you to review (15mb).    

```java
--------------------- SMALL --------------------
index     : 68095
nanos  0  : 161661528738 0999951
millis 0  : 161661528738 1
nanos  1  : 161661528738 1000000
diff      : 0
------------------------------------------------
```

From `0999951` to `1000000` is only `49ns` right? We were at `380`, and then we got to `381000000` blank. All within `49ns` ns, correct?  

Now, let us look at **the largest** with the previous row added right before to see where we were before. 

Again, ** this is the same iteration!** 

`System.nanoTime()` minus constant `DIFF` that we've calculated once and is `final`. We should get a linear behaviour!    

```java                    
--------------------- LARGE --------------------
index    : 93413
nanos  0 : 161661528738 5999263
millis 0 : 161661528738 5
nanos  1 : 161661528738 5999363
diff     : -

index    : 93414
nanos  0 : 161661528738 5999568
millis 0 : 161661528738 6
nanos  1 : 161661528738 5999663
diff     : 432 
------------------------------------------------
```                            

First on `index 93413` we can see all at `38 5` still. Then on `index 93414` we see that at least `568ns - 363ns = 205ns` has elapsed. `millis 0` has flipped to `38 6`, and when we `Nano.time()` right after we are still at `85`. Remember, before we we able to creep up to to the ms down to `49ns`, and so when we we now move `663ns - 568ns = 95ns` we should have expected at least a flip on `nano 1`. 
 
If we got the error size down to max `49ns` before how could `95ns` elapse and get a flip of the `millis`? 

Question: If we plot `System.nanoTime()` over time, will we get a `100%` perfectly linear graph? If we do so for `System.currentTimeMillis()`, do we as well? One of the two is not 100% `linear`. Thats for sure! 

In truth, `System.currentTimeMillis()` can not be consistent against `System.nanoTime()` and will not flip a `ms` on the exact `1000 00`0ns because it is only millisecond precision and **we can not expect it to time** a `nanosecond` precisely.     

It should be highlighted that once calibrated our reference point stays constant, always remains the same, and never changes. That means you have the ability to issue `System.nanoTime()` yet have it refer to a time ***very very*** close to `System.currentTimeMillis()`.


##### Java API actually has info on the accuracy so our proofs where not required 

In fact we can read in the Java API of the accuracy of [System.currentTimeMillis()](https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/lang/System.html#currentTimeMillis()) where we find: 

> Note that while the unit of time of the return value is a millisecond, the **granularity** of the value depends on the underlying operating system and may be larger. For example, many operating systems measure time in units of **tens of milliseconds**.

That means an error size of up to `100 0000ns * 0.1 = 100 000ns` which means a millisecond might be reported earlier or later of up to `0.1 milliseconds` off. 

This is what we've noticed in our generated data as well but rarely to those extremes but when we generate 100 million data points we could see larger discrepancies occur!

##### Final comments
  
For us, what is most important is not being as close to any `System.currentTimeMillis()` as possible, always but just to get close enough to an average of them, and do note, an average is better than being close to one due to the error margin inherit in `System.currentTimeMillis()` already discussed.

It should only be seen as a higher precision version of `System.currentTimeMillis()` as `System.currentTimeMillis()` will often prove useless when invoked tightly, while `System.nanoTime()` will show always show a diff and now so will `Nano.time()`.  

Our code just synchronizes the two and allows you to map `System.nanoTime()` to one based on a sane and constant reference frame rather than the randomness of when the JVM turned on.

### Getting started

There's basically only one class, `Nanotime.java`, but we've provide another one due to API call looking better through `Nano.time()` since `Nanotime.get()` is not a static method. 
 * [Nano.java](src/momomo/com/Nano.java) 
    This is just a class utilizing what should only ever be, one instance of Nanotime. 
 * [Nanotime.java](src/momomo/com/Nanotime.java)
 
For normal use, you'd just call `Nano.time()`. Thats' it!

To configure `Nanotime.java` just call `Nanotime.setInstance( new Nanotime() )` prior to any use of `Nano.time()`. You can also create your own instance version ti be accessed separately.

#### Sample run and results    

A sample test run on our example code within will output the following, which also shows the **rounding** of `System.currentTimeMillis()` **fits extremely well** within bounds.

You can [view or download the 100 000 rows of output here](https://github.com/momomo/momomo.com.github.statics/blob/master/momomo.com.platform.Nanotime/generated/output.txt?raw=true). Just scroll through it and try to detect and expect the flips to occur.  

Here are some highlights from a different smaller sample run that analyzes the similar data found in that file 

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
--------------------------------------------------

---------------------- SMALL --------------------
index     : 41948
nanos  0  : 1616623610044999909
millis 0  : 1616623610045
nanos  1  : 1616623610045000000
nanos  2  : 1616623610045000047
millis 1  : 1616623610045
nano cost : 47
diff      : 0
--------------------------------------------------

---------------------- LARGE --------------------
index     : 74341
nanos  0  : 1616623610053999918
millis 0  : 1616623610054
nanos  1  : 1616623610053999969
nanos  2  : 1616623610053999996
millis 1  : 1616623610054
nano cost : 27
diff      : 31
--------------------------------------------------

---------------------- LARGE --------------------
index     : 79042
nanos  0  : 1616623610054999874
millis 0  : 1616623610055
nanos  1  : 1616623610054999926
nanos  2  : 1616623610054999953
millis 1  : 1616623610055
nano cost : 27
diff      : 74
--------------------------------------------------
```

```java
=========================================
Smallest: 0.0
Largest : 74.0
=========================================
```

### Contribute
Send an email to `opensource{at}momomo.com` if you would like to contribute in any way, make changes or otherwise have thoughts and/or ideas on things to improve.