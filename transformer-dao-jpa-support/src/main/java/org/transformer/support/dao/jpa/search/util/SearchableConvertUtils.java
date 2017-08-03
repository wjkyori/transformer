package org.transformer.support.dao.jpa.search.util;

import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.CollectionUtils;
import org.transformer.support.dao.jpa.search.SearchOperator;
import org.transformer.support.dao.jpa.search.Searchable;
import org.transformer.support.dao.jpa.search.exception.InvalidSearchPropertyException;
import org.transformer.support.dao.jpa.search.exception.InvalidSearchValueException;
import org.transformer.support.dao.jpa.search.exception.SearchException;
import org.transformer.support.dao.jpa.search.filter.AndCondition;
import org.transformer.support.dao.jpa.search.filter.Condition;
import org.transformer.support.dao.jpa.search.filter.OrCondition;
import org.transformer.support.dao.jpa.search.filter.SearchFilter;
import org.transformer.util.SpringUtils;

import java.util.Collection;
import java.util.List;

/**
 * The Class SearchableConvertUtils.
 * 
 */
public final class SearchableConvertUtils {

  /** 日志. */
  private static Logger logger = LoggerFactory.getLogger(SearchableConvertUtils.class);

  /** The conversion service. */
  private static volatile ConversionService conversionService;

  /**
   * 设置用于类型转换的conversionService 把如下代码放入spring配置文件即可 <bean class=
   * "org.springframework.beans.factory.config.MethodInvokingFactoryBean">
   * <property name="staticMethod"
   * value=SearchableConvertUtils.setConversionService" /> <property
   * name="arguments" ref="conversionService"/> </bean>
   * 
   * @param conversionService
   *            the new conversion service
   */
  public static void setConversionService(ConversionService conversionService) {
    SearchableConvertUtils.conversionService = conversionService;
  }

  /**
   * Gets the conversion service.
   * 
   * @return the conversion service
   */
  public static ConversionService getConversionService() {
    if (conversionService == null) {
      synchronized (SearchableConvertUtils.class) {
        if (conversionService == null) {
          try {
            conversionService = SpringUtils.getBean(ConversionService.class);
          } catch (Exception exception) {
            logger.error("get bean error", exception);
            throw new SearchException(
                "conversionService is null, " + "search param convert must use conversionService. "
                    + "please see [com.sishuok.es.common.entity.search.utils."
                    + "SearchableConvertUtils#setConversionService]");
          }
        }
      }
    }
    return conversionService;
  }

  /**
   * Convert search value to entity value.
   * 
   * @param <T>
   *            the generic type
   * @param search
   *            查询条件
   * @param entityClass
   *            实体类型
   */
  public static <T> void convertSearchValueToEntityValue(final Searchable search,
      final Class<T> entityClass) {

    if (search.isConverted()) {
      return;
    }

    Collection<SearchFilter> searchFilters = search.getSearchFilters();
    BeanWrapperImpl beanWrapper = new BeanWrapperImpl(entityClass);
    beanWrapper.setAutoGrowNestedPaths(true);
    beanWrapper.setConversionService(getConversionService());

    for (SearchFilter searchFilter : searchFilters) {
      convertSearchValueToEntityValue(beanWrapper, searchFilter);

    }
  }

  /**
   * Convert search value to entity value.
   * 
   * @param beanWrapper
   *            the bean wrapper
   * @param searchFilter
   *            the search filter
   */
  private static void convertSearchValueToEntityValue(BeanWrapperImpl beanWrapper,
      SearchFilter searchFilter) {
    if (searchFilter instanceof Condition) {
      Condition condition = (Condition) searchFilter;
      convert(beanWrapper, condition);
      return;
    }

    if (searchFilter instanceof OrCondition) {
      for (SearchFilter orFilter : ((OrCondition) searchFilter).getOrFilters()) {
        convertSearchValueToEntityValue(beanWrapper, orFilter);
      }
      return;
    }

    if (searchFilter instanceof AndCondition) {
      for (SearchFilter andFilter : ((AndCondition) searchFilter).getAndFilters()) {
        convertSearchValueToEntityValue(beanWrapper, andFilter);
      }
      return;
    }

  }

  /**
   * Convert.
   * 
   * @param beanWrapper
   *            the bean wrapper
   * @param condition
   *            the condition
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static void convert(BeanWrapperImpl beanWrapper, Condition condition) {
    String searchProperty = condition.getSearchProperty();

    // 自定义的也不转换
    if (condition.getOperator() == SearchOperator.custom) {
      return;
    }

    //自定义的ql语句也不转换
    if (condition.getOperator() == SearchOperator.customQl) {
      return;
    }

    // 一元运算符不需要计算
    if (condition.isUnaryFilter()) {
      return;
    }

    String entityProperty = condition.getEntityProperty();

    Object value = condition.getValue();

    Object newValue = null;
    boolean isCollection = value instanceof Collection;
    boolean isArray = value != null && value.getClass().isArray();
    if (isCollection || isArray) {
      List<Object> list = Lists.newArrayList();
      if (isCollection) {
        list.addAll((Collection) value);
      } else {
        list = Lists.newArrayList(CollectionUtils.arrayToList(value));
      }
      int length = list.size();
      for (int i = 0; i < length; i++) {
        list.set(i, getConvertedValue(beanWrapper, searchProperty, entityProperty, list.get(i)));
      }
      newValue = list;
    } else {
      newValue = getConvertedValue(beanWrapper, searchProperty, entityProperty, value);
    }
    condition.setValue(newValue);
  }

  /**
   * Gets the converted value.
   * 
   * @param beanWrapper
   *            the bean wrapper
   * @param searchProperty
   *            the search property
   * @param entityProperty
   *            the entity property
   * @param value
   *            the value
   * @return the converted value
   */
  private static Object getConvertedValue(final BeanWrapperImpl beanWrapper,
      final String searchProperty, final String entityProperty, final Object value) {

    Object newValue;
    try {

      beanWrapper.setPropertyValue(entityProperty, value);
      newValue = beanWrapper.getPropertyValue(entityProperty);
    } catch (InvalidPropertyException e) {
      throw new InvalidSearchPropertyException(searchProperty, entityProperty, e);
    } catch (Exception e) {
      throw new InvalidSearchValueException(searchProperty, entityProperty, value, e);
    }

    return newValue;
  }

}
