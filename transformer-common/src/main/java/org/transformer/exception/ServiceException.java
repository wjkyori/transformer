package org.transformer.exception;

/**
 * <b>Description</b>:业务异常.
 * <br/>
 */
public class ServiceException extends AppException {

  private static final long serialVersionUID = -2965806399492478231L;

  public ServiceException() {
    super();
  }

  public ServiceException(String message) {
    super(message);
  }

  public ServiceException(String message, Throwable cause) {
    super(message, cause);
  }

}
