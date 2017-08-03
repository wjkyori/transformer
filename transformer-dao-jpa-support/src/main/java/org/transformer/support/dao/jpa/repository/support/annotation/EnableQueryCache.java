package org.transformer.support.dao.jpa.repository.support.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启查询缓存.
 * 
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableQueryCache {

  /**
   * Value.
   * 
   * @return true, if successful
   */
  boolean value() default true;

}
