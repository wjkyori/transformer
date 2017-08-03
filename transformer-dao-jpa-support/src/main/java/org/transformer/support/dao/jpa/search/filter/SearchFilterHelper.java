package org.transformer.support.dao.jpa.search.filter;

import org.apache.commons.lang3.ArrayUtils;
import org.transformer.support.dao.jpa.search.SearchOperator;
import org.transformer.support.dao.jpa.search.exception.SearchException;

import java.util.Arrays;

/**
 * The Class SearchFilterHelper.
 */
public final class SearchFilterHelper {

  /**
   * 自定义生成Condition.
   * 
   * @param ql
   *            如 paramA - paramB.
   * @param value
   *            the value
   * @return the search filter
   * @throws SearchException the search exception
   */
  public static SearchFilter newCustomQlCondition(final String ql, final Object value)
      throws SearchException {
    return Condition.newCustomQlCondition(ql, value);
  }

  /**
   * 根据查询key和值生成Condition.
   * 
   * @param key
   *            如 name_like
   * @param value
   *            the value
   * @return the search filter
   * @throws SearchException
   *             the search exception
   */
  public static SearchFilter newCondition(final String key, final Object value)
      throws SearchException {
    return Condition.newCondition(key, value);
  }

  /**
   * 根据查询属性、操作符和值生成Condition.
   * 
   * @param searchProperty
   *            the search property
   * @param operator
   *            the operator
   * @param value
   *            the value
   * @return the search filter
   */
  public static SearchFilter newCondition(final String searchProperty,
      final SearchOperator operator, final Object value) {
    return Condition.newCondition(searchProperty, operator, value);
  }

  /**
   * 拼or条件.
   * 
   * @param first
   *            the first
   * @param others
   *            the others
   * @return the search filter
   */
  public static SearchFilter or(SearchFilter first, SearchFilter... others) {
    OrCondition orCondition = new OrCondition();
    orCondition.getOrFilters().add(first);
    if (ArrayUtils.isNotEmpty(others)) {
      orCondition.getOrFilters().addAll(Arrays.asList(others));
    }
    return orCondition;
  }

  /**
   * 拼and条件.
   * 
   * @param first
   *            the first
   * @param others
   *            the others
   * @return the search filter
   */
  public static SearchFilter and(SearchFilter first, SearchFilter... others) {
    AndCondition andCondition = new AndCondition();
    andCondition.getAndFilters().add(first);
    if (ArrayUtils.isNotEmpty(others)) {
      andCondition.getAndFilters().addAll(Arrays.asList(others));
    }
    return andCondition;
  }

}
