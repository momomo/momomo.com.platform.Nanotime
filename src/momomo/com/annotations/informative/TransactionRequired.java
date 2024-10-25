/* Copyright(C) 2014 - 2020 Momomo LTD. Proprietary and confidential. Usage of this file on any medium without a written consent by Momomo LTD. is strictly prohibited. All Rights Reserved. */
package momomo.com.annotations.informative;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * @author Joseph S.
 *
 * This is just an informative annotation notifying the developer that the annotated method requires a transaction to be in progress before call
 * This annotion does little other than that.
 *
 * No longer used. Only in older projects.
 */
@Target( { METHOD })
public @interface TransactionRequired{}
