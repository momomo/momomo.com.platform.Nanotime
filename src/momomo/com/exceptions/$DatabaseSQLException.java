package momomo.com.exceptions;

import java.sql.SQLException;

/**
 * @author Joseph S.
 */
public final class $DatabaseSQLException extends $DatabaseException {
    
    public $DatabaseSQLException(SQLException cause) {
        super(cause);
    }
    
}
