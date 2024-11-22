/* Copyright(C) 2014 - 2020 Momomo LTD. Proprietary and confidential. Usage of this file on any medium without a written consent by Momomo LTD. is strictly prohibited. All Rights Reserved. */
package momomo.com.annotations.informative;

/**
 * An annotation that declares something intended as private but possibly othwerwise for other reasons, or language limitations.
 *
 * @author Joseph S.
 */
public @interface Private {
    String value() default "";
}
