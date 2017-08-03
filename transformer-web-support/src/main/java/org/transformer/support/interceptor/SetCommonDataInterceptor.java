package org.transformer.support.interceptor;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.transformer.support.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 设置通用数据的Interceptor 1、ctx---->request.contextPath 2、currentURL---->当前地址
 */
public class SetCommonDataInterceptor extends HandlerInterceptorAdapter {

  /** The path matcher. */
  private final PathMatcher pathMatcher = new AntPathMatcher();

  /** The Constant DEFAULT_EXCLUDE_PARAMETER_PATTERN. */
  private static final String[] DEFAULT_EXCLUDE_PARAMETER_PATTERN = new String[] {
      "\\&\\w*page.pn=\\d+", "\\?\\w*page.pn=\\d+", "\\&\\w*page.size=\\d+" };

  /** The exclude parameter patterns. */
  private String[] excludeParameterPatterns = DEFAULT_EXCLUDE_PARAMETER_PATTERN;

  /** The exclude url patterns. */
  private String[] excludeUrlPatterns = null;

  /**
   * Sets the exclude parameter patterns.
   * 
   * @param excludeParameterPatterns
   *            the new exclude parameter patterns
   */
  public void setExcludeParameterPatterns(String[] excludeParameterPatterns) {
    this.excludeParameterPatterns = excludeParameterPatterns;
  }

  /**
   * Sets the exclude url patterns.
   * 
   * @param excludeUrlPatterns
   *            the new exclude url patterns
   */
  public void setExcludeUrlPatterns(final String[] excludeUrlPatterns) {
    this.excludeUrlPatterns = excludeUrlPatterns;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    if (isExclude(request)) {
      return true;
    }

    if (request.getAttribute(Constants.CONTEXT_PATH) == null) {
      request.setAttribute(Constants.CONTEXT_PATH, getBasePath(request));
    }
    if (request.getAttribute(Constants.CURRENT_URL) == null) {
      request.setAttribute(Constants.CURRENT_URL, extractCurrentUrl(request, true));
    }
    if (request.getAttribute(Constants.BACK_URL) == null) {
      request.setAttribute(Constants.BACK_URL, extractBackUrl(request));
    }

    return true;
  }

  /**
   * Checks if is exclude.
   * 
   * @param request
   *            the request
   * @return true, if is exclude
   */
  private boolean isExclude(final HttpServletRequest request) {
    if (excludeUrlPatterns == null) {
      return false;
    }
    for (String pattern : excludeUrlPatterns) {
      if (pathMatcher.match(pattern, request.getServletPath())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Extract current url.
   * 
   * @param request
   *            the request
   * @param needQueryString
   *            the need query string
   * @return the string
   */
  private String extractCurrentUrl(HttpServletRequest request, boolean needQueryString) {
    String url = request.getRequestURI();
    String queryString = request.getQueryString();
    if (!StringUtils.isEmpty(queryString)) {
      queryString = "?" + queryString;
      for (String pattern : excludeParameterPatterns) {
        queryString = queryString.replaceAll(pattern, "");
      }
      if (queryString.startsWith("&")) {
        queryString = "?" + queryString.substring(1);
      }
    }
    if (!StringUtils.isEmpty(queryString) && needQueryString) {
      url = url + queryString;
    }
    return getBasePath(request) + url;
  }

  /**
   * 上一次请求的地址 1、先从request.parameter中查找BackURL 2、获取header中的 referer
   * 
   * @param request
   *            the request
   * @return the string
   */
  private static String extractBackUrl(HttpServletRequest request) {
    String url = request.getParameter(Constants.BACK_URL);

    // 使用Filter时 文件上传时 getParameter时为null 所以改成Interceptor

    if (StringUtils.isEmpty(url)) {
      url = request.getHeader("Referer");
    }

    if (!StringUtils.isEmpty(url) && (url.startsWith("http://") || url.startsWith("https://"))) {
      return url;
    }

    if (!StringUtils.isEmpty(url) && url.startsWith(request.getContextPath())) {
      url = getBasePath(request) + url;
    }
    return url;
  }

  /**
   * Gets the base path.
   * 
   * @param req
   *            the req
   * @return the base path
   */
  private static String getBasePath(HttpServletRequest req) {
    StringBuffer baseUrl = new StringBuffer();
    String scheme = req.getScheme();
    baseUrl.append(scheme); // http, https
    baseUrl.append("://");
    baseUrl.append(req.getServerName());
    //端口号
    int port = req.getServerPort();
    if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
      baseUrl.append(':');
      baseUrl.append(req.getServerPort());
    }
    return baseUrl.toString() + req.getContextPath();
  }

}
