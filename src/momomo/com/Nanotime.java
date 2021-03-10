/*****************************************************************************************************************************************
 Momomo LTD Opensource License 'MoL1' (https://raw.githubusercontent.com/momomo/momomo.com.Licenses/HEAD/MoL1)                       
 
 Copyrightⓒ 2014-2021, Momomo LTD. All rights reserved.                                                                             
 
 (1) Use of this source code, wether identical, changed or altered is allowed, for commercial and non-commercial use.                
 
 (2) This source code may be changed and altered freely to be used only within your entity/organisation, given that a notice of all  
 changes introduced must listed and included at the end of an exact copy of this notice, including the date and name of the      
 entity/organization that introduced them.                                                                                       
 
 (3) The redistribution and/or publication of this source code to the public, if changed or altered, is prohibited using any         
 medium not priorly approved by Momomo LTD unless a written consent has been requested and recieved by authorized                
 representatives of Momomo LTD.                                                                                                  
 
 (4) The distribution of any work derived through the use of this source code, wether identical, changed or altered,                 
 is however allowed, as long as such distribution does not contradict (3).                                                       
 
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

import momomo.com.exceptions.$InterruptedException;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

/**
 * For direct usage
 *  @see Nano
 *  @see Nano#time()
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
                Thread.sleep(0, (int) Randoms.Long(300, 1000) );  // Sleep random nanos, so we can repeat the measurement at a more "random" time
            }
            catch (InterruptedException ignore) {}
        }
        
        DIFF = Math.round(total.divide( new BigInteger("" + to) ).doubleValue());
    }
}
