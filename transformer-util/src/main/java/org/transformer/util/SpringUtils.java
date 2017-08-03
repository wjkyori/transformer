package org.transformer.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * Spring容器工具类.
 * 通过SPRING扫描，自动注册
 * 
 */
@Component
public final class SpringUtils implements BeanFactoryPostProcessor {

  /** The bean factory. */
  private static ConfigurableListableBeanFactory beanFactory; // Spring应用上下文环境

  @Override
  public void postProcessBeanFactory(
      ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    SpringUtils.beanFactory = configurableListableBeanFactory;
  }

  /**
   * 获取对象.
   * 
   * @param <T>
   *            the generic type
   * @param name
   *            the name
   * @return Object 一个以所给名字注册的bean的实例
   * @throws BeansException
   *             the beans exception
   */
  @SuppressWarnings("unchecked")
  public static <T> T getBean(String name) throws BeansException {
    return (T) beanFactory.getBean(name);
  }

  /**
   * 获取类型为requiredType的对象.
   * 
   * @param <T>
   *            the generic type
   * @param clz
   *            the clz
   * @return the bean
   * @throws BeansException
   *             the beans exception
   */
  @SuppressWarnings("cast")
  public static <T> T getBean(Class<T> clz) throws BeansException {
    T result = (T) beanFactory.getBean(clz);
    return result;
  }

  /**
   * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true.
   * 
   * @param name
   *            the name
   * @return boolean
   */
  public static boolean containsBean(String name) {
    return beanFactory.containsBean(name);
  }

  /**
   * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。
   * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）.
   * 
   * @param name
   *            the name
   * @return boolean
   * @throws NoSuchBeanDefinitionException
   *             the no such bean definition exception
   */
  public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
    return beanFactory.isSingleton(name);
  }

  /**
   * Gets the type.
   * 
   * @param name
   *            the name
   * @return Class 注册对象的类型
   * @throws NoSuchBeanDefinitionException
   *             the no such bean definition exception
   */
  public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
    return beanFactory.getType(name);
  }

  /**
   * 如果给定的bean名字在bean定义中有别名，则返回这些别名.
   * 
   * @param name
   *            the name
   * @return the aliases
   * @throws NoSuchBeanDefinitionException
   *             the no such bean definition exception
   */
  public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
    return beanFactory.getAliases(name);
  }

  /**
   * Gets the bean factory.
   * 
   * @return the bean factory
   */
  public static ConfigurableListableBeanFactory getBeanFactory() {
    return beanFactory;
  }

}
