package momomo.com;

import java.util.Base64;

/**
 * 
 * @author Joseph S.
 */
public final class $Base64 {
    
    /////////////////////////////////////////////////////////////////////
    
    public static byte[] from64(CharSequence val) {
        return from64(Interface.DECODER_DEFAULT, val);
    }
    
    public static byte[] from64(Base64.Decoder decoder, CharSequence val) {
        return from64(decoder, Strings.toBytes(val));
    }
    
    public static byte[] from64(byte[] bytes) {
        return from64(Interface.DECODER_DEFAULT, bytes);
    }
    
    public static byte[] from64(Base64.Decoder decoder, byte[] bytes) {
        return decoder.decode(bytes);
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public static byte[] to64(Base64.Encoder encoder, byte[] bytes) {
        return encoder.encode(bytes);
    }
    
    public static String to64String(byte[] bytes) {
        return to64String(Interface.ENCODER_DEFAULT, bytes);
    }
    
    public static String to64String(CharSequence seq) {
        return to64String(Strings.toBytes(seq));
    }
    
    public static String to64String(long number) {
        return to64String(Interface.ENCODER_DEFAULT_WITHOUT_PADDING, Numbers.toBytes(number));
    }
    
    public static String to64String(Base64.Encoder encoder, byte[] bytes) {
        return new String(to64(encoder, bytes), Strings.UTF_8);
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Interface with default methods for quick adding of functionality to classes. Otherwise, you can use the inline static methods instead.
     * 
     * Never use the built in encodeToString in Base64.Enccoder package as it is flawed because it does not use a consistent CharSet.
     *
     * @author Joseph S.
     */
    public static interface Interface {
        static final Base64.Encoder ENCODER_DEFAULT                 = Base64.getEncoder().withoutPadding();
        static final Base64.Encoder ENCODER_DEFAULT_WITHOUT_PADDING = ENCODER_DEFAULT.withoutPadding();
        static final Base64.Decoder DECODER_DEFAULT                 = Base64.getDecoder();
        static final Base64.Encoder ENCODER_URL                     = Base64.getUrlEncoder();
        static final Base64.Decoder DECODER_URL                     = Base64.getUrlDecoder();
    
        default String to64(byte[] bytes) {
            return to64String(bytes);
        }
    
        default byte[] from64(CharSequence val) {
            return $Base64.from64(val);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
}
