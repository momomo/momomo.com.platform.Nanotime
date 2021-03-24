package momomo.com.sources;

import momomo.com.Lambda;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Best one. Most versatile.
 * <p>
 * Differs from CountDownLatch in that the counter can be incremented and decremented at any time and is not fixed.
 * <p>
 * Once it notifies, by cancel, decrement(number) or unlock ( will release despite what the actual counter is ), status will reset and the counter can continue to be incremented and decremented.
 * <p>
 * Should be 100% concurrently safe.
 *
 * @author Joseph S.
 */
public final class LockCount implements Lock<LockCount>, Lock.Await<LockCount> {
    
    protected final Object lock   = new Object();
    protected       Status status = null;
    
    protected long waiters = 0;
    protected long count   = 0;
    
    public LockCount() {
        this(0);
    }
    
    public LockCount(int count) {
        this.count += count;
    }
    
    @Override
    public LockCount cancel() {
        synchronized (lock) {
            count = 0; unlock(Status.CANCELED); return this;
        }
    }
    
    @Override
    public LockCount lock() {
        increment(); return this;
    }
    
    @Override
    public LockCount unlock() {
        decrement(); return this;
    }
    
    protected LockCount unlock(Status status) {
        synchronized (lock) {
            if ( this.status == null ) {
                this.status = status;
                lock.notifyAll();
            }
            
            return this;
        }
    }
    
    public long increment() {
        return increment(1L);
    }
    
    /**
     * Can be used with negative values to decrement without notifying.
     */
    public long increment(long times) {
        synchronized (lock) {
            return (count += times);
        }
    }
    
    public long decrement() {
        return decrement(0L);
    }
    
    public long decrement(long notifyOn) {
        synchronized (lock) {
            if ( --count == notifyOn ) {
                unlock(Status.UNLOCKED);
            }
            return count;
        }
    }
    
    public long count() {
        // Lock likely not needed, but better be safe than sorry. It's still just likely and to figure that out with 100% accuracy is a time consuming task not worthy the optimization.
        synchronized (lock) {
            return count;
        }
    }
    
    /**
     * Example:
     * <p>
     * Decrement and notify.
     *
     * <code>
     * lock.synchronize(()->{
     *     lock.decrement(lock.get()-1);
     * });
     * </code>
     */
    public <E extends Exception> void synchronize(Lambda.VE<E> lambda) throws E {
        synchronized (lock) {
            lambda.call();
        }
    }
    
    public Status await(Long start, long remaining) {
        synchronized (lock) {
            try {
                ++waiters;
                
                if ( count == 0 ) {
                    return Status.UNLOCKED; // throw new IllegalStateException("You should increment the lock at least once prior to calling await, as another thread can only wake you up upon decrement.");
                }
                
                while ( this.status == null ) {
                    try {
                        if ( start == null ) {
                            lock.wait();
                        }
                        else if ( remaining >= 0 ) {
                            long m; lock.wait(m = TimeUnit.NANOSECONDS.toMillis(remaining), (int) (remaining - TimeUnit.MILLISECONDS.toNanos(m)));
                            
                            // remaining - elapsed -> remaining
                            remaining -= System.nanoTime() - start;
                        }
                        else {
                            return Status.TIMEOUT;
                        }
                    }
                    catch ( InterruptedException e ) {
                        // The waiting thread was interrupted for some reason, like system shutdown unless status has been intentionally set and interrupted (do not think the latter occurs)
                        return status == null ? Status.INTERRUPTED : status;
                    }
                }
                
                return status;
            } finally {
                // Reset the lock first when all already registered callers have exited
                if ( --waiters == 0 && this.status != null ) {
                    this.status = null;
                }
            }
        }
    }
    
}
