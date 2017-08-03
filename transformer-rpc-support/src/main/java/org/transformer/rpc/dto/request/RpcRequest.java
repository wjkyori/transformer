package org.transformer.rpc.dto.request;

/**
 * Rpc请求.
 * T 请求实体类型
 */
public final class RpcRequest<T> {

  /**
   * 请求数据.
   */
  private T data;

  /**
   * @return 获取  请求数据.
   */
  public T getData() {
    return data;
  }

  /**
   * @param 设置 请求数据.
   */
  public void setData(T data) {
    this.data = data;
  }

}
