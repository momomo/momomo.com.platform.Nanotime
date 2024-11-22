package momomo.com.annotations.informative;

/**
 * @author Joseph S.
 */
public @interface See {
    Class<?>[] value() default {};
}
