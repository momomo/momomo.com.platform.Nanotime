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
package momomo.com;

import momomo.com.sources.Nanotime;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * @see momomo.com.sources.Nanotime
 * 
 * @author Joseph S.
 */
public final class Nano { private Nano(){}
    
    /////////////////////////////////////////////////////////////////////

    /**
     * Returns higher time precision than System.currentTimeMillis() in nano seconds expressed in a long 
     */
    public static long time() {
        return Nanotime.getInstance().get();
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Returns higher time precision than System.currentTimeMillis() in nano seconds, sets and returns a Timestamp which has support for nanosecond resolution
     * 
     * toString() -> 2021-03-25 22:15:28.986068681 
     */
    public static Timestamp timestamp() {
        return Nanotime.getInstance().timestamp();
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * toString() -> 2021-03-25T21:18:49.431440982Z 
     */
    public static Instant instant() {
        return Nanotime.getInstance().instant();
    }
    
    /////////////////////////////////////////////////////////////////////
    /**
     * toString() -> 2021-03-25T21:15:28.989876426
     */
    public static LocalDateTime datetime() {
        return Nanotime.getInstance().datetime();
    }
    
    public static LocalDateTime datetime(ZoneOffset zone) {
        return Nanotime.getInstance().datetime(zone);
    }
    
    /////////////////////////////////////////////////////////////////////
    /**
     * toString() -> 21:18:34.260363177
     */
    public static LocalTime localtime() {
        return Nanotime.getInstance().localtime();
    }
    
    public static LocalTime localtime(ZoneId zone) {
        return Nanotime.getInstance().localtime(zone);
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * toString() -> 2021-03-25T21:18:49.434622190Z
     */
    public static OffsetDateTime offsettime() {
        return Nanotime.getInstance().offsettime();
    }
    
    public static OffsetDateTime offsettime(ZoneOffset zone) {
        return Nanotime.getInstance().offsettime(zone);
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * toString() -> 2021-03-25T21:18:49.434488996Z
     */
    public static ZonedDateTime zonedtime() {
        return Nanotime.getInstance().zonedtime();
    }
    
    public static ZonedDateTime zonedtime(ZoneId zone) {
        return Nanotime.getInstance().zonedtime(zone);
    }
    
    /////////////////////////////////////////////////////////////////////
    
    private static void main(String[] args) {
        System.out.println(timestamp());
        System.out.println(datetime());
        System.out.println(localtime());
        System.out.println(instant());
        System.out.println(zonedtime());
        System.out.println(offsettime());
    }
}
    