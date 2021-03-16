/*****************************************************************************************************************************************
 Momomo LTD Opensource License 'MoL1' (https://raw.githubusercontent.com/momomo/momomo.com.Licenses/HEAD/MoL1)                       
 
 Copyrightⓒ 2014-2021, Momomo LTD. All rights reserved.                                                                             
 
 (1) Use of this source code, wether identical, changed or altered is allowed, for commercial as well as non-commercial use.                
 
 (2) This source code may be changed and altered freely to be used only within the entity/organisation that introduced them, 
 provided that a notice of all changes introduced must be listed and included at the end of an exact copy of this notice, 
 including the date and name of the person, entity and/or organization that introduced them.                                                                                       
 
 (3) The redistribution and/or publication of this source code, if changed or altered, is prohibited using any medium not priorly 
 approved by Momomo LTD unless a written consent has been requested and recieved by authorized representatives of Momomo LTD. 
 
 (4) The distribution of any work derived through the use of this source code, wether identical, changed or altered, is however allowed, 
 as long as such distribution does not contradict (3) in any way.                                                       
 
 (5) Momomo LTD considers the techniques, design patterns, the naming, naming combinations, used and employed in the source as      
 unique and copyright protected where the redistribution of this source code using altered names, and/or rearranging and/or     
 restructuring of this source as a severe breach of this license and relevant copyright laws.                                   
 Momomo LTD reserves all rights to puruse any and all legal options.                                                            
 
 (6) All copies of this source code, wether identical, changed/altered must include this license in its entirety, list all changes   
 made including the name and date of the entity/organization that introduced them, as well as the following disclaimer:          
 
 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND                                                 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED                                                   
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE                                                          
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR                                                 
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES                                                  
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;                                                    
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND                                                     
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT                                                      
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS                                                   
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                                                                    
 
 Contact us on opensource{at}momomo.com if you have an improvement to this source code you'd like to contribute in any way.   
 *****************************************************************************************************************************************/
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// https://github.com/momomo/momomo.com.platform.Nanotime
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
package momomo.com.sources;

import momomo.com.Nano;
import momomo.com.Randoms;
import momomo.com.annotations.informative.Development;
import momomo.com.exceptions.$InterruptedException;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

