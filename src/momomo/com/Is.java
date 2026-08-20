/* Copyright(C) 2014 - 2020 Momomo LTD. Proprietary and confidential. Usage of this file on any medium without a written consent by Momomo LTD. is strictly prohibited. All Rights Reserved. */
package momomo.com;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * We use uppercase on methods to stay consistent so we also can do keyword protected checks, such as 
 * 
 *  Is.Null      (..) 
 *  Is.False     (..)
 *  Is.True      (..)
 *  Is.Double    (..)
 *  
 *  Other methods are
 *
 *  Is.Number    (..)
 *  Is.Equal     (..)
 *  Is.Primitive (..)
 *  Is.Between   (..)
 *  Is.Ok        (..)
 *  Is.Or        (..)
 *  Is.Any       (..)
 *  Is.In        (..)
 *  
 * and
 *  
 *  Is.Windows ()
 *  Is.Linux   ()
 *  Is.Mac     ()
 *             ()
 * and
 * 
 *  Is.Production  ()
 *  Is.Production  (true)
 *  Is.Development ()
 *  Is.Test        ()
 *  
 *  and 
 *  
 *  Is.RunningWithinIntellij ()
 *  
 *  ... and more ...
 *  
 *  
 * So all methods are uppercase for consistency.
 *
 * @author Joseph S.
 */
public class Is { private Is(){}

    public static final boolean
        On     = true,
        Off    = false,
        First  = On,
        Second = Off
    ;
    
