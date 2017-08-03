package org.transformer.support.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 控制器基类.
 */
public class Controller {

  protected Logger logger = LoggerFactory.getLogger(getClass());

  /** The view prefix. */
  private String viewPrefix;

  protected Controller() {
    setViewPrefix(defaultViewPrefix());
  }

  /**
   * 当前模块 视图的前缀 默认 1、获取当前类头上的@RequestMapping中的value作为前缀 2、如果没有就使用当前模型小写的简单类名.
   * 
   * @param viewPrefix
   *            the new view prefix
   */
  public void setViewPrefix(String viewPrefix) {
    if (viewPrefix.startsWith("/")) {
      viewPrefix = viewPrefix.substring(1);
    }
    this.viewPrefix = viewPrefix;
  }

  /**
   * Gets the view prefix.
   * 
   * @return the view prefix
   */
  public String getViewPrefix() {
    return viewPrefix;
  }

  /**
   * 获取视图名称：即prefixViewName + "/" + suffixName.
   * 
   * @param suffixName
   *            the suffix name
   * @return the string
   */
  public String viewName(String suffixName) {
    if (!suffixName.startsWith("/")) {
      suffixName = "/" + suffixName;
    }
    return defaultViewPrefix() + suffixName;
  }

  /**
   * Redirect to url.
   * 
   * @param backUrl
   *            null 将重定向到默认getViewPrefix()
   * @return the string
   */
  protected String redirectToUrl(String backUrl) {
    if (StringUtils.isEmpty(backUrl)) {
      backUrl = getViewPrefix();
    }
    if (!backUrl.startsWith("/") && !backUrl.startsWith("http")) {
      backUrl = "/" + backUrl;
    }
    return "redirect:" + backUrl;
  }

  /**
   * Default view prefix.
   * 
   * @return the string
   */
  protected String defaultViewPrefix() {
    String currentViewPrefix = "";
    RequestMapping requestMapping = AnnotationUtils.findAnnotation(getClass(),
        RequestMapping.class);
    if (requestMapping != null && requestMapping.value().length > 0) {
      currentViewPrefix = requestMapping.value()[0];
    }
    return currentViewPrefix;
  }

}
