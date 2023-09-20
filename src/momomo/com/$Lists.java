/* Copyright(C) 2014 - 2020 Momomo LTD. Proprietary and confidential. Usage of this file on any medium without a written consent by Momomo LTD. is strictly prohibited. All Rights Reserved. */
package momomo.com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 *
 * Operations on lists (List<?>) and a one stop reference to things performed on Lists  
 *
 * @author Joseph S.
 */
public final class $Lists {  private $Lists() {}
    
    /**
     * Returns the last item of an array. 
     */
    public static <T> T last(List<T> lst) {
        return lst.get(lst.size() - 1);
    }
    
    public static <T> List<T> remove( List<T> lst, int fromIndex, int toIndex ) {
        lst.subList(fromIndex, toIndex).clear();
        return lst;
    }
    
    public static <T> T removeLast(List<T> lst ) {
        T last = last(lst); lst.remove(lst.size()-1); return last;
    }
    
    @SafeVarargs
    public static <T, C extends Collection<T>> C addAll( C collection, T ... args ) {
        if ( Is.Ok(args)) {
            java.util.Collections.addAll(collection, args);
        }
        return collection;
    }
    
    /**
     * Returns the list after it has been reversed and acts as one stop reference
     */
    public static <T> List<T> reverse(List<T> lst) {
        java.util.Collections.reverse(lst); return lst;
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public static <T> List<T> copy(List<T> lst) {
        java.util.ArrayList<T> newList = new java.util.ArrayList<>();
        
        java.util.Collections.copy(lst, newList);
        
        return newList;
    }
    
    public static String join(java.util.List<String> col, java.lang.String delim) {
        return $Collections.join(col, delim);
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public static <R, T> ArrayList<R> collect(T[] elements, Lambda.R1<R, T> lambda) {
        return $Collections.collect(Arrays.asList(elements), lambda);
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    public static <T> T[] toArray( Class<T> klass, List<T> lst ) {
        return $Collections.toArray(klass, lst);
    }
    
    /**
     * Creates a list within the integer range 
     */
    public static <T> java.util.ArrayList<Integer> toList(int from, int to ) {
        java.util.ArrayList<Integer> lst = new java.util.ArrayList<>(to - from);
        while (from <= to) {
            lst.add(from);
            from++;
        }
        
        return lst;
    }
    
    /**
     * Creates a list within the character range 
     */
    public static <T> java.util.ArrayList<Character> toList(char from, char to ) {
        java.util.ArrayList<Character> lst = new java.util.ArrayList<>(to - from);
        while (from <= to) {
            lst.add(from); from++;
        }
        return lst;
    }
    
    public static HashMap<String, String> toMap(String... args ) {
        HashMap<String, String> map = new HashMap<>();
        int i = -1;  while ( ++i < args.length - 1) {
            map.put( args[i], args[++i] );
        }
        return map;
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
}
