package momomo.com;

import momomo.com.exceptions.$DatabaseSQLException;
import momomo.com.exceptions.$IOException;
import momomo.com.exceptions.$InterruptedException;
import momomo.com.exceptions.$ReflectionException;
import momomo.com.exceptions.$RuntimeException;
import momomo.com.sources.WriterPrint;

import java.io.IOException;
import java.sql.SQLException;


/**
 * Exceptions related utility 
 * 
 *@author Joseph S. 
 */
public class Ex { private Ex(){}

    private static final String INDENT = Strings.TAB;
    
    /////////////////////////////////////////////////////////////////////
    
    private static final boolean LOG_IGNORE = true;
    
    /**
     * Logs and throws
     */
    public static void log(Lambda.VE<? extends Exception> lambda) {
        log(lambda, LOG_IGNORE);
    }
    
    /**
     * Logs and throws
     */
    public static void log(Lambda.VE<? extends Exception> lambda, boolean ignore) {
        log(lambda.RE(), ignore);
    }
    
    /**
     * Logs and throws
     */
    public static <T>T log(Lambda.RE<T, ? extends Exception> lambda) {
        return log(lambda, LOG_IGNORE);
    }
    
    /**
     * Logs and throws unless ignored
     */
    public static <T>T log(Lambda.RE<T, ? extends Exception> lambda, boolean ignore) {
        try {
            return lambda.call();
        }
        catch (Throwable e) {
            log.error(Ex.class, e);
            
            if ( !ignore ) {
                throw runtime(e); 
            }
        }
        
        return null;
    }
    
    /////////////////////////////////////////////////////////////////////
    // Transforms a checked IOException to a runtime based $IOException
    /////////////////////////////////////////////////////////////////////
    
    public static <T> T io(Lambda.RE<T, ? extends IOException> lambda) {
        try {
            return lambda.call();
        }catch(IOException e) {
            throw new $IOException(e);
        }
    }
    
