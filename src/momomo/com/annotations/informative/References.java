/* Copyright(C) 2014 - 2020 Momomo LTD. Proprietary and confidential. Usage of this file on any medium without a written consent by Momomo LTD. is strictly prohibited. All Rights Reserved. */
package momomo.com.annotations.informative;

/**
 * We can leave notes, and attach a referencing class that is somehow relevant. 
 *
 * @author Joseph S.
 */
public @interface References {
    String[] key() default {};
    Class<?>[] value() default {};
}
