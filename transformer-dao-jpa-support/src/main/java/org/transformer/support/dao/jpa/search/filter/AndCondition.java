package org.transformer.support.dao.jpa.search.filter;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * and 条件.
 * 
 */
public class AndCondition implements SearchFilter {

  /** The and filters. */
  private List<SearchFilter> andFilters = Lists.newArrayList();

  /**
   * Instantiates a new and condition.
   */
  AndCondition() {
  }

  /**
   * Adds the.
   * 
   * @param filter
   *            the filter
   * @return the and condition
   */
  public AndCondition add(SearchFilter filter) {
    this.andFilters.add(filter);
    return this;
  }

  /**
   * Gets the and filters.
   * 
   * @return the and filters
   */
  public List<SearchFilter> getAndFilters() {
    return andFilters;
  }

  @Override
  public String toString() {
    return "AndCondition{" + andFilters + '}';
  }
}
