package in.rahul.learning.util;

import in.rahul.learning.exceptions.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class AESUtil {


    /**
     * AES does NOT accept “any random string” safely unless you properly derive a key.
     * <p>
     * AES requires:
     * 16 bytes (AES-128)
     * 32 bytes (AES-256)
     *
     *
     */

    @Value("${aes.secret:Secret}")
    private String secretKey;

    @Value("${aes.algorithm:AES}")
    private String algorithm;

    // Build key dynamically
    private SecretKey getKey() {
        return new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8),
                algorithm
        );
    }

    // ENCRYPT
    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, getKey());

            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            throw new CustomException("Encryption failed: " + e.getMessage());
        }
    }

    // DECRYPT
    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, getKey());

            byte[] decoded = Base64.getDecoder().decode(encryptedData);

            return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new CustomException("Decryption failed: " + e.getMessage());
        }
    }
}