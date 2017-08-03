package org.transformer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 *DES加解密工具.
 * @author wjk
 *
 */
public class DesUtils {

  /** 日志. */
  private static Logger logger = LoggerFactory.getLogger(JsonMapper.class);

  /** 加解密秘钥.*/
  private String key;

  /**小数点位数. */
  private static final int RADIX = 16;

  /** byte数组转换为hex时的&运算参数. */
  private static final int INT_TO_HEX_NUM = 0xff;

  /**
   * 构造函数.
   * @param key 秘钥
   */
  public DesUtils(String key) {
    this.key = key;
  }

  /**
   * 解密数据.     
   * @param message 需要解密的字符串.
   * @return 解密后的字符，如果失败，返回空字符串.
   */
  public String decrypt(String message) {
    String result = "";
    if (message == null || message.length() < 1) {
      return result;
    }
    try {
      byte[] bytesrc = convertHexString(message);
      Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
      DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
      SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
      IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));

      cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

      byte[] retByte = cipher.doFinal(bytesrc);
      result = new String(retByte);
    } catch (Exception ex) {
      logger.error("解密失败", ex);
    }
    return result;
  }

  /**
   * 加密数据.
   * @param message 需要加密的字符串.
   * @return 加密后的字符串
   * @throws Exception 失败抛出异常
   * */
  public String encrypt(String message) throws Exception {
    if (message == null || message.length() < 1) {
      return "";
    }

    Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

    DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));

    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
    SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
    IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

    return toHexString(cipher.doFinal(message.getBytes("UTF-8")));
  }

  /**
   * HEX字符串转换byte数组.
   * @param ss 需要转换的字符串
   * @return 转换后的结果
   */
  private static byte[] convertHexString(String ss) {
    byte[] digest = new byte[ss.length() / 2];
    for (int i = 0; i < digest.length; i++) {
      String byteString = ss.substring(2 * i, 2 * i + 2);
      int byteValue = Integer.parseInt(byteString, RADIX);
      digest[i] = (byte) byteValue;
    }

    return digest;
  }

  /**
   * byte数据转换位HEX字符串.
   * @param bytes 需要转换的byte数组
   * @return 转换后的结果
   */
  private static String toHexString(byte[] bytes) {
    StringBuffer hexString = new StringBuffer();
    for (int i = 0; i < bytes.length; i++) {
      String plainText = Integer.toHexString(INT_TO_HEX_NUM & bytes[i]);
      if (plainText.length() < 2) {
        plainText = "0" + plainText;
      }
      hexString.append(plainText);
    }

    return hexString.toString().toUpperCase();
  }
}
