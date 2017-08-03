package org.transformer.support.filter;

import org.springframework.web.filter.OncePerRequestFilter;
import org.transformer.support.warpper.HttpServletRequestWrapper;
import org.transformer.support.warpper.HttpServletResponseWrapper;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 封装外部调用的输入和输出.
 * 创建日期 2015年2月25日
 */
public class InboundServiceFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    HttpServletRequest requestWrapper = request;
    if (!(request instanceof HttpServletRequestWrapper)) {
      //自定义的请求Wrapper,可重复读取request
      requestWrapper = new HttpServletRequestWrapper(request);
    }
    //自定义的响应Wrapper,可修改response
    HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(response);
    super.doFilter(requestWrapper, responseWrapper, filterChain);
    byte[] data = responseWrapper.getResponseData();
    response.setContentLength(data.length);
    try (ServletOutputStream output = response.getOutputStream();) {
      output.write(data);
      output.flush();
      output.close();
    } catch (Exception e) {
      throw e;
    }
  }
}
