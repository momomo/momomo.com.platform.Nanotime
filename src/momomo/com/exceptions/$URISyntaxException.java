package momomo.com.exceptions;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * @author Joseph S.
 */
public final class $URISyntaxException extends $RuntimeException {
    
    public $URISyntaxException(MalformedURLException cause) {
        super(cause);
    }
    
    public $URISyntaxException(URISyntaxException cause) {
        super(cause);
    }
}
