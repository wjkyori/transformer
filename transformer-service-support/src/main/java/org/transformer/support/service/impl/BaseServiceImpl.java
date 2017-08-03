package org.transformer.support.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.transformer.support.dao.jpa.repository.BaseDao;
import org.transformer.support.dao.jpa.search.Searchable;
import org.transformer.support.entity.AbstractEntity;
import org.transformer.support.service.BaseService;
import org.transformer.support.service.annotation.DaoToService;
import org.transformer.util.Reflections;
import org.transformer.util.SpringUtils;

import java.util.List;

import javax.persistence.EntityManager;

/**
 * 抽象Service层基类 提供一些简便方法<br/>
 * 泛型 ： M 表示实体类型；D表示Dao类型  .
 * 
 * @param <M> 实体类型            
 * @param <D> Dao类型            
 */
public class BaseServiceImpl<M extends AbstractEntity<Long>, D extends BaseDao<M, Long>>
    implements BaseService<M> {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  protected D dao;

  protected EntityManager em;

  protected BaseServiceImpl() {
    if (this.getClass().isAnnotationPresent(DaoToService.class)) {
      DaoToService daoToService = this.getClass().getAnnotation(DaoToService.class);
      this.dao = SpringUtils.getBean(daoToService.name());
    } else {
      Class<D> daoClass = Reflections.findParameterizedType(getClass(), 1);
      this.dao = SpringUtils.getBean(daoClass);
    }
    em = this.dao.getEntityManager();
  }

  @Override
  public void delete(Long[] ids) {
    this.dao.delete(ids);

  }

  @Override
  public void delete(Long id) {
    this.dao.delete(id);
  }

  @Override
  public List<M> findAll() {
    return this.dao.findAll();
  }

  @Override
  public List<M> findAll(Sort sort) {
    return this.dao.findAll(sort);
  }

  @Override
  public Page<M> findAll(Pageable pageable) {
    return this.dao.findAll(pageable);
  }

  @Override
  public Page<M> findAll(Searchable searchable) {
    return this.dao.findAll(searchable);
  }

  @Override
  public long count(Searchable searchable) {
    return this.dao.count(searchable);
  }

  @Override
  public M findOne(Long id) {
    return this.dao.findOne(id);
  }

  @Override
  public M findOneAndEvict(Long id) {
    M entity = this.dao.findOne(id);
    //从session中清除
    if (entity != null) {
      //HibernateUtils.getSession(em).evict(entity);
      em.detach(entity);
    }
    return entity;
  }

  @Override
  public M save(M entity) {
    return this.dao.save(entity);
  }

  @Override
  public List<M> save(Iterable<M> ms) {
    return this.dao.save(ms);
  }

  /** 
  * 获取dao. 
  * @return dao dao 
  */
  public D getDao() {
    return dao;
  }

}
