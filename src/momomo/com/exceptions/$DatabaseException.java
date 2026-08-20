package momomo.com.exceptions;

import momomo.com.exceptions.$RuntimeException;

/**
 * @author Joseph S.
 */
public class $DatabaseException extends $RuntimeException {
    public $DatabaseException() {}
    
    public $DatabaseException(String message) {
        super(message);
    }
    
    public $DatabaseException(Throwable cause) {
        super(cause);
    }
}
