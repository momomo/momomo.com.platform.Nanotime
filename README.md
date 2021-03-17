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

##### Our other repositories

* [`momomo.com.platform.Core`](https://github.com/momomo/momomo.com.platform.Lambda)  
Is essentially what makes the our the core of several of momomo.com's public releases and contains a bunch of `Java` utility.

* [`momomo.com.platform.Lambda`](https://github.com/momomo/momomo.com.platform.Lambda)  
Contains a bunch of `functional interfaces` similar to `Runnable`, `Supplier`, `Function`, `BiFunction`, `Consumer` `...` and so forth all packed in a easily accessed 
and understood intuitive pattern.  
`Lambda.V1E`, `Lambda.V2E`, `Lambda.R1E`, `Lambda.R2E` are used plenty in examples below.

### Info

First, know that `System.nanoTime()` is elapsed nanos since an arbitrary origin, usually *the start of the JVM* and can usually only be used to measure elapsed time 
between two invocations. 

What this implementation does is provide you with a way of getting higher precision when asking for the time, with *nanosecond* precision.

Normally, you can get the time from your system using `System.currentTimeMillis(`) with millisecond precision but when invoked twice right after each other, calls 
to `System.currentTimeMillis()` will usually return the same value.

This library provides you with nanosecond precision similar to `System.currentTimeMillis()` by essentially calibrating `System.nanoTime()` which records nanos 
elapsed since JVM started with `System.currentTimeMillis()`.  

When calibrating the two, our code will:  
   1. Ask `System.currentTimeMillis()` right after asking `System.nanoTime()`, in a one liner. 
   2. We will record the difference between the two. 
   3. `Sleep` a random amount of nano seconds. 
   4. Repeat this process 1000 times (cheap operation).

The reason we repeast is that two calls two `System.nanoTime()` will never return a constant diff. 
So we take the average recorded difference and use this average to go from `System.nanoTime()` to a `System.currentTimeMillis()` by subtracting the average as a calculated `DIFF`. 

##### Is this a *100% accurate* record of current time in nanos? 
* Is there even **such a definition**? What is time?   
Even atomic clocks do not give a 100% accurate definition of time at any given moment.   
If recalibration is off, we gurantee you that two calls to `Time.nano()` will always return a diff equivalent to two calls to `System.nanoTime()` and always stay 
100% linearly proportional to `System.nanoTime()`. 
That means an size of the error, similar to the error in an atomic clock will always remain constant to the size of error `System.nanoTime()` over time.  

It should be seen as a higher precision version of `System.currentTimeMillis()` as `System.currentTimeMillis()` will often prove useless when invoked tightly, 
while `System.nanoTime()` will show always show a diff, and so will `Nano.time()`.  

Our code just calibrates the two and allows you to map `System.nanoTime()` to one based on a sane and constant reference frame, usually to when baby Jesus was born 
rather than when the JVM turned on.

##### Recalibration 

Note, recalibration by default is turned off, but you may pass a value of your choice to trigger a recalibration how often you'd like 
using `Nano.setInstance( new Nanotime(...) )`, but there is *nothing to suggest* a recalibration is required unless the *underlying system specification* differs
 drastically during runtime in where two calls to `System.nanoTime()` will diverge. 

Recalibration also introduces complex requirements regarding when to start using the newly calibrated value so to ensure a proper behaviour we've decided to turn off 
calibration every to ensure we stay within proper bounds and give a constant reference frame of time once established.

### Usage

There's basically only one class, `Nanotime.java`, but we've provide another one due to API call looking better through `Nano.time()` since `Nanotime.get()` is not a static method. 
 * [Nano.java](src/momomo/com/Nano.java) 
    This is just a class utilizing what should only ever be, one instance of Nanotime. 
 * [Nanotime.java](src/momomo/com/Nanotime.java)
 
For normal use, you'd just call `Nano.time()`. Thats' it!

To configure `Nanotime.java` just call `Nanotime.setInstance( new Nanotime() )` prior to any use of `Nano.time()`. You can also create your own instance version ti be accessed separately.

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
// Output has been separated with a whitespace for readability.

nanos : 1615923349193 947203
millis: 1615923349194 
                      
nanos : 1615923349193 951495
millis: 1615923349194 
                      
nanos : 1615923349193 955582
millis: 1615923349194 
                      
nanos : 1615923349193 959922
millis: 1615923349194 
                      
nanos : 1615923349193 964834
millis: 1615923349194 
                      
nanos : 1615923349193 969360
millis: 1615923349194 
                      
nanos : 1615923349193 973703
millis: 1615923349194 
                      
nanos : 1615923349193 977802
millis: 1615923349194 
                      
nanos : 1615923349193 981898
millis: 1615923349194 
                      
nanos : 1615923349193 985985
millis: 1615923349194 
                      
nanos : 1615923349193 997998
millis: 1615923349194 
                      
nanos : 1615923349194 002532
millis: 1615923349194 
                      
nanos : 1615923349194 006594
millis: 1615923349194 
                      
nanos : 1615923349194 010553
millis: 1615923349194 
                      
nanos : 1615923349194 014653
millis: 1615923349194 
                      
nanos : 1615923349194 018949
millis: 1615923349194 
                      
nanos : 1615923349194 023036
millis: 1615923349194 
                      
nanos : 1615923349194 026981
millis: 1615923349194 
                      
nanos : 1615923349194 030927
millis: 1615923349194 
                      
nanos : 1615923349194 034796
millis: 1615923349194 
                      
nanos : 1615923349194 038682
millis: 1615923349194 
                      
nanos : 1615923349194 042481
millis: 1615923349194 
                      
nanos : 1615923349194 046424
millis: 1615923349194 
                      
nanos : 1615923349194 053093
millis: 1615923349194 
                      
nanos : 1615923349194 057724
millis: 1615923349194 
                      
nanos : 1615923349194 066775
millis: 1615923349194 
                      
nanos : 1615923349194 070955
millis: 1615923349194 
                      
nanos : 1615923349194 074774
millis: 1615923349194 
                      
nanos : 1615923349194 078624
millis: 1615923349194 
                      
nanos : 1615923349194 082430
millis: 1615923349194 
                      
nanos : 1615923349194 086182
millis: 1615923349194 
                      
nanos : 1615923349194 089974
millis: 1615923349194 
                      
nanos : 1615923349194 093682
millis: 1615923349194 
                      
nanos : 1615923349194 097517
millis: 1615923349194 
                      
nanos : 1615923349194 101248
millis: 1615923349194 
                      
nanos : 1615923349194 104905
millis: 1615923349194 
                      
nanos : 1615923349194 108519
millis: 1615923349194 
                      
nanos : 1615923349194 112192
millis: 1615923349194 
                      
nanos : 1615923349194 115964
millis: 1615923349194 
                      
nanos : 1615923349194 119628
millis: 1615923349194 
                      
nanos : 1615923349194 123365
millis: 1615923349194 
                      
nanos : 1615923349194 127011
millis: 1615923349194 
                      
nanos : 1615923349194 130613
millis: 1615923349194 
                      
nanos : 1615923349194 134200
millis: 1615923349194 
                      
nanos : 1615923349194 141768
millis: 1615923349194 
                      
nanos : 1615923349194 145892
millis: 1615923349194 
                      
nanos : 1615923349194 149433
millis: 1615923349194 
                      
nanos : 1615923349194 153161
millis: 1615923349194 
                      
nanos : 1615923349194 156589
millis: 1615923349194 
                      
nanos : 1615923349194 162622
millis: 1615923349194 
                      
nanos : 1615923349194 168581
millis: 1615923349194 
                      
nanos : 1615923349194 172236
millis: 1615923349194 
                      
nanos : 1615923349194 175626
millis: 1615923349194 
                      
nanos : 1615923349194 179389
millis: 1615923349194 
                      
nanos : 1615923349194 182917
millis: 1615923349194 
                      
nanos : 1615923349194 186372
millis: 1615923349194 
                      
nanos : 1615923349194 189967
millis: 1615923349194 
                      
nanos : 1615923349194 193367
millis: 1615923349194 
                      
nanos : 1615923349194 196832
millis: 1615923349194 
                      
nanos : 1615923349194 200237
millis: 1615923349194 
                      
nanos : 1615923349194 203702
millis: 1615923349194 
                      
nanos : 1615923349194 207009
millis: 1615923349194 
                      
nanos : 1615923349194 210414
millis: 1615923349194 
                      
nanos : 1615923349194 213719
millis: 1615923349194 
                      
nanos : 1615923349194 217193
millis: 1615923349194 
                      
nanos : 1615923349194 220550
millis: 1615923349194 
                      
nanos : 1615923349194 230263
millis: 1615923349194 
                      
nanos : 1615923349194 234302
millis: 1615923349194 
                      
nanos : 1615923349194 237753
millis: 1615923349194 
                      
nanos : 1615923349194 241103
millis: 1615923349194 
                      
nanos : 1615923349194 244411
millis: 1615923349194 
                      
nanos : 1615923349194 248085
millis: 1615923349194 
                      
nanos : 1615923349194 251494
millis: 1615923349194 
                      
nanos : 1615923349194 254901
millis: 1615923349194 
                      
nanos : 1615923349194 258334
millis: 1615923349194 
                      
nanos : 1615923349194 261686
millis: 1615923349194 
                      
nanos : 1615923349194 265102
millis: 1615923349194 
                      
nanos : 1615923349194 270140
millis: 1615923349194 
                      
nanos : 1615923349194 273647
millis: 1615923349194 
                      
nanos : 1615923349194 276954
millis: 1615923349194 
                      
nanos : 1615923349194 280302
millis: 1615923349194 
                      
nanos : 1615923349194 283599
millis: 1615923349194 
                      
nanos : 1615923349194 286869
millis: 1615923349194 
                      
nanos : 1615923349194 290073
millis: 1615923349194 
                      
nanos : 1615923349194 293356
millis: 1615923349194 
                      
nanos : 1615923349194 296697
millis: 1615923349194 
                      
nanos : 1615923349194 300223
millis: 1615923349194 
                      
nanos : 1615923349194 303511
millis: 1615923349194 
                      
nanos : 1615923349194 306848
millis: 1615923349194 
                      
nanos : 1615923349194 310156
millis: 1615923349194 
                      
nanos : 1615923349194 313698
millis: 1615923349194 
                      
nanos : 1615923349194 321384
millis: 1615923349194 
                      
nanos : 1615923349194 325691
millis: 1615923349194 
                      
nanos : 1615923349194 329315
millis: 1615923349194 
                      
nanos : 1615923349194 332879
millis: 1615923349194 
                      
nanos : 1615923349194 430337
millis: 1615923349194 
                      
nanos : 1615923349194 442848
millis: 1615923349194 
                      
nanos : 1615923349194 459099
millis: 1615923349194 
                      
nanos : 1615923349194 463835
millis: 1615923349194 
                      
nanos : 1615923349194 467404
millis: 1615923349194 
                      
nanos : 1615923349194 470742
millis: 1615923349194 
                      
nanos : 1615923349194 473903
millis: 1615923349194 
                      
nanos : 1615923349194 477105
millis: 1615923349194 
                      
nanos : 1615923349194 480254
millis: 1615923349194 
                      
nanos : 1615923349194 485776
millis: 1615923349194 
                      
nanos : 1615923349194 489077
millis: 1615923349194 
                      
nanos : 1615923349194 492230
millis: 1615923349194 
                      
nanos : 1615923349194 495482
millis: 1615923349194 
                      
nanos : 1615923349194 498592
millis: 1615923349194 
                      
nanos : 1615923349194 501727
millis: 1615923349195 
                      
nanos : 1615923349194 507327
millis: 1615923349195 
                      
nanos : 1615923349194 510682
millis: 1615923349195 
                      
nanos : 1615923349194 513826
millis: 1615923349195 
                      
nanos : 1615923349194 516978
millis: 1615923349195 
                      
nanos : 1615923349194 520035
millis: 1615923349195 
                      
nanos : 1615923349194 523269
millis: 1615923349195 
                      
nanos : 1615923349194 526284
millis: 1615923349195 
                      
nanos : 1615923349194 529279
millis: 1615923349195 
                      
nanos : 1615923349194 532327
millis: 1615923349195 
                      
nanos : 1615923349194 535289
millis: 1615923349195 
                      
nanos : 1615923349194 538170
millis: 1615923349195 
                      
nanos : 1615923349194 541206
millis: 1615923349195 
                      
nanos : 1615923349194 545420
millis: 1615923349195 
                      
nanos : 1615923349194 548475
millis: 1615923349195 
                      
nanos : 1615923349194 551827
millis: 1615923349195 
                      
nanos : 1615923349194 554858
millis: 1615923349195 
                      
nanos : 1615923349194 557828
millis: 1615923349195 
                      
nanos : 1615923349194 560675
millis: 1615923349195 
                      
nanos : 1615923349194 563661
millis: 1615923349195 
                      
nanos : 1615923349194 566741
millis: 1615923349195 
                      
nanos : 1615923349194 569751
millis: 1615923349195 
                      
nanos : 1615923349194 574834
millis: 1615923349195 
                      
nanos : 1615923349194 578005
millis: 1615923349195 
                      
nanos : 1615923349194 581069
millis: 1615923349195 
                      
nanos : 1615923349194 584748
millis: 1615923349195 
                      
nanos : 1615923349194 589969
millis: 1615923349195 
                      
nanos : 1615923349194 593559
millis: 1615923349195 
                      
nanos : 1615923349194 596462
millis: 1615923349195 
                      
nanos : 1615923349194 599484
millis: 1615923349195 
                      
nanos : 1615923349194 602443
millis: 1615923349195 
                      
nanos : 1615923349194 605288
millis: 1615923349195 
                      
nanos : 1615923349194 608029
millis: 1615923349195 
                      
nanos : 1615923349194 610894
millis: 1615923349195 
                      
nanos : 1615923349194 613785
millis: 1615923349195 
                      
nanos : 1615923349194 617042
millis: 1615923349195 
                      
nanos : 1615923349194 620452
millis: 1615923349195 
                      
nanos : 1615923349194 623519
millis: 1615923349195 
                      
nanos : 1615923349194 627354
millis: 1615923349195 
```

Watch for the rounding of nanos to millis, and when a rounding should occur.   
**Look at the switch** from `1615923349194` to `1615923349195` which we've repeated and commented below: 

```java
nanos : 1615923349194 492230
millis: 1615923349194 
                      
nanos : 1615923349194 495482
millis: 1615923349194 
                      
nanos : 1615923349194 498592     // ..944. We are getting close to an expected 95, will it come?
millis: 1615923349194            // ..94
                      
nanos : 1615923349194 501727     // ..945. 945 when rounding should become. You guessed it. 95. 
millis: 1615923349195            // ..95
                      
nanos : 1615923349194 507327
millis: 1615923349195 
```

#### Contribute
Send an email to `opensource{at}momomo.com` if you would like to contribute in any way, make changes or otherwise have thoughts and/or ideas on things to improve.