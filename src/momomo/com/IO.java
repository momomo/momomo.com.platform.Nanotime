/* Copyright(C) 2014 - 2020 Momomo LTD. Proprietary and confidential. Usage of this file on any medium without a written consent by Momomo LTD. is strictly prohibited. All Rights Reserved. */
package momomo.com;

import momomo.com.annotations.informative.See;
import momomo.com.exceptions.$IOException;
import momomo.com.exceptions.$URISyntaxException;
import momomo.com.sources.ContentType;
import momomo.com.sources.IteratorFile;
import momomo.com.sources.IteratorJar;
import momomo.com.sources.IteratorURL;
import momomo.com.sources.IteratorZip;
import momomo.com.sources.Jar;
import momomo.com.sources.SSL;
import momomo.com.sources.Zip;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * TODO move in getResource ...
 * @author Joseph S.
 */
public class IO { private IO() {}

    public static final int                  BUFFER_SIZE         = 8192;
    public static final boolean              WRITE_MKWAY         = true;
    public static final boolean              MKDIR_CLEAN         = false;
    public static final StandardCopyOption[] MOVE_COPY_OPTIONS   = {StandardCopyOption.REPLACE_EXISTING};
    public static final StandardCopyOption[] RENAME_COPY_OPTIONS = {StandardCopyOption.REPLACE_EXISTING};
    public static final boolean              COPY_CLEAN          = false;
    public static final boolean              COPY_SAME           = false;
    public static final StandardCopyOption[] COPY_OPTIONS        = {StandardCopyOption.REPLACE_EXISTING};
    
    public static final String  SAFE_FILENAME_REGEX      = "[^0-9A-Za-z._-]";                    // dash - has to be in the end
    public static final Pattern SAFE_FILENAME_PATTERN    = Pattern.compile(SAFE_FILENAME_REGEX);
    public static final String  TEMPORARY_FILE_SEPARATOR = "-";
    
    /**
     * Can be cleared, and or added to. Global.
     */
    public static final Set<String> IGNORED = new HashSet<>(); static {
        IGNORED.add(".fseventsd");
        IGNORED.add(".DocumentRevisions-V100");
        IGNORED.add(".Spotlight-V100");
        IGNORED.add(".TemporaryItems");
        IGNORED.add(".Trashes");
        IGNORED.add(".DS_Store");
    }
    
    private static final Set<Integer> BOOMS = new HashSet<>(); static {
        BOOMS.add((int) '\uFEFF');
    }
    
    /////////////////////////////////////////////////////////////////////
    // SSL
    /////////////////////////////////////////////////////////////////////
    
    public static final momomo.com.sources.SSL SSL = new SSL();
    
    /////////////////////////////////////////////////////////////////////
    // Iterate. IO.Iterate.File.each, IO.Iterate.File.eachRecurse, IO.Iterate.Jar.each ... 
    /////////////////////////////////////////////////////////////////////
    
    public static final class Iterate {
        public static final IteratorFile File = new IteratorFile () {};
        public static final IteratorZip   Zip = new IteratorZip  () {};
        public static final IteratorJar   Jar = new IteratorJar  () {};
    
        /**
         * In production mode, when resources might be inside a war file, iterating a directory can only be safely done using URLIterator. 
         * Note that it is a pretty expensive thing to do to iterate a WAR file as all files must be iterated, no matter how big or small the directory is. 
         * Should only be done once per folder per runtime instance, such as for setups / caching or resources. 
         */
        public static final IteratorURL Url  = new IteratorURL() {};
    }
    
    /////////////////////////////////////////////////////////////////////
    // eachLine(String)
    /////////////////////////////////////////////////////////////////////
    
    /**
     * @param lambda passes line only, iterates all
     */
    public static <E extends Exception> void eachLine(CharSequence filepath, Lambda.V1E<String, E> lambda) throws E {
        eachLine(filepath, Strings.CHARSET, lambda);
    }
    
    /**
     * @param lambda passes line and line index, iterates all
     */
    public static <E extends Exception> void eachLine(CharSequence filepath, Lambda.V2E<String, Integer, E> lambda) throws E {
        eachLine(filepath, Strings.CHARSET, lambda);
    }
    
    /**
     * @param lambda passes line only, return false to abort iteration anytime
     */
    public static <E extends Exception> void eachLine(CharSequence filepath, Lambda.R1E<Boolean, String, E> lambda) throws E {
        eachLine(filepath, Strings.CHARSET, lambda);
    }
    
