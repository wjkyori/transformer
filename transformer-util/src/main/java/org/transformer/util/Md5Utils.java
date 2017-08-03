package org.transformer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

/**
 * MD5加密工具.
 */
public final class Md5Utils {

  /**
   * 默认构造函数.
   */
  private Md5Utils() {

  }

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(Md5Utils.class);

  /** byte数组转换为hex时的&运算参数. */
  private static final int INT_TO_HEX_NUM = 0xff;

  /** byte数组转换为hex时的&运算最大值. */
  private static final int INT_TO_HEX_MAX_NUM = 0x10;

  /**小数点位数. */
  private static final int RADIX = 16;

  /**
   * 获取字符串的MD5值.
   *
   * @param string 字符串 
   * @return MD5结果
   */
  private static byte[] md5(String string) {
    MessageDigest algorithm;
    try {
      algorithm = MessageDigest.getInstance("MD5");
      algorithm.reset();
      algorithm.update(string.getBytes("UTF-8"));
      byte[] messageDigest = algorithm.digest();
      return messageDigest;
    } catch (Exception e) {
      LOGGER.error("MD5 Error...", e);
    }
    return null;
  }

  /**
   * byte[]转化为hex字符串.
   *
   * @param hash the hash
   * @return the string
   */
  private static String toHex(byte[] hash) {
    if (hash == null) {
      return null;
    }
    StringBuffer buf = new StringBuffer(hash.length * 2);

    int index;

    for (index = 0; index < hash.length; index++) {
      if ((hash[index] & INT_TO_HEX_NUM) < INT_TO_HEX_MAX_NUM) {
        buf.append("0");
      }
      buf.append(Long.toString(hash[index] & INT_TO_HEX_NUM, RADIX));
    }
    return buf.toString();
  }

  /**
   * 获取MD5值.
   *
   * @param string the s
   * @return the string
   */
  public static String hash(String string) {
    try {
      return new String(toHex(md5(string)).getBytes("UTF-8"), "UTF-8");
    } catch (Exception e) {
      LOGGER.error("not supported charset...{}", e);
      return string;
    }
  }

}
