package org.transformer.support.dao.jpa.search.exception;

import org.springframework.core.NestedRuntimeException;

// TODO: Auto-generated Javadoc
/**
 * The Class SearchException.
 * 
 */
public class SearchException extends NestedRuntimeException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2738766356657081174L;

  /**
   * Instantiates a new search exception.
   * 
   * @param msg
   *            the msg
   */
  public SearchException(String msg) {
    super(msg);
  }

  /**
   * Instantiates a new search exception.
   * 
   * @param msg
   *            the msg
   * @param cause
   *            the cause
   */
  public SearchException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
