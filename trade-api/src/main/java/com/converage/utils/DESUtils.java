package com.converage.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static com.converage.constance.CommonConst.UTF_8;

public class DESUtils {
    public static final String PASSWORD_CRYPT_KEY = "05477492";

    public static byte[] des(int mode, byte[] data, byte[] keyData) {
        byte[] ret = null;
        if (data != null && data.length > 0 && keyData != null && keyData.length == 8) {
            try {
                Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
                DESKeySpec desKeySpec = new DESKeySpec(keyData);
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
                IvParameterSpec iv = new IvParameterSpec(keyData);
                cipher.init(mode, secretKey, iv);
                ret = cipher.doFinal(data);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException | InvalidKeyException | InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }


    public static byte[] encrypt(byte[] datasource, String password) throws UnsupportedEncodingException {
        return des(Cipher.ENCRYPT_MODE, datasource, password.getBytes(UTF_8));
    }


    public static String encryptWithBase64(String src, String password) throws UnsupportedEncodingException {
        byte[] b = encrypt(src.getBytes(UTF_8), password);
        return new BASE64Encoder().encodeBuffer(b);

    }

    public static byte[] decrypt(byte[] src, String password) throws Exception {
        return des(Cipher.DECRYPT_MODE, src, password.getBytes(UTF_8));
    }

    public static String decryptWithBase64(String src, String password) throws Exception {
        byte[] b = decrypt(new BASE64Decoder().decodeBuffer(src), password);
        return new String(b, UTF_8);
    }


    public static void main(String[] args) throws Exception {
        //待加密内容
        String str = "4641";
        //密码，长度要是8的倍数
        String password = "Iteo4ky8";

        String result = DESUtils.encryptWithBase64(str, password);
        System.out.println("加密后：" + result);
        //直接将如上内容解密
        try {
            result = "1GKEjY+L3Tk22j4eNi6orVSGra0Z7bQkHw2FrueAex0Yxxvusiqyw8mM4a7ny2s1kei+8++1/HKF/v0gyLZWmMDwxTbzmkuWfX8SKSHVNQc=";
            String decryResult = DESUtils.decryptWithBase64(result, password);
            System.out.println("解密后：" + decryResult);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }
}
