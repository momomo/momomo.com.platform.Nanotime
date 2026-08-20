package momomo.com;

import momomo.com.annotations.informative.Development;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Operations on Numbers and a one stop reference to things performed on Numbers.
 * 
 * @author Joseph S.
 */
public final class Numbers { private Numbers(){};
    private static final AtomicLong NEXT = new AtomicLong(0L);
    
    /**
     * Should start over with zero once MAX_VALUE has been reached
     */
    public static long next() {
        return NEXT.incrementAndGet();
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Example
     *      $Numbers.each( 1, 999, (i) -> {
     *           sb.append("<transponder frequency='"+ i +"'/>");
     *      });
     * 
     * Very similar to IntStream.range().forEach 
     * 
     * @param from inclusive
     * @param to exlusive
     */
    public static <E extends Exception> void each(int from, int to, Lambda.V1E<Integer,E> lambda) throws E {
        while ( from < to ) {
            lambda.call(from++);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    public static double toDouble(Number value) {
        return value == null ? 0D : value.doubleValue();
    }
    
    public static Double toDouble(Object value) {
        return value == null ? null : value instanceof Number ? ((Number) value).doubleValue() : toDouble(value.toString());
    }
    
    public static Double toDouble(String value) {
        if ( value == null ) return null;
        
        try {
            return Double.parseDouble(value);
        }
        catch (Throwable ignore) {
            return null;
        }
    }
    
    /**
     * With locale. 
     * 
     * Example
     *   toDouble("123,3232,434.23", Locale.US)
     */
    public static Double toDouble(String value, Locale locale) {
        if ( value == null ) return null;
    
        try {
            return NumberFormat.getInstance(locale).parse(value).doubleValue();
        } catch (Throwable ignore) {
            return null;
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public static int toInt(Number value) {
        return value == null ? 0 : value.intValue();
    }
    
    public static Integer toInt(Object value) {
        return value == null ? null : value instanceof Number ? ((Number) value).intValue() : toInt(value.toString());
    }
    
    public static Integer toInt(String value) {
        if ( value == null ) return null;
        
        try {
            return Integer.parseInt(value);
        }
        catch (Throwable e) {
            return null;
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public static long toLong(Number value) {
        return value == null ? 0L : value.longValue();
    }
    
    public static Long toLong(Object value) {
        return value == null ? null : value instanceof Number ? ((Number) value).longValue() : toLong(value.toString());
    }
    
    public static Long toLong(String value) {
        if ( value == null ) return null;
        
        try {
            return Long.parseLong(value);
        }
        catch (Throwable e) {
            return null;
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Used to increment versions from for instance, pom.xml or plugin.xml
     * 
     * Example
     *  increment("1.22", 0.01, 0, 2) == "1.23"
     *  increment("1.22", 0.1 , 0, 2) == "1.32"
     *   
     */
    public static String increment(Object number, double with, int minDecimals, int maxDecimals) {
        return format( toDouble(number) + with, 1, minDecimals, maxDecimals );
    }
    
    /**
     * Formats a String and the number of integer number, as well as min max decimals
     * 
     * Example
     *    $Numbers.toString(1.23456, 1, 0, 2) == "1.23"
     *    $Numbers.toString(1.23456, 2, 0, 2) == "01.23"
     *    $Numbers.toString(1.23456, 1, 0, 3) == "1.234"
     *    $Numbers.toString(1.2    , 1, 2, 5) == "1.20"
     *    $Numbers.toString(1.2    , 2, 2, 5) == "01.20"
     */
    public static String format(double number, int minimiumInteger, int minDecimals, int maxDecimals) {
        return formatter(minimiumInteger, minDecimals, maxDecimals).format(number);
    }
    
    public static String format(long number, int minimiumInteger, int minDecimals, int maxDecimals) {
        return formatter(minimiumInteger, minDecimals, maxDecimals).format(number);
    }
    
    public static NumberFormat formatter(int minimiumInteger, int minDecimals, int maxDecimals) {
        return formatter(minimiumInteger, minDecimals, maxDecimals, null);
    }
    public static NumberFormat formatter(int minimiumInteger, int minDecimals, int maxDecimals, RoundingMode roundingMode) {
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMinimumFractionDigits(minDecimals);
        formatter.setMaximumFractionDigits(maxDecimals);
        formatter.setMinimumIntegerDigits(minimiumInteger);
        formatter.setGroupingUsed(false);
        
        if ( roundingMode != null ) {
            formatter.setRoundingMode(roundingMode);
        }
        return formatter;
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Returns the shortest form of bytes for a number / long
     * @author Joseph S.
     */
    public static byte[] toBytes(long number) {
        ArrayList<Byte> list = new ArrayList<>(8);

        list.add( (byte) number         );
        list.add( (byte) (number >> 8)  );
        list.add( (byte) (number >> 16) );
        list.add( (byte) (number >> 24) );
        list.add( (byte) (number >> 32) );
        list.add( (byte) (number >> 40) );
        list.add( (byte) (number >> 48) );
        list.add( (byte) (number >> 56) );

        int size; while ( list.get( size = list.size() - 1 ) == 0 ) {
            list.remove( size ); // O(1) operation
        }

        byte[] array = new byte[list.size()];
        int j = -1;
        while (++j < list.size()) {
            array[j] = list.get(j);
        }

        return array;
    }
    /**
     * @param bytes representing a number
     */
    public static long fromBytes(byte[] bytes) {
        long number = 0L;

        for (int i = 0; i < bytes.length; i++){
            number = number | ((bytes[i] & 0xff) << i*8);
        }

        return number;
    }

    public static String toBase64(long number) {
        return $Base64.to64String(number);
    }
    
    public static long fromBase64(String number) {
        return fromBytes($Base64.from64(number));
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public static String toHex(Number number) {
        return Long.toHexString(number.longValue());
    }

    /////////////////////////////////////////////////////////////////////
    
    /////////////////////////////////////////////////////////////////////
    
    @Development private static void example() {
        String s = toBase64(64);
        
        System.out.println(s);
        
        long l = fromBase64(s);
        
        System.out.println(l);
    }
    
}
