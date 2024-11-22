package momomo.com;

import momomo.com.sources.Prepend;
import momomo.com.sources.Replace;
import momomo.com.sources.Wrap;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * 
 * @see momomo.com.sources.Prepend for other operations
 * 
 * @author Joseph S.
 */
public class Strings { private Strings(){}
    
    public static final String LINE  = "------------------------------------------------------------------------";
    public static final String TAB   = "\t";
    public static final String SLASH = "/";
    public static final String QUOTE = "\"";
    
    public static final String  NEWLINE             = System.getProperty("line.separator");
    public static final String  NEWLINES            = "(?:\\r\\n|\\r|\\n)";
    
    public static final Pattern NEWLINES_PATTERN    = Pattern.compile(NEWLINES);
    public static final String  NEWLINE_DEVELOPMENT = !Is.Production(true) ? NEWLINE : "";  // Conditional NEWLINE
    
    public static final Charset CHARSET         = Charset.defaultCharset();
    public static final Charset UTF_8           = StandardCharsets.UTF_8;
    public static final Charset UTF_16          = StandardCharsets.UTF_16;
    public static final Charset CP1252          = Charset.forName("windows-1252");
    
    public static final boolean QUOTE_ESCAPE_FORWARD_SLASH = false;
    
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * IO.Replace.all(...)
     */
    public static final momomo.com.sources.Replace Replace = new Replace();
    
    public static final momomo.com.sources.Prepend Prepend = new Prepend();
    
    /////////////////////////////////////////////////////////////////////
    /// eachLine(CharSequence) 
    /////////////////////////////////////////////////////////////////////
    
    public static <E extends Exception> void eachLine(CharSequence text, Lambda.V1E<String, E> lambda) throws E {
        eachLine(text, lambda.R2E());
    }
    public static <E extends Exception> void eachLine(CharSequence text, Lambda.V2E<String, Integer, E> lambda) throws E {
        eachLine(text, lambda.R2E());
    }
    
    public static <E extends Exception> void eachLine(CharSequence text, Lambda.R1E<Boolean, String, E> lambda) throws E {
        eachLine(text, lambda.R2E());
    }
    
    public static <E extends Exception> void eachLine(CharSequence text, Lambda.R2E<Boolean, String, Integer, E> lambda) throws E {
        eachLine(new Scanner(text.toString()), lambda);
    }
    
    /////////////////////////////////////////////////////////////////////
    /// eachLine(Scanner) 
    /////////////////////////////////////////////////////////////////////
    
    public static <E extends Exception> void eachLine(Scanner scanner, Lambda.V1E<String, E> lambda) throws E {
        eachLine(scanner, lambda.R2E());
    }
    
    public static <E extends Exception> void eachLine(Scanner scanner, Lambda.V2E<String, Integer, E> lambda) throws E {
        eachLine(scanner, lambda.R2E());
    }
    
    public static <E extends Exception> void eachLine(Scanner scanner, Lambda.R1E<Boolean, String, E> lambda) throws E {
        eachLine(scanner, lambda.R2E());
    }
    
