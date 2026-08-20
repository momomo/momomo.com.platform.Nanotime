package momomo.com;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * @author Joseph S.
 */
public final class $Bytes implements $Base64.Interface {
    
    final static protected char[] HEX_ARRAY = "0123456789abcdef".toCharArray();     // Small cap to match Long.toHexString\
    
    public final byte[] bytes;
    
    public $Bytes(byte[] bytes) {
        this.bytes = bytes;
    }
    
    public String toHex() {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    @Override
    public String toString() {
        return toString(bytes, Strings.CHARSET);
    }
    
    public static String toString(byte[] bytes, Charset charset) {
        try {
            return new String(bytes, charset.toString());    
        }
        catch(UnsupportedEncodingException e) {
            throw Ex.runtime(e);
        }
    }
    
}
