package momomo.com.sources;

import momomo.com.Strings;

import java.io.StringWriter;
import java.io.Writer;

/**
 * Just delegate needed to expose out what is normally protected in PrintWriter
 *
 * @author Joseph S.
 */
public final class WriterPrint extends java.io.PrintWriter {
    
    public WriterPrint() {
        super(new StringWriter());
    }
    
    public Writer getWriter() {
        return this.out;
    }
    
    
    /**
     * We use the printwrite to get the proper class name resolution in the console so that one can navigate as usual using intellij for instance (click)
     */
    public boolean write(StackTraceElement[] trace, String indentation_current) {
        int l = trace.length;
        
        boolean modified = l > 0;
        if ( modified ) {
            println();
        }
        
        var i = -1; while ( ++i < l ) {
            print(indentation_current);
            print(trace[i] + Strings.NEWLINE);
        }
        
        return modified;
    }
    
}
