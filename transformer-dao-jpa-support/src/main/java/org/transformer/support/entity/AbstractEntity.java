package org.transformer.support.entity;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;

/**
 * 抽象实体基类.
 * 
 * @param <I> 主键类型
 *            
 */
public abstract class AbstractEntity<I extends Serializable> implements Persistable<I> {

  /**
   * 序列ID.
   */
  private static final long serialVersionUID = 2418434540005640261L;

  @Override
  public abstract I getId();

  /**
   * Sets the id of the entity.
   * 
   * @param id
   *            the id to set
   */
  public abstract void setId(final I id);

  @Override
  public boolean isNew() {

    return null == getId();
  }

  /**
   * 判断对象是否相等.
   * 如果类型相同且ID相同，也认为相等
   */
  @Override
  public boolean equals(Object obj) {

    if (null == obj) {
      return false;
    }

    if (this == obj) {
      return true;
    }

    if (!getClass().equals(obj.getClass())) {
      return false;
    }

    AbstractEntity<?> that = (AbstractEntity<?>) obj;

    return null == this.getId() ? false : this.getId().equals(that.getId());
  }

  @Override
  public int hashCode() {

    int hashCode = 17;

    hashCode += null == getId() ? 0 : getId().hashCode() * 31;

    return hashCode;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
