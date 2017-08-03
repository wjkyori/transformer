package org.transformer.support.dao.jpa.repository.support.annotation;

import org.transformer.support.dao.jpa.repository.callback.SearchCallback;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 覆盖默认的根据条件查询数据.
 * 
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SearchableQuery {

  /**
   * 覆盖默认的查询所有ql.
   * 
   * @return the string
   */
  String findAllQuery() default "";

  /**
   * 覆盖默认的统计所有ql.
   * 
   * @return the string
   */
  String countAllQuery() default "";

  /**
   * 给ql拼条件及赋值的回调类型.
   * 
   */
  Class<? extends SearchCallback> callbackClass() default SearchCallback.class;

  /**
   * Joins.
   * 
   * @return the query join[]
   */
  QueryJoin[] joins() default {};

}
