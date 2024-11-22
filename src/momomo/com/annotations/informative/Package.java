package momomo.com.annotations.informative;

/**
 * An annotation that declares something intended as package-private but possibly othwerwise for other reasons, or language limitations.
 *
 * @author Joseph S.
 */
public @interface Package {
    String value() default "";
}
