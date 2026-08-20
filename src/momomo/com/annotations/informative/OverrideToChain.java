/* Copyright(C) 2014 - 2020 Momomo LTD. Proprietary and confidential. Usage of this file on any medium without a written consent by Momomo LTD. is strictly prohibited. All Rights Reserved. */
package momomo.com.annotations.informative;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 *
 * Override this method, delegate to it, then return this to retain the type on your subclass.  
 *
* @author Joseph S.
*/
@Target( { METHOD } )
public @interface OverrideToChain {}
