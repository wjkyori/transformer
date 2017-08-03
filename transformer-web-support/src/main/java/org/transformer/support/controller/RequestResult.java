package org.transformer.support.controller;

/**
 * 请求结果.
 *
 */
public class RequestResult<T> {

  /** 提示信息. */
  private String msg;

  /** 返回码. */
  private String code;

  /**返回数据. */
  private T data;

  /**
   * @return 获取  提示信息.
   */
  public String getMsg() {
    return msg;
  }

  /**
   * @param 设置 提示信息.
   */
  public void setMsg(String msg) {
    this.msg = msg;
  }

  /**
   * @return 获取  返回码.
   */
  public String getCode() {
    return code;
  }

  /**
   * @param 设置 返回码.
   */
  public void setCode(String code) {
    this.code = code;
  }

  /**
   * @return 获取  返回数据.
   */
  public T getData() {
    return data;
  }

  /**
   * @param 设置 返回数据.
   */
  public void setData(T data) {
    this.data = data;
  }

}
