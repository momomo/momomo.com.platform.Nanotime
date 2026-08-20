package momomo.com.sources;

import momomo.com.annotations.informative.Development;
import momomo.com.exceptions.$IOException;
import momomo.com.IO;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.zip.ZipFile;

/**
 * @see Zip
 * 
 * Example url:
 *      jar:file:/momomo/Other/Software/tomcat/apache-tomcat-8.0.21/webapps/ROOT/WEB-INF/lib/_App-1.0.jar!/assets/App/views/globals/
 *
 *      new Jar(url).each(lambda);
 *
 *      or
 *          
 *      new Jar(url).unpack(...)
 *      
 * @author Joseph S.     
 */
public final class Jar extends Zip {
    
    public Jar(CharSequence url) {
        this(IO.toURL(url));
    }
    
    public Jar(URL url) {
        this(new Constructor(url));
    }
    
    /////////////////////////////////////////////////////////////////////
    /**
     * Hack to achieve what is otherwise impossible which is to create two Object's from one param since in a constructor everything has to be in the initial line in order to call super(...)
     *
     * @author Joseph S.
     */
    private static final class Constructor {
        private final ZipFile zip;
        private final String  path;
        
        private Constructor(URL url) {
            JarURLConnection connection;
            try {
                connection = (JarURLConnection) url.openConnection();
            } catch (IOException e) {
                throw new $IOException("Could not connect to the jar file: " + url, e);
            }
            
            try {
                zip = connection.getJarFile( );
            } catch (IOException e) {
                throw new $IOException("Could not connection().getJarFile()", e);
            }
            
            path = connection.getEntryName();
        }
    }
    private Jar(Constructor c) {
        super(c.zip, c.path);
    }
    /////////////////////////////////////////////////////////////////////
    
    @Development private static void examplePack() throws IOException {
        Jar jar = new Jar( "jar:file:/momomo/Other/Software/tomcat/apache-tomcat-8.0.21/webapps/ROOT/WEB-INF/lib/_App-1.0.jar!/assets/" ) ;
        
        File to = new File("/momomo/tmp");
        
        IO.clean(to);
        
        jar.unpack(to, true);
    }
    
    @Development private static void exampleUnpack() throws IOException {
        Jar zip = new Jar("zip:/momomo/Other/Software/jrebel/jrebel-5.5.1.zip!/jrebel-5.5.1/jrebel.lic");
        
        zip.unpack(new File("/momomo/Other"), true);
    }
    
}
