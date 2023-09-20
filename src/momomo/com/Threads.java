/* Copyright(C) 2014 - 2020 Momomo LTD. Proprietary and confidential. Usage of this file on any medium without a written consent by Momomo LTD. is strictly prohibited. All Rights Reserved. */
package momomo.com;

import momomo.com.exceptions.$InterruptedException;
import momomo.com.sources.Lock;
import momomo.com.sources.LockCount;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * Threads related stuff without the checked exception annoyances. 
 * 
 * Also some util methods for doing some common things with threads.  
 * 
 * @author Joseph S.
 */
public final class Threads { private Threads(){}
    
    public static final boolean CONSTANT = true;
    public static final boolean ASYNC = false;
    
    /////////////////////////////////////////////////////////////////////
    
    public static final class Configurable {
        public static boolean DAEMON_GOBBLE    = true;
        public static boolean DEAMON_DEFAULT   = true;
        
        public static boolean SLEEP_GOBBLE     = false;
        public static int     SLEEP_RANDOM_MIN = 300;
        public static int     SLEEP_RANDOM_MAX = 800;
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Creates a thread with certain defaults
     */
    public static Thread thread(Lambda.VE<?> lambda) {
        Thread thread = new Thread(()-> {
            try {
                lambda.call();
            }
            catch(Throwable e) {
                // Make a check that system is not shutting down before proceeding with the throw.
                Runtimes.pauseIfShuttingDownAtomically();
                
                throw Ex.runtime(e);
            }
        });
        
        setDaemon(thread); return thread;
    }
    
    /**
     * This method creates a new thread, sets it as a daemon thread values and starts it.
     *
     * It catches the checked InterruptedException which is instead caught and turned into our runtime equivalent.
     *
     * @throws momomo.com.exceptions.$InterruptedException
     */
    public static Thread async(Lambda.VE<?> lambda) {
        Thread thread = thread(lambda); thread.start(); return thread;
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    public static void setDaemon(Thread thread) {
        setDaemon(thread, Configurable.DEAMON_DEFAULT);
    }
    
    public static void setDaemon(Thread thread, boolean value) {
        thread.setDaemon(value);    // We attempt to set this thread to a daemon thread to allow for the system to shutdown if possible.
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Basically Thread.sleep without the checked InterruptedException which is instead caught and turned into our runtime equivalent.
     *
     * @throws momomo.com.exceptions.$InterruptedException
     */
    public static boolean sleep(long millis) {
        return sleep(millis, Configurable.SLEEP_GOBBLE);
    }
    
    /**
     * Basically Thread.sleep without the checked InterruptedException which is instead caught and turned into our runtime equivalent.
     *
     * @throws momomo.com.exceptions.$InterruptedException
     */
    public static boolean sleep(long millis, boolean gobble) {
        return sleep(millis, 0, gobble);
    }
    
    /**
     * Sleep a random amount of time between min and max
     *
     * @throws momomo.com.exceptions.$InterruptedException
     */
    public static boolean sleep(long min, long max) {
        return sleep(Randoms.Long(min, max));
    }
    
    /**
     * Sleep a configurable random amount of time
     *
     * This is used mostly in development for throttling to test various scenarios
     */
    @momomo.com.annotations.informative.Development
    public static void sleep() {
        sleep(Randoms.Long(Configurable.SLEEP_RANDOM_MIN, Configurable.SLEEP_RANDOM_MAX));
    }
    
    public static boolean sleep(long millis, int nanos, boolean gobble) {
        if ( millis <= 0 && nanos == 0 ) return false;
        
        try {
            Thread.sleep(millis, nanos);
        } catch (InterruptedException e) {
            if ( !gobble) throw new $InterruptedException(e);
        }
        
        return true;
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Wait until this atomic integer is no longer zero.
     *
     * Will wake up when notify on that same instance occurs.
     *
     * @see Threads#decrement(java.util.concurrent.atomic.AtomicInteger) for util to release 
     */
    public static void await(AtomicInteger atomic) {
        synchronized (atomic) {
            // while status has not been reset to null in decrement yet, meaning all waiters has had the oppurtonity to exit before we, the unlocker gets to continue running
            while (atomic.get() > 0) {
                try {
                    atomic.wait();
                } catch (InterruptedException e) {
                    throw new $InterruptedException(e);
                }
            }
        }
    }
    
    /**
     * Notifies on atomic when decrement hits zero and releases potential awaitees. 
     *
     * @see Threads#await(java.util.concurrent.atomic.AtomicInteger)
     */
    public static void decrement(AtomicInteger atomic) {
        synchronized (atomic) {
            if (atomic.decrementAndGet() == 0) {
                atomic.notify();
            }
        }
    }
    
    /**
     * Like sleep uses wait to achieve it which is fitting in JXBrowser ( Doesn't lock everyting like sleep does there ). 
     *
     * The timeout eventually will wake the awaitee. 
     */
    public static boolean await(long timeout) {
        LockCount lock = new LockCount(1);
        
        if ( lock.await(timeout) == Lock.Await.Status.TIMEOUT ) {
            return true;
        };
        
        return false;
    }
    
    
    /////////////////////////////////////////////////////////////////////
    // setInterval
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Execute lambda every millis forever.  
     *
     * Will lock forever.  
     */
    public static void setInterval(Lambda.VE<?> lambda, long millis) {
        setInterval(lambda, millis, ()-> false);
    }
    
    /**
     * Execute lambda every millis, untile conditions is met. 
     * 
     * Will lock
     */
    public static void setInterval(Lambda.VE<?> lambda, long millis, Lambda.RE<Boolean, ?> condition) {
        setInterval(lambda, millis, condition, CONSTANT, ASYNC);
    }
        
    /**
     * Similar to JavaScripts setInterval, where a command will run over and over again in a thread of its own, until a condition has been met. 
     * 
     * Note that JavaScripts setInterval by default does not execute immediately, but does so after the sleep. We however execute immediately and then each time. 
     *
     * @param constant rate. If true, we decrease the amount of sleep depending on how long everything took to execyte to ensure we run the logic at the same rate each time and the execution time does not impact the rate of execution. 
     * @param async if you desire to run this within a new thread, or wether perhaps you are already in a thread and which run while blocking further execution. 
     */
    public static void setInterval(Lambda.VE<?> lambda, long millis, Lambda.RE<Boolean, ?> condition, boolean constant, boolean async) {
        Lambda.V whiled = () -> {
            
            while ( true ) {
                try {
                    long sleep = millis;
                    
                    long now   = Nano.time();
                    
                    if ( Is.True(condition.call()) ) break;
    
                    lambda.call();
                     
                    if ( constant)  {
                        long elapsed = TimeUnit.MILLISECONDS.toMillis(Nano.time() - now);
                        sleep        = millis - elapsed;
                    }
                    
                    sleep( sleep );
                }
                catch(Throwable e) {
                    log.error(Threads.class, e); // We continue, an exception does not break us free, only the condition becoming true will.
                }
            }
        };
        
        if ( async ) {
            async(whiled);
        }
        else {
            whiled.call();    
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    @momomo.com.annotations.informative.Development
    public static final class Development {
        public static void setTimeout(ScheduledThreadPoolExecutor executor, Lambda.VE<?> lambda, double time) {
            executor.schedule(() -> {
                try {
                    lambda.call();
                } catch (Throwable e) {
                    throw Ex.runtime(e);
                }
                
            }, Numbers.toLong(time * 1000000), TimeUnit.NANOSECONDS);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
}






