    private static final IdentityHashMap<Class<?>, Boolean> PRIMITIVES = new IdentityHashMap<>();
    static {
        PRIMITIVES.put( Boolean.class  , Boolean.TRUE );
        PRIMITIVES.put( Character.class, Boolean.TRUE );
        PRIMITIVES.put( Byte.class     , Boolean.TRUE );
        PRIMITIVES.put( Short.class    , Boolean.TRUE );
        PRIMITIVES.put( Integer.class  , Boolean.TRUE );
        PRIMITIVES.put( Long.class     , Boolean.TRUE );
        PRIMITIVES.put( Float.class    , Boolean.TRUE );
        PRIMITIVES.put( Double.class   , Boolean.TRUE );
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    public static boolean Null(Object o) {
        return o == null;
    }
    
    public static boolean NotNull(Object o) {
        return o != null;
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    public static boolean Ok( Object o) {
        if ( Null(o) ) {
            return false;
        }
        
        if (o instanceof Boolean ) {
            return Is.Ok((Boolean) o);
        }
        
        if ( o instanceof CharSequence ) {
            return NotNull.Ok((CharSequence) o);
        }
        
        if ( o instanceof Number ) {
            return NotNull.Ok((Number) o);
        }
    
        if ( o instanceof Map ) {
            return NotNull.Ok((Map) o);
        }
        
        if ( o instanceof Collection ) {
            return NotNull.Ok((Collection) o);
        }
    
        if ( NotNull.isArray(o) ) {
            return NotNull.Array((Object[]) o);
        }
        
        return true;
    }
    
    public static boolean Ok(Boolean o) {
        return True(o);
    }
    
    public static boolean Ok(CharSequence o) {
        return o != null && NotNull.Ok(o);
    }
    public static boolean Ok(Number o) {
        return o != null && NotNull.Ok(o);
    }
    public static boolean Ok(Map o) {
        return o != null && NotNull.Ok(o);
    }
    public static boolean Ok(Object[] array) {
        return array != null && NotNull.Array(array);
    }
    public static boolean Ok(Collection<?> o) {
        return o != null && NotNull.Ok(o);
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    /**
     * If all Is.Ok(...).
     */
    public static boolean Ok(Object a, Object b) {
        return Ok(a) && Ok(b);
    }
    /**
     * If all Is.Ok(...)
     */
    public static boolean Ok( Object a, Object b, Object ... args) {
        boolean ok = Ok(a) && Ok(b); 
    
        if ( ok ) {
            
            if ( Is.NotNull(args) ) {
                for (Object arg : args) {
                    if ( !Ok(arg) ) return false;
                }
            }
        
            return true;
        }
        else {
            return false;
        }
    }
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Returns first if it is Ok() otherwise calls and returns whatever the lambda has to offer. 
     * 
     * Example
     *   Is.Ok(object, ()-> { 
     *      return somethingOnElse;  
     *   })
     */
    public static <R, E extends Exception> R Else(R o, Lambda.RE<R, E> Else) throws E {
        return Is.Ok(o) ? o : Else.call();
    }
    
    /**
     * Example
     *   Is.Ok(object, ()-> {
     *      // Do something on else 
     *   })
     *   
     *   Does not return anything.
     */
    public static <R, E extends Exception> void Else(R o, Lambda.VE<E> Else) throws E {
        Else(o, Else.RE());
    }
    
    /**
     * Is.Else(obj, ()-> { compute and return if true }, () -> { computer and return if false }) 
     * 
     * This method can be crucial when in a constructor and requiring a one line ( Java ) to create some magic prior to calling super. 
     * If else blocks there won't work, but this will.
     */
    public static <R, E extends Exception> R Else(R o, Lambda.RE<R, E> If, Lambda.RE<R, E> Else) throws E {
        return Is.Ok(o) ? If.call() : Else.call();
    }
    
    /**
     * Similar to the Groovy language elvis operator. 
     *      object ?: elze
     * Or JavaScript
     *      object || elze
     *      
     * Here we would do it as: 
     *      Is.Or(object, elze)
     */
    public static <R> R Or(R o, R elze) {
        return Ok(o) ? o : elze;
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    public static boolean False(Boolean o) {
        return Boolean.FALSE.equals(o);
    }
    
    public static boolean True(Boolean o) {
        return Boolean.TRUE.equals(o);
    }
    
    /**
     * Example
     *      Is.Between(100, 50, 200) ==> true
     *      Is.Between( 50, 50, 200) ==> true
     *      Is.Between(300, 50, 200) ==> false
     *      Is.Between( 10, 50, 200) ==> false
     *
     * @param from inclusive 
     * @param to   inclusive 
     */
    public static boolean Between(Number number, Number from, Number to) {
        return number.floatValue() >= from.floatValue() && number.floatValue() <= to.floatValue();
    }
    
    /**
     * If the number is a double number
     */
    public static boolean Double(Number o) {
        return o != null && o.toString().contains(".");
    }
    public static boolean Array(Object o) {
        return o != null && NotNull.isArray(o);
    }
    public static boolean Primitive(Object obj) {
        return obj != null && NotNull.isPrimitive(obj);
    }
    public static boolean Equal(Object a, Object b) {
        return Objects.equals(a, b);
    }
    
    public static boolean Equal(char[] word, char[] characters) {
        return Equal(word, characters, 0);
    }
    
    /**
     * @param from a certain index and forward
     */
    public static boolean Equal(char[] word, char[] characters, int from) {
        
        // lenght not the same, return false
        int length = word.length; if ( length > characters.length + from ) {
            return false;
        }
        
        // Compare character by character
        int i = 0; while ( i < length ) {
            if ( word[i] != characters[i++ + from] ) return false;
        }
        
        return true;
    }
    
    /////////////////////////////////////////////////////////////////////
    // In() & In.Jar() ... 
    /////////////////////////////////////////////////////////////////////
    
    /**
     * If a is in args.
     * 
     * @param a if null, then always false
     *          
     * @return searches through all args to try to find first instance of a, and if so, returns true, otherwise false.  
     */
    public static boolean In(Object a, Object ... args) {
        if ( a == null ) return false;
        
        if ( Is.NotNull(args) ) {
            for (Object o : args) {
                if (a.equals(o)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public static final class In { private In(){}
        
        /**
         * Example
         *  if ( Is.In.Jar(url) ) 
         *
         * @return true if url is in a jar file
         */
        public static boolean Jar(URL url) {
            return url.getProtocol().equals("jar");
        }
        
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    /**
     * @return true if any arg Is.Ok() otherwise false
     */
    public static boolean Any(Object ... args) {
        return Any(Is::Ok, args);
    }
    
    /**
     * @return return true if any arg is (arg != null && lambda.call(arg) == true), otherwise false. 
     */
    @SafeVarargs
    public static <A> boolean Any(Lambda.R1<Boolean, A> lambda, A... args) {
        Boolean ok;
        if ( Is.NotNull(args) ) {
            for (A arg : args) {
                if (Is.NotNull(arg) && Is.NotNull(ok = lambda.call(arg)) && Is.True(ok)) {
                    return true;    // We found something to be true
                }
            }
        }
        
        return false; // Nothing was true
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    public static boolean Symlink(File file) {
        return Is.Symlink( IO.toPath(file) );
    }
    public static boolean Symlink(Path path) {
        return Files.isSymbolicLink(path);
    }
    
    public static boolean Empty(File directory) {
        return Is.Empty( IO.toPath(directory) );
    }
    public static boolean Empty(Path directory) {
        try( DirectoryStream<Path> stream = Files.newDirectoryStream(directory) ) {
            return !stream.iterator().hasNext();
        }
        catch (IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    public static boolean Empty(Map<?, ?> o) {
        return o.isEmpty();
    }
    
    public static boolean Empty(Collection<?> o) {
        return o.isEmpty();
    }
    
    public static boolean Empty(CharSequence o) {
        return o.length() == 0;
    }
    
    public boolean Directory(CharSequence filepath) {
        return Directory( IO.toFile(filepath) );
    }
    public boolean Directory(File file) {
        return file.isDirectory();
    }
    
    public static boolean Empty(Object[] o) {
        return Array.getLength(o) == 0;
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    public static boolean Windows() {
        return Runtimes.isWindows();
    }
    
    public static boolean Mac() {
        return Runtimes.isMac();
    }
    
    public static boolean Linux() {
        return Runtimes.isLinux();
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    public static boolean RunningWithinIntellij() {
        return Runtimes.isRunningWithinIntellij(); 
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    
    public static boolean Production() {
        return $Environment.isProduction();
    }
    
    /**
     * @param loose true leads to environment treated as development despite it being production if also the flag to allow it is passed.
     */
    public static boolean Production(boolean loose) {
        return $Environment.isProduction(loose);
    }
    
    public static boolean Development() {
        return $Environment.isDevelopment();
    }
    
    public static boolean Test() {
        return $Environment.isTest();
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////// 
    
    /**
     * Private static class that performs operations on things that have already passed the null check. 
     * 
     * @author Joseph S.
     */
    private static final class NotNull { private NotNull(){}
    
        private static boolean Ok(Number o) {
            return true;    // A number is alway valid if not null
        }
        
        private static boolean Array(Object[] o) {
            return !Is.Empty(o);
        }
        
        private static boolean Ok(CharSequence o) {
            return !Is.Empty(o);
        }
        
        private static boolean Ok(Collection o) {
            return !Is.Empty(o);
        }
        
        private static boolean Ok(Map o) {
            return !Is.Empty(o);
        }
        
        private static boolean isArray(Object o) {
            return o.getClass().isArray();
        }
    
        private static boolean isPrimitive(Object obj) {
            return PRIMITIVES.containsKey(obj.getClass());
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
}
