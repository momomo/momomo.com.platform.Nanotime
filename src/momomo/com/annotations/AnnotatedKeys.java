package momomo.com.annotations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Interface to be implemented by any interface that has 'String[] keys default {}' defined.
 * <p>
 * {@link $Exclude}
 *
 * @author Joseph S.
 */
public interface AnnotatedKeys<A extends java.lang.annotation.Annotation> extends Annotated<A> {
    String[] keys(A annotation);
    
    default boolean has(Field field, String key) {
        return has(field.getDeclaredAnnotation(annotationClass()), key);
    }
    
    default boolean has(Method method, String key) {
        return has(method.getDeclaredAnnotation(annotationClass()), key);
    }
    
    default boolean has(A annotation, String key) {
        if (annotation != null) {
            for (String k : keys(annotation)) {
                if (key.equals(k)) {
                    return true;
                }
            }
        }
        
        return false;
    }
}
