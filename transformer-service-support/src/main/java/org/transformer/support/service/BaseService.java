package org.transformer.support.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.transformer.support.dao.jpa.search.Searchable;
import org.transformer.support.entity.AbstractEntity;

import java.util.List;

/**
 * 抽象Service层基类 提供一些简便方法.
 * 泛型 ： M 表示实体类型；
 * 
 * @param <M> 实体类型            
 */
public interface BaseService<M extends AbstractEntity<Long>> {

  /**
   * 通过ID查找实体.
   * @param id 主键
   * @return 实体对象
   */
  public M findOne(Long id);

  /**
   * 通过ID查找实体,并从session中清除，变成游离态对象.
   * @param id 主键
   * @return 实体对象
   */
  public M findOneAndEvict(Long id);

  /**
   * saveOrUpdate实体.
   * @param m 实体
   * @return 保存后的实体
   */
  public M save(M entity);

  /**
   * 保存实体.
   * @param ms 实体列表
   * @return 保存后的实体列表
   */
  public List<M> save(Iterable<M> ms);

  /**
   * 根据主键删除.
   * 
   * @param id
   *            the ids
   */
  public void delete(Long id);

  /**
   * 根据主键删除.
   * 
   * @param ids
   *            the ids
   */
  public void delete(Long[] ids);

  /**
   * 查询所有.
   */
  public List<M> findAll();

  /**
   * 查询所有，排序.
   */
  public List<M> findAll(Sort sort);

  /**
   * 根据条件查询所有，分页.
   */
  public Page<M> findAll(Pageable pageable);

  /**
   * 根据条件查询所有 条件 + 分页 + 排序.
   * 
   * @param searchable
   *            the searchable
   * @return the page
   */
  public Page<M> findAll(Searchable searchable);

  /**
   * 根据条件统计所有记录数.
   * 
   * @param searchable
   *            the searchable
   * @return the long 记录数
   */
  public long count(Searchable searchable);

}
