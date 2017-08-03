package org.transformer.support.dao.jpa.repository.support.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.persistence.criteria.JoinType;

/**
 * The Interface QueryJoin.
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryJoin {

  /**
   * 连接的名字.
   * 
   * @return the string
   */
  String property();

  /**
   * Join type.
   * 
   * @return the join type
   */
  JoinType joinType();

}
