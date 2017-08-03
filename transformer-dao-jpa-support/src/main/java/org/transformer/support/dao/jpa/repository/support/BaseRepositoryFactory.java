package org.transformer.support.dao.jpa.repository.support;

import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.util.StringUtils;
import org.transformer.support.dao.jpa.repository.BaseDao;
import org.transformer.support.dao.jpa.repository.callback.SearchCallback;
import org.transformer.support.dao.jpa.repository.support.annotation.SearchableQuery;

import java.io.Serializable;

import javax.persistence.EntityManager;

public class BaseRepositoryFactory<M, I extends Serializable> extends JpaRepositoryFactory {

  private EntityManager entityManager;

  public BaseRepositoryFactory(EntityManager entityManager) {
    super(entityManager);
    this.entityManager = entityManager;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected Object getTargetRepository(RepositoryInformation information) {
    Class<?> repositoryInterface = information.getRepositoryInterface();

    if (isBaseRepository(repositoryInterface)) {

      JpaEntityInformation<M, I> entityInformation = getEntityInformation(
          (Class<M>) information.getDomainType());
      BaseRepository<M, I> repository = new BaseRepository<M, I>(entityInformation, entityManager);

      SearchableQuery searchableQuery = AnnotationUtils.findAnnotation(repositoryInterface,
          SearchableQuery.class);
      if (searchableQuery != null) {
        String countAllQl = searchableQuery.countAllQuery();
        if (!StringUtils.isEmpty(countAllQl)) {
          repository.setCountAllQl(countAllQl);
        }
        String findAllQl = searchableQuery.findAllQuery();
        if (!StringUtils.isEmpty(findAllQl)) {
          repository.setFindAllQl(findAllQl);
        }
        Class<? extends SearchCallback> callbackClass = searchableQuery.callbackClass();
        if (callbackClass != null && callbackClass != SearchCallback.class) {
          repository.setSearchCallback(BeanUtils.instantiate(callbackClass));
        }

        repository.setJoins(searchableQuery.joins());

      }

      return repository;
    }
    return super.getTargetRepository(information);
  }

  @Override
  protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
    if (isBaseRepository(metadata.getRepositoryInterface())) {
      return BaseRepository.class;
    }
    return super.getRepositoryBaseClass(metadata);
  }

  private static boolean isBaseRepository(Class<?> repositoryInterface) {
    return BaseDao.class.isAssignableFrom(repositoryInterface);
  }

}
