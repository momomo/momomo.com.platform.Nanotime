/* Copyright(C) 2014 - 2020 Momomo LTD. Proprietary and confidential. Usage of this file on any medium without a written consent by Momomo LTD. is strictly prohibited. All Rights Reserved. */
package momomo.com;

import momomo.com.annotations.informative.Development;

/**
 * Normally, you can get the time from your system using System.currentTimeInMillis() with millisecond precision.
 *
 * Two subsequence calls to System.currentTimeInMillis() will usually return the same value.  
 *
 * What this implementation does is allow you to get a higher precision when asking for the time, in nanosecond precision. 
 *
 * How it achieves it is utilizing and calibrating System.nanoTime() which records nanos elapsed since JVM started to System.currentTimeInMillis(). 
 * When calibrating, our code will 
 * 1. ask System.currentTimeInMillis() right after asking System.nanoTime(). 
 * 2. It will record the difference.
 * 3. Sleep a random amount of nano seconds. 
 * 4. Repeat 1000 times.
 *
 * We then take the average difference, and now we use this average to go from subsequent calls to System.nanoTime() and subtract the DIFF to get to a time. 
 *
 * Now, beware. 
 * This is not 100% accurate record of current time in nanos, if there is ever such a definition, but rather a higher precision one than System.currentTimeInMillis(). 
 * System.currentTimeInMillis() will often provde useless to record small time diffs, while System.nanoTime() will show a diff. 
 * This method just calibrates the two and allows you to map System.nanoTime() to a one in time calibrated setting. 
 *
 * A sample test run on our example() code within will output the following, which also shows the rounding fits extremely well within bounds of what System.currentTimeInMillis seems to round to.
 *
 * nanos : 1615126882489003232
 * millis: 1615126882489
 *
 * nanos : 1615126882493273119
 * millis: 1615126882493
 *
 * nanos : 1615126882494329915
 * millis: 1615126882494
 *
 * nanos : 1615126882495395828
 * millis: 1615126882495
 *
 * nanos : 1615126882496721679
 * millis: 1615126882497
 *
 * nanos : 1615126882498032849
 * millis: 1615126882498
 *
 * nanos : 1615126882499353386
 * millis: 1615126882499
 *
 * nanos : 1615126882500468002
 * millis: 1615126882500
 *
 * nanos : 1615126882501778743
 * millis: 1615126882502
 *
 * nanos : 1615126882503093843
 * millis: 1615126882503
 *
 * nanos : 1615126882504406183
 * millis: 1615126882504
 *
 * nanos : 1615126882505717248
 * millis: 1615126882506
 *
 * nanos : 1615126882507028078
 * millis: 1615126882507
 *
 * nanos : 1615126882508195821
 * millis: 1615126882508
 *
 * nanos : 1615126882509535248
 * millis: 1615126882510
 *
 * nanos : 1615126882510863640
 * millis: 1615126882511
 *
 * nanos : 1615126882512185780
 * millis: 1615126882512
 *
 * nanos : 1615126882513496066
 * millis: 1615126882514
 *
 * nanos : 1615126882514807325
 * millis: 1615126882515
 *
 * nanos : 1615126882516117553
 * millis: 1615126882516
 *
 * nanos : 1615126882517427255
 * millis: 1615126882517
 *
 * nanos : 1615126882518573781
 * millis: 1615126882519
 *
 * nanos : 1615126882519888050
 * millis: 1615126882520
 *
 * nanos : 1615126882521202132
 * millis: 1615126882521
 *
 * nanos : 1615126882522512572
 * millis: 1615126882523
 *
 * nanos : 1615126882523630083
 * millis: 1615126882524
 *
 * nanos : 1615126882524941226
 * millis: 1615126882525
 *
 * nanos : 1615126882526010794
 * millis: 1615126882526
 *
 * nanos : 1615126882527172485
 * millis: 1615126882527
 *
 * nanos : 1615126882528487609
 * millis: 1615126882529
 *
 * nanos : 1615126882529801912
 * millis: 1615126882530
 *
 * nanos : 1615126882531112833
 * millis: 1615126882531
 *
 * nanos : 1615126882532420061
 * millis: 1615126882532
 *
 * nanos : 1615126882533740579
 * millis: 1615126882534
 *
 * nanos : 1615126882535005523
 * millis: 1615126882535
 *
 * nanos : 1615126882536325448
 * millis: 1615126882536
 *
 * nanos : 1615126882537636238
 * millis: 1615126882538
 *
 * nanos : 1615126882538980797
 * millis: 1615126882539
 *
 * nanos : 1615126882540332307
 * millis: 1615126882540
 *
 * nanos : 1615126882541491596
 * millis: 1615126882542
 *
 * nanos : 1615126882542793457
 * millis: 1615126882543
 *
 * nanos : 1615126882543897008
 * millis: 1615126882544
 *
 * nanos : 1615126882545303146
 * millis: 1615126882545
 *
 * nanos : 1615126882546712743
 * millis: 1615126882547
 *
 * nanos : 1615126882548071264
 * millis: 1615126882548
 *
 * nanos : 1615126882549514006
 * millis: 1615126882550
 *
 * nanos : 1615126882551039710
 * millis: 1615126882551
 *
 * nanos : 1615126882552402464
 * millis: 1615126882552
 *
 * nanos : 1615126882553894890
 * millis: 1615126882554
 *
 * nanos : 1615126882555346340
 * millis: 1615126882555
 *
 * nanos : 1615126882556791721
 * millis: 1615126882557
 *
 * nanos : 1615126882558221053
 * millis: 1615126882558
 *
 * nanos : 1615126882559629310
 * millis: 1615126882560
 *
 * nanos : 1615126882561028768
 * millis: 1615126882561
 *
 * nanos : 1615126882562484092
 * millis: 1615126882562
 *
 * nanos : 1615126882563966192
 * millis: 1615126882564
 *
 * nanos : 1615126882565440330
 * millis: 1615126882565
 *
 * nanos : 1615126882566798251
 * millis: 1615126882567
 *
 * nanos : 1615126882568223619
 * millis: 1615126882568
 *
 * nanos : 1615126882569777616
 * millis: 1615126882570
 *
 * nanos : 1615126882571131857
 * millis: 1615126882571
 *
 * nanos : 1615126882572584722
 * millis: 1615126882573
 *
 * nanos : 1615126882574089018
 * millis: 1615126882574
 *
 * nanos : 1615126882575290793
 * millis: 1615126882575
 *
 * nanos : 1615126882576656877
 * millis: 1615126882577
 *
 * nanos : 1615126882578172644
 * millis: 1615126882578
 *
 * nanos : 1615126882579585578
 * millis: 1615126882580
 *
 * nanos : 1615126882581071592
 * millis: 1615126882581
 *
 * nanos : 1615126882582504603
 * millis: 1615126882583
 *
 * nanos : 1615126882583899790
 * millis: 1615126882584
 *
 * nanos : 1615126882585222624
 * millis: 1615126882585
 *
 * nanos : 1615126882586535716
 * millis: 1615126882587
 *
 * nanos : 1615126882587846161
 * millis: 1615126882588
 *
 * nanos : 1615126882589173390
 * millis: 1615126882589
 *
 * nanos : 1615126882590503933
 * millis: 1615126882591
 *
 * nanos : 1615126882591866565
 * millis: 1615126882592
 *
 * nanos : 1615126882593176415
 * millis: 1615126882593
 *
 * nanos : 1615126882594367339
 * millis: 1615126882594
 *
 * nanos : 1615126882595676971
 * millis: 1615126882596
 *
 * nanos : 1615126882597029552
 * millis: 1615126882597
 *
 * nanos : 1615126882598339010
 * millis: 1615126882598
 *
 * nanos : 1615126882599647806
 * millis: 1615126882600
 *
 * nanos : 1615126882600964487
 * millis: 1615126882601
 *
 * nanos : 1615126882602276025
 * millis: 1615126882602
 *
 * nanos : 1615126882603335699
 * millis: 1615126882603
 *
 * nanos : 1615126882604658217
 * millis: 1615126882605
 *
 * nanos : 1615126882605976650
 * millis: 1615126882606
 *
 * nanos : 1615126882607025539
 * millis: 1615126882607
 *
 * nanos : 1615126882608075824
 * millis: 1615126882608
 *
 * nanos : 1615126882609114277
 * millis: 1615126882609
 *
 * nanos : 1615126882610211999
 * millis: 1615126882610
 *
 * nanos : 1615126882611522435
 * millis: 1615126882612
 *
 * nanos : 1615126882612789469
 * millis: 1615126882613
 *
 * nanos : 1615126882613924380
 * millis: 1615126882614
 *
 * nanos : 1615126882615120988
 * millis: 1615126882615
 *
 * nanos : 1615126882616432906
 * millis: 1615126882616
 *
 * nanos : 1615126882617742966
 * millis: 1615126882618
 *
 * nanos : 1615126882619056464
 * millis: 1615126882619
 *
 * nanos : 1615126882620364869
 * millis: 1615126882620
 *
 * nanos : 1615126882621683248
 * millis: 1615126882622
 *
 * Note, recalibration by default occurs every 60 minutes but you may pass a value of your choice to trigger a recalibration how often you'd like and even turn it off completely by passing a null value. 
 * There is nothing to suggest a recalibration is required unless the underlying system specs differs drastically during runtime. 
 * The only difference that might occur is that the time between System.currentTimeInMillis() - System.nanoTime() will increase or decrease depending on changing system spec and/or load. 
 * To ensure a proper behaviour always we recalibrate every now and then to ensure we stay within proper bounds. 
 * 
 * @see Nanotime
 * @see Nano#setInstance(Nanotime) 
 * 
 * This is just a class utilizing what should only ever be, one instance of Nanotime. 
 * 
 * Call Nano.setInstance prior to any call and your instance will be the default.
 * 
 * Mainly the calibration time is what is configurable which as a default is current 60 minutes.  
 * 
 * @author Joseph S.
 */
public final class Nano { private Nano(){}

    /**
     * Returns higher time precision than System.currentTimeMillis() in nano seconds
     */
    public static long time() {
        return getInstance().get();
    }
    
    /////////////////////////////////////////////////////////////////////
    // The setup of the singleton instance. 
    /////////////////////////////////////////////////////////////////////
    public static Nanotime setInstance(Nanotime instance) {
        return INSTANCE = instance;
    }
    private static final Object LOCK = new Object(); private static Nanotime INSTANCE; private static Nanotime getInstance() {
        if ( INSTANCE == null ) {
            synchronized (LOCK) {
                if ( INSTANCE == null ) {
                    return setInstance(new Nanotime());
                }
            }
        }
        return INSTANCE;
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    @Development private static void example() throws InterruptedException {
        int i = -1; while (++i < 100) {
            System.out.println(
                "nanos : " + Nano.time() + "\n" +
                "millis: " + System.currentTimeMillis() + "\n"
            );
            
            // Sleep a random about of nanoseconds
            Thread.sleep(0, (int) Randoms.Long(0, 1000));
        }
    }
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
}
