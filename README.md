<!---
-->

## momomo.com.platform.Nanotime

###### Allows for nanosecond time resolution when asking for time from Java Runtime in contrast with System.currentTimeMillis().`.

##### Maven dependencies available on maven central [search.maven.org](https://search.maven.org/search?q=com.momomo)
##### Dependency   
```xml
<dependency>
  <groupId>com.momomo</groupId>
  <artifactId>momomo.com.platform.Nanotime</artifactId>
  <version>1.3.8</version>
</dependency>                                                      
```                         
##### Repository
```xml
<repository>
    <id>maven-central</id>
    <url>http://repo1.maven.org/maven2</url>
</repository>
```

First, know that `System.nanoTime()` is elapsed nanos since an arbitrary origin, usually *the start of the JVM* and can usually only be used to measure elapsed time between two invocations. 

What this implementation does is allow you to get a higher precision when asking for the time, with *nanosecond* precision.

Normally, you can get the time from your system using `System.currentTimeMillis(`) with millisecond precision but when invoked twice right after each other, calls to `System.currentTimeMillis()` will usually return the same value. 

This provides you with nanosecond precision for a method similar to `System.currentTimeMillis()` by essentially calibrating `System.nanoTime()` which records nanos elapsed since JVM started with `System.currentTimeMillis()`.  

When calibrating the two, our code will:  
   1. Ask `System.currentTimeMillis()` right after asking `System.nanoTime()`, in a one liner. 
   2. We will record the difference between the two. 
   3. `Sleep` a random amount of nano seconds. 
   4. Repeat this process 1000 times.

We then take the average recorded difference and use this average to go from `System.nanoTime()` to a `System.currentTimeMillis()` by subtracting the average as a calculated `DIFF`. 

##### Is this a *100% accurate* record of current time in nanos? 
* Is there even **such a definition**? What is time?   
Even atomic clocks do not give a 100% accurate definition of time at any given moment.  

When we ask for `Time.nano()` we can expect some discrepancy, as even the cost of calling `System.nanoTime()` has a cost and in reality only represent a time in the past once given access to it.

Rather it a higher precision one than `System.currentTimeMillis()` as `System.currentTimeMillis()` will often prove useless when invoked tightly, while `System.nanoTime()` will show always show a diff, and so will `Nano.time()`. 

Our code just calibrates the two and allows you to map `System.nanoTime()` to one based on a sane and constant reference frame usually to when baby Jesus was born, rather than when the JVM turned on.

##### Recalibration 

Note, recalibration by default is turned off, but you may pass a value of your choice to trigger a recalibration how often you'd like using `Nano.setInstance( new Nanotime(...) )`, but there is *nothing to suggest* a recalibration is required unless the *underlying system specification* differs drastically during runtime in where two calls to `System.nanoTime()` will diverge. 

Recalibration also introduces complex requirements regarding when to start using the newly calibrated value so to ensure a proper behaviour we've decided to turn off calibration every to ensure we stay within proper bounds and give a constant reference frame of time once established1.


##### Our other repositories

