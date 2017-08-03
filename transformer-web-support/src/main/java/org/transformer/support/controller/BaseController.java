package org.transformer.support.controller;

import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.transformer.support.controller.annotation.ServiceToController;
import org.transformer.support.entity.AbstractEntity;
import org.transformer.support.service.BaseService;
import org.transformer.util.Reflections;
import org.transformer.util.SpringUtils;

/**
 * 抽象DAO层基类 提供一些简便方法<br/>
 * 想要使用该接口需要在spring配置文件的jpa:repositories中添加
 * factory-class="BaseRepositoryFactoryBean"
 * 泛型 ： M 表示实体类型；S表示SERVICE类型 .
 * 
 * @param <M> 实体类型            
 * @param <S> SERVICE类型            
 */
public abstract class BaseController<M extends AbstractEntity<Long>, S extends BaseService<M>>
    extends Controller {

  /** 服务类. */
  protected final S service;

  /** 实体类型. */
  protected final Class<M> entityClass;

  /** 服务类型. */
  protected final Class<S> serviceClass;

  /**
   * Instantiates a new base controller.
   */
  protected BaseController() {
    super();
    this.entityClass = Reflections.findParameterizedType(getClass(), 0);
    this.serviceClass = Reflections.findParameterizedType(getClass(), 1);
    if (this.getClass().isAnnotationPresent(ServiceToController.class)) {
      ServiceToController serviceToController = this.getClass()
          .getAnnotation(ServiceToController.class);
      this.service = SpringUtils.getBean(serviceToController.name());
    } else {
      this.service = SpringUtils.getBean(serviceClass);
    }
  }

  /**
   * New model.
   * 
   * @return the m
   */
  protected M newModel() {
    try {
      return entityClass.newInstance();
    } catch (Exception e) {
      throw new IllegalStateException("can not instantiated model : " + this.entityClass, e);
    }
  }

  /**
   * 共享的验证规则 验证失败返回true.
   * 
   * @param entity
   *            the m
   * @param result
   *            the result
   * @return true, if successful
   */
  protected boolean hasError(M entity, BindingResult result) {
    Assert.notNull(entity, "对象不能为空");
    return result.hasErrors();
  }

  /**
   * Gets the model.
   * 
   * @param id
   *            the id
   * @return the model
   */
  @ModelAttribute
  public M getModel(@RequestParam(required = false) Long id) {
    M entity = null;
    if (id != null) {
      entity = toModel(id);
    }
    if (entity == null) {
      entity = newModel();
    }
    return entity;
  }

  /**
   * To model.
   * 
   * @param id
   *            the id
   * @return the m
   */
  protected M toModel(Long id) {
    return this.service.findOneAndEvict(id);
  }

  /** 
  * 获取服务类. 
  * @return service 服务类 
  */
  public BaseService<M> getService() {
    return service;
  }
}
