package momomo.com.sources;

import java.io.File;

/**
 * See {@link momomo.com.IO.Iterate} for direct usage
 * 
 * Note, this is used from {@link IteratorURL} and not {@link momomo.com.test.IteratorFile} where we opt to use File instead directly
 * 
 * @author Joseph S.
 */
public final class IteratorFileEntry implements IteratorBaseEntry {
    private final File   file;
    private final String relativePath;
    private final String path;
    
    public IteratorFileEntry(File file, int substring) {
        this.file         = file;
        path              = file.getAbsolutePath();
        this.relativePath = path.substring(substring);
    }
    
    @Override
    public String getPath() {
        return path;
    }
    
    @Override
    public String getRelativeIterationPath() {
        return relativePath;
    }
    
    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }
    
}
