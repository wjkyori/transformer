package org.transformer.support.context;

import org.transformer.util.IpUtils;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * http请求对象容器.
 * @author wangjk
 */
public final class RequestContext {

  /** The Constant contexts. */
  private static final ThreadLocal<RequestContext> contexts = new ThreadLocal<RequestContext>();

  /** The context. */
  private ServletContext context;

  /** The session. */
  private HttpSession session;

  /** The request. */
  private HttpServletRequest request;

  /** The response. */
  private HttpServletResponse response;

  /** The cookies. */
  private Map<String, Cookie> cookies;
  private String base;

  /**
   * 构造请求对象容器.
   * @param ctx servlet上下文
   * @param req 请求对象
   * @param res 返回对象
   * @return 请求对象容器
   */
  public static RequestContext begin(ServletContext ctx, HttpServletRequest req,
      HttpServletResponse res) {
    RequestContext rc = new RequestContext();
    rc.context = ctx;
    rc.request = req;
    rc.response = res;
    rc.session = req.getSession(false);
    rc.cookies = new HashMap<String, Cookie>();
    Cookie[] cookies = req.getCookies();
    if (cookies != null) {
      for (Cookie ck : cookies) {
        rc.cookies.put(ck.getName(), ck);
      }
    }
    rc.base = req.getScheme() + "://" + req.getServerName()
        + (req.getServerPort() == 80 ? "" : (":" + req.getServerPort()))
        + ("/".equalsIgnoreCase(req.getContextPath()) ? "" : req.getContextPath());
    contexts.set(rc);
    return rc;
  }

  public static RequestContext get() {
    return contexts.get();
  }

  /**
   * 清除容器.
   */
  public void end() {
    this.context = null;
    this.request = null;
    this.response = null;
    this.session = null;
    this.cookies = null;
    this.base = null;
    contexts.remove();
  }

  /** servlet容器. */
  public ServletContext context() {
    return context;
  }

  /** session对象. */
  public HttpSession session() {
    return session;
  }

  /** session对象，没有则创建. */
  public HttpSession session(boolean create) {
    return (session == null && create) ? (session = request.getSession(create)) : session;
  }

  /** request对象. */
  public HttpServletRequest request() {
    return request;
  }

  /** response对象. */
  public HttpServletResponse response() {
    return response;
  }

  /** 请求IP地址. */
  public String requestIp() {
    return IpUtils.getIpAddr(request);
  }

  public String base() {
    return this.base;
  }
}
