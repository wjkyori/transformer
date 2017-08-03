package org.transformer.support.dao.jpa.repository.callback;

import org.transformer.support.dao.jpa.search.Searchable;

import javax.persistence.Query;

/**
 * The Class NoneSearchCallback.
 */
public final class NoneSearchCallback implements SearchCallback {

  @Override
  public void prepareQl(StringBuilder ql, Searchable search) {
  }

  @Override
  public void prepareOrder(StringBuilder ql, Searchable search) {
  }

  @Override
  public void setValues(Query query, Searchable search) {
  }

  @Override
  public void setPageable(Query query, Searchable search) {
  }
}
