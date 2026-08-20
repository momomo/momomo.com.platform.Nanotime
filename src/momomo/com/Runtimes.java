package momomo.com;

import momomo.com.sources.Id;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * System runtime operations class. 
 * 
 * @author Joseph S.
 */
public class Runtimes { private Runtimes(){}

    // Can always be used to measure elapsed time from 'this point' it is initialized
    public static final long      START = System.nanoTime(); 
    public static final RuntimeId ID    = new RuntimeId();
    
    // Faster read, can be used by non-critical processes to see if a shutdown has been initiated
    private static       boolean       SHUTDOWN_CHECK_ATOMIC_NOT = false;
    private static final AtomicBoolean SHUTDOWN_CHECK_ATOMIC     = new AtomicBoolean(false);
    
    private static final AtomicInteger  OUR_SHUTDOWN_HOOKS_WHO_HASH_RUN   = new AtomicInteger();
    private static final List<Lambda.V> OUR_SHUTDOWN_HOOKS                = new ArrayList<>();
    private static final Thread         OUR_SHUTDOWN_HOOK_LAST            = new Thread(Runtimes::boom);
    public  static final long           OUR_SHUTDOWN_HOOK_LAST_WAIT_FIRST = Is.Production() ? 15000 : 250;
    public  static final long           OUR_SHUTDOWN_HOOK_LAST_WAIT_FINAL = 1000;

    static {} 
    public static void configure() { /** Can be invoked to ensure statics are initialized **/ } 
    public static void configure(boolean onShutdown) {
        if ( onShutdown ) {
            /**
             * We used to run this in a static block before, however, since we are to release this as a shared library, we do not want to 
             * superimpose our shutdown logic, which now has to be called explictly 
             */
            onShutdown(Runtimes::shutdown);
        }
    }
    
    /**
     * Register shutdown hook. We do this by creating a thread for each which is a requirement.
     * Then we also add it our own shutdown hook logic with the idea that whoever runs first,  
     * 
     */
    public static void onShutdown(Lambda.V lambda) {
        AtomicBoolean called = new AtomicBoolean(); 
        
        Lambda.V wrapped = () -> {
            // We have every registered hook register as a fast as we can the info that we have begun shutdown
            SHUTDOWN_CHECK_ATOMIC.set((SHUTDOWN_CHECK_ATOMIC_NOT = true));  
        
            // If we have already invoked this, then we do not need to invoke it again 
            if ( called.getAndSet(true) ) return;
            
            // We try to run all of our own shutdown hooks as soon as we get the chance. 
            shutdown();
        
            try {
                lambda.run();
            } 
            catch (Throwable e) {
                log(e);
            }
        
            if ( OUR_SHUTDOWN_HOOKS_WHO_HASH_RUN.decrementAndGet() == 0 ) {
                try {
                    OUR_SHUTDOWN_HOOK_LAST.start();
                } 
                catch (Throwable e) {
                    log(e);              // It might fail if it is too late to start a thread
    
                    OUR_SHUTDOWN_HOOK_LAST.run();   // We try to run it without starting the thread
                }
            }
        };
    
        addShutdownHook(wrapped);
    }
    
    public static void shutdown() {
        // Run all hooks concurrently, and fire selfDestruct when they are all done.
        for (Lambda.V lambda : OUR_SHUTDOWN_HOOKS) {
            try {
                lambda.call();
            }
            catch (Throwable ignore) {
                // Continue with next
            }
        }
    }
    
    private static void addShutdownHook(Lambda.V wrapped) {
        OUR_SHUTDOWN_HOOKS_WHO_HASH_RUN.incrementAndGet();
        OUR_SHUTDOWN_HOOKS.add(wrapped);
        
        // Now register this hook also on system level
        Thread thread = new Thread(wrapped); getRuntime().addShutdownHook(thread); 
    }
    
    public static boolean isShuttingDown() {
        return SHUTDOWN_CHECK_ATOMIC_NOT;
    }
    
    public static boolean isShuttingDownAtomically() {
        return SHUTDOWN_CHECK_ATOMIC.get();
    }
    
    /**
     * Can be used for critical detection for if a shutdown has been initiated to abort what they are doing.
     *
     * Will have the thread calling sleep forever basically and allow for sporading wake ups for the system to be able to react if needed.   
     */
    public static void pauseIfShuttingDown() {
        while ( isShuttingDown() ) {
            sleepForever();
        }
    }
    
    /**
     * Can be used for critical detection for if a shutdown has been initiated to abort what they are doing.
     *
     * Will have the thread calling sleep forever basically and allow for sporading wake ups for the system to be able to react if needed.   
     */
    public static void pauseIfShuttingDownAtomically() {
        while ( isShuttingDownAtomically() ) {
            sleepForever();
        }
    }
    
    private static void sleepForever() {
        
        while (true) {
            Threads.sleep(1000, true);
        }
    }
    
