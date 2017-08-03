package org.transformer.support.filter;

import org.springframework.web.filter.OncePerRequestFilter;
import org.transformer.support.context.RequestContext;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

public class RequestContextFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    HttpServletRequest requestWrapper = request;
    if (!(request instanceof HttpServletRequestWrapper)) {
      //自定义的请求Wrapper,可重复读取request
      requestWrapper = new HttpServletRequestWrapper(request);
    }
    RequestContext rc = RequestContext.begin(getServletContext(), requestWrapper, response);
    try {
      filterChain.doFilter(requestWrapper, response);
    } finally {
      if (rc != null) {
        rc.end();
      }
    }
  }

}
