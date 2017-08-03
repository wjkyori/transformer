package org.transformer.support.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class DefaultEntityListener {
  /** The logger. */
  private static Logger logger = LoggerFactory.getLogger(DefaultEntityListener.class);

  /**
   * Pre persist.
   * 
   * @param object
   *            the object
   */
  @PrePersist
  public void prePersist(Object object) {
    if (object instanceof BaseEntity) {
      BaseEntity baseEntity = (BaseEntity) object;
      baseEntity.setCreateTime(new Date());
      baseEntity.setModifyTime(new Date());
    }
  }

  /**
   * Pre update.
   * 
   * @param object
   *            the object
   */
  @PreUpdate
  public void preUpdate(Object object) {
    if (object instanceof BaseEntity) {
      BaseEntity baseEntity = (BaseEntity) object;
      // 修改旧对象
      baseEntity.setModifyTime(new Date());
      logger.info("{}对象(ID:{}) 在 {} 修改", new Object[] { object.getClass().getName(),
          baseEntity.getId(), baseEntity.getModifyBy(), new Date() });
    }
  }
}
