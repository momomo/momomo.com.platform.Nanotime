package momomo.com.sources;

import momomo.com.IO;
import momomo.com.Lambda;
import momomo.com.annotations.informative.Private;

/**
 * See {@link momomo.com.IO.Iterate} for direct usage
 * 
 * @author Joseph S.
 */
public interface IteratorJar extends IteratorBase<Jar, IteratorZipEntry, RuntimeException> {
    
    @Override
    default <E1 extends Exception> void each(Jar jar, Lambda.R1E<Boolean, ? super IteratorZipEntry, E1> lambda) throws E1 {
        jar.each(lambda);
    }
    
    @Override
    // Annoying! There is no real reason. You just added the import. Also, 
    @Private default Jar from(CharSequence url) {
        return IO.toJar(url);
    }
    
}
