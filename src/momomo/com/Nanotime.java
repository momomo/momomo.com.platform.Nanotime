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
