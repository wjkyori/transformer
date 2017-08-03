package org.transformer.support.dao.jpa.repository.callback;

import org.transformer.support.dao.jpa.search.Searchable;

import javax.persistence.Query;

/**
 * The Interface SearchCallback.
 * 
 */
public interface SearchCallback {

  /** The Constant NONE. */
  public static final SearchCallback NONE = new NoneSearchCallback();

  /** The Constant DEFAULT. */
  public static final SearchCallback DEFAULT = new DefaultSearchCallback();

  /**
   * 动态拼HQL where、group by having.
   * 
   * @param ql
   *            the ql
   * @param search
   *            the search
   */
  public void prepareQl(StringBuilder ql, Searchable search);

  /**
   * Prepare order.
   * 
   * @param ql
   *            the ql
   * @param search
   *            the search
   */
  public void prepareOrder(StringBuilder ql, Searchable search);

  /**
   * 根据search给query赋值及设置分页信息.
   * 
   * @param query
   *            the query
   * @param search
   *            the search
   */
  public void setValues(Query query, Searchable search);

  /**
   * Sets the pageable.
   * 
   * @param query
   *            the query
   * @param search
   *            the search
   */
  public void setPageable(Query query, Searchable search);

}
