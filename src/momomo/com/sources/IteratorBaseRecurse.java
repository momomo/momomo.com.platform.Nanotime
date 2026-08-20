package momomo.com.sources;

import momomo.com.Lambda;

/**
 * See {@link momomo.com.IO.Iterate} for direct usage
 * 
 * This adds recursive implemetation to IteratorBase 
 * 
 * @author Joseph S.
 */
public interface IteratorBaseRecurse<Type, Entry, E0 extends Exception> extends IteratorBase<Type, Entry, E0> {
    
    /**
     * Implemment method from {@link IteratorBase}
     */
    @Override
    default <E1 extends Exception> void each(Type dir, Lambda.R1E<Boolean, ? super Entry, E1> lambda) throws E0, E1 {
        each(dir, lambda, false);
    }
    
    /////////////////////////////////////////////////////////////////////
    // eachRecurse
    /////////////////////////////////////////////////////////////////////
    
    // Walk through each directory and file recursively, until all are processed
    default <E extends Exception> void eachRecurse(Type dir, Lambda.V1E<? super Entry, E> lambda) throws E {
        eachRecurse(dir, lambda.R1E());
    }
    
    default <E extends Exception> void eachRecurse(Type dir, Lambda.R1E<Boolean, ? super Entry, E> lambda) throws E {
        each(dir, lambda, true);
    }
    
    abstract <E extends Exception> Boolean each(Type dir, Lambda.R1E<Boolean, ? super Entry, E> lambda, boolean recurse) throws E;
    
}
