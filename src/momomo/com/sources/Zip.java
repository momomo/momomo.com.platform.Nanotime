package momomo.com.sources;

import momomo.com.Ex;
import momomo.com.IO;
import momomo.com.Is;
import momomo.com.Lambda;
import momomo.com.Strings;
import momomo.com.exceptions.$IOException;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @see Jar
 * 
 * Example:
 *
 *      url = "zip:file:/momomo/Other/Software/tomcat/apache-tomcat-8.0.21/webapps/ROOT/WEB-INF/lib/_App-1.0.zip!/assets/App/views/globals/"
 *      
 *      new Zip(url).each(lambda);
 *      
 *      or
 *      
 *      new Zip(url).unpack(...)
 * 
 * @author Joseph S.
 */
public class Zip implements AutoCloseable {
    
    private final ZipFile zip;
    private final String  path; // May be null
    
    public Zip(CharSequence url) {
        this( new Constructor(url.toString(), "zip") );
    }
    
    public Zip(File zip) {
        this( Ex.runtime(() -> new ZipFile(zip)) );
    }
    
    public Zip(ZipFile zip) {
        this(zip, null);
    }
    
    private Zip(Constructor c) {
        this( Ex.runtime(() -> new ZipFile(c.url) ), c.path);
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Hack to achieve what is otherwise impossible which is to create two Object's from one param since in a constructor everything has to be in the initial line in order to call super(...)
     *
     * @author Joseph S.
     */
    private static final class Constructor {
        final String url, path;
    
        private Constructor(String url, String protocol) {
            String a = protocol + ":";
        
            if ( !url.startsWith(a) ) {
                throw new $IOException("The protocol '" + protocol + ":' was not in the url. This class requires it and makes Zip behave like a jar file.");
            }
        
            url = url.substring(a.length());
        
            String b = "." + protocol + "!";
            int i = url.indexOf(b);
            if ( i > -1 ) {
                path = Strings.unslash(url.substring(i + b.length()), true, false); // Note! The last slash is important, otherwise directories won't be resolved. First not so.
                url  = url.substring(0, i + b.length() - 1);
            }
            else {
                path = null;
            }
        
            this.url = Strings.unchar(url, a, true, false);
        }
    }
    
    protected Zip(ZipFile zip, String path) {
        this.zip  = zip;
        this.path = path;
    }
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Does not ensure any iteration order. Order including nested folders may be iterated as encountered.
     */
    public <E extends Exception> void each(Lambda.R1E<Boolean, ? super IteratorZipEntry, E> lambda) throws E {
        each(path, lambda);
    }
    
    /**
     * Does not ensure any iteration order. Order including nested folders may be iterated as encountered.
     * @param path can be null
     */
    public final <E extends Exception> void each(String path, Lambda.R1E<Boolean, ? super IteratorZipEntry, E> lambda) throws E {
        int substring; boolean all, found;
        if ( Is.Ok(path) && !path.equals("/") ) {
            substring = path.length();
            all = found = false;
        }
        else {
            substring = 0;
            all = found = true;
        }
        
        Enumeration<? extends ZipEntry> entries = zip.entries(); while( entries.hasMoreElements() ) {
            ZipEntry entry = entries.nextElement();
            String   name  = entry.getName();
            
            if ( all == true || name.startsWith(path) ) {
                
                if ( !found && name.equals(path) ) {
                    // One time only
                    found = true;
                    
                    // We do not invoke it for the root folder to stay consitent with the FileIterator
                    continue;
                }
                
                if ( Is.False( lambda.call( new IteratorZipEntry(entry, substring))) ) {
                    return;
                }
                
            }
        }
    }
    
    public final File unpack(File to) {
        return unpack(to, path);
    }
    
    public final File unpack(File to, boolean retain) {
        return unpack(to, path, retain);
    }
    
    public final File unpack(File to, String path) {
        return unpack(to, path, false);
    }
    
    public final File unpack(File to, String path, boolean retain) {
        ZipEntry entry = zip.getEntry(path);
        
        // Basically, wether to extract it and retain the folder structure as withing the zip file or just extract it as a single file. Defaults to false
        String name;
        if (retain) {
            name = entry.getName();
        }
        else {
            name = new File(entry.getName()).getName();
        }
        
        File file = new File(to, name);
        if ( entry.isDirectory() ) {
            file.mkdirs();
            
            // We have to iterate the entire zip file here and extract all paths that matches entryPath directory
            each(path, e -> {
                String relativePath = e.getRelativeIterationPath();
                File create = new File(file, relativePath);
                ZipEntry zipEntry = zip.getEntry(path + relativePath);
                
                if ( zipEntry.isDirectory() ) {
                    create.mkdirs();
                }
                else {
                    create.getParentFile().mkdirs();
                    
                    IO.write(create, IO.getInputStream(zip, zipEntry));
                }
                
                return null;
            });
        }
        else {
            file.getParentFile().mkdirs();
            
            IO.write(file, IO.getInputStream(zip, entry));
        }
        
        return file;
    }
    
    @Override
    public void close() {
        try {
            zip.close();
        } catch (IOException e) {
            throw Ex.runtime(e);
        }
    }
    
}
