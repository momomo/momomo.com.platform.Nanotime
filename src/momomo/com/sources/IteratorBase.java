package momomo.com.sources;

import momomo.com.Lambda;
import momomo.com.annotations.informative.Protected;

/**
 * This only provides implemetation without recursion. 
 * 
 * See {@link momomo.com.IO.Iterate} for direct usage
 * 
 * @author Joseph S.
 */
public interface IteratorBase<Type, Entry, E0 extends Exception> {
    
    /**
     * Note that order cannot be guranteed for this, and that URLIterator, JarIterator is always recursive
     */
    default <E1 extends Exception> void each(CharSequence url, Lambda.V1E<? super Entry, E1> lambda) throws E0, E1 {
        each(url, lambda.R1E());
    }
    /**
     * Note that order cannot be guranteed for this, and that URLIterator, JarIterator is always recursive
     */
    default <E1 extends Exception> void each(CharSequence url, Lambda.R1E<Boolean, ? super Entry, E1> lambda) throws E0, E1 {
        each(from(url), lambda);
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Note that order cannot be guranteed for this, and that URLIterator, JarIterator is always recursive
     */
    default <E1 extends Exception> void each(Type url, Lambda.V1E<? super Entry, E1> lambda) throws E0, E1 {
        each(url, lambda.R1E());
    }
    
    /**
     * Note that order cannot be guranteed for this, and that URLIterator, JarIterator is always recursive
     */
    abstract <E1 extends Exception> void each(Type url, Lambda.R1E<Boolean, ? super Entry, E1> lambda) throws E0, E1;
        
    
    @Protected Type from(CharSequence url);
    
}
