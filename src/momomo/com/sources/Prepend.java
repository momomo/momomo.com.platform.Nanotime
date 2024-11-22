package momomo.com.sources;

import momomo.com.Lambda;
import momomo.com.Strings;

/**
 * A utility class to pepend something before each line. Useful for indentation of code or similar.
 * 
 * @see momomo.com.Strings#Prepend for usage
 * 
 * @author Joseph S.
 */
public class Prepend {
    
    public <E extends Exception> String with(CharSequence text, Lambda.R1E<String, Integer, E> lambda) throws E {
        return with(new StringBuilder(), text, lambda);
    }
    
    public <E extends Exception> String with(StringBuilder sb, CharSequence text, Lambda.R1E<String, Integer, E> lambda) throws E {
        Strings.eachLine(text, (line, number) -> {
            sb.append( lambda.call(number) ).append(line).append( Strings.NEWLINE );
        });
        
        return sb.toString();
    }
    
    public String with(CharSequence text, CharSequence with) {
        final String constant = with.toString(); // One toString call in case it is expensive
        
        return with(text, number -> {
            return constant;
        });
    }

    public String linenumber(CharSequence text) {
        return with(text, number -> {
            return number + Strings.TAB;
        });
    }
    
}
