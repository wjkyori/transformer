package org.transformer.support.dao.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.transformer.support.dao.jpa.search.Searchable;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

/**
 * 抽象DAO层基类 提供一些简便方法<br/>
 * 想要使用该接口需要在spring配置文件的jpa:repositories中添加
 * factory-class="BaseRepositoryFactoryBean"
 * 泛型 ： M 表示实体类型；ID表示主键类型.
 * 
 * @param <M> 实体类型            
 * @param <ID> 主键类型            
 */
@NoRepositoryBean
public interface BaseDao<M, ID extends Serializable> extends JpaRepository<M, ID> {

  /**
   * 根据主键删除.
   * 
   * @param ids
   *            the ids
   */
  public void delete(ID[] ids);

  /**
   * 查询所有.
   */
  @Override
  List<M> findAll();

  /**
   * 查询所有，排序.
   */
  @Override
  List<M> findAll(Sort sort);

  /**
   * 根据条件查询所有，分页.
   */
  @Override
  Page<M> findAll(Pageable pageable);

  /**
   * 根据条件查询所有 条件 + 分页 + 排序.
   * 
   * @param searchable
   *            the searchable
   * @return the page
   */
  public Page<M> findAll(Searchable searchable);

  /**
   * 复杂查询,返回多条记录.
   * @param spec 查询条件
   * @return 查询结果记录集合
   */
  public List<M> findAll(Specification<M> spec);

  /**
   * 复杂查询,返回分页结果.
   * @param spec 查询条件
   * @return 分页结果
   */
  public Page<M> findAll(Specification<M> spec, Pageable pageable);

  /**
   * 根据条件统计所有记录数.
   * 
   * @param searchable
   *            the searchable
   * @return the long
   */
  public long count(Searchable searchable);

  /**
   * 复杂查询,返回单条记录.
   * @param spec 查询条件
   * @return 查询结果记录
   */
  public M findOne(Specification<M> spec);

  /**
   * 获取实体管理器.
   * @return 实体管理器
   */
  public EntityManager getEntityManager();

}
