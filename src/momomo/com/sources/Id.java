package momomo.com.sources;

import momomo.com.Randoms;

/**
 * Any id considered an id, could / shall extend this. 
 * 
 * @author Joseph S.
 */
public class Id implements $CharSeq {
    public static final long serialVersionUID = 1L;

    private final String val;
    
    public Id() {
        this(Randoms.UUID(1));
    }
    
    public Id(CharSequence id) {
        this.val = id.toString();
    }
    
    @Override
    public String toString() {
        return val;
    }
    
    public String id() {
        return toString(); 
    }
    
    /////////////////////////////////////////////////////////////////////
    // Just delegates to implementation in super class CharSeq
    /////////////////////////////////////////////////////////////////////
    
    @Override
    public boolean equals(Object o) {
        return $CharSeq.super.$equals$(o);
    }
    
    @Override
    public int hashCode() {
        return $CharSeq.super.$hashCode$();
    }
    
    /////////////////////////////////////////////////////////////////////
    
}
