package momomo.com.sources;

import momomo.com.IO;
import momomo.com.Lambda;
import momomo.com.annotations.informative.Private;

/**
 * See {@link momomo.com.IO.Iterate} for direct usage
 * 
 * @author Joseph S.
 */
public interface IteratorZip extends IteratorBase<Zip, IteratorZipEntry, RuntimeException> {
    
    @Override
    default <E1 extends Exception> void each(Zip zip, Lambda.R1E<Boolean, ? super IteratorZipEntry, E1> lambda) throws E1 {
        zip.each(lambda);
    }
    
    @Override
    @Private default Zip from(CharSequence url) {
        return IO.toZip(url);
    }
    
}
