package momomo.com.sources;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Joseph S.
 */
public class ContentType implements $CharSeq {
    private static final Map<String, ContentType> INDEX   = new HashMap<>();
    
    public static final ContentType CSS                   = new ContentType("css" , "text/css");
    public static final ContentType LESS                  = new ContentType("less", "text/css");
                                                   
    public static final ContentType TTF                   = new ContentType("ttf" , "application/x-font-ttf");
    public static final ContentType WOFF                  = new ContentType("woff", "application/font-woff");
                                                   
    public static final ContentType PNG                   = new ContentType("png" , "image/png");
    public static final ContentType PLAIN                 = new ContentType("txt" , "text/plain");
    public static final ContentType HTML                  = new ContentType("html", "text/html");
    public static final ContentType JS                    = new ContentType("js"  , "text/javascript", false);
    public static final ContentType JS_APPICATION         = new ContentType("js"  , "application/javascript");
    public static final ContentType JSON                  = new ContentType("json", "application/json");
    
    private final String extension, contentType;
    
    public ContentType(CharSequence extension, CharSequence contentType) {
        this(extension, contentType, true);
    }
    
    public ContentType(CharSequence extension, CharSequence contentType, boolean index) {
        this.extension   = extension.toString();
        this.contentType = contentType.toString();
        
        if ( index ) {
            INDEX.put(this.extension, this);
        }
    }
    
    public String getExtension() {
        return extension;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public static boolean isJs(String extension) {
        return JS.getExtension().equals(extension);
    }
    
    public static boolean isLess(String extension) {
        return LESS.getExtension().equals(extension);
    }

    @Override
    public int hashCode() {
        return $hashCode$();
    }

    @Override
    public boolean equals(Object obj) {
        return $equals$(obj);
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Return the registered / indexed contenttype for the extension
     */
    public static ContentType get(CharSequence extension) {
        return INDEX.get(extension.toString());
    }
    /////////////////////////////////////////////////////////////////////
}
