/* Copyright(C) 2014 - 2020 Momomo LTD. Proprietary and confidential. Usage of this file on any medium without a written consent by Momomo LTD. is strictly prohibited. All Rights Reserved. */
package momomo.com;

import momomo.com.Lambda;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Operations on collections (sets, lists, ... ) and a one stop reference to things performed on Collections that do not fit into the dedicated ones.   
 * 
 * @author Joseph S.
 */
public class $Collections {
    protected $Collections(){}
    
    public static String join(java.util.Collection<String> col, java.lang.String delim) {
        return String.join(delim, col);
    }
    
    /**
     *  Find and return from collection following a boolean criteria
     */
    public static <T> T find(java.util.Collection<T> objects, Lambda.R1<Boolean, T> cloj) {
        for ( T obj : objects) {
            if ( cloj.call(obj) )                     // If user lambda returned true, then object is found and we abort and return it
                return obj;
        }
        return null;
    }
    
    /**
     *  Find and return from an array following a boolean criteria
     */
    public static <T> T find(T[] objects, Lambda.R1<Boolean, T> cloj) {
        for ( T obj : objects) {
            if ( cloj.call(obj) )                     // If user lambda returned true, then object is found and we abort and return it
                return obj;
        }
        return null;
    }
    
    /* Allows you return a modified list of each entry */
    public static <IN, OUT> ArrayList<OUT> collect(java.util.Collection<IN> lst, Lambda.R1<OUT, IN> cloj) {
        ArrayList<OUT> newLst = new ArrayList<>();
        for ( IN obj : lst )
            newLst.add ( cloj.call(obj) );
        return newLst;
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public static <T> T[] toArray(Class<T> klass, Collection<T> collection) {
        return collection.toArray((T[]) Array.newInstance(klass, collection.size()));
    }
    
    
    
}
