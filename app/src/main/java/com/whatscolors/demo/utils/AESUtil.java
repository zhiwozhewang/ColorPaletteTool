package com.whatscolors.demo.utils;

import android.text.TextUtils;
import android.util.Base64;

import com.whatscolors.demo.base.BaseApplication;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
    public static final String SEED_16_CHARACTER = "82ab301825863ba729e1eb5ad2ca399b";

    // http://stackoverflow.com/questions/17535918/aes-gets-different-results-in-ios-and-java

    /**
     * Encodes a String in AES-256 with a given key
     *
     * @return String Base64 and AES encoded String
     */
    public static String encode(String stringToEncode) throws NullPointerException {
        String keyString = SEED_16_CHARACTER;
        if (keyString.length() == 0 || keyString == null) {
            throw new NullPointerException("Please give Password");
        }

        if (stringToEncode.length() == 0 || stringToEncode == null) {
            throw new NullPointerException("Please give text");
        }

        try {
            SecretKeySpec skeySpec = getKey(keyString);
            byte[] clearText = stringToEncode.getBytes("UTF8");
            // IMPORTANT TO GET SAME RESULTS ON iOS and ANDROID
            final byte[] iv = new byte[16];
            Arrays.fill(iv, (byte) 0x00);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            // Cipher is not thread safe
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);

            String encrypedValue = Base64.encodeToString(cipher.doFinal(clearText), Base64.DEFAULT);
            LogUtils.logd("aes加密：" + encrypedValue);
            return encrypedValue.replace('\n', ' ').replace("/", ":")
                    .replace("+", ".").replace("=", "")
                    .replaceAll("\\s*", "");

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Decodes a String using AES-256 and Base64
     *
     * @param password
     * @param text
     * @return desoded String
     */
    public String decode(String password, String text) throws NullPointerException {

        if (password.length() == 0 || password == null) {
            throw new NullPointerException("Please give Password");
        }

        if (text.length() == 0 || text == null) {
            throw new NullPointerException("Please give text");
        }

        try {
            SecretKey key = getKey(password);

            // IMPORTANT TO GET SAME RESULTS ON iOS and ANDROID
            final byte[] iv = new byte[16];
            Arrays.fill(iv, (byte) 0x00);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            byte[] encrypedPwdBytes = Base64.decode(text, Base64.DEFAULT);
            // cipher is not thread safe
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
            byte[] decrypedValueBytes = (cipher.doFinal(encrypedPwdBytes));

            String decrypedValue = new String(decrypedValueBytes);
            return decrypedValue;

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Generates a SecretKeySpec for given password
     *
     * @param password
     * @return SecretKeySpec
     * @throws UnsupportedEncodingException
     */
    private static SecretKeySpec getKey(String password) throws UnsupportedEncodingException {

        // You can change it to 128 if you wish
        int keyLength = 256;
        byte[] keyBytes = new byte[keyLength / 8];
        // explicitly fill with zeros
        Arrays.fill(keyBytes, (byte) 0x0);

        // if password is shorter then key length, it will be zero-padded
        // to key length
        byte[] passwordBytes = password.getBytes("UTF-8");
        int length = passwordBytes.length < keyBytes.length ? passwordBytes.length : keyBytes.length;
        System.arraycopy(passwordBytes, 0, keyBytes, 0, length);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        return key;
    }

    public static String toGson() {
        return toGsonByBol(false);
    }

    /**
     * 不携带ticket
     */
    public static String toGsonRegister() {
        return toGsonByBol(true);
    }

    private static String toGsonByBol(boolean isRegister) {
        JsonObject jsonObject = new JsonObject();
        long timeStamp = System.currentTimeMillis() / 1000l;//new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date(System.currentTimeMillis()));
        jsonObject.addProperty("timestamp", timeStamp);
        jsonObject.addProperty("client", "Android");
        jsonObject.addProperty("time_zone", getTimeZone());
        jsonObject.addProperty("mac", "");//MacUtils.getMacAddress(BaseApplication.getAppContext()));
        String user_key = SPUtils.getSharedStringData(BaseApplication.getAppContext(), "user_key");
        if (!isRegister && !TextUtils.isEmpty(user_key)) {
            jsonObject.addProperty("user_key", user_key);
        }
        jsonObject.addProperty("ip", NetWorkUtils.getIPAddress(BaseApplication.getAppContext()));
        jsonObject.addProperty("device_id", MacUtils.getUuId32());////随机生成36 位 uuid

        LogUtils.logd("加密参数：" + jsonObject.toString());
        return jsonObject.toString();
    }

    public static String getTimeZone() {

//        return TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT);
        return Calendar.getInstance().getTimeZone().getID();
    }


    public static void main(String args[]) {
        try {
            System.out.println(getTimeZone());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
