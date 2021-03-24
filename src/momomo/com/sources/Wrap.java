package momomo.com.sources;

import momomo.com.Is;

/**
 * Often used in combination with having to set a property from within a lambda which is not possible and the usual hack is to either use an AtomicReference, and Array with one item, or now, you can do this. 
 *
 * Example
 *   Wrap<Ticker> ticker = Wrap.it();
 *      or 
 *   Wrap<Ticker> ticker = Wrap.it(previousTicker);
 *   
 *   map.compute(..., k-> {
 *       ticker.it = value; 
 *   })
 *
 *   System.out.println( ticker.it.getName() ); 
 *
 * @author Joseph S.
 */
public final class Wrap<T> {
    public T it;
    
    private Wrap() { 
        this(null);   
    }
    private Wrap(T it) { 
        this.it = it; 
    }
    
    public static <T> Wrap<T> it() {
        return it(null);
    }
    /**
     * Allows it to be infered by methods and declarations for cleaner casting
     */
    public static <T> Wrap<T> it(T val) {
        return new Wrap<T>(val);
    }
    
    public boolean isOk() {
        return Is.NotNull(it);
    }
}
