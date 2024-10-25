package momomo.com.sources;

import momomo.com.Is;
import momomo.com.Numbers;

/**
 * System.getProperty() | System.setProperty() related. 
 * 
 * A convinient wrapper for setting and getting system property values
 * 
 * Example
 *         public static final Sysprop REDIS_URL      = new Sysprop("momomo.com.redis.url"     , "127.0.0.1");
 *         public static final Sysprop REDIS_PORT     = new Sysprop("momomo.com.redis.port"    , "9002");
 *         public static final Sysprop REDIS_IDLE     = new Sysprop("momomo.com.redis.idle"    , "32");
 *         public static final Sysprop REDIS_PASSWORD = new Sysprop("momomo.com.redis.password", "NMsuiyh2b289BKbjs980HNOLB9982y3g2vj3209_s");
 *    
 *    which can then be used as: 
 *    
 *    REDIS_URL.get()
 *           or 
 *    REDIS_URL.set(...)
 *    
 * If the value has been passed as a system property, from say command line then that takes precedence, otherwise, the value specified in the java code will be used. 
 * This allows for the overriding of properties from the outside, while being able to utilize defaults for development environment and without having to rely on magic configuration files that nobody knows how to configure.  
 * 
 * @author Joseph S.
 */
public final class Sysprop {
    
    private final String key, dephault;
    private       String cached;
    
    public Sysprop(String key) {
        this(key, null);
    }
    
    public Sysprop(CharSequence key, Object dephault) {
        this.key      = key.toString();
        this.dephault = dephault == null ? null : ("" + dephault);
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public Sysprop set(String value) {
        System.setProperty(key, this.cached = value); return this;
    }
    public Sysprop setTrue() {
        return set("true");
    }
    
    public Sysprop setFalse() {
        return set("false");
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public String get() {
        return get(null);
    }
    
    /**
     * Returns the cached value if already called, otherwise reads if from System.getProperpty()
     * If alst not set, will use our supplied parameter if ok. 
     * If also not set, will attempt to see if there is a default value stored.
     */
    public String get(String fallback) {
        return Is.Ok(cached) ? cached : Is.Or(cached = System.getProperty(key), Is.Or(fallback, this.dephault) );
    }
    
    public String key() {
        return key; 
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public boolean isSet() {
        return !isNull();
    }
    
    public boolean isNull() {
        return get() == null;
    }
    
    public boolean isEqual(CharSequence value) {
        return value.toString().equals(get());
    }
    
    public boolean isTrue() {
        return isEqual("true");
    }
    
    public boolean isFalse() {
        return isEqual("false");
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public int toInteger() {
        return Numbers.toInt(this.get());
    }
    
}
