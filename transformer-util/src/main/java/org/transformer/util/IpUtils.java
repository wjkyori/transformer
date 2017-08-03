package org.transformer.util;

import javax.servlet.http.HttpServletRequest;

/**
 * IP工具类.
 * 获取顺序：
 * 1.x-forwarded-for
 * 2.Proxy-Client-IP
 * 3.X-Forwarded-For
 * 4.WL-Proxy-Client-IP
 * 5.X-Real-IP
 * 6.remoteAddr
 */
public final class IpUtils {

  /**
   * Instantiates a new ip utils.
   */
  private IpUtils() {
  }

  /**
   * 获取请求真实IP地址.
   * @param request 请求体
   * @return IP地址
   */
  public static String getIpAddr(HttpServletRequest request) {
    if (request == null) {
      return "unknown";
    }
    String ip = request.getHeader("x-forwarded-for");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("X-Forwarded-For");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("X-Real-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }
}
