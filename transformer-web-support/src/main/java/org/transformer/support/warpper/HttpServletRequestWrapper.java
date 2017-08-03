package org.transformer.support.warpper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

/**
 * 构造可重复执行的wrapper.
 */
public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {

  /** 日志. */
  private static Logger logger = LoggerFactory.getLogger(HttpServletRequestWrapper.class);

  private byte[] body;

  /**
   * 构造RequestWrapper.
   * @param request 请求
   * @throws IOException IO异常
   */
  public HttpServletRequestWrapper(HttpServletRequest request) throws IOException {
    super(request);
    try (InputStream is = request.getInputStream();
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream()) {
      int ch;
      while ((ch = is.read()) != -1) {
        bytestream.write(ch);
      }
      body = bytestream.toByteArray();
      bytestream.close();
      is.close();
    } catch (IOException exception) {
      throw exception;
    }
  }

  @Override
  public BufferedReader getReader() throws IOException {
    return new BufferedReader(new InputStreamReader(getInputStream()));
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    final ByteArrayInputStream bais = new ByteArrayInputStream(body);
    return new ServletInputStream() {

      @Override
      public int read() throws IOException {
        return bais.read();
      }

      @Override
      public boolean isFinished() {
        return true;
      }

      @Override
      public boolean isReady() {
        return true;
      }

      @Override
      public void setReadListener(ReadListener readListener) {
        // TODO Auto-generated method stub
      }

    };
  }

  public byte[] getRequestBody() {
    return this.body;
  }

  @Override
  public String getParameter(String name) {
    String value = super.getParameter(name);
    if (value != null) {
      value = value.trim();
    }
    return value;
  }

  /**
   * 获取ParameterMap.
   */
  @Override
  public Map<String, String[]> getParameterMap() {
    Map<String, String[]> map = super.getParameterMap();
    try {
      if (map != null && !map.isEmpty()) {
        for (Entry<String, String[]> entry : map.entrySet()) {
          String key = entry.getKey();
          String[] value = entry.getValue();
          if (key != null && value != null) {
            for (int i = 0; i < value.length; i++) {
              if (value[i] != null) {
                value[i] = value[i].trim();
              }
            }
            map.put(key, value);
          }
        }
      }
    } catch (Exception exception) {
      logger.error("获取不到参数", exception);
    }
    return map;
  }

  @Override
  public String[] getParameterValues(String name) {
    String[] value = super.getParameterValues(name);
    if (value != null) {
      for (int i = 0; i < value.length; i++) {
        if (value[i] != null) {
          value[i] = value[i].trim();
        }
      }
    }
    return value;
  }
}