package momomo.com.exceptions;

/**
 * This exception is intended for all RuntimeExceptions to extend in order for us to properly identify and catch only our runtime exceptions if needed.
 *
 * @author Joseph S.
 */
public class $InterruptedException extends $RuntimeException {
    
    public $InterruptedException() {}
    public $InterruptedException(InterruptedException cause) {
        super(cause);
    }
    
}
