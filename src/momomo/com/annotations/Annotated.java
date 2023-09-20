package momomo.com.annotations;

/**
 * @author Joseph S.
 */
public interface Annotated<A extends java.lang.annotation.Annotation> {
    Class<A> annotationClass();
}
