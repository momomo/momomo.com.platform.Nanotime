package momomo.com.sources;

import momomo.com.annotations.informative.Delegatable;
import momomo.com.annotations.informative.Protected;

import java.io.Serializable;

/**
 * @author Joseph S.
 */
public interface $CharSeq extends CharSequence, Serializable {
    public static final long serialVersionUID = 1L;
    
    @Override
    public String toString();
    
    @Override
    default int length() {
        return toString().length();
    }
    
    @Override
    default char charAt(int index) {
        return toString().charAt(index);
    }
    
    @Override
    default CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }
    
    @Delegatable @Protected default boolean $equals$(Object obj) {
        if ( this == obj ) return true;
        
        if ( obj == null ) return false;
    
        return toString().equals(obj.toString());
    }
    
    @Delegatable @Protected default int $hashCode$() {
        return toString().hashCode();
    }
    
}
