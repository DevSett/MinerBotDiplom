package app.classes.services;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * A utility class that encrypts or decrypts a file.
 *
 * @author www.codejava.net
 */
public class CryptoUtils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    public static byte[] encrypt(String key, byte[] bytes)
            throws CryptoException {
        return doCrypto(Cipher.ENCRYPT_MODE, key, bytes);
    }

    public static byte[] decrypt(String key, byte[] bytes)
            throws CryptoException {
        return doCrypto(Cipher.DECRYPT_MODE, key, bytes);
    }

    private static byte[] doCrypto(int cipherMode, String key, byte[] bytes) throws CryptoException {
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            cipher.init(cipherMode, secretKey);


            byte[] outputBytes = cipher.doFinal(bytes);

            return outputBytes;

        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }
}