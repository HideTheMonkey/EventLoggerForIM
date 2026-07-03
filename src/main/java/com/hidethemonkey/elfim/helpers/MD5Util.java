package com.hidethemonkey.elfim.helpers;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * From https://en.gravatar.com/site/implement/images/java/
 */
public class MD5Util {

  /**
   * @param message
   * @return
   */
  public static String md5Hex(String message) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      return HexFormat.of().formatHex(md.digest(message.getBytes("CP1252")));
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
      return null;
    }
  }
}