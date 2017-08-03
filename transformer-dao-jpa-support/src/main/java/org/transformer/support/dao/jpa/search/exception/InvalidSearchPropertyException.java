package org.transformer.support.dao.jpa.search.exception;

/**
 * The Class InvalidSearchPropertyException.
 */
public final class InvalidSearchPropertyException extends SearchException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7269443994287794296L;

  /**
   * Instantiates a new invalid search property exception.
   * 
   * @param searchProperty
   *            the search property
   * @param entityProperty
   *            the entity property
   */
  public InvalidSearchPropertyException(String searchProperty, String entityProperty) {
    this(searchProperty, entityProperty, null);
  }

  /**
   * Instantiates a new invalid search property exception.
   * 
   * @param searchProperty
   *            the search property
   * @param entityProperty
   *            the entity property
   * @param cause
   *            the cause
   */
  public InvalidSearchPropertyException(String searchProperty, String entityProperty,
      Throwable cause) {
    super(
        "Invalid Search Property [" + searchProperty + "] Entity Property [" + entityProperty + "]",
        cause);
  }

}
