package momomo.com.sources;

import java.util.zip.ZipEntry;

/**
 * See {@link momomo.com.IO.Iterate} for direct usage
 * 
 * @author Joseph S.
 */
public class IteratorZipEntry implements IteratorBaseEntry {
    private final ZipEntry entry;
    private final String   path;
    private final String   relativePath;

	public IteratorZipEntry(ZipEntry entry, int substring) {
        this.entry        = entry;
        path              = entry.getName();
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
		return entry.isDirectory();
	}
}

