/* Copyright(C) 2014 - 2020 Momomo LTD. Proprietary and confidential. Usage of this file on any medium without a written consent by Momomo LTD. is strictly prohibited. All Rights Reserved. */
package momomo.com.annotations.informative;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * Just an informative annotation intended to be used to signal that this method IS already overriden by someone. 
 * 
 * @author Joseph S.
*/
@Target( { METHOD} )
public @interface Overriden {
	Class<?>[] value() default {};
}
