package momomo.com.sources;

import momomo.com.IO;
import momomo.com.Is;
import momomo.com.Lambda;
import momomo.com.annotations.informative.Private;

import java.io.File;

/**
 * See {@link momomo.com.IO.Iterate} for direct usage
 * 
 * @author Joseph S.
 */
public interface IteratorFile extends IteratorBaseRecurse<File, File, RuntimeException> {
    
    /////////////////////////////////////////////////////////////////////
    // each with recurse option
    /////////////////////////////////////////////////////////////////////
    
    @Override
    default <E extends Exception> Boolean each(File dir, Lambda.R1E<Boolean, ? super File, E> lambda, boolean recurse) throws E {
        String[] files = dir.list();
        
        if (files != null) {
            for (String filename : files) {
                File file = new File(dir, filename);
                
                if ( Is.False(lambda.call(file))
                     ||
                    recurse && file.isDirectory() && (each(file, lambda, recurse) == Boolean.FALSE)
                ) {
                    return Boolean.FALSE;
                }
            }
        }
        
        return Boolean.TRUE;
    }
    
    /////////////////////////////////////////////////////////////////////
    // findFile
    /////////////////////////////////////////////////////////////////////
    
    default File find(File dir, Lambda.R1<Boolean, File> cloj) {
        return findRecurse(dir, cloj, true);
    }
    
    default File findRecurse(File dir, Lambda.R1<Boolean, File> cloj, boolean recurse) {
        if ( cloj.call(dir) ) return dir;
        
        final Wrap<File> found = Wrap.it();
        each(dir, (File file) -> {
            if (cloj.call(file)) {
                found.it = file;
                
                return Boolean.FALSE;                                                // We found the file. Abort iteration
            }
            else if ( !recurse ) {
                return Boolean.FALSE;
            }
            else if ( file.isDirectory() ) {
                return ( found.it = findRecurse(file, cloj, recurse) ) == null;  // True if null -> continue with the next file, false if found meaning stop iteration
            }
            else {
                return Boolean.TRUE;                                                 // Continue iteration
            }
        });
        
        return found.it;
    }
    
    @Override
    @Private default File from(CharSequence url) {
        return IO.toFile(url);
    }
}
