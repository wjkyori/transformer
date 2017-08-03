package org.transformer.support.dao.jpa.repository.support;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.transformer.support.dao.jpa.repository.BaseDao;
import org.transformer.support.dao.jpa.repository.RepositoryHelper;
import org.transformer.support.dao.jpa.repository.callback.SearchCallback;
import org.transformer.support.dao.jpa.repository.support.annotation.QueryJoin;
import org.transformer.support.dao.jpa.search.Searchable;
import org.transformer.util.Reflections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * <p>
 * 抽象基础Custom Repository 实现
 * </p>
 * .
 * 
 * @param <M>
 *            the generic type
 * @param <I>
 *            the generic type
 */
public class BaseRepository<M, I extends Serializable> extends SimpleJpaRepository<M, I>
    implements BaseDao<M, I> {

  /** 日志. */
  private static Logger logger = LoggerFactory.getLogger(BaseRepository.class);

  /** The Constant DELETE_ALL_QUERY_STRING. */
  public static final String DELETE_ALL_QUERY_STRING = "delete from %s x where x in (?1)";

  /** The Constant FIND_QUERY_STRING. */
  public static final String FIND_QUERY_STRING = "from %s x where 1=1 ";

  /** The Constant COUNT_QUERY_STRING. */
  public static final String COUNT_QUERY_STRING = "select count(x) from %s x where 1=1 ";

  /** The em. */
  private final EntityManager em;

  /** The entity information. */
  private final JpaEntityInformation<M, I> entityInformation;

  /** The repository helper. */
  private final RepositoryHelper repositoryHelper;

  /** The entity class. */
  protected Class<M> entityClass;

  /** The entity name. */
  private String entityName;

  /** The id name. */
  private String idName;

  /** 查询所有的QL. */
  private String findAllQl;

  /** 统计QL. */
  private String countAllQl;

  /** The joins. */
  private QueryJoin[] joins;

  /** The search callback. */
  private SearchCallback searchCallback = SearchCallback.DEFAULT;

  /**
   * Instantiates a new simple base repository.
   * 
   * @param entityInformation
   *            the entity information
   * @param entityManager
   *            the entity manager
   */
  public BaseRepository(JpaEntityInformation<M, I> entityInformation, EntityManager entityManager) {
    super(entityInformation, entityManager);

    this.entityInformation = entityInformation;
    this.entityClass = this.entityInformation.getJavaType();
    this.entityName = this.entityInformation.getEntityName();
    this.idName = this.entityInformation.getIdAttributeNames().iterator().next();
    this.em = entityManager;

    repositoryHelper = new RepositoryHelper(entityClass);
    repositoryHelper.setEntityManager(entityManager);

    findAllQl = String.format(FIND_QUERY_STRING, entityName);
    countAllQl = String.format(COUNT_QUERY_STRING, entityName);
  }

  /**
   * 设置searchCallback.
   * 
   * @param searchCallback
   *            the new search callback
   */
  public void setSearchCallback(SearchCallback searchCallback) {
    this.searchCallback = searchCallback;
  }

  /**
   * 设置查询所有的ql.
   * @param findAllQL
   *            the new find all ql
   */
  public void setFindAllQl(String findAllQl) {
    this.findAllQl = findAllQl;
  }

  /**
   * 设置统计的ql.
   * 
   * @param countAllQL
   *            the new count all ql
   */
  public void setCountAllQl(String countAllQl) {
    this.countAllQl = countAllQl;
  }

  /**
   * Sets the joins.
   * 
   * @param joins
   *            the new joins
   */
  public void setJoins(QueryJoin[] joins) {
    this.joins = joins;
  }

  /**
   * 根据主键删除相应实体.
   * 
   * @param id
   *            主键
   */
  @Transactional
  @Override
  public void delete(final I id) {
    M entity = findOne(id);
    delete(entity);
  }

  /**
   * 删除实体.
   * @param entity
   *            实体
   */
  @Transactional
  @Override
  public void delete(final M entity) {
    if (entity == null) {
      return;
    }
    super.delete(entity);
  }

  /**
   * 根据主键删除相应实体.
   * 
   * @param ids
   *            实体
   */
  @Transactional
  @Override
  public void delete(final I[] ids) {
    if (ArrayUtils.isEmpty(ids)) {
      return;
    }
    List<M> models = new ArrayList<M>();
    for (I id : ids) {
      M model = null;
      try {
        model = entityClass.newInstance();
      } catch (Exception e) {
        throw new RuntimeException("batch delete " + entityClass + " error", e);
      }
      try {
        Reflections.setFieldValue(model, idName, id);
      } catch (Exception e) {
        throw new RuntimeException("batch delete " + entityClass + " error, can not set id", e);
      }
      models.add(model);
    }
    deleteInBatch(models);
  }

  @Transactional
  @Override
  public void deleteInBatch(final Iterable<M> entities) {
    super.delete(entities);
  }

  /**
   * 按照主键查询.
   * 
   * @param id
   *            主键
   * @return 返回id对应的实体
   */
  @Override
  public M findOne(I id) {
    if (id == null) {
      return null;
    }
    if (id instanceof Integer && ((Integer) id).intValue() == 0) {
      return null;
    }
    if (id instanceof Long && ((Long) id).longValue() == 0L) {
      return null;
    }
    return super.findOne(id);
  }

  @Override
  public M findOne(Specification<M> spec) {
    try {
      return getQuery(spec, (Sort) null).getSingleResult();
    } catch (NoResultException e) {
      logger.info("no result", e);
      return null;
    }
  }

  @Override
  public List<M> findAll(Iterable<I> ids) {

    return getQuery(new Specification<M>() {
      @SuppressWarnings("synthetic-access")
      @Override
      public Predicate toPredicate(Root<M> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Path<?> path = root.get(entityInformation.getIdAttribute());
        return path.in(cb.parameter(Iterable.class, "ids"));
      }
    }, (Sort) null).setParameter("ids", ids).getResultList();
  }

  @Override
  public List<M> findAll(Specification<M> spec) {
    return getQuery(spec, (Sort) null).getResultList();
  }

  @Override
  public Page<M> findAll(Specification<M> spec, Pageable pageable) {

    TypedQuery<M> query = getQuery(spec, pageable);
    return pageable == null ? new PageImpl<M>(query.getResultList())
        : readPage(query, pageable, spec);
  }

  @Override
  public List<M> findAll(Specification<M> spec, Sort sort) {

    return getQuery(spec, sort).getResultList();
  }

  @Override
  public List<M> findAll() {
    return repositoryHelper.findAll(findAllQl);
  }

  @Override
  public List<M> findAll(final Sort sort) {
    return repositoryHelper.findAll(findAllQl, sort);
  }

  @Override
  public Page<M> findAll(final Pageable pageable) {
    return new PageImpl<M>(repositoryHelper.<M>findAll(findAllQl, pageable), pageable,
        repositoryHelper.count(countAllQl));
  }

  @Override
  public Page<M> findAll(final Searchable searchable) {
    List<M> list = repositoryHelper.findAll(findAllQl, searchable, searchCallback);
    long total = searchable.hasPageable() ? count(searchable) : list.size();
    return new PageImpl<M>(list, searchable.getPage(), total);
  }

  @Override
  public long count(Specification<M> spec) {

    return getCountQuery(spec).getSingleResult();
  }

  @Override
  public long count() {
    return repositoryHelper.count(countAllQl);
  }

  @Override
  public long count(final Searchable searchable) {
    return repositoryHelper.count(countAllQl, searchable, searchCallback);
  }

  /**
   * Reads the given {@link javax.persistence.TypedQuery} into a
   * 
   * @param query
   *            must not be {@literal null}.
   * @param pageable
   *            can be {@literal null}.
   * @param spec
   *            can be {@literal null}.
   * @return the page {@link org.springframework.data.domain.Page} applying
   *         the given {@link org.springframework.data.domain.Pageable} and
   *         {@link org.springframework.data.jpa.domain.Specification}.
   */
  @Override
  protected Page<M> readPage(TypedQuery<M> query, Pageable pageable, Specification<M> spec) {

    query.setFirstResult(pageable.getOffset());
    query.setMaxResults(pageable.getPageSize());
    Long total = executeCountQuery(getCountQuery(spec));
    List<M> content = total > pageable.getOffset() ? query.getResultList()
        : Collections.<M>emptyList();

    return new PageImpl<M>(content, pageable, total);
  }

  private static Long executeCountQuery(TypedQuery<Long> countQuery) {
    Assert.notNull(countQuery, "CountQuery must not null");

    List<Long> totals = countQuery.getResultList();
    Long total = 0L;

    if (totals != null) {
      for (Long element : totals) {
        total += element == null ? 0 : element;
      }
    }
    return total;
  }

  /**
   * Creates a new count query for the given.
   * 
   * @param spec
   *            can be {@literal null}.
   * @return the count query
   *         {@link org.springframework.data.jpa.domain.Specification}.
   */
  @Override
  protected TypedQuery<Long> getCountQuery(Specification<M> spec) {

    CriteriaBuilder builder = em.getCriteriaBuilder();
    CriteriaQuery<Long> query = builder.createQuery(Long.class);

    Root<M> root = applySpecificationToCriteria(spec, query);

    if (query.isDistinct()) {
      query.select(builder.countDistinct(root));
    } else {
      query.select(builder.count(root));
    }

    TypedQuery<Long> tq = em.createQuery(query);
    repositoryHelper.applyEnableQueryCache(tq);
    return tq;
  }

  /**
   * Creates a new {@link javax.persistence.TypedQuery} from the given
   * 
   * @param spec
   *            can be {@literal null}.
   * @param pageable
   *            can be {@literal null}.
   * @return the query
   *         {@link org.springframework.data.jpa.domain.Specification}.
   */
  @Override
  protected TypedQuery<M> getQuery(Specification<M> spec, Pageable pageable) {

    Sort sort = pageable == null ? null : pageable.getSort();
    return getQuery(spec, sort);
  }

  /**
   * Creates a {@link javax.persistence.TypedQuery} for the given
   * 
   * @param spec
   *            can be {@literal null}.
   * @param sort
   *            can be {@literal null}.
   * @return the query
   *         {@link org.springframework.data.jpa.domain.Specification} and
   *         {@link org.springframework.data.domain.Sort}.
   */
  @Override
  protected TypedQuery<M> getQuery(Specification<M> spec, Sort sort) {

    CriteriaBuilder builder = em.getCriteriaBuilder();
    CriteriaQuery<M> query = builder.createQuery(entityClass);

    Root<M> root = applySpecificationToCriteria(spec, query);
    query.select(root);

    applyJoins(root);

    if (sort != null) {
      query.orderBy(QueryUtils.toOrders(sort, root, builder));
    }

    TypedQuery<M> tq = em.createQuery(query);

    repositoryHelper.applyEnableQueryCache(tq);

    return applyLockMode(tq);
  }

  /**
   * Apply joins.
   * 
   * @param root
   *            the root
   */
  private void applyJoins(Root<M> root) {
    if (joins == null) {
      return;
    }

    for (QueryJoin join : joins) {
      root.join(join.property(), join.joinType());
    }
  }

  /**
   * Applies the given.
   * 
   * @param <S>
   *            the generic type
   * @param spec
   *            can be {@literal null}.
   * @param query
   *            must not be {@literal null}.
   * @return the root
   *         {@link org.springframework.data.jpa.domain.Specification} to the
   *         given {@link javax.persistence.criteria.CriteriaQuery}.
   */
  private <S> Root<M> applySpecificationToCriteria(Specification<M> spec, CriteriaQuery<S> query) {

    Assert.notNull(query, "query must not null");
    Root<M> root = query.from(entityClass);

    if (spec == null) {
      return root;
    }

    CriteriaBuilder builder = em.getCriteriaBuilder();
    Predicate predicate = spec.toPredicate(root, query, builder);

    if (predicate != null) {
      query.where(predicate);
    }

    return root;
  }

  /**
   * Apply lock mode.
   * 
   * @param query
   *            the query
   * @return the typed query
   */
  private TypedQuery<M> applyLockMode(TypedQuery<M> query) {
    return query;
  }

  /**
   * 重写默认的 这样可以走一级/二级缓存.
   * 
   * @param id
   *            the id
   * @return true, if successful
   */
  @Override
  public boolean exists(I id) {
    return findOne(id) != null;
  }

  @Override
  public EntityManager getEntityManager() {
    return this.em;
  }

}
