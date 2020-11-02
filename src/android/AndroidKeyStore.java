package com.bionworks.contentproviderplugin;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import com.bionworks.contentproviderplugin.SaveUtilData;

public class AndroidKeyStore {
    private final static String alias = "myKey";
    private static final String KEYSTORE = "AndroidKeyStore";
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final String CHAR_SET = "UTF-8";

    public boolean encryptData(String data,Context context) {
        try {
            SecretKey secretKey = createKey();
            if(secretKey == null) {
                return false;
            }
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();
            byte[] dataBytes = data.getBytes(CHAR_SET);
            byte[] encryptedDataBytes = cipher.doFinal(dataBytes);
            String encryptedData = Base64.encodeToString(encryptedDataBytes,Base64.DEFAULT);
            String encryptedIv = Base64.encodeToString(iv,Base64.DEFAULT);
            SaveUtilData saveUtilData = new SaveUtilData(context);
            saveUtilData.setData(encryptedData,encryptedIv);
        } catch (Exception e) {
            Log.e("AndroidKeyStore","Exception in encrypt data ->"+e.getMessage());
            return false;
        }
        return true;
    }

    public String decryptData (String key, String enIv) {
        String passPhrase = "";
        try {
            byte[] enIvBytes = Base64.decode(enIv,Base64.DEFAULT);
            byte[] keyBytes = Base64.decode(key,Base64.DEFAULT);
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE);
            keyStore.load(null);
            SecretKey secretKey = (SecretKey) keyStore.getKey(alias,null);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            final GCMParameterSpec spec = new GCMParameterSpec(128, enIvBytes);
            cipher.init(Cipher.DECRYPT_MODE,secretKey,spec);
            byte[] decryptKeyBytes = cipher.doFinal(keyBytes);
            passPhrase = new String (decryptKeyBytes,CHAR_SET);
            Log.i("AndroidKeyStore","Decrypted data is ->"+passPhrase);
        } catch (Exception e) {
            Log.e("AndroidKeyStore","Exception in decryptData"+e.getMessage());
        }
        return passPhrase;
    }
    public SecretKey createKey() {
        SecretKey secretKey = null;
        try {
            final KeyGenerator keyGenerator;
            final KeyGenParameterSpec keyGenParameterSpec;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Log.i("AndroidKeyStore","executing the above if part");
                keyGenerator = KeyGenerator
                        .getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE);
                keyGenParameterSpec = new KeyGenParameterSpec.Builder(alias,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build();
                keyGenerator.init(keyGenParameterSpec);
                secretKey = keyGenerator.generateKey();
            } else {
                //TO handle Android API level below 23
            }
        } catch (Exception e) {
            Log.e("AndroidKeyStore"," createKey method Throws ann exception"+e.getMessage());
        }
        return  secretKey;
    }
}
