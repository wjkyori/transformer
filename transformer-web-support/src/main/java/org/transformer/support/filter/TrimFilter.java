package org.transformer.support.filter;

import org.springframework.web.filter.OncePerRequestFilter;
import org.transformer.support.warpper.HttpServletRequestWrapper;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 去除空格.
 */
public class TrimFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    ServletRequest requestWrapper = request;
    if (!(request instanceof HttpServletRequestWrapper)) {
      //自定义的请求Wrapper,可重复读取request
      requestWrapper = new HttpServletRequestWrapper(request);
    }
    super.doFilter(requestWrapper, response, filterChain);
  }

}
