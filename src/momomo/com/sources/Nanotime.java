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

import momomo.com.Numbers;
import momomo.com.annotations.informative.Development;
import momomo.com.exceptions.$InterruptedException;

import java.util.concurrent.TimeUnit;

/**
 * Allows for nanosecond precision when asking for time from Java Runtime than standard System.currentTimeMillis.
 *
 * First, know that System.nanoTime() is elapsed nanos since an arbitrary origin, usually the start of the JVM and can usually only be used to measure elapsed time between two invocations.
 *
 * What this implementation does is allow you to get a higher precision when asking for the time, with nanosecond precision.
 *
 * Normally, you can get the time from your system using System.currentTimeMillis() with millisecond precision but when invoked twice right after each other, calls to System.currentTimeMillis() will usually return the same value.
 *
 * This provides you with nanosecond precision for a method similar to System.currentTimeMillis() by essentially calibrating System.nanoTime() which records nanos elapsed since JVM started with System.currentTimeMillis().
 *
 * When calibrating the two, our code will:
 *
 * Ask System.currentTimeMillis() right after asking System.nanoTime(), in a one liner.
 *  1. We will record the difference between the two.
 *  2. Sleep a random amount of nano seconds.
 *  3. Repeat this process 1000 times.
 *
 * We then take the average recorded difference and use this average to go from System.nanoTime() to a System.currentTimeMillis() by subtracting the average as a calculated DIFF.
 *
 * Is this a 100% accurate record of current time in nanos?
 * Is there even such a definition? What is time? Even atomic clocks do not give a 100% accurate definition of time at any given moment.
 *
 * When we ask for Time.nano() we can expect some discrepancy, as even the cost of calling System.nanoTime() has a cost and in reality only represent a time in the past once given access to it.
 *
 * Rather it a higher precision one than System.currentTimeMillis() as System.currentTimeMillis() will often prove useless when invoked tightly, while System.nanoTime() will show always show a diff, and so will Nano.time().
 *
 * Our code just calibrates the two and allows you to map System.nanoTime() to one based on a sane and constant reference frame usually to when baby Jesus was born, rather than when the JVM turned on.
 *
 * Recalibration
 * Note, recalibration by default is turned off, but you may pass a value of your choice to trigger a recalibration how often you'd like using Nano.setInstance( new Nanotime(...) ), but there is nothing to suggest a recalibration is required unless the underlying system specification differs drastically during runtime in where two calls to System.nanoTime() will diverge.
 *
 * Recalibration also introduces complex requirements regarding when to start using the newly calibrated value so to ensure a proper behaviour we've decided to turn off calibration every to ensure we stay within proper bounds and give a constant reference frame of time once established.
 *
 * A sample test run on our example() code within will output the following, which also shows the rounding of System.currentTimeMillis fits extremely well within bounds.
 *
 * nanos : 1615923349193929741
 * millis: 1615923349194
 *
 * nanos : 1615923349193934170
 * millis: 1615923349194
 *
 * nanos : 1615923349193938359
 * millis: 1615923349194
 *
 * nanos : 1615923349193942637
 * millis: 1615923349194
 *
 * nanos : 1615923349193947203
 * millis: 1615923349194
 *
 * nanos : 1615923349193951495
 * millis: 1615923349194
 *
 * nanos : 1615923349193955582
 * millis: 1615923349194
 *
 * nanos : 1615923349193959922
 * millis: 1615923349194
 *
 * nanos : 1615923349193964834
 * millis: 1615923349194
 *
 * nanos : 1615923349193969360
 * millis: 1615923349194
 *
 * nanos : 1615923349193973703
 * millis: 1615923349194
 *
 * nanos : 1615923349193977802
 * millis: 1615923349194
 *
 * nanos : 1615923349193981898
 * millis: 1615923349194
 *
 * nanos : 1615923349193985985
 * millis: 1615923349194
 *
 * nanos : 1615923349193997998
 * millis: 1615923349194
 *
 * nanos : 1615923349194002532
 * millis: 1615923349194
 *
 * nanos : 1615923349194006594
 * millis: 1615923349194
 *
 * nanos : 1615923349194010553
 * millis: 1615923349194
 *
 * nanos : 1615923349194014653
 * millis: 1615923349194
 *
 * nanos : 1615923349194018949
 * millis: 1615923349194
 *
 * nanos : 1615923349194023036
 * millis: 1615923349194
 *
 * nanos : 1615923349194026981
 * millis: 1615923349194
 *
 * nanos : 1615923349194030927
 * millis: 1615923349194
 *
 * nanos : 1615923349194034796
 * millis: 1615923349194
 *
 * nanos : 1615923349194038682
 * millis: 1615923349194
 *
 * nanos : 1615923349194042481
 * millis: 1615923349194
 *
 * nanos : 1615923349194046424
 * millis: 1615923349194
 *
 * nanos : 1615923349194053093
 * millis: 1615923349194
 *
 * nanos : 1615923349194057724
 * millis: 1615923349194
 *
 * nanos : 1615923349194066775
 * millis: 1615923349194
 *
 * nanos : 1615923349194070955
 * millis: 1615923349194
 *
 * nanos : 1615923349194074774
 * millis: 1615923349194
 *
 * nanos : 1615923349194078624
 * millis: 1615923349194
 *
 * nanos : 1615923349194082430
 * millis: 1615923349194
 *
 * nanos : 1615923349194086182
 * millis: 1615923349194
 *
 * nanos : 1615923349194089974
 * millis: 1615923349194
 *
 * nanos : 1615923349194093682
 * millis: 1615923349194
 *
 * nanos : 1615923349194097517
 * millis: 1615923349194
 *
 * nanos : 1615923349194101248
 * millis: 1615923349194
 *
 * nanos : 1615923349194104905
 * millis: 1615923349194
 *
 * nanos : 1615923349194108519
 * millis: 1615923349194
 *
 * nanos : 1615923349194112192
 * millis: 1615923349194
 *
 * nanos : 1615923349194115964
 * millis: 1615923349194
 *
 * nanos : 1615923349194119628
 * millis: 1615923349194
 *
 * nanos : 1615923349194123365
 * millis: 1615923349194
 *
 * nanos : 1615923349194127011
 * millis: 1615923349194
 *
 * nanos : 1615923349194130613
 * millis: 1615923349194
 *
 * nanos : 1615923349194134200
 * millis: 1615923349194
 *
 * nanos : 1615923349194141768
 * millis: 1615923349194
 *
 * nanos : 1615923349194145892
 * millis: 1615923349194
 *
 * nanos : 1615923349194149433
 * millis: 1615923349194
 *
 * nanos : 1615923349194153161
 * millis: 1615923349194
 *
 * nanos : 1615923349194156589
 * millis: 1615923349194
 *
 * nanos : 1615923349194162622
 * millis: 1615923349194
 *
 * nanos : 1615923349194168581
 * millis: 1615923349194
 *
 * nanos : 1615923349194172236
 * millis: 1615923349194
 *
 * nanos : 1615923349194175626
 * millis: 1615923349194
 *
 * nanos : 1615923349194179389
 * millis: 1615923349194
 *
 * nanos : 1615923349194182917
 * millis: 1615923349194
 *
 * nanos : 1615923349194186372
 * millis: 1615923349194
 *
 * nanos : 1615923349194189967
 * millis: 1615923349194
 *
 * nanos : 1615923349194193367
 * millis: 1615923349194
 *
 * nanos : 1615923349194196832
 * millis: 1615923349194
 *
 * nanos : 1615923349194200237
 * millis: 1615923349194
 *
 * nanos : 1615923349194203702
 * millis: 1615923349194
 *
 * nanos : 1615923349194207009
 * millis: 1615923349194
 *
 * nanos : 1615923349194210414
 * millis: 1615923349194
 *
 * nanos : 1615923349194213719
 * millis: 1615923349194
 *
 * nanos : 1615923349194217193
 * millis: 1615923349194
 *
 * nanos : 1615923349194220550
 * millis: 1615923349194
 *
 * nanos : 1615923349194230263
 * millis: 1615923349194
 *
 * nanos : 1615923349194234302
 * millis: 1615923349194
 *
 * nanos : 1615923349194237753
 * millis: 1615923349194
 *
 * nanos : 1615923349194241103
 * millis: 1615923349194
 *
 * nanos : 1615923349194244411
 * millis: 1615923349194
 *
 * nanos : 1615923349194248085
 * millis: 1615923349194
 *
 * nanos : 1615923349194251494
 * millis: 1615923349194
 *
 * nanos : 1615923349194254901
 * millis: 1615923349194
 *
 * nanos : 1615923349194258334
 * millis: 1615923349194
 *
 * nanos : 1615923349194261686
 * millis: 1615923349194
 *
 * nanos : 1615923349194265102
 * millis: 1615923349194
 *
 * nanos : 1615923349194270140
 * millis: 1615923349194
 *
 * nanos : 1615923349194273647
 * millis: 1615923349194
 *
 * nanos : 1615923349194276954
 * millis: 1615923349194
 *
 * nanos : 1615923349194280302
 * millis: 1615923349194
 *
 * nanos : 1615923349194283599
 * millis: 1615923349194
 *
 * nanos : 1615923349194286869
 * millis: 1615923349194
 *
 * nanos : 1615923349194290073
 * millis: 1615923349194
 *
 * nanos : 1615923349194293356
 * millis: 1615923349194
 *
 * nanos : 1615923349194296697
 * millis: 1615923349194
 *
 * nanos : 1615923349194300223
 * millis: 1615923349194
 *
 * nanos : 1615923349194303511
 * millis: 1615923349194
 *
 * nanos : 1615923349194306848
 * millis: 1615923349194
 *
 * nanos : 1615923349194310156
 * millis: 1615923349194
 *
 * nanos : 1615923349194313698
 * millis: 1615923349194
 *
 * nanos : 1615923349194321384
 * millis: 1615923349194
 *
 * nanos : 1615923349194325691
 * millis: 1615923349194
 *
 * nanos : 1615923349194329315
 * millis: 1615923349194
 *
 * nanos : 1615923349194332879
 * millis: 1615923349194
 *
 * nanos : 1615923349194430337
 * millis: 1615923349194
 *
 * nanos : 1615923349194442848
 * millis: 1615923349194
 *
 * nanos : 1615923349194459099
 * millis: 1615923349194
 *
 * nanos : 1615923349194463835
 * millis: 1615923349194
 *
 * nanos : 1615923349194467404
 * millis: 1615923349194
 *
 * nanos : 1615923349194470742
 * millis: 1615923349194
 *
 * nanos : 1615923349194473903
 * millis: 1615923349194
 *
 * nanos : 1615923349194477105
 * millis: 1615923349194
 *
 * nanos : 1615923349194480254
 * millis: 1615923349194
 *
 * nanos : 1615923349194485776
 * millis: 1615923349194
 *
 * nanos : 1615923349194489077
 * millis: 1615923349194
 *
 * nanos : 1615923349194492230
 * millis: 1615923349194
 *
 * nanos : 1615923349194495482
 * millis: 1615923349194
 *
 * nanos : 1615923349194498592
 * millis: 1615923349194
 *
 * nanos : 1615923349194501727
 * millis: 1615923349195
 *
 * nanos : 1615923349194507327
 * millis: 1615923349195
 *
 * nanos : 1615923349194510682
 * millis: 1615923349195
 *
 * nanos : 1615923349194513826
 * millis: 1615923349195
 *
 * nanos : 1615923349194516978
 * millis: 1615923349195
 *
 * nanos : 1615923349194520035
 * millis: 1615923349195
 *
 * nanos : 1615923349194523269
 * millis: 1615923349195
 *
 * nanos : 1615923349194526284
 * millis: 1615923349195
 *
 * nanos : 1615923349194529279
 * millis: 1615923349195
 *
 * nanos : 1615923349194532327
 * millis: 1615923349195
 *
 * nanos : 1615923349194535289
 * millis: 1615923349195
 *
 * nanos : 1615923349194538170
 * millis: 1615923349195
 *
 * nanos : 1615923349194541206
 * millis: 1615923349195
 *
 * nanos : 1615923349194545420
 * millis: 1615923349195
 *
 * nanos : 1615923349194548475
 * millis: 1615923349195
 *
 * nanos : 1615923349194551827
 * millis: 1615923349195
 *
 * nanos : 1615923349194554858
 * millis: 1615923349195
 *
 * nanos : 1615923349194557828
 * millis: 1615923349195
 *
 * nanos : 1615923349194560675
 * millis: 1615923349195
 *
 * nanos : 1615923349194563661
 * millis: 1615923349195
 *
 * nanos : 1615923349194566741
 * millis: 1615923349195
 *
 * nanos : 1615923349194569751
 * millis: 1615923349195
 *
 * nanos : 1615923349194574834
 * millis: 1615923349195
 *
 * nanos : 1615923349194578005
 * millis: 1615923349195
 *
 * nanos : 1615923349194581069
 * millis: 1615923349195
 *
 * nanos : 1615923349194584748
 * millis: 1615923349195
 *
 * nanos : 1615923349194589969
 * millis: 1615923349195
 *
 * nanos : 1615923349194593559
 * millis: 1615923349195
 *
 * nanos : 1615923349194596462
 * millis: 1615923349195
 *
 * nanos : 1615923349194599484
 * millis: 1615923349195
 *
 * nanos : 1615923349194602443
 * millis: 1615923349195
 *
 * nanos : 1615923349194605288
 * millis: 1615923349195
 *
 * nanos : 1615923349194608029
 * millis: 1615923349195
 *
 * nanos : 1615923349194610894
 * millis: 1615923349195
 *
 * nanos : 1615923349194613785
 * millis: 1615923349195
 *
 * nanos : 1615923349194617042
 * millis: 1615923349195
 *
 * nanos : 1615923349194620452
 * millis: 1615923349195
 *
 * nanos : 1615923349194623519
 * millis: 1615923349195
 *
 * nanos : 1615923349194627354
 * millis: 1615923349195
 *
 * There's basically only one class, Nanotime.java, but we've provide another one due to API call looking better through Nano.time() since Nanotime.get() is not a static method.
 *
 * For normal use, you'd just call Nano.time(). Thats' it!
 *
 * To configure Nanotime.java just call Nanotime.setInstance( new Nanotime() ) prior to any use of Nano.time(). You can also create your own instance version ti be accessed separately.
 *
 * @see momomo.com.Nano#time()
 * @see Nanotime#setInstance(Nanotime)
 *
 * @author Joseph S.
 */
