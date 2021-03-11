## momomo.com.plaform.Nanotime

##### A one method Java time that allows for nanosecond precision when asking for time from Java Runtime than standard `System.currentTimeMillis`.

First, know that `System.nanoTime()` is elapsed nanos since an arbitrary origin, usually *the start of the JVM* and can usually only be used to measure elapsed time between two invocations. 

What this implementation does is allow you to get a higher precision when asking for the time, with *nanosecond* precision.

Normally, you can get the time from your system using `System.currentTimeMillis(`) with millisecond precision but when invoked twice right after each other, calls to `System.currentTimeMillis()` will usually return the same value. 

We provide nanosecond precision for a method similar to `System.currentTimeMillis()` by essentially calibrating `System.nanoTime()` which records nanos elapsed since JVM started with `System.currentTimeMillis()`.  

When calibrating the two, our code will:  
   1. Ask `System.currentTimeMillis()` right after asking `System.nanoTime()`, in a one liner. 
   2. We will record the difference between the two. 
   3. `Sleep` a random amount of nano seconds. 
   4. Repeat this process 1000 times.

We then take the average difference recorded difference and use this average to go from `System.nanoTime()` to a `System.currentTimeMillis()` and as well as subtracting the average and calculated `DIFF`. 

##### Now, be aware!
 
This is not a *100%* accurate record of current time in nanos, if there ever **could be such a definition** as even atomic clocks do not give 100% accurate definition of time. 

Rather it a higher precision one than `System.currentTimeMillis()` as `System.currentTimeMillis()` will often prove useless when invoked tightly, while `System.nanoTime()` will show always show a diff.  

Our code just calibrates the two and allows you to map `System.nanoTime()` to one based on a sane origin, usually `EPOC` something we as humans can make sense of.

#### Maven dependency available on maven central (search.maven.org)
##### Dependency   
```
<dependency>
  <groupId>com.momomo</groupId>
  <artifactId>momomo.com.platform.Nanotime</artifactId>
  <version>1.3.5</version>
</dependency>                                                      
```                         
##### Repository
```
<repository>
    <id>maven-central</id>
    <url>http://repo1.maven.org/maven2</url>
</repository>
```

Note, **recalibration** by default occurs every **60 minutes** but you may pass a value of your choice to trigger a recalibration how often you'd like and even turn it off completely by passing a null value to the constructor of `Nano.setInstance( new Nanotime(...) )`, 
but there is *nothing to suggest* a recalibration is required unless the *underlying system specification* differs drastically during runtime.
 
The only difference that might occur is that the diff between `System.currentTimeMillis() - System.nanoTime()` will increase or decrease depending on *changing system specification* and/or load. 
To ensure a proper behaviour always we recalibrate every now and then to ensure we stay within proper bounds.

### Guide

There's basically only one class, `Nanotime.java`, but we've provide another one due to API call looking better through `Nano.time()` since `Nanotime.get()` is not a static method. 
 * [Nano.java](src/momomo/com/Nano.java) 
    This is just a class utilizing what should only ever be, one instance of Nanotime. 
 * [Nanotime.java](src/momomo/com/Nanotime.java)
 
For normal use, you'd just call `Nano.time()`. Thats' it. 

To configure `Nanotime.java` just call `Nanotime.setInstance( new Nanotime() )` prior to any use of `Nano.time()`. You can also create your own instance version that is accessed separately.

### Sample run and results    

A sample test run on our example() code within will output the following, which also shows the **rounding** of `System.currentTimeMillis` **fits extremely well** within bounds.

