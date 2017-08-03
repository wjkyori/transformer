package org.transformer.support.filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * 在view中打开EntityManager.
 */
public class OpenEntityManagerInView extends OpenEntityManagerInViewFilter {

  /** 忽略的请求路径. */
  private String excludePath;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    if (StringUtils.isNotBlank(excludePath)) {
      String[] excludePaths = excludePath.split(",");
      for (String p : excludePaths) {
        if (StringUtils.isNotBlank(p)) {
          if (request.getRequestURI().startsWith(request.getContextPath() + p)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /** 
  * 获取忽略的请求路径 .
  * @return excludePaths 忽略的请求路径 
  */
  public String getExcludePath() {
    return excludePath;
  }

  /** 
  * 设置忽略的请求路径. 
  * @param excludePaths 忽略的请求路径 
  */
  public void setExcludePath(String excludePath) {
    this.excludePath = excludePath;
  }
}
