/*****************************************************************************************************************************************
 Momomo LTD Opensource License 'MoL1' (https://raw.githubusercontent.com/momomo/momomo.com.yz.licenses/HEAD/MoL1)                       
 
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

import momomo.com.IO;
import momomo.com.Numbers;
import momomo.com.Strings;
import momomo.com.annotations.informative.Development;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

import static java.time.ZoneOffset.UTC;

/**
 * Documentation is available on 
 *   
 *   https://github.com/momomo/momomo.com.platform.Nanotime
 *
 * @see momomo.com.Nano#time()
 * @see Nanotime#setInstance(Nanotime)
 *
 * @author Joseph S.
 */
public class Nanotime {
    private static final ZoneOffset ZONE = UTC;
    
    public final long diff;    // Between System.nanoTime and System.currentTimeMillis() in order to give the current time in nanos, which is used to estimate the cost of the System.nanoTime operation
    
    public Nanotime() {
        this(0, 100);
    }
    
    /**
     * @param adjust for some reason on our computer changing the value to 1000ns will yield smaller maximum error sizes.
     *               
     * We leave the default as 0 though because we do not know the effect this has on other computers. 
     * There is no reason really to be even looking at getting as close to System.currentTimeMillis() as possible.
     *               
     * @param how many rounds we wish to go when calibrating and will ultimately affect the setup cost. for most cases a round of 1 would be sufficient.                
     */
    public Nanotime(long adjust, long rounds) {
        MovingAverageConverging average = new MovingAverageConverging(0.678);
        
        long nowMillis, nowNanos, lastMillis = System.currentTimeMillis(), lastNanos = System.nanoTime();
        do {
            nowNanos  = System.nanoTime(); 
            nowMillis = System.currentTimeMillis();
    
            if (nowMillis == (lastMillis + 1)) {
                average.add(nowMillis * 1000000L - nowNanos + (-System.nanoTime() + System.nanoTime()));
        
                if ( --rounds <= 0 ) break;
            }
    
            lastMillis = nowMillis;
    
        } while (true);
        
        this.diff = Math.round(average.get()) + adjust;
    }
    
    /**
     * For synchronization across several machines a diff would be calculated prior to creating the instance in order for us to retain the final aspects of the diff to ensure constant and linear behaviour against System.nanoTime()
     */
    public Nanotime(long diff) {
        this.diff = diff;
    }
    
    /**
     * Returns higher time precision than System.currentTimeMillis() in nano seconds
     */
    public long get() {
        return System.nanoTime() + diff;
    }
    
    /**
     * Returns higher time precision than System.currentTimeMillis() as a java.sql.Timestamp
     */
    public Timestamp timestamp() {
        long now     = get();
        long seconds = TimeUnit.NANOSECONDS.toSeconds(now);
        int  nanos   = (int) (now - TimeUnit.SECONDS.toNanos(seconds));
        
        Timestamp timestamp = new Timestamp(TimeUnit.SECONDS.toMillis(seconds));
        timestamp.setNanos(nanos);
        
        return timestamp;
    }
    
    public Instant instant() {
        long now     = get();
        long seconds = TimeUnit.NANOSECONDS.toSeconds(now);
        int  nanos   = (int) (now - TimeUnit.SECONDS.toNanos(seconds));
        
        return Instant.ofEpochSecond(seconds, nanos);
    }
    
    public LocalDateTime datetime() {
        return datetime(ZONE);
    }
    public LocalDateTime datetime(ZoneOffset zone) {
        long now     = get();
        long seconds = TimeUnit.NANOSECONDS.toSeconds(now);
        int  nanos   = (int) (now - TimeUnit.SECONDS.toNanos(seconds));
        
        return LocalDateTime.ofEpochSecond(seconds, nanos, zone);
    }
    
    public OffsetDateTime offsettime() {
        return offsettime(ZONE);
    }
    public OffsetDateTime offsettime(ZoneOffset zone) {
        return instant().atOffset(zone);
    }
    
    public LocalTime localtime() {
        return localtime(ZONE);
    }
    public LocalTime localtime(ZoneId zone) {
        return LocalTime.ofInstant(instant(), zone);
    }
    
