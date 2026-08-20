package momomo.com.sources;

import momomo.com.annotations.informative.Private;
import momomo.com.Is;

import java.util.concurrent.TimeUnit;

/**
 * Base interface for our Locks
 * @author Joseph S.
 */
public interface Lock<L extends Lock<L>> {
    
    L lock();
    L unlock();
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    interface Await<L extends Await<L>> {
        
        default Status await() {
            return await(null, 0);
        }
        default Status await(long timeout, TimeUnit unit) {
            return await(System.nanoTime(), unit.toNanos(timeout) );
        }
        default Status await(long timeout) {
            return await(timeout, TimeUnit.MILLISECONDS);
        }
        
        @Private
        Status await(Long start, long remaining);
        
        L cancel();
        
        /////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////
        enum Status {
            LOCKED, UNLOCKED, CANCELED, TIMEOUT, INTERRUPTED;
            
            public boolean in(Status... statuses) {
                return Is.In(this, statuses);
            }
        }
        /////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////
    }
    
}