    public static void io(Lambda.VE<? extends IOException> lambda) {
        try {
            lambda.call();
        }catch(IOException e) {
            throw new $IOException(e);
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////
    // Transforms a checked InterruptedException to a runtime based $InterruptedException
    /////////////////////////////////////////////////////////////////////
    
    public static <T> T interrupted(Lambda.RE<T, ? extends InterruptedException> lambda) {
        try {
            return lambda.call();
        }catch(InterruptedException e) {
            throw new $InterruptedException(e);
        }
    }
    
    public static void interrupted(Lambda.VE<? extends InterruptedException> lambda) {
        try {
            lambda.call();
        }catch(InterruptedException e) {
            throw new $InterruptedException(e);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    // Transforms any checked Throwable to a runtime based one if handled, otherwise, a $RuntimeException 
    /////////////////////////////////////////////////////////////////////

    /**
     * Converts a checked exception into an unchecked runtime exception
     * Ignores checked exceptions and throws Runtime once instead, allowing us to ignore catching exceptions if we do not want to
     */
    public static void runtime(Lambda.VE<? extends Throwable> lambda) {
        try {
            lambda.call();
        }catch(Throwable e) {
            throw runtime(e);
        }
    }

    /**
     * Converts a checked exception into an unchecked runtime exception
     * Ignores checked exceptions and throws Runtime once instead, allowing us to ignore catching exceptions if we do not want to
     */
    public static <T> T runtime(Lambda.RE<T, ? extends Throwable> lambda) {
        try {
            return lambda.call();
        }catch(Throwable e) {
            throw runtime(e);
        }
    }
    
    public static RuntimeException runtime(Throwable e) {
        if ( e instanceof RuntimeException ) {
            return runtime( (RuntimeException) e );  // No need to wrap. Throw as is!
        }
    
        if ( e instanceof IOException ) {
            return runtime( (IOException) e );
        }
        
        if ( e instanceof InterruptedException ) {
            return runtime((InterruptedException) e);
        }
    
        if ( e instanceof SQLException ) {
            return runtime((SQLException) e);
        }
    
        if ( e instanceof ReflectiveOperationException ) {
            return runtime( ( ReflectiveOperationException) e );
        }
    
        return new $RuntimeException(e); 
    }
    
    public static RuntimeException runtime(RuntimeException e) {
        return e;
    }
    
    public static $RuntimeException runtime(IOException e) {
        return new $IOException(e);
    }
    
    public static $InterruptedException runtime(InterruptedException e) {
        return new $InterruptedException(e);
    }
    
    public static $DatabaseSQLException runtime(SQLException e) {
        return new $DatabaseSQLException(e);
    }
    
    public static $ReflectionException runtime(ReflectiveOperationException e) {
        return new $ReflectionException(e);
    }

    /////////////////////////////////////////////////////////////////////
    // Safed
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Can be used for method that call a lambda, but that does not allow the throwing of an exception.
     * 
     * This wraps the lambda, provides a non throwing one, then allwows you to call your logic passing the safed lambda to use, and if an exception occurs, the exception will be thrown after. 
     * 
     * Example  
     * 
     *     return Ex.safed(lambda, (safed) -> {
     *         return (R) GLOBALS.computeIfAbsent(lambda, (k)-> {
     *             return safed.call();
     *         });
     *     });
     *   
     */
    public static  <P, E extends Exception> void safed(Lambda.V1E<P, E> lambda, Lambda.V1<Lambda.V1<P>> safed) throws E {
        Throwable[] ex = new Throwable[0];
        
        safed.call((u)-> {
            try {
                lambda.call(u);
            } catch (Throwable e) {
                ex[0] = e;
            }
        });
        
        if ( ex[0] != null ) throw (E) ex[0];
    }
    
    /**
     * Can be used for method that call a lambda, but that does not allow the throwing of an exception.
     *
     * This wraps the lambda, provides a non throwing one, then allwows you to call your logic passing the safed lambda to use, and if an exception occurs, the exception will be thrown after. 
     *
     * Example  
     *
     *     return Ex.safed(lambda, (safed) -> {
     *         return (R) GLOBALS.computeIfAbsent(lambda, (k)-> {
     *             return safed.call();
     *         });
     *     });
     *
     */
    public static  <R, E extends Exception> R safed(Lambda.RE<R, E> lambda, Lambda.R1<R, Lambda.R<R>> safed) throws E {
        Throwable[] ex = new Throwable[0];
        
        R returns = safed.call(()-> {
            try {
                return lambda.call();
            } catch (Throwable e) {
                ex[0] = e; return null;
            }
        });
    
        if ( ex[0] != null ) throw (E) ex[0];
        
        return returns;
    }
    
    /////////////////////////////////////////////////////////////////////
    // Bubble
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Use with extreme caution!
     * All usage of this method must be commented with a logical explanation to why it is used.
     * Currently forbidden to use these methods in any project at Momomo LTD, except for when casting to a RuntimeException does not make sense such as in map.computeIfAbsent. 
     * 
     * Should be called as Ex.<E>Bubble(ex);
     *
     * Turns any checked exception into whatever exception you are sure to be throwing without casting or converting the exception type, but allows the excpetion type to be dismissed into whatever by the compiler.
     * There are cases where this makes sense becasue Java will not work well with multiple exception types for lambdas that throws several different exception types.
     * But these cases can usually be resolved by other means, such as using anonymous inner class over lambdas.
     * 
     * Similar to lombok 
     * @see lombok.SneakyThrows
     */
    public static <E extends Throwable> E bubble(Throwable ex) throws E {
        throw (E) ex;       // Silences checked exceptions. Casting won't actually work but fools the compiler.
    }
    public static <R, E extends Throwable> R bubble(Lambda.RE<R, ? extends Throwable> lambda) throws E {
        try {
            return lambda.call();
        } catch (Throwable e) {
            throw Ex.<E>bubble(e);
        }
    }
    public static <E extends Throwable> void bubble(Lambda.VE<? extends Throwable> lambda) throws E {
        try {
            lambda.call();
        } catch (Throwable e) {
            throw Ex.<E>bubble(e);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    
    public static void append(StringBuilder sb, Throwable ex) {
        append(sb, ex, "");
    }
    
    public static void append(StringBuilder sb, StackTraceElement[] trace) {
        sb.append( toString(trace) );
    }
    
    public static void append(StringBuilder sb, Throwable ex, String indent) {
        WriterPrint writer = new WriterPrint();
        
        StackTraceElement[] stracktrace = ex.getStackTrace();
        
        // An exception always starts with a newline
        writer.println(indent + Strings.LINE);
        writer.println(indent + ex.getClass().getName());
        writer.println(indent + Strings.LINE);
        
        if ( writer.write(stracktrace, indent) ) {
            writer.println();
        }
        
        String message = ex.getLocalizedMessage();
        if ( Is.Ok(message) ) {
            
            if ( Is.Off && Is.Production() ) {
                // Just intend the first line of the message
                message = indent + message;
            }
            else {
                // In non production environments, we try a little harder to get the exception message indented properly,
                // This is in order to prevent confusion on where a message stars and ends
                // It's a bit expensive so we ignore it in production.
                message = Strings.Prepend.with(message, indent);
            }
            
            writer.println(Strings.NEWLINE + Strings.unchar(message, Strings.NEWLINE, LOG_IGNORE, LOG_IGNORE));
        }
        
        sb.append(writer.getWriter().toString());
        
        // Unsure about the formatting for surpressed and if they are in the right location right now
        Throwable[] suppressed = ex.getSuppressed();
        if ( suppressed.length > 0 ) {
            sb.append(Strings.NEWLINE);
            for (Throwable exception : suppressed) {
                append(sb, exception, indent + INDENT);
            }
        }
        
        Throwable cause = ex.getCause();
        if (cause != null) {
            sb.append(Strings.NEWLINE);
            append(sb, cause, indent + INDENT);
        }
    }
    
    public static String toString(StackTraceElement[] trace) {
        WriterPrint writer;
        
        (writer = new WriterPrint()).write(trace, "");
        
        return writer.toString();
    }

    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
}