    public static <E extends Exception> void eachLine(Scanner scanner, Lambda.R2E<Boolean, String, Integer, E> lambda) throws E {
        try (Scanner scan = scanner) {
            int i = 0;
            while ( scan.hasNextLine() ) {
                if ( Is.False(lambda.call(scan.nextLine(), i++)) ) break;
            }
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    // eachChar(CharSequence)
    /////////////////////////////////////////////////////////////////////
    
    public static <E extends Exception> void eachChar(CharSequence text, Lambda.V1E<Character, E> lambda) throws E {
        eachChar(text, lambda.R2E());
    }
    
    public static <E extends Exception> void eachChar(CharSequence text, Lambda.V2E<Character, Integer, E> lambda) throws E {
        eachChar(text, lambda.R2E());
    }
    
    public static <E extends Exception> void eachChar(CharSequence text, Lambda.R1E<Boolean, Character, E> lambda) throws E {
        eachChar(text, lambda.R2E());
    }
    
    public static <E extends Exception> void eachChar(CharSequence text, Lambda.R2E<Boolean, Character, Integer, E> lambda) throws E {
        int i = -1; while ( ++i < text.length() ) {
            if ( Is.False(lambda.call(text.charAt(i), i)) ) { return; };    
        }
    }
    
    /**
     * Append scanner to sb
     */
    public static StringBuilder append(StringBuilder sb, Scanner scanner) {
        Strings.eachLine(scanner, (String line) -> sb.append(line)); return sb;
    }
    
    /////////////////////////////////////////////////////////////////////
    /// misc
    /////////////////////////////////////////////////////////////////////
    
    public static StringBuilder clear(StringBuilder sb) {
        /*sb.delete(0, sb.length());*/ sb.setLength(0); return sb;
    }
    
    /**
     * Example
     *    Strings.multiply("*", 15) => "***************"
     * 
     */
    public static String multiply( CharSequence c, int i ) {
        StringBuilder chars = new StringBuilder(i); while ( --i >= 0 ) {
            chars.append( c );
        }
        return chars.toString();
    }
    
    /////////////////////////////////////////////////////////////////////
    // join
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Example
     *    join(".", "a", "b", "c") => "a.b.c" 
     * 
     * @param between each object
     */
    public static String join(CharSequence between, Object ... objects) {
        return join("", between, objects);
    }
    
    /**
     Example
     *    join(".", "a", "b", "c") => "a.b.c"
     *    
     * @param between each object
     */
    public static String join(CharSequence between, Collection<?> objects) {
        return join("", between, objects);
    }
    
    /**
     * Example
     *    join("tableAlias", ",", "a", "b", "c") => "tableAlias.a, tableAlias.b, tableAlias.c"
     * 
     * @param prepend each item
     * @param between each item 
     */
    public static String join( CharSequence prepend, CharSequence between, Object ... args ) {
        return join(prepend, between, Arrays.asList(args));
    }
    
    /**
     * Example
     *    join("tableAlias", ",", "a", "b", "c") => "tableAlias.a, tableAlias.b, tableAlias.c"
     * 
     * @param prepend each item
     * @param between each item
     */
    public static String join( CharSequence prepend, CharSequence between, Collection<?> args ) {
        StringBuilder sb = new StringBuilder();
        
        if (Is.Ok(args)) {
            int i = 0, l = args.size();
            for (Object o : args) {
                ++i;
                
                if ( o != null ) {
                    String s = o.toString();
                    if ( Is.Ok(s) ) {
                        sb.append(prepend).append(s);
                        
                        if (i != l) {
                            sb.append(between);
                        }
                    }
                    
                }
            }
            
        }
        return sb.toString();
    }
    
    /////////////////////////////////////////////////////////////////////
    // quote
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Simply adds quotes around a string => " string ", without escaping or parsing the string 
     */
    public static String quoted(Object obj) {
        return Strings.QUOTE + obj.toString() + Strings.QUOTE;
    }
    
    /**
     * Adds quotes around a string while escaping it 
     */
    public static String quote(Object o) {
        return quote(o.toString());
    }
    
    /**
     * Adds quotes around a string while escaping it
     */
    public static String quote(CharSequence str) {
        return quote(str, QUOTE_ESCAPE_FORWARD_SLASH);
    }
    
    /**
     * Adds quotes around a string while escaping it
     * 
     * Inspired from http://grepcode.com/file/repo1.maven.org/maven2/org.codehaus.jettison/jettison/1.3.7/org/codehaus/jettison/json/JSONObject.java?av=f
     */
    public static String quote(CharSequence characters, boolean escapeForwardSlash) {
        if (characters == null || characters.length() == 0) {
            return Strings.QUOTE + Strings.QUOTE;
        }
        
        int           length = characters.length();
        StringBuilder sb     = new StringBuilder(Numbers.toInt(length * 1.2)).append(Strings.QUOTE);
        
        char c; int i = -1; while ( ++i < length ) {
            c = characters.charAt(i);
            switch (c) {
                case '\\':
                case '"':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '/':
                    if (escapeForwardSlash || i > 0 && characters.charAt(i - 1) == '<') {
                        sb.append('\\');
                    }
                    sb.append(c);
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                default:
                    if (c < ' ') {
                        String t = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                    } else {
                        sb.append(c);
                    }
            }
        }
        
        sb.append('"'); return sb.toString();
    }
    
    /////////////////////////////////////////////////////////////////////
    // to
    /////////////////////////////////////////////////////////////////////
    
    public static byte[] toBytes(CharSequence characters) {
        return toBytes(characters, UTF_8);
    }
    
    public static byte[] toBytes(CharSequence characters, Charset charset) {
        return characters == null ? null : characters.toString().getBytes(charset);
    }
    
    public static char[] toCharArray(CharSequence characters) {
        char[] chars = new char[characters.length()];
        
        eachChar(characters, (c, i) -> {
            chars[i] = c;
        });
        
        return chars;
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Example
     *      caseDownFirst("ComputerSystem") ==> "computerSystem"  
     */
    public static String caseDownFirst(CharSequence characters) {
        return ("" + characters.charAt(0)).toLowerCase() + characters.subSequence(1, characters.length());
    }
    
    /**
     * Example
     *      caseUpFirst("joseph is amazing!") ==> "Joseph is amazing!"  
     */
    public static String caseUpFirst(CharSequence characters) {
        return ("" + characters.charAt(0)).toUpperCase() + characters.subSequence(1, characters.length());
    }
    
    /**
     * Recursibvely removes the slash from the start or end
     *
     * Example
     *      unslash("/path/to/dir/"      , true, true)  => path/to/dir
     *      unslash("////path/to/dir////", true, true)  => path/to/dir
     *      unslash("/path/to/dir/"      , false, true) => /path/to/dir
     *
     * @param start true if you want to remove from start of the string
     * @param end true if you want to remove from start of the string 
     */
    public static String unslash(CharSequence characters, boolean start, boolean end) {
        return unchar(characters, Strings.SLASH, start, end);
    }
    
    /**
     * @see Strings#unslash(CharSequence, boolean, boolean)
     *
     * @param remove rather than a "/" it can be any character
     */
    public static String unchar(CharSequence characters, CharSequence remove, boolean start, boolean end) {
        String str = characters.toString(), character = remove.toString();
        
        if ( str != null ) {
            if (start && str.startsWith(character)) {
                return unchar(str.substring(1), character, start, end);
            }
            else
            if ( end && str.endsWith(character) ){
                return unchar(str.substring(0, str.length() - 1), character, start, end);
            }
        }
        
        return str;
    }
    
    /**
     * Differs from lastIndexOf in that it repeats its occurrences number of times, so the requirement is that
     * it finds something x number of times. For instance, lastIndexOf("/path/to/file.txt", "/", 2)
     * would return the index of "path/" .. the second(2) slash from end.
     *
     * Furthermore, if there are no matches at all, then it will return -1, otherwise, it will return the last found match. 
     * So occurrences would indicate a maximum number of slashes, but if fewer it would get that.
     */
    public static int lastIndexOf(String of, String target, int occurrences) {
        int j = -1, k = of.length();
        while ( occurrences-- > 0 ) {
            if ( (k = of.lastIndexOf(target, k-1)) > -1 ) {
                j = k;
            }
            else {
                break;
            }
        }
        
        return j;   // Either -1 or a previous k
    }
    
    /**
     * @param limit do not look further than this index in the string as we are only looking for a match up to this index. 
     */
    public static int indexOf(CharSequence characters, char character, long limit) {
        Wrap<Integer> offset = Wrap.it(); Strings.eachChar(characters, (c, i) -> {
            if (i > limit) {
                return false;
            }
            
            if ( c == character ) {
                offset.it = i; return false;
            }
            
            return true;
        });
        
        return offset.it;
    }
    
    /**
     * @return the number of occurrences a word exists in characters
     */
    public static int occurrences(CharSequence characters, CharSequence word) {
        String str = characters.toString(), w = word.toString();
        
        int at = 0, found = 0; do {
            at = str.indexOf(w, at);
            
            // Eventually there will be no more matches, and we return the total found so far. 
            if ( at == -1 ) { return found;  }
            
            at += word.length();
            
            found++;
        } while( true );
    }
    
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Simple json object that can be used for javascript engine purposes
     *
     * Note, does not quote keys, or values intentionally in order to preserve ability to retain object references as is
     * 
     * @see Strings#model(boolean, boolean, Object...)  
     *
     * @param args key val alternating
     */
    public static String model(Object ... args) {
        return model(false, false, args);
    }
    
    /**
     * Example
     *     String model = Strings.model(
     *         "downlodable", "aaa",
     *         "title"      , "bb",
     *         "interval"   , "ccc",
     *         "date"       , Strings.quote("ddd")
     *     );
     *
     *     model == "{...}" 
     *     
     * @param quoteKey if you wish to quote keys
     * @param quoteVal if you with to quote values
     * @param args key val alternating                 
     */
    public static String model(boolean quoteKey, boolean quoteVal, Object ... args) {
        StringBuilder sb = new StringBuilder("{");
        
        int length = args.length, i = -1; while ( i < length ) {
            Object key = args[++i]; sb.append( quoteKey ? Strings.quote(key) : key );
            sb.append(":");
            Object val = args[++i]; sb.append( quoteVal ? Strings.quote(val) : val );
        
            // Separate by comma for all but the last
            if ( i < length ) {  sb.append(","); } 
        }
        
        return sb.append("}").toString();
    }

}