```
nanos : 1615126882489 003232    
millis: 1615126882489

nanos : 1615126882493 273119
millis: 1615126882493

nanos : 1615126882494 329915
millis: 1615126882494 
                      
nanos : 1615126882495 395828
millis: 1615126882495 
                      
nanos : 1615126882496 721679
millis: 1615126882497 
                      
nanos : 1615126882498 032849
millis: 1615126882498 
                      
nanos : 1615126882499 353386
millis: 1615126882499 
                      
nanos : 1615126882500 468002
millis: 1615126882500 
                      
nanos : 1615126882501 778743
millis: 1615126882502 
                      
nanos : 1615126882503 093843
millis: 1615126882503 
                      
nanos : 1615126882504 406183
millis: 1615126882504 
                      
nanos : 1615126882505 717248
millis: 1615126882506 
                      
nanos : 1615126882507 028078
millis: 1615126882507 
                      
nanos : 1615126882508 195821
millis: 1615126882508 
                      
nanos : 1615126882509 535248
millis: 1615126882510 
                      
nanos : 1615126882510 863640
millis: 1615126882511 
                      
nanos : 1615126882512 185780
millis: 1615126882512 
                      
nanos : 1615126882513 496066
millis: 1615126882514 
                      
nanos : 1615126882514 807325
millis: 1615126882515 
                      
nanos : 1615126882516 117553
millis: 1615126882516 
                      
nanos : 1615126882517 427255
millis: 1615126882517 
                      
nanos : 1615126882518 573781
millis: 1615126882519 
                      
nanos : 1615126882519 888050
millis: 1615126882520 
                      
nanos : 1615126882521 202132
millis: 1615126882521 
                      
nanos : 1615126882522 512572
millis: 1615126882523 
                      
nanos : 1615126882523 630083
millis: 1615126882524 
                      
nanos : 1615126882524 941226
millis: 1615126882525 
                      
nanos : 1615126882526 010794
millis: 1615126882526 
                      
nanos : 1615126882527 172485
millis: 1615126882527 
                      
nanos : 1615126882528 487609
millis: 1615126882529 
                      
nanos : 1615126882529 801912
millis: 1615126882530 
                      
nanos : 1615126882531 112833
millis: 1615126882531 
                      
nanos : 1615126882532 420061
millis: 1615126882532 
                      
nanos : 1615126882533 740579
millis: 1615126882534 
                      
nanos : 1615126882535 005523
millis: 1615126882535 
                      
nanos : 1615126882536 325448
millis: 1615126882536 
                      
nanos : 1615126882537 636238
millis: 1615126882538 
                      
nanos : 1615126882538 980797
millis: 1615126882539 
                      
nanos : 1615126882540 332307
millis: 1615126882540 
                      
nanos : 1615126882541 491596
millis: 1615126882542 
                      
nanos : 1615126882542 793457
millis: 1615126882543 
                      
nanos : 1615126882543 897008
millis: 1615126882544 
                      
nanos : 1615126882545 303146
millis: 1615126882545 
                      
nanos : 1615126882546 712743
millis: 1615126882547 
                      
nanos : 1615126882548 071264
millis: 1615126882548 
                      
nanos : 1615126882549 514006
millis: 1615126882550 
                      
nanos : 1615126882551 039710
millis: 1615126882551 
                      
nanos : 1615126882552 402464
millis: 1615126882552 
                      
nanos : 1615126882553 894890
millis: 1615126882554 
                      
nanos : 1615126882555 346340
millis: 1615126882555 
                      
nanos : 1615126882556 791721
millis: 1615126882557 
                      
nanos : 1615126882558 221053
millis: 1615126882558 
                      
nanos : 1615126882559 629310
millis: 1615126882560 
                      
nanos : 1615126882561 028768
millis: 1615126882561 
                      
nanos : 1615126882562 484092
millis: 1615126882562 
                      
nanos : 1615126882563 966192
millis: 1615126882564 
                      
nanos : 1615126882565 440330
millis: 1615126882565 
                      
nanos : 1615126882566 798251
millis: 1615126882567 
                      
nanos : 1615126882568 223619
millis: 1615126882568 
                      
nanos : 1615126882569 777616
millis: 1615126882570 
                      
nanos : 1615126882571 131857
millis: 1615126882571 
                      
nanos : 1615126882572 584722
millis: 1615126882573 
                      
nanos : 1615126882574 089018
millis: 1615126882574 
                      
nanos : 1615126882575 290793
millis: 1615126882575 
                      
nanos : 1615126882576 656877
millis: 1615126882577 
                      
nanos : 1615126882578 172644
millis: 1615126882578 
                      
nanos : 1615126882579 585578
millis: 1615126882580 
                      
nanos : 1615126882581 071592
millis: 1615126882581 
                      
nanos : 1615126882582 504603
millis: 1615126882583 
                      
nanos : 1615126882583 899790
millis: 1615126882584 
                      
nanos : 1615126882585 222624
millis: 1615126882585 
                      
nanos : 1615126882586 535716
millis: 1615126882587 
                      
nanos : 1615126882587 846161
millis: 1615126882588 
                      
nanos : 1615126882589 173390
millis: 1615126882589 
                      
nanos : 1615126882590 503933
millis: 1615126882591 
                      
nanos : 1615126882591 866565
millis: 1615126882592 
                      
nanos : 1615126882593 176415
millis: 1615126882593 
                      
nanos : 1615126882594 367339
millis: 1615126882594 
                      
nanos : 1615126882595 676971
millis: 1615126882596 
                      
nanos : 1615126882597 029552
millis: 1615126882597 
                      
nanos : 1615126882598 339010
millis: 1615126882598 
                      
nanos : 1615126882599 647806
millis: 1615126882600 
                      
nanos : 1615126882600 964487
millis: 1615126882601 
                      
nanos : 1615126882602 276025
millis: 1615126882602 
                      
nanos : 1615126882603 335699
millis: 1615126882603 
                      
nanos : 1615126882604 658217
millis: 1615126882605 
                      
nanos : 1615126882605 976650
millis: 1615126882606 
                      
nanos : 1615126882607 025539
millis: 1615126882607 
                      
nanos : 1615126882608 075824
millis: 1615126882608 
                      
nanos : 1615126882609 114277
millis: 1615126882609 
                      
nanos : 1615126882610 211999
millis: 1615126882610 
                      
nanos : 1615126882611 522435
millis: 1615126882612 
                      
nanos : 1615126882612 789469
millis: 1615126882613 
                      
nanos : 1615126882613 924380
millis: 1615126882614 
                      
nanos : 1615126882615 120988
millis: 1615126882615 
                      
nanos : 1615126882616 432906
millis: 1615126882616 
                      
nanos : 1615126882617 742966
millis: 1615126882618 
                      
nanos : 1615126882619 056464
millis: 1615126882619 
                      
nanos : 1615126882620 364869
millis: 1615126882620 
                      
nanos : 1615126882621 683248
millis: 1615126882622 
```

### Other
Also see our other repositories

   * [momomo.com.platform.Lambda](https://github.com/momomo/momomo.com.platform.Lambda)

#### Contribute
Send an email to `opensource{at}momomo.com` if you would like to contribute in any way, make changes or otherwise have thoughts and/or ideas on things to improve.