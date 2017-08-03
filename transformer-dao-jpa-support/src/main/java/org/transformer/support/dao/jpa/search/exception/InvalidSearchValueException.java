package org.transformer.support.dao.jpa.search.exception;

// TODO: Auto-generated Javadoc
/**
 * The Class InvalidSearchValueException.
 * 
 */
public final class InvalidSearchValueException extends SearchException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6818515241484512312L;

  /**
   * Instantiates a new invalid search value exception.
   * 
   * @param searchProperty
   *            the search property
   * @param entityProperty
   *            the entity property
   * @param value
   *            the value
   */
  public InvalidSearchValueException(String searchProperty, String entityProperty, Object value) {
    this(searchProperty, entityProperty, value, null);
  }

  /**
   * Instantiates a new invalid search value exception.
   * 
   * @param searchProperty
   *            the search property
   * @param entityProperty
   *            the entity property
   * @param value
   *            the value
   * @param cause
   *            the cause
   */
  public InvalidSearchValueException(String searchProperty, String entityProperty, Object value,
      Throwable cause) {
    super("Invalid Search Value, searchProperty [" + searchProperty + "], " + "entityProperty ["
        + entityProperty + "], value [" + value + "]", cause);
  }

}
