package com.anthonyzero.seckill.common.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class Md5Util {

    /**
     * 客户端固定盐值
     */
    private static final String salt = "anthonyzero";

    /**
     * 客户端明文固定盐MD5加密
     * @param plaintext （用户真实的密码）
     * @return 网络传输中的加密密码
     */
    public static String inputPassEncrypt(String plaintext) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + plaintext + salt.charAt(6) + salt.charAt(4);
        return DigestUtils.md5Hex(str);
    }

    /**
     * 服务端密文随机盐MD5加密
     * @param ciphertext   密文(由客户端传入)
     * @param dataBaseSalt 数据库用户的盐值
     * @return
     */
    public static String getMd5(String ciphertext, String dataBaseSalt) {
        String str = "" + dataBaseSalt.charAt(0) + dataBaseSalt.charAt(2) + ciphertext + dataBaseSalt.charAt(6) + dataBaseSalt.charAt(4);
        return DigestUtils.md5Hex(str);
    }

    public static void main(String[] args) {
        String password = "123456";
        System.out.println(inputPassEncrypt(password));
        System.out.println(getMd5(inputPassEncrypt(password), salt));
    }
}
