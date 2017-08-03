package org.transformer.support.web.search;

import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.transformer.support.dao.jpa.search.PageableDefaults;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 请求分页数据绑定到Pageable，支持请求参数和uri template数据的绑定.
 * 使用指南：
 * 1.1、简单的分页请求参数格式如下：
 * page.size=10  分页大小
 * page.pn=1    页码 从1开始
 * 1.2、控制器处理方法写法
 * public void test(Pageable page);
 * 2.1、带排序的分页请求参数格式如下：
 * page.size=10  分页大小
 * page.pn=1    页码 从1开始
 * sort.a.b=desc
 * sort.c=asc
 * 默认按照排序关键词的字典顺序排（因为Map存储是无序） 如果想有序排 可以在sort之后跟一个顺序号
 * sort2.a.b=desc
 * sort1.c=asc
 * 2.2、控制器处理方法写法
 * public void test(Pageable page);
 * 3.1、带前缀的 排序分页请求参数格式如下：
 * test_page.size=10  分页大小
 * test_page.pn=1    页码 从1开始
 * test_sort.a.b=desc
 * test_sort.c=asc
 * foo_page.size=10  分页大小
 * foo_page.pn=1    页码 从1开始
 * foo_sort.a.b=desc
 * foo_sort.c=asc
 * 排序默认按照请求时顺序排
 * 3.2、控制器处理方法写法
 * public void test(@Qualifier("test") Pageable page1, @Qualifier("test") Pageable page2);
 * 错误的用法，如果有多个请使用@Qualifier指定前缀
 * public void fail(Pageable page1, Pageable page2);
 * 
 */
public class PageableMethodArgumentResolver extends BaseMethodArgumentResolver {

  /** 日志. */
  private static Logger logger = LoggerFactory.getLogger(PageableMethodArgumentResolver.class);

  /** The Constant DEFAULT_PAGE_REQUEST. */
  private static final Pageable DEFAULT_PAGE_REQUEST = new PageRequest(0, 10);

  /** The Constant DEFAULT_PAGE_PREFIX. */
  private static final String DEFAULT_PAGE_PREFIX = "pager";

  /** The Constant DEFAULT_SORT_PREFIX. */
  private static final String DEFAULT_SORT_PREFIX = "sorter";

  /** The fallback pagable. */
  private Pageable fallbackPagable = DEFAULT_PAGE_REQUEST;

  /** The page prefix. */
  private String pagePrefix = DEFAULT_PAGE_PREFIX;

  /** The sort prefix. */
  private String sortPrefix = DEFAULT_SORT_PREFIX;

  /** The min page size. */
  private int minPageSize = 5;

  /** The max page size. */
  private int maxPageSize = 100;

  /**
   * 设置最小分页大小 默认10.
   * 
   * @param minPageSize
   *            the new min page size
   */
  public void setMinPageSize(int minPageSize) {
    this.minPageSize = minPageSize;
  }

  /**
   * 设置最大分页大小 默认100.
   * 
   * @param maxPageSize
   *            the new max page size
   */
  public void setMaxPageSize(int maxPageSize) {
    this.maxPageSize = maxPageSize;
  }

  /**
   * Setter to configure a fallback instance of {@link Pageable} that is being
   * used to back missing parameters. Defaults to
   * 
   * @param fallbackPagable
   *            the fallbackPagable to set {@value #DEFAULT_PAGE_REQUEST}.
   */
  public void setFallbackPagable(Pageable fallbackPagable) {
    this.fallbackPagable = null == fallbackPagable ? DEFAULT_PAGE_REQUEST : fallbackPagable;
  }

  /**
   * Setter to configure the prefix of request parameters to be used to
   * retrieve paging information. Defaults to {@link #DEFAULT_PAGE_PREFIX}.
   * 
   * @param pagePrefix
   *            the prefix to set
   */
  public void setPagePrefix(String pagePrefix) {
    this.pagePrefix = null == pagePrefix ? DEFAULT_PAGE_PREFIX : pagePrefix;
  }

  /**
   * Sets the sort prefix.
   * 
   * @param sortPrefix
   *            the new sort prefix
   */
  public void setSortPrefix(String sortPrefix) {
    this.sortPrefix = null == sortPrefix ? DEFAULT_SORT_PREFIX : sortPrefix;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return Pageable.class.isAssignableFrom(parameter.getParameterType());
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

    PageableDefaults pageableDefaults = getPageableDefaults(parameter);
    // 默认的page request
    Pageable defaultPageRequest = getDefaultFromAnnotationOrFallback(pageableDefaults);

    String pageableNamePrefix = getPagePrefix(parameter);
    String sortNamePrefix = getSortPrefix(parameter);
    Map<String, String[]> pageableMap = getPrefixParameterMap(pageableNamePrefix, webRequest, true);
    Map<String, String[]> sortMap = getPrefixParameterMap(sortNamePrefix, webRequest, false);

    Sort sort = getSort(sortNamePrefix, sortMap, defaultPageRequest, webRequest);
    if (pageableMap.size() == 0) {
      return new PageRequest(defaultPageRequest.getPageNumber(), defaultPageRequest.getPageSize(),
          sort == null ? defaultPageRequest.getSort() : sort);
    }

    int pn = getPn(pageableMap, defaultPageRequest);
    int pageSize = getPageSize(pageableMap, defaultPageRequest);

    return new PageRequest(pn - 1, pageSize, sort);

  }

