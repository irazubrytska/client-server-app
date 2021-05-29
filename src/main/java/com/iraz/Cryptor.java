package com.iraz;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Cryptor {

    private static Cryptor instance;
    private final byte[] initVector;
    private final SecretKey secretKey;

    private Cryptor() throws NoSuchAlgorithmException {
        initVector=createInitializationVector();
        secretKey=createAESKey();
    }

    public static Cryptor getInstance() throws NoSuchAlgorithmException {
        if(instance == null){
            synchronized (Cryptor.class){
                if(instance == null){
                    instance = new Cryptor();
                }
            }
        }
        return instance;
    }

    private SecretKey createAESKey() throws NoSuchAlgorithmException {
        SecureRandom securerandom = new SecureRandom();
        KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
        keygenerator.init(256, securerandom);
        return keygenerator.generateKey();
    }

    private byte[] createInitializationVector() {
        byte[] initializationVector = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(initializationVector);
        return initializationVector;
    }

    public byte[] encrypt(final String message) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initVector);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey,ivParameterSpec);
        return cipher.doFinal(message.getBytes());
    }

    public String decrypt(final byte[] message) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initVector);
        cipher.init(Cipher.DECRYPT_MODE, secretKey,ivParameterSpec);
        return new String(cipher.doFinal(message));
    }

}
