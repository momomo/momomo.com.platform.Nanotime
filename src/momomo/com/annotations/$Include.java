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
@Target( { METHOD, FIELD, ANNOTATION_TYPE, TYPE }) @Retention(RUNTIME) public @interface $Include {
    String[] keys() default {};
    
    public static final $ $ = new $(); static final class $ implements AnnotatedKeys<$Include> {
        private $() {}
        
        @Override
        public Class<$Include> annotationClass() {
            return $Include.class;
        }
        
        @Override
        public String[] keys($Include annotation){
            return annotation.keys();
        }
    }
    
}