public class Nanotime {
    // Between System.nanoTime and System.currentTimeMillis() in order to give the current time in nanos, which is used to estimate the cost of the System.nanoTime operation
    protected long DIFF;
    
    public Nanotime() {
        this(null);
    }
    
    public Nanotime(Long recalibrate) {
        calibrate();
        
        if ( recalibrate != null ) {
            Thread thread = new Thread(() -> {
                while (true) {
                    // We recalibrate using a sleeping thread, not really required but we do so anyway
                    
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
        return System.nanoTime() + DIFF;
    }
    
    /**
     * Here we calibrate System.currentTimeMillis with System.nanoTime. 
     */
    public void calibrate() {
        long start = System.nanoTime();
    
        MovingAverageConverging average = new MovingAverageConverging(0.567);
    
        long millis = System.currentTimeMillis(), nanos, cost, added = 0, max = 1000;
    
        for( ;; ) {
            // We are only interested in comparing at the switches of the milliseconds. When that happens, we also read the System.nanoTime(). 
            // We repeat this enough times to get a good 
            // We need to run this logic as tight as we possibly can 
            // We do it in a one liner to avoid having other logic run in between
            if ( millis != (millis = System.currentTimeMillis()) && (nanos = System.nanoTime()) != 0 && (cost = System.nanoTime() - System.nanoTime()) != 0 ) {
                average.add(
                    // The diff between both as already recorded at the switch
                    millis * 1000000L - nanos
                    
                    -
                
                    // We also remove the estimated cost of invoking System.nanoTime() at this time as a way to estimate the cost to the 
                    // time of System.currentTimeMillis() was invoked as there is no easy way to measure the latter.  
                    cost
                );
            
                if ( ++added == max ) {
                    break;
                }
            }
        }
    
        // Time to calibrate
        long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start); // log.info(getClass(), "Took ", elapsed, "ms to Nanotime.calibrate()");
        DIFF         = Math.round(average.get()) + 1000;
    }
    
    /////////////////////////////////////////////////////////////////////
    // The setup of the singleton instance. 
    /////////////////////////////////////////////////////////////////////
    public static Nanotime setInstance(Nanotime instance) {
        return INSTANCE = instance;
    }
    protected static final Object LOCK = new Object(); private static Nanotime INSTANCE; public static Nanotime getInstance() {
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
    // Examples and "tests" used in development
    /////////////////////////////////////////////////////////////////////
    
    @Development public static void main(String[] args) {
        Long[][] array = sample();
    
        errorsize(array, true); 
        
        print(array);
    }
    
    /**
     * Takes a bunch of array generated recordings, and compares them at the switches. 
     * When we get say 100ms  and nanos 99.9998ms at the `same` we say the .0002 is the error margin here.
     * Or, we might have 99ms and nanos 100.0002ms we say again the error size is 0.002 since expect the 99m to very close to becoming a hundred.  
     * Since there is no such thing as same time as even on our `super computer`, we get a cost of ~30nanos for two System.nanoTime() calls
     * Therefore, the real test for the second example is wether a call right after will get you 100ms. 
     * 
     * Therefore when we print, we print millis first, then nanos, then nanos, then millis again.
     * If the last call to millis is what we expect we feel the error margin is within an acceptable range. 
     */
    @Development private static double errorsize(Long[][] array, boolean print) {
        double largest = Double.MIN_VALUE;
        int i = -1; for (Long[] it : array) {
            
            if  ( ++i < 500 ) continue; 
            
            int length = ("" + it[0]).length();
            Long same  = Numbers.toLong(("" + it[1]).substring(0, length));
    
            long diff = it[0] - same;
            
            // We want to check the ones creeping up to being the same
            if   ( Math.abs(diff) == 1 ) {
                if ( diff == 1 ) {
                    diff = (same + 1) * 1000000L - it[1];
                }
                else {
                    diff = it[1] - same * 1000000L;
                }
    
                if ( diff > largest ) {
                    if ( print ) {
                        System.out.println(
                            "index     : " + i     + "\n" +
                            "millis 1  : " + it[0] + "\n" +
                            "nanos  1  : " + it[1] + "\n" +
                            "nanos  2  : " + it[2] + "\n" +
                            "millis 2  : " + it[3] + "\n" +
        
                            "diff   n  : " + (it[2] - it[1]) + "\n" +
                            "previous  : " + largest   + "\n" +
                            "current   : " + diff      + "\n"
                        );
                    }
    
                    largest = diff;
                }
            }
        }
        
        System.out.println("Largest: " + largest + "\n");
        
        return largest;
    }
    
    @Development private static Long[][] sample() {
        return sample(Nanotime.getInstance());
    }
    
    @Development private static Long[][] sample(Nanotime nano) {
        // We are trying to get as tight as possible while still being able to retain values.
        // Array is going to be faster than a LinkedList. 
        int to = 1000000; Long[][] array = new Long[to][4]; int i = -1; while (++i < to) {
            array[i] = new Long[]{ System.currentTimeMillis(), nano.get(), nano.get(), System.currentTimeMillis() };
        }
        return array;
    }
    
    @Development private static void print(Long[][] array) {
        // We generate these without a print to system.out.print which has a cost and will impact how tightly we can get the values.
        // We are trying to get as tight as possible while still being able to retain values.
        // Array is going to be faster than a LinkedList
        int i = -1; for ( Long[] it: array ) {
            if ( ++i > 10000 ) break;    // No more than 5000
            
            System.out.println(
                    "index  : " + i      + "\n" +
                    "millis : " + it[0]  + "\n" +
                    "nanos  : " + it[1]  + "\n" +
                    "nanos  : " + it[2]  + "\n" +
                    "millis : " + it[3]  + "\n"
            );
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
}
