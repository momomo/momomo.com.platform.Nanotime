package momomo.com.exceptions;

import java.io.IOException;

/**
 * @author Joseph S.
 */
public class $IOException extends $RuntimeException {
    
    public $IOException(IOException cause) {
        super(cause);
    }
    
    public $IOException(String message) {
        super(message);
    }
    
    public $IOException(String message, Throwable cause) {
        super(message, cause);
    }
}
