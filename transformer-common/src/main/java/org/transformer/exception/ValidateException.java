package org.transformer.exception;

/**
 * 数据验证错误.
 */
public class ValidateException extends ServiceException {

  /**
   * 序列号版本号.
   */
  private static final long serialVersionUID = 5735896615624348553L;

  public ValidateException(String errorMsg) {
    super(errorMsg);
  }
}