/**
 * Allows for nanosecond precision when asking for time from Java Runtime than standard System.currentTimeMillis.
 * First, know that System.nanoTime() is elapsed nanos since an arbitrary origin, usually the start of the JVM and can usually only be used to measure elapsed time between two invocations.
 *
 * What this implementation does is allow you to get a higher precision when asking for the time, with nanosecond precision.
 *
 * Normally, you can get the time from your system using System.currentTimeMillis() with millisecond precision but when invoked twice right after each other, calls to System.currentTimeMillis() will usually return the same value.
 *
 * We provide nanosecond precision for a method similar to System.currentTimeMillis() by essentially calibrating System.nanoTime() which records nanos elapsed since JVM started with System.currentTimeMillis().
 *
 * When calibrating the two, our code will:
 *
 * Ask System.currentTimeMillis() right after asking System.nanoTime(), in a one liner.
 * 1. We will record the difference between the two.
 * 2. Sleep a random amount of nano seconds.
 * 3. Repeat this process 1000 times.
 * 
 * We then take the average difference recorded difference and use this average to go from System.nanoTime() to a System.currentTimeMillis() and as well as subtracting the average and calculated DIFF.
 *
 * Now, be aware!
 * This is not a 100% accurate record of current time in nanos, if there ever could be such a definition as even atomic clocks do not give 100% accurate definition of time.
 *
 * Rather it a higher precision one than System.currentTimeMillis() as System.currentTimeMillis() will often prove useless when invoked tightly, while System.nanoTime() will show always show a diff.
 *
 * Our code just calibrates the two and allows you to map System.nanoTime() to one based on a sane origin, usually EPOC something we as humans can make sense of.
 *
 * Note, recalibration by default occurs every 60 minutes but you may pass a value of your choice to trigger a recalibration how often you'd like and even turn it off completely by passing a null value to the constructor of Nano.setInstance( new Nanotime(...) ), but there is nothing to suggest a recalibration is required unless the underlying system specification differs drastically during runtime.
 *
 * The only difference that might occur is that the diff between System.currentTimeMillis() - System.nanoTime() will increase or decrease depending on changing system specification and/or load. To ensure a proper behaviour always we recalibrate every now and then to ensure we stay within proper bounds.
 *
 * A sample test run on our example() code within will output the following, which also shows the rounding of System.currentTimeMillis fits extremely well within bounds.
 *
 * nanos : 1615126882489 003232
 * millis: 1615126882489 
 *                       
 * nanos : 1615126882493 273119
 * millis: 1615126882493 
 *                       
 * nanos : 1615126882494 329915
 * millis: 1615126882494 
 *                       
 * nanos : 1615126882495 395828
 * millis: 1615126882495 
 *                       
 * nanos : 1615126882496 721679
 * millis: 1615126882497 
 *                       
 * nanos : 1615126882498 032849
 * millis: 1615126882498 
 *                       
 * nanos : 1615126882499 353386
 * millis: 1615126882499 
 *                       
 * nanos : 1615126882500 468002
 * millis: 1615126882500 
 *                       
 * nanos : 1615126882501 778743
 * millis: 1615126882502 
 *                       
 * nanos : 1615126882503 093843
 * millis: 1615126882503 
 *                       
 * nanos : 1615126882504 406183
 * millis: 1615126882504 
 *                       
 * nanos : 1615126882505 717248
 * millis: 1615126882506 
 *                       
 * nanos : 1615126882507 028078
 * millis: 1615126882507 
 *                       
 * nanos : 1615126882508 195821
 * millis: 1615126882508 
 *                       
 * nanos : 1615126882509 535248
 * millis: 1615126882510 
 *                       
 * nanos : 1615126882510 863640
 * millis: 1615126882511 
 *                       
 * nanos : 1615126882512 185780
 * millis: 1615126882512 
 *                       
 * nanos : 1615126882513 496066
 * millis: 1615126882514 
 *                       
 * nanos : 1615126882514 807325
 * millis: 1615126882515 
 *                       
 * nanos : 1615126882516 117553
 * millis: 1615126882516 
 *                       
 * nanos : 1615126882517 427255
 * millis: 1615126882517 
 *                       
 * nanos : 1615126882518 573781
 * millis: 1615126882519 
 *                       
 * nanos : 1615126882519 888050
 * millis: 1615126882520 
 *                       
 * nanos : 1615126882521 202132
 * millis: 1615126882521 
 *                       
 * nanos : 1615126882522 512572
 * millis: 1615126882523 
 *                       
 * nanos : 1615126882523 630083
 * millis: 1615126882524 
 *                       
 * nanos : 1615126882524 941226
 * millis: 1615126882525 
 *                       
 * nanos : 1615126882526 010794
 * millis: 1615126882526 
 *                       
 * nanos : 1615126882527 172485
 * millis: 1615126882527 
 *                       
 * nanos : 1615126882528 487609
 * millis: 1615126882529 
 *                       
 * nanos : 1615126882529 801912
 * millis: 1615126882530 
 *                       
 * nanos : 1615126882531 112833
 * millis: 1615126882531 
 *                       
 * nanos : 1615126882532 420061
 * millis: 1615126882532 
 *                       
 * nanos : 1615126882533 740579
 * millis: 1615126882534 
 *                       
 * nanos : 1615126882535 005523
 * millis: 1615126882535 
 *                       
 * nanos : 1615126882536 325448
 * millis: 1615126882536 
 *                       
 * nanos : 1615126882537 636238
 * millis: 1615126882538 
 *                       
 * nanos : 1615126882538 980797
 * millis: 1615126882539 
 *                       
 * nanos : 1615126882540 332307
 * millis: 1615126882540 
 *                       
 * nanos : 1615126882541 491596
 * millis: 1615126882542 
 *                       
 * nanos : 1615126882542 793457
 * millis: 1615126882543 
 *                       
 * nanos : 1615126882543 897008
 * millis: 1615126882544 
 *                       
 * nanos : 1615126882545 303146
 * millis: 1615126882545 
 *                       
 * nanos : 1615126882546 712743
 * millis: 1615126882547 
 *                       
 * nanos : 1615126882548 071264
 * millis: 1615126882548 
 *                       
 * nanos : 1615126882549 514006
 * millis: 1615126882550 
 *                       
 * nanos : 1615126882551 039710
 * millis: 1615126882551 
 *                       
 * nanos : 1615126882552 402464
 * millis: 1615126882552 
 *                       
 * nanos : 1615126882553 894890
 * millis: 1615126882554 
 *                       
 * nanos : 1615126882555 346340
 * millis: 1615126882555 
 *                       
 * nanos : 1615126882556 791721
 * millis: 1615126882557 
 *                       
 * nanos : 1615126882558 221053
 * millis: 1615126882558 
 *                       
 * nanos : 1615126882559 629310
 * millis: 1615126882560 
 *                       
 * nanos : 1615126882561 028768
 * millis: 1615126882561 
 *                       
 * nanos : 1615126882562 484092
 * millis: 1615126882562 
 *                       
 * nanos : 1615126882563 966192
 * millis: 1615126882564 
 *                       
 * nanos : 1615126882565 440330
 * millis: 1615126882565 
 *                       
 * nanos : 1615126882566 798251
 * millis: 1615126882567 
 *                       
 * nanos : 1615126882568 223619
 * millis: 1615126882568 
 *                       
 * nanos : 1615126882569 777616
 * millis: 1615126882570 
 *                       
 * nanos : 1615126882571 131857
 * millis: 1615126882571 
 *                       
 * nanos : 1615126882572 584722
 * millis: 1615126882573 
 *                       
 * nanos : 1615126882574 089018
 * millis: 1615126882574 
 *                       
 * nanos : 1615126882575 290793
 * millis: 1615126882575 
 *                       
 * nanos : 1615126882576 656877
 * millis: 1615126882577 
 *                       
 * nanos : 1615126882578 172644
 * millis: 1615126882578 
 *                       
 * nanos : 1615126882579 585578
 * millis: 1615126882580 
 *                       
 * nanos : 1615126882581 071592
 * millis: 1615126882581 
 *                       
 * nanos : 1615126882582 504603
 * millis: 1615126882583 
 *                       
 * nanos : 1615126882583 899790
 * millis: 1615126882584 
 *                       
 * nanos : 1615126882585 222624
 * millis: 1615126882585 
 *                       
 * nanos : 1615126882586 535716
 * millis: 1615126882587 
 *                       
 * nanos : 1615126882587 846161
 * millis: 1615126882588 
 *                       
 * nanos : 1615126882589 173390
 * millis: 1615126882589 
 *                       
 * nanos : 1615126882590 503933
 * millis: 1615126882591 
 *                       
 * nanos : 1615126882591 866565
 * millis: 1615126882592 
 *                       
 * nanos : 1615126882593 176415
 * millis: 1615126882593 
 *                       
 * nanos : 1615126882594 367339
 * millis: 1615126882594 
 *                       
 * nanos : 1615126882595 676971
 * millis: 1615126882596 
 *                       
 * nanos : 1615126882597 029552
 * millis: 1615126882597 
 *                       
 * nanos : 1615126882598 339010
 * millis: 1615126882598 
 *                       
 * nanos : 1615126882599 647806
 * millis: 1615126882600 
 *                       
 * nanos : 1615126882600 964487
 * millis: 1615126882601 
 *                       
 * nanos : 1615126882602 276025
 * millis: 1615126882602 
 *                       
 * nanos : 1615126882603 335699
 * millis: 1615126882603 
 *                       
 * nanos : 1615126882604 658217
 * millis: 1615126882605 
 *                       
 * nanos : 1615126882605 976650
 * millis: 1615126882606 
 *                       
 * nanos : 1615126882607 025539
 * millis: 1615126882607 
 *                       
 * nanos : 1615126882608 075824
 * millis: 1615126882608 
 *                       
 * nanos : 1615126882609 114277
 * millis: 1615126882609 
 *                       
 * nanos : 1615126882610 211999
 * millis: 1615126882610 
 *                       
 * nanos : 1615126882611 522435
 * millis: 1615126882612 
 *                       
 * nanos : 1615126882612 789469
 * millis: 1615126882613 
 *                       
 * nanos : 1615126882613 924380
 * millis: 1615126882614 
 *                       
 * nanos : 1615126882615 120988
 * millis: 1615126882615 
 *                       
 * nanos : 1615126882616 432906
 * millis: 1615126882616 
 *                       
 * nanos : 1615126882617 742966
 * millis: 1615126882618 
 *                       
 * nanos : 1615126882619 056464
 * millis: 1615126882619 
 *                       
 * nanos : 1615126882620 364869
 * millis: 1615126882620 
 *                       
 * nanos : 1615126882621 683248
 * millis: 1615126882622 
 *
 * Guide
 * 
 * There's basically only one class, Nanotime.java, but we've provide another one due to API call looking better through Nano.time() since Nanotime.get() is not a static method.
 *
 * For normal use, you'd just call Nano.time(). Thats' it.
 *
 * To configure Nanotime.java just call Nanotime.setInstance( new Nanotime() ) prior to any use of Nano.time(). You can also create your own instance version that is accessed separately.
 * 
 * @see momomo.com.Nano#time()
 * @see Nanotime#setInstance(Nanotime)
 * 
 * @author Joseph S.
 */
