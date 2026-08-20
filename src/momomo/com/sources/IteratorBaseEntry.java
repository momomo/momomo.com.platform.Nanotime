package momomo.com.sources;

/**
 * See {@link momomo.com.IO.Iterate} for direct usage
 * 
 * @author Joseph S.
 */
public interface IteratorBaseEntry {
    String  getPath                 ();
    String  getRelativeIterationPath();
    boolean isDirectory             ();
}
