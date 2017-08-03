package org.transformer.util;

import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

/**
 * Web工具类.
 */
public class WebUtils {

  /**
   * 判断是否同域请求.
   * @param request.
   * @return .
   */
  public static boolean isSameOrigin(HttpServletRequest request) {
    String orgin = request.getHeader(HttpHeaders.ORIGIN);
    if (orgin == null) {
      return true;
    }
    UriComponents actualUrl = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString())
        .build();
    UriComponents originUrl = UriComponentsBuilder
        .fromOriginHeader(request.getHeader(HttpHeaders.ORIGIN)).build();
    return (actualUrl.getHost().equals(originUrl.getHost())
        && getPort(actualUrl) == getPort(originUrl));
  }

  private static int getPort(UriComponents uri) {
    int port = uri.getPort();
    if (port == -1) {
      if ("http".equals(uri.getScheme()) || "ws".equals(uri.getScheme())) {
        port = 80;
      } else if ("https".equals(uri.getScheme()) || "wss".equals(uri.getScheme())) {
        port = 443;
      }
    }
    return port;
  }

}
