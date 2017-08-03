package org.transformer.support.dao.jpa.search.filter;

import com.google.common.collect.Lists;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * or 条件.
 * 
 */
public class OrCondition implements SearchFilter {

  /** The or filters. */
  private List<SearchFilter> orFilters = Lists.newArrayList();

  /**
   * Instantiates a new or condition.
   */
  OrCondition() {
  }

  /**
   * Adds the.
   * 
   * @param filter
   *            the filter
   * @return the or condition
   */
  public OrCondition add(SearchFilter filter) {
    this.orFilters.add(filter);
    return this;
  }

  /**
   * Gets the or filters.
   * 
   * @return the or filters
   */
  public List<SearchFilter> getOrFilters() {
    return orFilters;
  }

  @Override
  public String toString() {
    return "OrCondition{" + orFilters + '}';
  }
}