    /**
     * Self destruct after sleep x if system still hasn't finished shutting down by itself by then. 
     * Will call System.exit(0) after which will blow up the entire universe so rest assured.    
     */
    private static void boom() {
        try {
            Runnable log = () -> {
                log("SHUTTING DOWN SOON! SHUTTING DOWN SOON! SHUTTING DOWN SOON! SHUTTING DOWN SOON! SHUTTING DOWN SOON!");
                log("SHUTTING DOWN SOON! SHUTTING DOWN SOON! SHUTTING DOWN SOON! SHUTTING DOWN SOON! SHUTTING DOWN SOON!");
                log("SHUTTING DOWN SOON! SHUTTING DOWN SOON! SHUTTING DOWN SOON! SHUTTING DOWN SOON! SHUTTING DOWN SOON!");
            };
            
            log.run();
    
            // Set myself as daemon if i am not already one. 
            Threads.sleep(OUR_SHUTDOWN_HOOK_LAST_WAIT_FIRST);
    
            log.run();
        }
        finally {
            // Note, System.exit(0); will not work as reliably, as it won't work unless the boom logic is called from a thread, separate from the shutdown hook logic, such as in its own thread
            // From documentation: Unlike the System.exit method, this method does not cause shutdown hooks to be started. 
            // For us, at this point, the boom method is known to have only been triggered from a shutdown.
            // However, to ensure full compliance, we opt to use a thread for the boom logic, to ensure everything else  
            try {
                System.exit(0);
            }
            finally {
                log("SHUTTING DOWN FORCIBLY IN " + OUR_SHUTDOWN_HOOK_LAST_WAIT_FINAL + "!");
                
                Threads.sleep(OUR_SHUTDOWN_HOOK_LAST_WAIT_FINAL);
                
                Runtime.getRuntime().halt(0); // This will forcebly exit all
            }
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Package private. Use Is.Windows() instead. 
     */
    private static Boolean windows; static boolean isWindows() {
        if ( Is.Null(windows) || !Is.Production() ) {
            return windows = $os$().contains("windows");
        }
        return windows;  
    }
    
    /**
     * Package private. Use Is.Mac() instead. 
     */
    private static Boolean mac; static boolean isMac() {
        if ( Is.Null(mac) || !Is.Production() ) {
            return mac = $os$().contains("mac os x");
        }
        return mac;
    }
    
    /**
     * Package private. Use Is.Linux() instead. 
     */
    private static Boolean linux; static boolean isLinux() {
        if ( Is.Null(linux) || !Is.Production() ) {
            return linux = !Is.Windows() && !Is.Mac();
        }
        return linux;
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Package private. Use Is.RunningWithinIntellij instead. 
     */
    static boolean isRunningWithinIntellij() {
        List<String> list = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String property : list) {
            if (property.toLowerCase().contains("intellij")) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return mac if os is mac, linux if linux, windows if windows, other for everything else.
     */
    private static String os; public static String os() {
        if ( Is.Null(os) || !Is.Production() ) {
            return os = Is.Mac() ? "mac" : Is.Linux() ? "linux" : Is.Windows() ? "windows" : "other";
        }
        return os; 
    }
    
    public static String getHostname() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

    public static int getAvailableProcessors() {
        return getRuntime().availableProcessors();
    }
    
    public static Runtime getRuntime() {
        return Runtime.getRuntime();
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Elapsed in nanoseconds since "system" start / access of this class
     */
    public static long getElapsedSinceStartInNanos() {
        return System.nanoTime() - START;
    }

    /**
     * Elapsed in nanoseconds since "system" start / access of this class
     */
    public static long getElapsedSinceStartInMillis() {
        return TimeUnit.NANOSECONDS.toMillis(getElapsedSinceStartInNanos());
    }
    
    public static String getObjectIdentityHashCode(Object o) {
        return Integer.toHexString(System.identityHashCode(o));
    }
    
    private static String $os$() {
        return System.getProperty("os.name").toLowerCase();
    }

    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////

    /**
     * Info about the Java Runtime Environment.
     * 
     * Example 
     *      $Runtimes.JRE.home()
     *      $Runtimes.JRE.bin()
     *      $Runtimes.JRE.java()
     * 
     * @author Joseph S.
     */
    public static final class JRE {
        /**
         * Example: /momomo/Other/Software/java/jdk1.8.0_40/jre/
         */
        public static String home() {
            return System.getProperty("java.home");
        }

        public static String bin() {
            return home() + "/bin";
        }

        public static String java() {
            return bin() + "/java";
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////

    public static final class RuntimeId extends Id {}

    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////

    // Note. At this point when these are used, on shutdown, we can no longer rely loggers to work.
    // So we print to console instead

    private static void log(Throwable e) {
        e.printStackTrace();
    }

    private static void log(String messag) {
        System.out.println(messag);
    }

    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////

}
