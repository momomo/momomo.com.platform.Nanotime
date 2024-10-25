/* Copyright(C) 2014 - 2020 Momomo LTD. Proprietary and confidential. Usage of this file on any medium without a written consent by Momomo LTD. is strictly prohibited. All Rights Reserved. */
package momomo.com.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Joseph S.
 */
@Target( { METHOD, FIELD, ANNOTATION_TYPE, TYPE }) @Retention(RUNTIME) public @interface $Exclude {
    String[] keys() default {};
    
    
    /**
     * Example
     *      $Exclude.$.has(field, key)
     * 
     * @author Joseph S.
     */
    public static final $ $ = new $(); static final class $ implements AnnotatedKeys<$Exclude> {
        @Override
        public Class<$Exclude> annotationClass() {
            return $Exclude.class;
        }
        
        @Override
        public String[] keys($Exclude annotation){
            return annotation.keys();
        }
    }
}
