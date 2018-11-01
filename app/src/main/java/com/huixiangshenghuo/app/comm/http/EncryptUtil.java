package com.huixiangshenghuo.app.comm.http;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtil {

    private static byte[] iv = {1, 2, 3, 4, 5, 6, 7, 8};

    /**
     * des加密 加密以 byte[] 明文输入 ,byte[] 密文输出
     *
     * @param encryptBytes :要加密的字节数组
     * @param encryptKey   ：秘钥，只能是八位字母和数字的组合
     * @return
     * @throws Exception
     * @author: jinguangzhang
     * @date: 2014-8-21
     */
    public static byte[] encryptDES(byte[] encryptBytes, String encryptKey)
            throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        byte[] encryptedData = cipher.doFinal(encryptBytes);

        return Base64Util.Encode(encryptedData);
    }

    /**
     * @param decryptBytes ：要解密的字节数组
     * @param decryptKey   ：解密的秘钥，只能是八位字母和数字的组合
     * @return
     * @throws Exception
     * @author: jinguangzhang
     * @date: 2014-8-21
     */
    public static byte[] decryptDES(byte[] decryptBytes, String decryptKey)
            throws Exception {
        byte[] byteMi = Base64Util.decode(decryptBytes);
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        byte decryptedData[] = cipher.doFinal(byteMi);

        return decryptedData;
    }
}
