package momomo.com.exceptions;

/**
 * This exception is intended for all RuntimeExceptions to extend in order for us to properly identify and catch only our runtime exceptions if needed.
 *
 * @author Joseph S.
 */
public class $RuntimeException extends RuntimeException {
    public static final String NEWLINE = System.getProperty( "line.separator" );
    
    private final String m;
    
    public $RuntimeException() {
        m = "";
    }
    
    public $RuntimeException(String message) {
        super(message);
        this.m = "";
    }
    
    public $RuntimeException(Throwable cause) {
        super(cause);
        this.m = "";
    }
    
    public $RuntimeException(String message, Throwable cause) {
        super(message, cause);
        this.m = message;
    }
    
    @Override
    public String getMessage() {
        return m + NEWLINE + (super.getCause() == null ? super.getMessage() : super.getCause().getMessage());
    }
    
}