public final class Nanotime {
    // Between System.nanoTime and System.currentTimeMillis() in order to give the current time in nanos, which is used to estimate the cost of the System.nanoTime operation
    private long DIFF;
    
    public Nanotime() {
        this( TimeUnit.MINUTES.toMillis(60) );
    }
    
    public Nanotime(Long recalibrate) {
        calibrate();
        
        if ( recalibrate != null ) {
            Thread thread = new Thread(() -> {
                while (true) {
                    // We recalibrate every hour using a sleeping thread, not really required but we do so anyway
                    
                    try { Thread.sleep(recalibrate); } catch (InterruptedException cast) { throw new $InterruptedException(cast); }
                    
                    calibrate();
                }
            });
            
            thread.setDaemon(true);
            thread.start();
        }
    }
    
    /**
     * Returns higher time precision than System.currentTimeMillis() in nano seconds
     */
    public long get() {
        return System.nanoTime() - DIFF;
    }
    
    /**
     * Here we calibrate System.currentTimeMillis with System.nanoTime. 
     */
    private void calibrate() {
        // We need to use BigInteger to add 1000 big numbers
        BigInteger total = new BigInteger("0"); int  i = -1, to = 1000; while ( ++i < to ) {
            total = total.add(
                new BigInteger("" + (System.nanoTime() - System.currentTimeMillis() * 1000000) )    // We do it in one liner
            );
            
            try {
                // Sleep random nanos, so we can repeat the measurement at a more "random" time
                Thread.sleep(0, Randoms.Integer(300, 1000) );  
            }
            catch (InterruptedException ignore) {}
        }
        
        DIFF = Math.round(total.divide( new BigInteger("" + to) ).doubleValue());
    }
    
    
    /////////////////////////////////////////////////////////////////////
    // The setup of the singleton instance. 
    /////////////////////////////////////////////////////////////////////
    public static Nanotime setInstance(Nanotime instance) {
        return INSTANCE = instance;
    }
    private static final Object LOCK = new Object(); private static Nanotime INSTANCE; public static Nanotime getInstance() {
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
    
    @Development
    private static void example() throws InterruptedException {
        int i = -1; while (++i < 100) {
            System.out.println(
                "nanos : " + Nano.time() + "\n" +
                "millis: " + System.currentTimeMillis() + "\n"
            );
            
            // Sleep a random about of nanoseconds
            Thread.sleep(0, Randoms.Integer(0, 1000));
        }
    }
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
}
