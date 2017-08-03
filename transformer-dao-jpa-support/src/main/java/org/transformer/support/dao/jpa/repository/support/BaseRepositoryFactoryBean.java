package org.transformer.support.dao.jpa.repository.support;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import java.io.Serializable;

import javax.persistence.EntityManager;

/**
 * 基础Repostory简单实现 factory bean 请参考 spring-data-jpa-reference [1.4.2. Adding
 * custom behaviour to all repositories]
 * 
 * @param <R>
 *            the generic type
 * @param <M>
 *            the generic type
 * @param <I>
 *            the generic type
 */
public class BaseRepositoryFactoryBean<R extends JpaRepository<M, I>, M, I extends Serializable>
    extends JpaRepositoryFactoryBean<R, M, I> {

  public BaseRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
    super(repositoryInterface);
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
    return new BaseRepositoryFactory(entityManager);
  }
}