  /**
   * Gets the sort.
   * 
   * @param sortNamePrefix
   *            the sort name prefix
   * @param sortMap
   *            the sort map
   * @param defaultPageRequest
   *            the default page request
   * @param webRequest
   *            the web request
   * @return the sort
   */
  @SuppressWarnings("synthetic-access")
  private static Sort getSort(String sortNamePrefix, Map<String, String[]> sortMap,
      Pageable defaultPageRequest, NativeWebRequest webRequest) {
    Sort sort = null;
    List<OrderedSort> orderedSortList = Lists.newArrayList();
    for (String name : sortMap.keySet()) {

      // sort1.abc
      int propertyIndex = name.indexOf(".") + 1;

      int order = 0;
      String orderStr = name.substring(sortNamePrefix.length(), propertyIndex - 1);
      try {
        if (!StringUtils.isEmpty(orderStr)) {
          order = Integer.valueOf(orderStr);
        }
      } catch (Exception e) {
        logger.error("获取排序出错", e);
      }

      String property = name.substring(propertyIndex);
      assertSortProperty(property);
      Sort.Direction direction = Sort.Direction.fromString(sortMap.get(name)[0]);

      orderedSortList.add(new OrderedSort(property, direction, order));
    }

    Collections.sort(orderedSortList);
    for (OrderedSort orderedSort : orderedSortList) {
      Sort newSort = new Sort(orderedSort.direction, orderedSort.property);
      if (sort == null) {
        sort = newSort;
      } else {
        sort = sort.and(newSort);
      }
    }

    if (sort == null) {
      return defaultPageRequest.getSort();
    }

    return sort;
  }

  /**
   * 防止sql注入，排序字符串只能包含字符 数字 下划线 点 ` ".
   * 
   * @param property
   *            the property
   */
  private static void assertSortProperty(String property) {
    if (!property.matches("[a-zA-Z0-9_、.`\"]*")) {
      throw new IllegalStateException("Sort property error, only contains [a-zA-Z0-9_.`\"]");
    }
  }

  /**
   * Gets the page size.
   * 
   * @param pageableMap
   *            the pageable map
   * @param defaultPageRequest
   *            the default page request
   * @return the page size
   */
  private int getPageSize(Map<String, String[]> pageableMap, Pageable defaultPageRequest) {
    int pageSize = 0;
    try {
      String pageSizeStr = pageableMap.get("size")[0];
      if (pageSizeStr != null) {
        pageSize = Integer.valueOf(pageSizeStr);
      } else {
        pageSize = defaultPageRequest.getPageSize();
      }
    } catch (Exception exception) {
      logger.error("获取总页数出错", exception);
      pageSize = defaultPageRequest.getPageSize();
    }

    if (pageSize < minPageSize) {
      pageSize = minPageSize;
    }

    if (pageSize > maxPageSize) {
      pageSize = maxPageSize;
    }
    return pageSize;
  }

  /**
   * Gets the 页码.
   * 
   * @param pageableMap
   *            the pageable map
   * @param defaultPageRequest
   *            the default page request
   * @return the pn
   */
  private static int getPn(Map<String, String[]> pageableMap, Pageable defaultPageRequest) {
    int pn = 1;
    try {
      String pnStr = pageableMap.get("pn")[0];
      if (pnStr != null) {
        pn = Integer.valueOf(pnStr);
      } else {
        pn = defaultPageRequest.getPageNumber();
      }
    } catch (Exception exception) {
      logger.error("获取页码出错", exception);
      pn = defaultPageRequest.getPageNumber();
    }

    if (pn < 1) {
      pn = 1;
    }

    return pn;
  }

  /**
   * Resolves the prefix to use to bind properties from. Will prepend a
   * possible {@link Qualifier} if available or return the configured prefix
   * otherwise.
   * 
   * @param parameter
   *            the parameter
   * @return the page prefix
   */
  private String getPagePrefix(MethodParameter parameter) {

    Qualifier qualifier = parameter.getParameterAnnotation(Qualifier.class);

    if (qualifier != null) {
      return new StringBuilder(qualifier.value()).append("_").append(pagePrefix).toString();
    }

    return pagePrefix;
  }

  /**
   * Gets the sort prefix.
   * 
   * @param parameter
   *            the parameter
   * @return the sort prefix
   */
  private String getSortPrefix(MethodParameter parameter) {

    Qualifier qualifier = parameter.getParameterAnnotation(Qualifier.class);

    if (qualifier != null) {
      return new StringBuilder(qualifier.value()).append("_").append(sortPrefix).toString();
    }

    return sortPrefix;
  }

