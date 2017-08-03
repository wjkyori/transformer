package org.transformer.support.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

/**
 * 抽象实体基类，提供统一的ID，和相关的基本功能方法.
 * 
 * @param <ID>
 *            the generic type
 */
@MappedSuperclass
@EntityListeners(value = { DefaultEntityListener.class })
@DynamicInsert
@DynamicUpdate
public abstract class BaseEntity extends AbstractEntity<Long> {

  /**
   * 序列号.
   */
  private static final long serialVersionUID = 2147379215227290693L;

  /** The create time. */
  @Column(name = "create_time", updatable = false)
  private Date createTime;

  /** The modify time. */
  @Column(name = "modify_time")
  private Date modifyTime;

  /** The create by. */
  @Column(name = "create_by", updatable = false)
  private String createBy;

  /** The modify by. */
  @Column(name = "modify_by")
  private String modifyBy;

  /**
   * Gets the creates the time.
   * 
   * @return the creates the time
   */
  public Date getCreateTime() {
    return createTime;
  }

  /**
   * Sets the creates the time.
   * 
   * @param createTime
   *            the new creates the time
   */
  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  /**
   * Gets the modify time.
   * 
   * @return the modify time
   */
  public Date getModifyTime() {
    return modifyTime;
  }

  /**
   * Sets the modify time.
   * 
   * @param modifyTime
   *            the new modify time
   */
  public void setModifyTime(Date modifyTime) {
    this.modifyTime = modifyTime;
  }

  /**
   * Gets the creates the by.
   * 
   * @return the creates the by
   */
  public String getCreateBy() {
    return createBy;
  }

  /**
   * Sets the creates the by.
   * 
   * @param createBy
   *            the new creates the by
   */
  public void setCreateBy(String createBy) {
    this.createBy = createBy;
  }

  /**
   * Gets the modify by.
   * 
   * @return the modify by
   */
  public String getModifyBy() {
    return modifyBy;
  }

  /**
   * Sets the modify by.
   * 
   * @param modifyBy
   *            the new modify by
   */
  public void setModifyBy(String modifyBy) {
    this.modifyBy = modifyBy;
  }

}
