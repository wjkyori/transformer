package org.transformer.rpc.dto.response;

/**
 * Rpc返回.
 * T 返回实体类型
 */
public final class RpcResponse<T> {

  /**
   * 返回数据.
   */
  private T data;

  /**
   * @return 获取  返回数据.
   */
  public T getData() {
    return data;
  }

  /**
   * @param 设置 请求返回.
   */
  public void setData(T data) {
    this.data = data;
  }

}