  /**
   * Gets the default from annotation or fallback.
   * 
   * @param pageableDefaults
   *            the pageable defaults
   * @return the default from annotation or fallback
   */
  private Pageable getDefaultFromAnnotationOrFallback(PageableDefaults pageableDefaults) {

    Pageable defaultPageable = defaultPageable(pageableDefaults);
    if (defaultPageable != null) {
      return defaultPageable;
    }

    return fallbackPagable;
  }

  /**
   * Gets the pageable defaults.
   * 
   * @param parameter
   *            the parameter
   * @return the pageable defaults
   */
  private static PageableDefaults getPageableDefaults(MethodParameter parameter) {
    // 首先从参数上找
    PageableDefaults pageableDefaults = parameter.getParameterAnnotation(PageableDefaults.class);
    // 找不到从方法上找
    if (pageableDefaults == null) {
      pageableDefaults = parameter.getMethodAnnotation(PageableDefaults.class);
    }
    return pageableDefaults;
  }

  /**
   * Default pageable.
   * 
   * @param pageableDefaults
   *            the pageable defaults
   * @return the pageable
   */
  private static Pageable defaultPageable(PageableDefaults pageableDefaults) {

    if (pageableDefaults == null) {
      return null;
    }

    int pageNumber = pageableDefaults.pageNumber();
    int pageSize = pageableDefaults.value();

    String[] sortStrArray = pageableDefaults.sort();
    Sort sort = null;

    for (String sortStr : sortStrArray) {
      String[] sortStrPair = sortStr.split("=");
      Sort newSort = new Sort(Sort.Direction.fromString(sortStrPair[1]), sortStrPair[0]);
      if (sort == null) {
        sort = newSort;
      } else {
        sort = sort.and(newSort);
      }
    }
    return new PageRequest(pageNumber, pageSize, sort);
  }

  /**
   * Asserts uniqueness of all {@link Pageable} parameters of the method of
   * the given {@link MethodParameter}.
   * 
   * @param parameter
   *            the parameter
   */
  @SuppressWarnings("unused")
  private static void assertPageableUniqueness(MethodParameter parameter) {

    Method method = parameter.getMethod();

    if (containsMoreThanOnePageableParameter(method)) {
      Annotation[][] annotations = method.getParameterAnnotations();
      assertQualifiersFor(method.getParameterTypes(), annotations);
    }
  }

  /**
   * Returns whether the given {@link Method} has more than one.
   * 
   * @param method
   *            the method
   * @return true, if successful {@link Pageable} parameter.
   */
  private static boolean containsMoreThanOnePageableParameter(Method method) {

    boolean pageableFound = false;

    for (Class<?> type : method.getParameterTypes()) {

      if (pageableFound && type.equals(Pageable.class)) {
        return true;
      }

      if (type.equals(Pageable.class)) {
        pageableFound = true;
      }
    }

    return false;
  }

  /**
   * Asserts that every {@link Pageable} parameter of the given parameters
   * carries an {@link org.springframework.beans.factory.annotation.Qualifier}
   * annotation to distinguish them from each other.
   * 
   * @param parameterTypes
   *            the parameter types
   * @param annotations
   *            the annotations
   */
  private static void assertQualifiersFor(Class<?>[] parameterTypes, Annotation[][] annotations) {

    Set<String> values = new HashSet<String>();

    for (int i = 0; i < annotations.length; i++) {

      if (Pageable.class.equals(parameterTypes[i])) {

        Qualifier qualifier = findAnnotation(annotations[i]);

        if (null == qualifier) {
          throw new IllegalStateException(
              "Ambiguous Pageable arguments in handler method. If you use multiple "
                  + "parameters of type Pageable you need to qualify them with @Qualifier");
        }

        if (values.contains(qualifier.value())) {
          throw new IllegalStateException("Values of the user Qualifiers must be unique!");
        }

        values.add(qualifier.value());
      }
    }
  }

  /**
   * Returns a {@link Qualifier} annotation from the given array of.
   * 
   * @param annotations
   *            the annotations
   * @return the qualifier {@link Annotation}s. Returns {@literal null} if the
   *         array does not contain a {@link Qualifier} annotation.
   */
  private static Qualifier findAnnotation(Annotation[] annotations) {

    for (Annotation annotation : annotations) {
      if (annotation instanceof Qualifier) {
        return (Qualifier) annotation;
      }
    }

    return null;
  }

  /**
   * The Class OrderedSort.
   * 
   * @author wuwei 2014-5-4
   */
  static class OrderedSort implements Comparable<OrderedSort> {

    /** The property. */
    private String property;

    /** The direction. */
    private Sort.Direction direction;

    /** The order. */
    private int order = 0; // 默认0 即无序

    /**
     * Instantiates a new ordered sort.
     * 
     * @param property
     *            the property
     * @param direction
     *            the direction
     * @param order
     *            the order
     */
    OrderedSort(String property, Sort.Direction direction, int order) {
      this.property = property;
      this.direction = direction;
      this.order = order;
    }

    @Override
    public int compareTo(OrderedSort orderedSort) {
      if (orderedSort == null) {
        return -1;
      }
      if (this.order > orderedSort.order) {
        return 1;
      } else if (this.order < orderedSort.order) {
        return -1;
      } else {
        return 0;
      }
    }
  }

}
