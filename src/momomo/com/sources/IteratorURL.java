package momomo.com.sources;

import momomo.com.Lambda;
import momomo.com.annotations.informative.Private;
import momomo.com.IO;
import momomo.com.Is;

import java.io.File;
import java.net.URL;

/**
 * See {@link momomo.com.IO.Iterate} for direct usage
 * 
 * @author Joseph S.
 */
public interface IteratorURL extends IteratorBase<URL, IteratorBaseEntry, RuntimeException> {
    
    @Override
    default <E extends Exception> void each(URL url, Lambda.R1E<Boolean, ? super IteratorBaseEntry, E> lambda) throws E {
        if ( Is.In.Jar(url) ) {
            IO.Iterate.Jar.each(IO.toJar(url), lambda);
        }
        else {
            final File dir       = IO.toFile( url );
            final int  substring = dir.getAbsolutePath(     ).length( ) + 1;        // Plus one because the slash is not included in absolutePath
            
            IO.Iterate.File.each(dir, file -> {
                return lambda.call( new IteratorFileEntry(file, substring) );
            });
        }
    }
    
    @Override
    @Private default URL from(CharSequence url) {
        return IO.toURL(url);
    }
}
