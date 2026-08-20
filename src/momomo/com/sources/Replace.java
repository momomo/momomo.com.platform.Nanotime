package momomo.com.sources;

import momomo.com.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @see momomo.com.Strings#Replace
 * 
 * IO.Replace.all(...)
 * 
 * @author Joseph S.
 */
public class Replace {
    private final boolean quoteReplacement;
    
    public Replace() {
        this(true);
    }
    
    public Replace(boolean quoteReplacement) {
        this.quoteReplacement = quoteReplacement;
    }
    
    /**
     * This method does not use regex for achieving this replacement, in contrast with String.replaceFrist
     */
    public String first(CharSequence in, String target, String replacement) {
        return first(in, target, replacement, 0);
    }
    
    public String first(CharSequence in, String target, String replacement, int from) {
        String text = in.toString();
        
        int index = text.indexOf(target, from);
        
        if ( index > -1 ) {
            return text.substring(0, index) + replacement + text.substring(index  + target.length());
        }
        
        return text;
    }
    
    /**
     * This one automatically quotes the replacement to avoid regex related issues 
     */
    public String all(CharSequence in, String regex, String replacement) {
        return all(in, regex, replacement, quoteReplacement);
    }
    
    /**
     * This one automatically quotes the replacement to avoid regex related issues 
     */
    public String all(CharSequence in, String regex, String replacement, boolean quote) {
        return in.toString().replaceAll(regex, getQuotedReplacement(replacement, quote));
    }
    
    /**
     *This one automatically quotes the replacement to avoid regex related issues.  
     */
    public String all(CharSequence text, Pattern pattern, String replacement) {
        return all(text, pattern, replacement, quoteReplacement);
    }
    
    /**
     *This one automatically quotes the replacement to avoid regex related issues unless quote is set to false.   
     */
    public String all(CharSequence text, Pattern pattern, String replacement, boolean quote) {
        return pattern.matcher(text).replaceAll(getQuotedReplacement(replacement, quote));
    }
    
    public String allNewLines(String input, String replacement) {
        return Strings.NEWLINES_PATTERN.matcher(input).replaceAll(replacement);
    }
    
    /**
     * Quote or not? 
     */
    protected String getQuotedReplacement(String replacement, boolean quoteReplacement) {
        return quoteReplacement ? Matcher.quoteReplacement(replacement) : replacement;
    }
    
}
