package org.transformer.util;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * HTTPSpost工具.
 * @author wjk
 */
public final class HttpsPostUtils {

  /**
   * 默认构造函数.
   */
  private HttpsPostUtils() {

  }

  /** 请求方式,默认POST. */
  private static final String METHOD_POST = "POST";

  /** 默认字符编码 UTF-8. */
  private static final String DEFAULT_CHARSET = "utf-8";

  /** 默认字符数组长度. */
  private static final int INIT_CHAR_LENGTH = 256;

  /**
   * 发起httpsPOST请求.
   * @param url 请求地址
   * @param params 请求参数
   * @param charset 字符串编码
   * @param connectTimeout 连接超时时间
   * @param readTimeout 调用超时时间
   * @return 调用返回值
   * @throws Exception 异常信息
   */
  public static String doPost(String url, String params, String charset, int connectTimeout,
      int readTimeout) throws Exception {
    String ctype = "application/text;charset=" + charset;
    byte[] content = {};
    if (params != null) {
      content = params.getBytes(charset);
    }
    return doPost(url, ctype, content, connectTimeout, readTimeout);
  }

  /**
   * 发起httpsPOST请求.
   * @param url 请求地址
   * @param ctype Content-Type，"application/text;charset=UTF-8"等
   * @param content 请求的内容，字节型
   * @param connectTimeout 连接超时时间
   * @param readTimeout 调用超时时间
   * @return 调用返回值
   * @throws Exception 异常信息
   */
  public static String doPost(String url, String ctype, byte[] content, int connectTimeout,
      int readTimeout) throws Exception {
    HttpsURLConnection conn = null;

    String rsp = null;
    try {
      try {
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() },
            new SecureRandom());
        SSLContext.setDefault(ctx);

        conn = getConnection(new URL(url), METHOD_POST, ctype);
        conn.setHostnameVerifier(new HostnameVerifier() {
          @Override
          public boolean verify(String hostname, SSLSession session) {
            return true;
          }
        });
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
      } catch (Exception e) {
        throw e;
      }
      try (OutputStream out = conn.getOutputStream()) {
        out.write(content);
        rsp = getResponseAsString(conn);
      } catch (IOException e) {
        throw e;
      }

    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }

    return rsp;
  }

  /**
   * 默认的加解密管理器.
   */
  private static class DefaultTrustManager implements X509TrustManager {

    public DefaultTrustManager() {
    }

    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1)
        throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1)
        throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return null;
    }

  }

  /**
   * 创建请求连接.
   * @param url 请求地址
   * @param method 请求类型，POST or GET
   * @param ctype Content-Type
   * @return 请求连接
   * @throws IOException IO异常
   */
  private static HttpsURLConnection getConnection(URL url, String method, String ctype)
      throws IOException {
    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
    conn.setRequestMethod(method);
    conn.setDoInput(true);
    conn.setDoOutput(true);
    conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
    conn.setRequestProperty("User-Agent", "stargate");
    conn.setRequestProperty("Content-Type", ctype);
    return conn;
  }

  /**
   * 调用请求并将返回值并转换字符串.
   * @param conn 请求连接
   * @return 返回值
   * @throws IOException IO异常
   */
  protected static String getResponseAsString(HttpURLConnection conn) throws IOException {
    String charset = getResponseCharset(conn.getContentType());
    try (InputStream es = conn.getErrorStream()) {
      if (es == null) {
        return getStreamAsString(conn.getInputStream(), charset);
      } else {
        String msg = getStreamAsString(es, charset);
        if (StringUtils.isEmpty(msg)) {
          throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
        } else {
          throw new IOException(msg);
        }
      }
    }
  }

  /**
   * 输入流转化为字符串.
   * @param stream 输入流
   * @param charset 字符串编码
   * @return 转化后的字符串
   * @throws IOException IO异常
   */
  private static String getStreamAsString(InputStream stream, String charset) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset))) {
      StringWriter writer = new StringWriter();

      char[] chars = new char[INIT_CHAR_LENGTH];
      int count = 0;
      while ((count = reader.read(chars)) > 0) {
        writer.write(chars, 0, count);
      }

      return writer.toString();
    } finally {
      if (stream != null) {
        stream.close();
      }
    }
  }

  /**
   * 获取响应值的编码方式.
   * @param ctype 响应内容的content-type
   * @return 编码方式
   */
  private static String getResponseCharset(String ctype) {
    String charset = DEFAULT_CHARSET;

    if (!StringUtils.isEmpty(ctype)) {
      String[] params = ctype.split(";");
      for (String param : params) {
        param = param.trim();
        if (param.startsWith("charset")) {
          String[] pair = param.split("=", 2);
          if (pair.length == 2) {
            if (!StringUtils.isEmpty(pair[1])) {
              charset = pair[1].trim();
            }
          }
          break;
        }
      }
    }
    return charset;
  }

}