* [momomo.com.platform.Lambda](https://github.com/momomo/momomo.com.platform.Lambda)  
Contains a bunch of `functional interfaces` similar to `Runnable`, `Supplier`, `Function`, `BiFunction`, `Consumer` `...` and so forth all packed in a easily accessed and understood intuitive pattern.  
`Lambda.V1E`, `Lambda.V2E`, `Lambda.R1E`, `Lambda.R2E` are used plenty in examples below.

* [momomo.com.platform.Core](https://github.com/momomo/momomo.com.platform.Lambda)  
Is essentially what makes the Core of several of momomo.com's public releases and contains a bunch of `Java` utility. 


### Guide

There's basically only one class, `Nanotime.java`, but we've provide another one due to API call looking better through `Nano.time()` since `Nanotime.get()` is not a static method. 
 * [Nano.java](src/momomo/com/Nano.java) 
    This is just a class utilizing what should only ever be, one instance of Nanotime. 
 * [Nanotime.java](src/momomo/com/Nanotime.java)
 
For normal use, you'd just call `Nano.time()`. Thats' it. 

To configure `Nanotime.java` just call `Nanotime.setInstance( new Nanotime() )` prior to any use of `Nano.time()`. You can also create your own instance version that is accessed separately.

### Sample run and results    

A sample test run on our example() code within will output the following, which also shows the **rounding** of `System.currentTimeMillis` **fits extremely well** within bounds.

```java
public static void main(String[] args) throws InterruptedException {
    int i = -1; while (++i < 10000) {
        System.out.println(
            "nanos : " + Nano.time()                + "\n" +
            "millis: " + System.currentTimeMillis() + "\n"  
        );
    }
}
```                         

##### Output

```java
nanos : 1615923349193929741
millis: 1615923349194

nanos : 1615923349193934170
millis: 1615923349194

nanos : 1615923349193938359
millis: 1615923349194

nanos : 1615923349193942637
millis: 1615923349194

nanos : 1615923349193947203
millis: 1615923349194

nanos : 1615923349193951495
millis: 1615923349194

nanos : 1615923349193955582
millis: 1615923349194

nanos : 1615923349193959922
millis: 1615923349194

nanos : 1615923349193964834
millis: 1615923349194

nanos : 1615923349193969360
millis: 1615923349194

nanos : 1615923349193973703
millis: 1615923349194

nanos : 1615923349193977802
millis: 1615923349194

nanos : 1615923349193981898
millis: 1615923349194

nanos : 1615923349193985985
millis: 1615923349194

nanos : 1615923349193997998
millis: 1615923349194

nanos : 1615923349194002532
millis: 1615923349194

nanos : 1615923349194006594
millis: 1615923349194

nanos : 1615923349194010553
millis: 1615923349194

nanos : 1615923349194014653
millis: 1615923349194

nanos : 1615923349194018949
millis: 1615923349194

nanos : 1615923349194023036
millis: 1615923349194

nanos : 1615923349194026981
millis: 1615923349194

nanos : 1615923349194030927
millis: 1615923349194

nanos : 1615923349194034796
millis: 1615923349194

nanos : 1615923349194038682
millis: 1615923349194

nanos : 1615923349194042481
millis: 1615923349194

nanos : 1615923349194046424
millis: 1615923349194

nanos : 1615923349194053093
millis: 1615923349194

nanos : 1615923349194057724
millis: 1615923349194

nanos : 1615923349194066775
millis: 1615923349194

nanos : 1615923349194070955
millis: 1615923349194

nanos : 1615923349194074774
millis: 1615923349194

nanos : 1615923349194078624
millis: 1615923349194

nanos : 1615923349194082430
millis: 1615923349194

nanos : 1615923349194086182
millis: 1615923349194

nanos : 1615923349194089974
millis: 1615923349194

nanos : 1615923349194093682
millis: 1615923349194

nanos : 1615923349194097517
millis: 1615923349194

nanos : 1615923349194101248
millis: 1615923349194

nanos : 1615923349194104905
millis: 1615923349194

nanos : 1615923349194108519
millis: 1615923349194

nanos : 1615923349194112192
millis: 1615923349194

nanos : 1615923349194115964
millis: 1615923349194

nanos : 1615923349194119628
millis: 1615923349194

nanos : 1615923349194123365
millis: 1615923349194

nanos : 1615923349194127011
millis: 1615923349194

nanos : 1615923349194130613
millis: 1615923349194

nanos : 1615923349194134200
millis: 1615923349194

nanos : 1615923349194141768
millis: 1615923349194

nanos : 1615923349194145892
millis: 1615923349194

nanos : 1615923349194149433
millis: 1615923349194

nanos : 1615923349194153161
millis: 1615923349194

nanos : 1615923349194156589
millis: 1615923349194

nanos : 1615923349194162622
millis: 1615923349194

nanos : 1615923349194168581
millis: 1615923349194

nanos : 1615923349194172236
millis: 1615923349194

nanos : 1615923349194175626
millis: 1615923349194

nanos : 1615923349194179389
millis: 1615923349194

nanos : 1615923349194182917
millis: 1615923349194

nanos : 1615923349194186372
millis: 1615923349194

nanos : 1615923349194189967
millis: 1615923349194

nanos : 1615923349194193367
millis: 1615923349194

nanos : 1615923349194196832
millis: 1615923349194

nanos : 1615923349194200237
millis: 1615923349194

nanos : 1615923349194203702
millis: 1615923349194

nanos : 1615923349194207009
millis: 1615923349194

nanos : 1615923349194210414
millis: 1615923349194

nanos : 1615923349194213719
millis: 1615923349194

nanos : 1615923349194217193
millis: 1615923349194

nanos : 1615923349194220550
millis: 1615923349194

nanos : 1615923349194230263
millis: 1615923349194

nanos : 1615923349194234302
millis: 1615923349194

nanos : 1615923349194237753
millis: 1615923349194

nanos : 1615923349194241103
millis: 1615923349194

nanos : 1615923349194244411
millis: 1615923349194

nanos : 1615923349194248085
millis: 1615923349194

nanos : 1615923349194251494
millis: 1615923349194

nanos : 1615923349194254901
millis: 1615923349194

nanos : 1615923349194258334
millis: 1615923349194

nanos : 1615923349194261686
millis: 1615923349194

nanos : 1615923349194265102
millis: 1615923349194

nanos : 1615923349194270140
millis: 1615923349194

nanos : 1615923349194273647
millis: 1615923349194

nanos : 1615923349194276954
millis: 1615923349194

nanos : 1615923349194280302
millis: 1615923349194

nanos : 1615923349194283599
millis: 1615923349194

nanos : 1615923349194286869
millis: 1615923349194

nanos : 1615923349194290073
millis: 1615923349194

nanos : 1615923349194293356
millis: 1615923349194

nanos : 1615923349194296697
millis: 1615923349194

nanos : 1615923349194300223
millis: 1615923349194

nanos : 1615923349194303511
millis: 1615923349194

nanos : 1615923349194306848
millis: 1615923349194

nanos : 1615923349194310156
millis: 1615923349194

nanos : 1615923349194313698
millis: 1615923349194

nanos : 1615923349194321384
millis: 1615923349194

nanos : 1615923349194325691
millis: 1615923349194

nanos : 1615923349194329315
millis: 1615923349194

nanos : 1615923349194332879
millis: 1615923349194

nanos : 1615923349194430337
millis: 1615923349194

nanos : 1615923349194442848
millis: 1615923349194

nanos : 1615923349194459099
millis: 1615923349194

nanos : 1615923349194463835
millis: 1615923349194

nanos : 1615923349194467404
millis: 1615923349194

nanos : 1615923349194470742
millis: 1615923349194

nanos : 1615923349194473903
millis: 1615923349194

nanos : 1615923349194477105
millis: 1615923349194

nanos : 1615923349194480254
millis: 1615923349194

nanos : 1615923349194485776
millis: 1615923349194

nanos : 1615923349194489077
millis: 1615923349194

nanos : 1615923349194492230
millis: 1615923349194

nanos : 1615923349194495482
millis: 1615923349194

nanos : 1615923349194498592
millis: 1615923349194

nanos : 1615923349194501727
millis: 1615923349195

nanos : 1615923349194507327
millis: 1615923349195

nanos : 1615923349194510682
millis: 1615923349195

nanos : 1615923349194513826
millis: 1615923349195

nanos : 1615923349194516978
millis: 1615923349195

nanos : 1615923349194520035
millis: 1615923349195

nanos : 1615923349194523269
millis: 1615923349195

nanos : 1615923349194526284
millis: 1615923349195

nanos : 1615923349194529279
millis: 1615923349195

nanos : 1615923349194532327
millis: 1615923349195

nanos : 1615923349194535289
millis: 1615923349195

nanos : 1615923349194538170
millis: 1615923349195

nanos : 1615923349194541206
millis: 1615923349195

nanos : 1615923349194545420
millis: 1615923349195

nanos : 1615923349194548475
millis: 1615923349195

nanos : 1615923349194551827
millis: 1615923349195

nanos : 1615923349194554858
millis: 1615923349195

nanos : 1615923349194557828
millis: 1615923349195

nanos : 1615923349194560675
millis: 1615923349195

nanos : 1615923349194563661
millis: 1615923349195

nanos : 1615923349194566741
millis: 1615923349195

nanos : 1615923349194569751
millis: 1615923349195

nanos : 1615923349194574834
millis: 1615923349195

nanos : 1615923349194578005
millis: 1615923349195

nanos : 1615923349194581069
millis: 1615923349195

nanos : 1615923349194584748
millis: 1615923349195

nanos : 1615923349194589969
millis: 1615923349195

nanos : 1615923349194593559
millis: 1615923349195

nanos : 1615923349194596462
millis: 1615923349195

nanos : 1615923349194599484
millis: 1615923349195

nanos : 1615923349194602443
millis: 1615923349195

nanos : 1615923349194605288
millis: 1615923349195

nanos : 1615923349194608029
millis: 1615923349195

nanos : 1615923349194610894
millis: 1615923349195

nanos : 1615923349194613785
millis: 1615923349195

nanos : 1615923349194617042
millis: 1615923349195

nanos : 1615923349194620452
millis: 1615923349195

nanos : 1615923349194623519
millis: 1615923349195

nanos : 1615923349194627354
millis: 1615923349195
```

The thing to watch for is the rounding of nanos to millis, and when a rounding should occur.   
Look at the switch from `1615923349194` to `1615923349195` we've repeated and commented below: 

```java
nanos : 1615923349194492230
millis: 1615923349194

nanos : 1615923349194495482
millis: 1615923349194
                     
nanos : 1615923349194498592     // ..944. We are getting close to an expected 95, will it come?
millis: 1615923349194           // ..94

nanos : 1615923349194501727     // ..945. 945 when rounding should become. You guessed it. 95. 
millis: 1615923349195           // ..95

nanos : 1615923349194507327
millis: 1615923349195
```

#### Contribute
Send an email to `opensource{at}momomo.com` if you would like to contribute in any way, make changes or otherwise have thoughts and/or ideas on things to improve.