    public ZonedDateTime zonedtime() {
        return zonedtime(ZONE);
    }
    public ZonedDateTime zonedtime(ZoneId zone) {
        return instant().atZone(zone);
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
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    // Examples and "tests" used in development
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    @Development private static void main(String[] args) {
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
    @Development private static double errorsize(Long[][] array, boolean printLarge) {
        double smallest = 100000, largest  = 0;
        int i = -1; for (Long[] it : array) {
            
            if  ( ++i < 500 ) continue; 
            
            int length = ("" + it[1]).length();
            Long same  = Numbers.toLong(("" + it[2]).substring(0, length));
    
            long diff = it[1] - same;
            
            // We want to check the ones creeping up to being the same
            if   ( Math.abs(diff) > 0  ) {
                if ( diff == 1 ) {
                    diff = (same + 1) * 1000000L - it[2];
                }
                else {
                    diff = it[2] - same * 1000000L;
                }
    
                if ( diff > largest ) {
                    if ( printLarge ) {
                        System.out.println(
                            "---------------------- LARGE --------------------" + "\n" +
                            "index     : " + i     + "\n" +
                            "nanos  0  : " + it[0] + "\n" +
                            "millis 0  : " + it[1] + "\n" +
                            "nanos  1  : " + it[2] + "\n" +
                            "nanos  2  : " + it[3] + "\n" +
                            "millis 1  : " + it[4] + "\n" +
        
                            "nano cost : " + (it[3] - it[2]) + "\n" +
                            "diff      : " + diff  + "\n" + 
                            "--------------------------------------------------" + "\n"
                        );
                    }
    
                    largest = diff;
                }
                else if (diff < smallest ) {
                    if ( true ) {
                        System.out.println(
                            "---------------------- SMALL --------------------" + "\n" +
                            "index     : " + i     + "\n" +
                            "nanos  0  : " + it[0] + "\n" +
                            "millis 0  : " + it[1] + "\n" +
                            "nanos  1  : " + it[2] + "\n" +
                            "nanos  2  : " + it[3] + "\n" +
                            "millis 1  : " + it[4] + "\n" +
            
                            "nano cost : " + (it[3] - it[2]) + "\n" +
                            "diff      : " + diff  + "\n" + 
                            "--------------------------------------------------" + "\n"
                        );
                    }
    
                    smallest = diff;
                }
            }
        }
    
        System.out.println("=========================================");
        System.out.println("Smallest: " + smallest); 
        System.out.println("Largest : " + largest );
        System.out.println("=========================================");
        
        return largest;
    }
    
    @Development private static Long[][] sample() {
        return sample(Nanotime.getInstance());
    }
    
    @Development private static Long[][] sample(Nanotime nano) {
        // We are trying to get as tight as possible while still being able to retain values.
        // Array is going to be faster than a LinkedList. 
        int to = 100000; Long[][] array = new Long[to][5]; int i = -1; while (++i < to) {
            array[i] = new Long[]{ nano.get(), System.currentTimeMillis(), nano.get(), nano.get(), System.currentTimeMillis() };
        }
        return array;
    }
    
    @Development private static void print(Long[][] array) {
        // We generate these without a print to system.out.print which has a cost and will impact how tightly we can get the values.
        // We are trying to get as tight as possible while still being able to retain values.
        // Array is going to be faster than a LinkedList
        
        StringBuilder sb = new StringBuilder();
        int i = -1; for ( Long[] it: array ) {
            if ( ++i > 100000 ) break;    // No more than 100000
            
            sb.append(
                    "index  : " + i      + "\n" +
                    "nanos  : " + it[0]  + "\n" +
                    "millis : " + it[1]  + "\n" +
                    "nanos  : " + it[2]  + "\n" +
                    "nanos  : " + it[3]  + "\n" +
                    "millis : " + it[4]  + "\n"
            ).append(Strings.NEWLINE).append(Strings.NEWLINE);
        }
        
        IO.write("/a/tmp.txt", sb);
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
}