    /**
     * @param lambda passes line and line index, return false to abort iteration anytime
     */
    public static <E extends Exception> void eachLine(CharSequence filepath, Lambda.R2E<Boolean, String, Integer, E> lambda) throws E {
        eachLine(filepath, Strings.CHARSET, lambda);
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * @param lambda passes line only, iterates all
     */
    public static <E extends Exception> void eachLine(CharSequence filepath, Charset charset, Lambda.V1E<String, E> lambda) throws E {
        eachLine(filepath, charset, lambda.R2E());
    }
    
    /**
     * @param lambda passes line and line index, iterates all
     */
    public static <E extends Exception> void eachLine(CharSequence filepath, Charset charset, Lambda.V2E<String, Integer, E> lambda) throws E {
        eachLine(filepath, charset, lambda.R2E());
    }
    
    /**
     * @param lambda passes line only, return false to abort iteration anytime
     */
    public static <E extends Exception> void eachLine(CharSequence filepath, Charset charset, Lambda.R1E<Boolean, String, E> lambda) throws E {
        eachLine(filepath, charset, lambda.R2E());
    }
    
    /**
     * @param lambda passes line and line index, return false to abort iteration anytime
     */
    public static <E extends Exception> void eachLine(CharSequence filepath, Charset charset, Lambda.R2E<Boolean, String, Integer, E> lambda) throws E {
        eachLine(toFile(filepath.toString()), charset, lambda);
    }
    
    /////////////////////////////////////////////////////////////////////
    // eachLine(File)
    /////////////////////////////////////////////////////////////////////
    
    /**
     * @param lambda passes line only, iterates all
     */
    public static <E extends Exception> void eachLine(File file, Lambda.V1E<String, E> lambda) throws E {
        eachLine(file, Strings.CHARSET, lambda);
    }
    
    /**
     * @param lambda passes line and line index, iterates all
     */
    public static <E extends Exception> void eachLine(File file, Lambda.V2E<String, Integer, E> lambda) throws E {
        eachLine(file, Strings.CHARSET, lambda);
    }
    
    /**
     * @param lambda passes line only, return false to abort iteration anytime
     */
    public static <E extends Exception> void eachLine(File file, Lambda.R1E<Boolean, String, E> lambda) throws E {
        eachLine(file, Strings.CHARSET, lambda);
    }
    
    /**
     * @param lambda passes line and line index, return false to abort iteration anytime
     */
    public static <E extends Exception> void eachLine(File file, Lambda.R2E<Boolean, String, Integer, E> lambda) throws E {
        eachLine(file, Strings.CHARSET, lambda);
    }
    
    
    /////////////////////////////////////////////////////////////////////
    // eachLine(File, ... charset ... ) 
    /////////////////////////////////////////////////////////////////////
    
    /**
     * @param lambda passes line only, iterates all
     */
    public static <E extends Exception> void eachLine(File file, Charset charset, Lambda.V1E<String, E> lambda) throws E {
        eachLine(file, charset, lambda.R2E());
    }
    
    /**
     * @param lambda passes line and line index, iterates all
     */
    public static <E extends Exception> void eachLine(File file, Charset charset, Lambda.V2E<String, Integer, E> lambda) throws E {
        eachLine(file, charset, lambda.R2E());
    }
    
    /**
     * @param lambda passes line only, return false to abort iteration anytime
     */
    public static <E extends Exception> void eachLine(File file, Charset charset, Lambda.R1E<Boolean, String, E> lambda) throws E {
        eachLine(file, charset, lambda.R2E());
    }
    
    /**
     * @param lambda passes line and line index, return false to abort iteration anytime
     */
    public static <E extends Exception> void eachLine(File file, Charset charset, Lambda.R2E<Boolean, String, Integer, E> lambda) throws E {
        eachLine(toURL(file), charset, lambda);
    }
    
    /////////////////////////////////////////////////////////////////////
    // eachLine(URL)
    /////////////////////////////////////////////////////////////////////
    
    /**
     * @param lambda passes line only, iterates all
     */
    public static <E extends Exception> void eachLine(URL url, Lambda.V1E<String, E> lambda) throws E {
        eachLine(url, Strings.CHARSET, lambda);
    }
    
    /**
     * @param lambda passes line only, return false to abort iteration anytime
     */
    public static <E extends Exception> void eachLine(URL url, Lambda.R1E<Boolean, String, E> lambda) throws E {
        eachLine(url, Strings.CHARSET, lambda);
    }
    
    /**
     * @param lambda passes line and line index, iterates all
     */
    public static <E extends Exception> void eachLine(URL url, Lambda.V2E<String, Integer, E> lambda) throws E {
        eachLine(url, Strings.CHARSET, lambda.R2E());
    }
    
    /**
     * @param lambda passes line and line index, return false to abort iteration anytime
     */
    public static <E extends Exception> void eachLine(URL url, Lambda.R2E<Boolean, String, Integer, E> lambda) throws E {
        eachLine(url, Strings.CHARSET, lambda);
    }
    
    /////////////////////////////////////////////////////////////////////
    // eachLine(URL, ... charset ... ) 
    /////////////////////////////////////////////////////////////////////
    
    /**
     * @param lambda passes line only, iterates all
     */
    public static <E extends Exception> void eachLine(URL url, Charset charset, Lambda.V1E<String, E> lambda) throws E {
        eachLine(url, charset, lambda.R2E());
    }
    
    /**
     * @param lambda passes line and line index, return false to abort iteration anytime
     */
    public static <E extends Exception> void eachLine(URL url, Charset charset, Lambda.V2E<String, Integer, E> lambda) throws E {
        eachLine(url, charset, lambda.R2E());
    }
    
    /**
     * @param lambda passes line only, return false to abort iteration anytime
     */
    public static <E extends Exception> void eachLine(URL url, Charset charset, Lambda.R1E<Boolean, String, E> lambda) throws E {
        eachLine(url, charset, lambda.R2E());
    }
    
    /**
     * @param lambda passes line and line index, return false to abort iteration anytime
     */
    public static <E extends Exception> void eachLine(URL url, Charset charset, Lambda.R2E<Boolean, String, Integer, E> lambda) throws E {
        try ( InputStream is = getInputStream( toConnection(url) ) ) {
            eachLine(is, charset, lambda);
        } catch (IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    // eachLine(InputStream)
    /////////////////////////////////////////////////////////////////////
    
    /**
     * @param lambda passes line only, iterates all
     */
    public static <E extends Exception> void eachLine(InputStream inputStream, Lambda.V1E<String, E> lambda) throws E {
        eachLine(inputStream, Strings.CHARSET, lambda);
    }
    
    /**
     * @param lambda passes line and line index, iterates all
     */
    public static <E extends Exception> void eachLine(InputStream is, Lambda.V2E<String, Integer, E> lambda) throws E {
        eachLine(is, Strings.CHARSET, lambda);
    }
    
    
    /**
     * @param lambda passes line only, return false to abort iteration anytime
     */
    public static <E extends Exception> void eachLine(InputStream is, Lambda.R1E<Boolean, String, E> lambda) throws E {
        eachLine(is, Strings.CHARSET, lambda);
    }
    
    /**
     * @param lambda passes line and line index, return false to abort iteration anytime
     */
    public static <E extends Exception> void eachLine(InputStream is, Lambda.R2E<Boolean, String, Integer, E> lambda) throws E {
        eachLine(is, Strings.CHARSET, lambda);
    }
    
    /////////////////////////////////////////////////////////////////////
    // eachLine(InputStream, ... charset ... ) 
    /////////////////////////////////////////////////////////////////////
    
    /**
     * @param lambda passes line only, iterates all
     */
    public static <E extends Exception> void eachLine(InputStream is, Charset charset, Lambda.V1E<String, E> lambda) throws E {
        eachLine(is, charset, lambda.R1E());
    }
    
    /**
     * @param lambda passes line and line index, iterates all
     */
    public static <E extends Exception> void eachLine(InputStream is, Charset charset, Lambda.V2E<String, Integer, E> lambda) throws E {
        eachLine(is, charset, lambda.R2E());
    }
    
    /**
     * @param lambda passes line only, return false to abort iteration anytime
     */
    public static <E extends Exception> void eachLine(InputStream is, Charset charset, Lambda.R1E<Boolean, String, E> lambda) throws E {
        eachLine(is, charset, (s, integer) -> {
            return lambda.call(s);
        });
    }
    
    /**
     * @param lambda passes line and line index, return false to abort iteration anytime
     */
    public static <E extends Exception> void eachLine(InputStream is, Charset charset, Lambda.R2E<Boolean, String, Integer, E> lambda) throws E {
        try (InputStreamReader isr = new InputStreamReader(is, charset); BufferedReader bfr = new BufferedReader(isr, BUFFER_SIZE) ) {
            int i = 0; String line; while ( (line = bfr.readLine()) != null ) {
                if ( Boolean.FALSE.equals(lambda.call(line, i++)) ) return;
            }
        }
        catch (IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////
    // bytes() = getBytes(String, File, URL, URLConnection, InputStream)
    /////////////////////////////////////////////////////////////////////
    public static byte[] bytes(CharSequence filepath) {
        return bytes(toFile(filepath.toString()));
    }
    
    public static byte[] bytes(File file) {
        try {
            return Files.readAllBytes(toPath(file));
        } catch (IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    public static byte[] bytes(URL url) {
        return bytes(toConnection(url));
    }
    
    public static byte[] bytes(URLConnection connection) {
        return bytes(getInputStream(connection));
    }
    
    public static byte[] bytes(InputStream is) {
        try ( is; ByteArrayOutputStream bos = new ByteArrayOutputStream(BUFFER_SIZE) ) {
            byte[] buffer = new byte[BUFFER_SIZE];
            
            int length;
            while ( (length = is.read(buffer)) != -1 ) {
                bos.write(buffer, 0, length);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    // text = getText(String, File, URL, URLConnection, InputStream)
    /////////////////////////////////////////////////////////////////////
    
    public static String text(CharSequence filepath) {
        return text(filepath, Strings.CHARSET);
    }
    public static String text(CharSequence filepath, Charset charset) {
        return text(toFile(filepath.toString()), charset);
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public static String text(File file) {
        return text(file, Strings.CHARSET);
    }
    public static String text(File file, Charset charset) {
        return $Bytes.toString(bytes(file), charset);
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public static String text(URL url) {
        return text(url, Strings.CHARSET);
    }
    public static String text(URL url, Charset charset) {
        return $Bytes.toString(bytes(url), charset);
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public static String text(URI uri) {
        return text(uri, Strings.CHARSET);
    }
    public static String text(URI uri, Charset charset) {
        return text(toURL(uri), charset);
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public static String text(URLConnection connection) {
        return text(connection, Strings.CHARSET);
    }
    public static String text(URLConnection connection, Charset charset) {
        return $Bytes.toString(bytes(connection), charset);
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public static String text(InputStream is) {
        return text(is, Strings.CHARSET);
    }
    public static String text(InputStream is, Charset charset) {
        return $Bytes.toString(bytes(is), charset);
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public static String text(BufferedReader reader) {
        StringBuilder sb = new StringBuilder();
        
        try {
            String line; while ( (line = reader.readLine()) != null ) {
                sb.append(line);
            }
        }
        catch(IOException e) {
            throw Ex.runtime(e);
        }
        
        return sb.toString();
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /////////////////////////////////////////////////////////////////////
    // write(CharSequence, File) text or bytes
    /////////////////////////////////////////////////////////////////////
    
    public static void write(CharSequence filepath, CharSequence text, OpenOption... options) {
        write(filepath, text, Strings.CHARSET, options);
    }
    public static void write(CharSequence filepath, CharSequence text, Charset charset, OpenOption... options) {
        write(toFile(filepath), text, charset, options);
    }
    public static void write(File file, CharSequence text, OpenOption... options) {
        write(file, text, Strings.CHARSET, options);
    }
    public static void write(File file, CharSequence text, Charset charset, OpenOption... options) {
        write(file, Strings.toBytes(text, charset), options);
    }
    
    ////////// 
    
    public static void write(CharSequence filepath, byte[] bytes, OpenOption... options) {
        write(toFile(filepath), bytes, options);
    }
    public static void write(File file, byte[] bytes, OpenOption... options) {
        write(file, bytes, WRITE_MKWAY, options);
    }
    public static void write(CharSequence filepath, byte[] bytes, boolean mkway, OpenOption... options) {
        write(toFile(filepath), bytes, mkway, options);
    }
    public static void write(File file, byte[] bytes, boolean mkway, OpenOption... options) {
        Objects.requireNonNull(bytes);  // ensure bytes is not null before opening file
        
        if ( mkway ) {
            mkway(file);
        }
        
        if ( bytes != null ) {
            Path path = toPath(file); try (OutputStream out = Files.newOutputStream(path, options) ) {
                int length = bytes.length; int remaining = length; while (remaining > 0) {
                    int n = Math.min(remaining, BUFFER_SIZE);
                    out.write(bytes, (length - remaining), n);
                    remaining -= n;
                }
            }
            catch (IOException e) {
                throw Ex.runtime(e);
            }
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    // write(InputStream)
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Replaces the contents of the target 
     */
    public static void write(CharSequence filepath, InputStream is) {
        write(filepath, is, WRITE_MKWAY);
    }
    /**
     * Replaces the contents of the target 
     * 
     * @see java.nio.file.StandardCopyOption for options
     */
    public static void write(CharSequence filepath, InputStream is, boolean mkway, CopyOption ... options) {
        write(toFile(filepath), is, mkway, options);
    }
    
    /**
     * Replaces the contents of the target 
     */
    public static void write(File file, InputStream is) {
        write(file, is, WRITE_MKWAY);
    }
    
    /**
     * Replaces the contents of the target
     * 
     * @see java.nio.file.StandardCopyOption for options
     */
    public static void write(File file, InputStream is, boolean mkway, CopyOption ... options) {
        if ( mkway ) {
            mkway(file);
        }
        
        try ( is ) {
            Files.copy(is, toPath(file), options);
        }
        catch (IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    // write(URL, URLConnection, OutputStream)
    /////////////////////////////////////////////////////////////////////
    
    public static void write(URL url, CharSequence text) {
        write(url, text, Strings.CHARSET);
    }
    public static void write(URL url, CharSequence text, Charset charset) {
        write( toConnection(url), text, charset);
    }
    public static void write(URLConnection connection, CharSequence text) {
        write(connection, text, Strings.CHARSET);
    }
    public static void write(URLConnection connection, CharSequence text, Charset charset) {
        connection.setDoOutput(true);
        
        try (OutputStream os = connection.getOutputStream()) {
            write(os, text, charset);
        } catch (IOException e) {
            throw Ex.runtime(e);
        }
    }
    public static void write(OutputStream os, CharSequence text) {
        write(os, text, Strings.CHARSET);
    }
    public static void write(OutputStream os, CharSequence text, Charset charset) {
        try (os; OutputStreamWriter out = new OutputStreamWriter(os, charset) ) {
            out.write(text.toString());
        } catch (IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    // append
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Like IO.write but does not replace but simply adds / appends 
     */
    public static void append(String filename, CharSequence text) {
        append(toFile(filename), text);
    }
    
    /**
     * Like IO.write but does not replace but simply adds / appends 
     */
    public static void append(File file, CharSequence text) {
        append(file, text, Strings.CHARSET);
    }
    /**
     * Like IO.write but does not replace but simply adds / appends 
     */
    public static void append(File file, CharSequence text, Charset charset) {
        if ( !file.exists() ) {
            write(file, "", charset);
        }
        try ( FileWriter fw = new FileWriter(file, charset, true) ) {
            fw.write(text.toString());
        } catch (IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    // toFile
    /////////////////////////////////////////////////////////////////////
    
    public static File toFile(CharSequence filepath) {
        return new File(filepath.toString());
    }
    
    /**
     * Be careful converting a uri to a file, since a resource contained in a jar file will not resolve properly because it is flat and not hierarchial.
     */
    public static File toFile(URI uri) {
        return new File(uri);
    }
    
    /**
     * Be careful converting a url to a file, since a resource contained in a jar file will not resolve properly because it is flat and not hierarchial.
     */
    public static File toFile(URL url) {
        return new File( toURI(url) );
    }
    
    /////////////////////////////////////////////////////////////////////
    // toURI
    /////////////////////////////////////////////////////////////////////
    
    public static URI toURI(CharSequence path) {
        try {
            return new URI(path.toString());
        } catch (URISyntaxException e) {
            throw Ex.runtime(e);
        }
    }
    
    public static URI toURI(File file) {
        return file.toURI();
    }
    
    public static URI toURI(URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new $URISyntaxException(e);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    // toURL
    /////////////////////////////////////////////////////////////////////
    
    public static URL toURL(CharSequence path) {
        try {
            return new URL(path.toString());
        } catch (MalformedURLException e) {
            throw Ex.runtime(e);
        }
    }
    
    public static URL toURL(File file) {
        return toURL( toURI(file) );
    }
    
    public static URL toURL(URI uri) {
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new $URISyntaxException(e);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    // toPath
    /////////////////////////////////////////////////////////////////////
    
    public static Path toPath(CharSequence filepath) {
        return toFile(filepath).toPath();
    }
    public static Path toPath(File file) {
        return file.toPath();
    }
    public static Path toPath(URL url) {
        return toPath(toURI(url));
    }
    public static Path toPath(URI uri) {
        return Paths.get(uri);
    }
    
    
    /////////////////////////////////////////////////////////////////////
    // toZip
    /////////////////////////////////////////////////////////////////////
    
    public static Zip toZip(CharSequence url) {
        return new Zip(url);
    }
    
    public static Jar toJar(CharSequence url) {
        return toJar(toURL(url));
    }
    public static Jar toJar(URL url) {
        return new Jar(url);
    }
    
    
    
    /////////////////////////////////////////////////////////////////////
    // toConnection
    /////////////////////////////////////////////////////////////////////
    
    public static URLConnection toConnection(URL url) {
        return toConnection(url, null);
    }
    public static URLConnection toConnection(URL url, Boolean caching) {
        try {
            URLConnection connection = url.openConnection();
            
            if ( caching != null ) {
                connection.setUseCaches(caching);
            }
            
            return connection;
        } catch (IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    // toExternal
    /////////////////////////////////////////////////////////////////////
    
    public static String toExternal(File file) {
        return toURI(file).toString();
    }
    
    public static String toExternal(URL url) {
        return url.toExternalForm();
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public static String toSafeFilename(CharSequence characters) {
        return Strings.Replace.all(characters.toString(), SAFE_FILENAME_PATTERN, "");
    }
    
    /////////////////////////////////////////////////////////////////////
    // symlink
    /////////////////////////////////////////////////////////////////////
    
    /**
     * More accurate than symlinkPath
     */
    public static File symlinkFile(File file) {
        Path path = symlinkPath(file);
        
        if ( path.isAbsolute() ) {
            return path.toFile();
        }
        else {
            return new File(file.getParentFile(), path.toString());
        }
    }
    
    /**
     * @param file
     * @return
     */
    public static Path symlinkPath(File file) {
        try {
            return Files.readSymbolicLink(toPath(file));
        } catch (IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    /**
     * Package private use, Is.Symlink instead
     */
    @See(Is.class) public static boolean symlinkIs(File file) {
        return Is.Symlink(file);
    }
    /**
     * private use, Is.Symlink instead
     */
    @See(Is.class) public static boolean symlinkIs(Path path) {
        return Is.Symlink(path);
    }
    
    /////////////////////////////////////////////////////////////////////
    // Temporary file / directory
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Creates a tmp file with a random filename within default temporary directory
     */
    public static File tmpFileCreate() {
        return tmpFileCreate(null);
    }
    
    /**
     * Creates a tmp file with the no special suffix, then writes text to it.
     */
    public static File tmpFileCreate(CharSequence text) {
        return tmpFileCreate(text, null);
    }
    
    /***
     * Creates a tmp file with the given suffix, then writes text to it.
     */
    public static File tmpFileCreate(CharSequence text, CharSequence suffix) {
        return tmpFileCreate(text, Strings.CHARSET, suffix);
    }
    
    /***
     * Creates a tmp file with the given suffix, then writes text to it.
     */
    public static File tmpFileCreate(CharSequence text, Charset charset, CharSequence suffix) {
        return tmpFileCreate(Strings.toBytes(text, charset), suffix);
    }
    
    /***
     * Creates a tmp file with the given suffix, then writes the bytes to it.
     */
    public static File tmpFileCreate(byte[] bytes, CharSequence suffix) {
        File file = tmpFileCreateWithSuffix(suffix);
        if ( Is.Ok(bytes) ) {
            IO.write(file, bytes);
        }
        return file;
    }
    
    /**
     * Creates a tmp file with the given suffix
     */
    public static File tmpFileCreateWithSuffix(CharSequence suffix) {
        try {
            return File.createTempFile( Nano.time() + TEMPORARY_FILE_SEPARATOR, suffix == null ? "" : suffix.toString() );
        } catch (IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    /**
     * Creates a tmp directory within the default temporary directory
     */
    public static File tmpDirCreate() {
        File tmp = new File( tmpDir(), Nano.time() + TEMPORARY_FILE_SEPARATOR + Randoms.Long() );
        
        if ( mkdir(tmp) ) {
            return tmp;
        }
        
        throw new $IOException("Was not able to create a new temporary directory for some reason.");
    }
    
    public static File tmpDir() {
        return toFile(System.getProperty("java.io.tmpdir"));
    }
    
    /////////////////////////////////////////////////////////////////////
    // touch
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Note, that this method does not create the directory structure. To do that, instead call mkfile. 
     */
    public static void touch(CharSequence filename) {
        touch(toFile(filename));
    }
    /**
     * Note, that this method does not create the directory structure. To do that, instead call mkfile. 
     */
    public static void touch(File file) {
        touch(file, System.currentTimeMillis());
    }
    /**
     * Note, that this method does not create the directory structure. To do that, instead call mkfile. 
     */
    private static void touch(File file, long timestamp) {
        if (!file.exists()) {
            try {
                new FileOutputStream(file).close();
            } catch (IOException e) {
                throw Ex.runtime(e);
            }
        }
        
        file.setLastModified(timestamp);
    }
    
    /////////////////////////////////////////////////////////////////////
    // mkfile, mkway, mkdir, mkdirs 
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Creates a file and ensures the directory path where it is to go is created as well. 
     */
    public static File mkfile(CharSequence filepath) {
        return mkfile( toFile(filepath) );
    }
    
    /**
     * Creates a file and ensures the directory path where it is to go is created as well. 
     */
    public static File mkfile(File file) {
        if ( !file.exists() ) {
            try {
                mkparent(file);       // Ensure parent is created
                
                file.createNewFile();
            }
            catch (IOException e) {
                throw new $IOException(e);
            }
        }
        return file;
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Creates the path up to the file, so it can then be safely created
     */
    public static void mkway(CharSequence filepath) {
        mkway(toFile(filepath));
    }
    /**
     * Creates the path up to the file, so it can then be safely created
     */
    public static void mkway(File file) {
        if ( file.isDirectory() ) {
            mkdirs(file);
        }
        else {
            mkparent(file);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    public static boolean mkdir(CharSequence dir) {
        return mkdir(toFile(dir));
    }
    
    public static boolean mkdir(File dir) {
        return dir.mkdir();
    }
    
    /**
     * Creates the path up to and including the directory
     */
    public static File mkdirs(CharSequence directory) {
        return mkdirs(toFile(directory));
    }
    public static File mkdirs(File directory) {
        directory.mkdirs(); return directory;
    }
    
    /**
     * @param clean if the directory should be cleaned as well ensuring a clean slate
     */
    public static File mkdirs(CharSequence directory, boolean clean) {
        return mkdirs(toFile(directory), clean);
    }
    /**
     * @param clean if the directory should be cleaned as well ensuring a clean slate
     */
    public static File mkdirs(File directory, boolean clean) {
        if ( !directory.mkdirs() && clean ) {
            clean(directory);   // Already created, and clean is true
        }
        
        return directory;
    }
    
    /**
     * Creates a directory with name inside directory
     */
    public static File mkdirs(CharSequence directory, CharSequence name) {
        return mkdirs(toFile(directory), name);
    }
    /**
     * Creates a directory with name inside directory
     */
    public static File mkdirs(File directory, CharSequence name) {
        return mkdirs(directory, name, MKDIR_CLEAN);
    }
    
    /**
     * Creates a directory with name inside directory
     *
     * @param clean if the directory should be cleaned as well ensuring a clean slate
     */
    public static File mkdirs(CharSequence directory, CharSequence name, boolean clean) {
        return mkdirs(toFile(directory), name, clean);
    }
    /**
     * Creates a directory with name inside directory
     *
     * @param clean if the directory should be cleaned as well ensuring a clean slate
     */
    public static File mkdirs(File directory, CharSequence name, boolean clean) {
        if (name != null ) {
            return mkdirs(new File(directory, name.toString()), clean);
        }
        return directory;
    }
    
    /**
     * Creates the directory structure to eventually hold the file
     */
    public static File mkparent(File file) {
        return mkdirs(file.getParent());
    }
    
    /////////////////////////////////////////////////////////////////////
    // move = mv
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Move a file from -> to. Will replace any existing file with that name if present.
     */
    public static void move(String from, String to) {
        move(from, to, MOVE_COPY_OPTIONS);
    }
    
    /**
     * Move a file from -> to. Will replace any existing file with that name if present.
     */
    public static void move(String from, String to, StandardCopyOption... options) {
        move(toFile(from), toFile(to), options);
    }
    
    /**
     * Move a file from -> to. Will replace any existing file with that name if present.
     */
    public static void move(File from, File to) {
        move(from, to, MOVE_COPY_OPTIONS);
    }
    /**
     * Move a file from -> to. Pass CopyOption to set properties.
     *
     * @see java.nio.file.StandardCopyOption for options
     */
    public static void move(File from, File to, CopyOption ... options) {
        try {
            Files.move(toPath(from), toPath(to), options);
        } catch (IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    // rename = mv
    /////////////////////////////////////////////////////////////////////
    
    /**
     * @param name new name of the file. Same location. Will replace any existing file with that name if present. 
     */    
    public static void rename(CharSequence filepath, CharSequence name) {
        rename(filepath, name, RENAME_COPY_OPTIONS);
    }
    /**
     * @param name new name of the file. Same location. Will replace any existing file with that name if present. 
     * @param options
     */    
    public static void rename(CharSequence filepath, CharSequence name, StandardCopyOption ... options) {
        rename(toFile(filepath), name, options);
    }
    
    /**
     * @param name new name of the file. Same location. Will replace any existing file with that name if present. 
     */
    public static void rename(File file, CharSequence name) {
        rename(file, name, RENAME_COPY_OPTIONS);
    }
    /**
     * @param name new name of the file. Same location. Pass CopyOption to set properties.
     *
     * @see java.nio.file.StandardCopyOption for options 
     */
    public static void rename(File file, CharSequence name, CopyOption ... options ) {
        move(file, new File(mkparent(file), name.toString()), options);
    }
    
    /////////////////////////////////////////////////////////////////////
    // copy = cp
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Copies the contents of from, into the folder of to.
     *
     * To will be created if needed.
     */
    public static void copy(CharSequence from, CharSequence to) {
        copy(from, to, COPY_CLEAN);
    }
    
    /**
     * Copies the contents of from, into the folder of to.
     *
     * To will be created if needed.
     */
    public static void copy(CharSequence from, CharSequence to, boolean clean) {
        copy(from, to, clean, COPY_SAME);
    }
    
    /**
     * Copies the contents of from, into the folder of to.
     *
     * To will be created if needed.
     */
    public static void copy(CharSequence from, CharSequence to, boolean clean, boolean same) {
        copy(from, to, clean, same, COPY_OPTIONS);
    }
    
    public static void copy(CharSequence from, CharSequence to, boolean clean, boolean same, CopyOption ... options) {
        copy(toFile(from), toFile(to), clean, same, options);
    }
    
    /////////////
    
    /**
     * Copies the contents of from, into the folder to.
     *
     * To will be created if needed.
     */
    public static void copy(File from, File to) {
        copy(from, to, COPY_CLEAN);
    }
    
    public static void copy(File from, File to, boolean clean) {
        copy(from, to, clean, COPY_SAME);
    }
    
    public static void copy(File from, File to, boolean clean, boolean same) {
        copy(from, to, clean, same, COPY_OPTIONS);
    }
    
    /**
     * @see java.nio.file.StandardCopyOption for options
     * 
     * Note, ignores any files in IO.IGNORED
     */
    public static void copy(File from, File to, boolean clean, CopyOption... options) {
        copy(from, to, clean, COPY_SAME, options);
    }
    /**
     * @see java.nio.file.StandardCopyOption for options
     * 
     * @param same if true, then a folder with the same name as from will be created in to if to.getName() is not the same as from.getName()
     * 
     * Note, ignores any files in IO.IGNORED
     */
    public static void copy(File from, File to, boolean clean, boolean same, CopyOption... options) {
        if ( from.isDirectory() ) {
            if ( same && !from.getName().equals(to.getName()) ) {
                to = new File(to, from.getName());
            }
            
            if ( !to.exists() ) {
                mkdirs(to);
            }
            
            if ( clean ) {
                clean(to);
            }
            
            String[] files = from.list();
            if ( files != null ) {
                for (String name : files) {
                    if ( !IO.isIgnored(name) ) {
                        copy( new File(from, name), new File(to, name), false);
                    }
                }
            }
        }
        else {
            try {
                Files.copy(toPath(from), toPath(to), options);
            }
            catch (IOException e) {
                throw Ex.runtime(e);
            }
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    // clean
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Does not remove the directory, only cleans its contents
     */
    public static File clean(CharSequence directory) {
        return clean(toFile(directory));
    }
    
    /**
     * Does not remove the directory, only cleans its contents
     */
    public static File clean(File directory) {
        String[] children = directory.list();
        
        if ( children != null ) {
            for (String child : children) {
                remove(
                    new File(directory, child)
                );
            }
        }                                                                                                
        return directory;
    }
    
    /////////////////////////////////////////////////////////////////////
    // remove = rm 
    /////////////////////////////////////////////////////////////////////
    
    public static void remove(CharSequence filepath) {
        remove(toFile(filepath));
    }
    
    public static void remove(File file) {
        if ( file.isDirectory() && !Is.Symlink(file) ) {
            clean(file);    // We clean first
        }
        
        file.delete();      // Now we delete
    }
    
    public static void remove(CharSequence filepath, boolean rm) {
        remove(toFile(filepath), rm);
    }
    
    /**
     * @param rm will remove only if true, otherwise will only clean
     */
    public static void remove(File file, boolean rm) {
        if ( rm ) {
            remove(file);
        }
        else {
            clean(file);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Use Is.Empty() instead. 
     */
    public static boolean isEmpty(File directory) {
        return Is.Empty(directory);
    }
    
    /**
     * Use Is.Empty() instead.
     */
    public static boolean isEmpty(Path directory) {
        return Is.Empty(directory);
    }
    
    /////////////////////////////////////////////////////////////////////
    // Read   
    /////////////////////////////////////////////////////////////////////
    
    public static String getCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        }
        catch(IOException e) {
            throw new $IOException(e);
        }
    }
    
    public static long getCreationTime(CharSequence file) {
        return getCreationTime(toFile(file));
    }
    public static long getCreationTime(File file) {
        return IO.getAttributes(file).creationTime().toMillis();
    }
    public static long getModificationTime(CharSequence file) {
        return getModificationTime(toFile(file));
    }
    public static long getModificationTime(File file) {
        return IO.getAttributes(file).lastModifiedTime().toMillis();
    }
    
    public static BasicFileAttributes getAttibutes(CharSequence file) {
        return getAttributes(toFile(file));
    }
    public static BasicFileAttributes getAttributes(File file) {
        return getAttributes(file, BasicFileAttributeView.class);
    }
    public static BasicFileAttributes getAttributes(File file, Class<BasicFileAttributeView> type) {
        try {
            return Files.getFileAttributeView(toPath(file), type).readAttributes();
        } catch (IOException e) {
            throw new $IOException(e);
        }
    }
    
    /**
     * @param extension If filename is "/path/to/file.txt" then the passed argument need to be just "text"
     * You can get this manually by calling IO.getExtension("/path/to/file.txt") which will return "txt"
     */
    public static String getContentType(CharSequence extension) {
        try {
            String contentType = ContentType.get(extension).getContentType();
            if ( contentType == null ) {
                
                // Seems this is not registered, we attempt to utilize the system probe mechanism for this purpose
                contentType = Files.probeContentType( toPath("." + extension) );  // URLConnection.guessContentTypeFromName(filename); 
                
                // If still not ok, we simply use plain
                if ( !Is.Ok(contentType) ) {
                    contentType = ContentType.PLAIN.getContentType();
                }
                
                // Only in production do we place them in index in order for us to be able to update logic
                if ( Is.Production() ) {
                    new ContentType(extension, contentType); // Will index it for us 
                }
            }
            
            return contentType;
        }
        catch(IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    public static InputStream getInputStream(URLConnection connection) {
        try {
            return connection.getInputStream();
        } catch (IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    public static InputStream getInputStream(ZipFile zip, ZipEntry entry) {
        try {
            return zip.getInputStream(entry);
        } catch (IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    public static FileReader getFileReader(File file) {
        try {
            return new FileReader(file);
        } catch (FileNotFoundException e) {
            throw Ex.runtime(e);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    // getRelativePath
    /////////////////////////////////////////////////////////////////////
    
    /**
     * @folder '/path/to/folder'
     * @file   '/path/to/folder/subfolder/file'
     * @return 'subfolder/file'
     */
    public static String getRelativePath(CharSequence folder, CharSequence file) {
        return file.toString().substring(folder.length() + 1);
    }
    
    /**
     * @folder '/path/to/folder'
     * @file   '/path/to/folder/subfolder/file'
     * @return 'subfolder/file'
     */
    public static String getRelativePath(File folder, File file) {
        try {
            return getRelativePath(folder.getCanonicalPath(), file.getCanonicalPath());
        } catch (IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    // extension get / strip
    /////////////////////////////////////////////////////////////////////
    
    /**
     * "/path/to/file.txt" returns "txt"
     */
    public static String extensionGet(CharSequence filepath) {
        String path = filepath.toString();
        
        if(path != null && !path.isEmpty()) {
            int index = path.lastIndexOf('.');           
            if(index >= 0 && index < path.length() - 1) {
                return path.substring(index + 1);
            }
        }
        
        return "";
    }
    
    /**
     * "/path/to/file.txt" returns "/path/to/file"
     */
    public static String extensionStrip(CharSequence filepath) {
        return extensionStrip(filepath, extensionGet(filepath));
    }
    
    /**
     * "/path/to/file.txt" returns "/path/to/file"
     * 
     * @param extension is already known, allows for quicker answer
     */
    public static String extensionStrip(CharSequence filepath, CharSequence extension) {
        String path = filepath.toString();
        
        if ( Is.Ok(extension) ) {
            return path.substring(0, path.length() - extension.length() - 1);      // Excluding the dot
        }
        
        // No extension was found, return as is
        return path;
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public static boolean isIgnored(File file) {
        return isIgnored(file.getName());
    }
    
    public static boolean isIgnored(String filename) {
        return IGNORED.contains(filename);
    }
    
    /////////////////////////////////////////////////////////////////////
    // Zip ( delegation ). Might require updating if Zip is updated. 
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    /**
     * http://stackoverflow.com/questions/1862908/serve-gzipped-content-with-java-servlets
     */
    public static void gzip(OutputStream outputStream, byte[] data) {
        try( GZIPOutputStream gzip = new GZIPOutputStream(outputStream) ) {
            gzip.write(data);
        }
        catch (IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Used for CSV parsers.
     * @see momomo.com.sources.Csv
     */
    public static <E extends Exception> void withBomAwareBufferedReader(Reader reader, Lambda.V1E<Reader, E> lambda) throws E {
        try {
            try ( reader; BufferedReader bfr = new BufferedReader(reader) ) {
                bfr.mark(1);
                
                int first = bfr.read(); if ( !BOOMS.contains(first) ) {
                    bfr.reset(); // resets back to mark
                }
                
                lambda.call(bfr);
            }
        } catch (IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    // Zip. 
    /////////////////////////////////////////////////////////////////////
    
    /**
     * @see Zip            for other zip cases / usage. 
     * @see IO.Iterate#Zip to iterate a zip file and its contents 
     * 
     * @param zip the zip file
     * @param compression level
     * @param files to include
     */
    public static File zip(File zip, int compression, File... files) {
        return zip(zip, compression, null, files);
    }
    
    /**
     * @see Zip            for other zip cases / usage. 
     * @see IO.Iterate#Zip to iterate a zip file and its contents
     * 
     * Example
     *      IO.zip((ZipEntry entry) -> {
     *             entry.setTime(0);
     *      }, 9, zip, hg);
     *
     * @param zip the zip file
     * @param compression level
     * @param files to include
     */
    public static File zip(File zip, int compression, Lambda.V1<ZipEntry> lambda, File... files) {
        try (FileOutputStream fo = new FileOutputStream(zip); ZipOutputStream zo = new ZipOutputStream(fo)) {
            
            zo.setLevel(compression);
            
            for (File file : files) {
                zip(zo, "", lambda, file);
            }
            
            zo.flush();
            zo.finish();
            
            return zip;
        }
        catch (IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    private static void zip(ZipOutputStream zo, String path, Lambda.V1<ZipEntry> lambda, File file) {
        try {
            path += file.getName();
            
            if ( file.isDirectory() ) {
                path += Strings.SLASH;
                
                IO.add(zo, path, lambda);
                
                for ( String filename : file.list() ) {
                    zip(zo, path, lambda, new File(file, filename) );
                }
            }
            else {
                IO.add(zo, path, lambda);
                
                Files.copy(IO.toPath(file), zo);
            }
            
            zo.closeEntry();
        }
        catch(IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    private static void add(ZipOutputStream zo, String path, Lambda.V1<ZipEntry> lambda) throws IOException {
        ZipEntry entry = new ZipEntry(path);
        if ( lambda != null ) {
            lambda.call(entry);
        }
        zo.putNextEntry(entry);
    }
    
    /**
     * @see Zip            for other zip cases / usage. 
     * @see IO.Iterate#Zip to iterate a zip file and its contents
     * 
     * A bit of redundancy here between this one and each in $Jar.java
     */
    public static File unzip(File zip, File to) {
        try {
            ZipFile zipfile = new ZipFile( zip );
            
            Enumeration<? extends ZipEntry> entries = zipfile.entries( ) ;
            
            while ( entries.hasMoreElements() ) {
                
                ZipEntry entry = entries.nextElement();
                File     file  = new File(to, entry.getName());
                
                if ( entry.isDirectory() ) {
                    mkdirs(file);
                }
                else {
                    mkdirs(file.getParentFile());
                    
                    try ( InputStream in = zipfile.getInputStream(entry) ) {
                        Files.copy(in, toPath(file));
                    }
                }
            }
            
            return zip;
        }
        catch(IOException e) {
            throw Ex.runtime(e);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
}
