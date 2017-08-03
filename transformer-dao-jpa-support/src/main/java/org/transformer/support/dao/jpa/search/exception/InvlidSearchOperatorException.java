package org.transformer.support.dao.jpa.search.exception;

import org.transformer.support.dao.jpa.search.SearchOperator;

/**
 * The Class InvlidSearchOperatorException.
 * 
 */
public final class InvlidSearchOperatorException extends SearchException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5994707035931223751L;

  /**
   * Instantiates a new invlid search operator exception.
   * 
   * @param searchProperty
   *            the search property
   * @param operatorStr
   *            the operator str
   */
  public InvlidSearchOperatorException(String searchProperty, String operatorStr) {
    this(searchProperty, operatorStr, null);
  }

  /**
   * Instantiates a new invlid search operator exception.
   * 
   * @param searchProperty
   *            the search property
   * @param operatorStr
   *            the operator str
   * @param cause
   *            the cause
   */
  public InvlidSearchOperatorException(String searchProperty, String operatorStr, Throwable cause) {
    super("Invalid Search Operator searchProperty [" + searchProperty + "], " + "operator ["
        + operatorStr + "], must be one of " + SearchOperator.toStringAllOperator(), cause);
  }
}
