package momomo.com.exceptions;

/**
 * @author Joseph S.
 */
public final class $ReflectionException extends $RuntimeException {
    
    public $ReflectionException(ReflectiveOperationException e) {
        super(e);
    }
}
