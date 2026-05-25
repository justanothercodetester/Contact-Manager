package org.lib;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * AES utility class providing static methods for AES-GCM encryption and decryption.
 *
 * <p>Uses AES-256-GCM with PBKDF2 key derivation. The encrypted output is a
 * Base64-encoded string containing the salt, IV, and ciphertext concatenated together.
 *
 * <p>Example usage:
 * <pre>
 *     String encrypted = AES.encrypt("Hello, World!", "myPassword");
 *     String decrypted = AES.decrypt(encrypted, "myPassword");
 *     // decrypted => "Hello, World!"
 *
 *     String bad = AES.decrypt(encrypted, "wrongPassword");
 *     // bad => "Wrong password"
 * </pre>
 */
public class AES {

    // AES-GCM parameters
    private static final String ALGORITHM          = "AES/GCM/NoPadding";
    private static final int    AES_KEY_SIZE_BITS  = 256;
    private static final int    GCM_TAG_LENGTH_BITS = 128;
    private static final int    GCM_IV_LENGTH_BYTES = 12;

    // PBKDF2 key derivation parameters
    private static final String KDF_ALGORITHM      = "PBKDF2WithHmacSHA256";
    private static final int    KDF_ITERATIONS     = 310_000;   // OWASP 2023 recommendation
    private static final int    SALT_LENGTH_BYTES  = 16;

    // Layout of the encoded blob: [salt (16)] [iv (12)] [ciphertext + GCM tag]
    private static final int SALT_OFFSET = 0;
    private static final int IV_OFFSET   = SALT_LENGTH_BYTES;
    private static final int CT_OFFSET   = IV_OFFSET + GCM_IV_LENGTH_BYTES;
    
    // Minimum encrypted data size: salt + IV + at least 1 byte ciphertext + GCM tag (16 bytes)
    private static final int MIN_ENCRYPTED_SIZE = SALT_LENGTH_BYTES + GCM_IV_LENGTH_BYTES + 1 + 16;

    /**
     * Encrypts {@code plainText} using AES-256-GCM with a key derived from {@code password}.
     *
     * @param plainText the plain-text string to encrypt (must not be null)
     * @param password  the password used to derive the encryption key (must not be null)
     * @return a Base64-encoded string containing the salt, IV, and ciphertext
     * @throws RuntimeException if encryption fails for any reason
     */
    public static String encrypt(String plainText, String password) {
        try {
            // Generate a random salt and IV
            SecureRandom random = new SecureRandom();

            byte[] salt = new byte[SALT_LENGTH_BYTES];
            random.nextBytes(salt);

            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            random.nextBytes(iv);

            // Derive a 256-bit AES key from the password
            SecretKey secretKey = deriveKey(password, salt);

            // Encrypt
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF-8"));

            // Pack: salt | iv | ciphertext
            byte[] packed = new byte[SALT_LENGTH_BYTES + GCM_IV_LENGTH_BYTES + cipherText.length];
            System.arraycopy(salt,       0, packed, SALT_OFFSET, SALT_LENGTH_BYTES);
            System.arraycopy(iv,         0, packed, IV_OFFSET,   GCM_IV_LENGTH_BYTES);
            System.arraycopy(cipherText, 0, packed, CT_OFFSET,   cipherText.length);

            return Base64.getEncoder().encodeToString(packed);

        } catch (Exception e) {
            throw new RuntimeException("Encryption failed: " + e.getMessage(), e);
        }
    }

    /**
     * Decrypts {@code encryptedText} using AES-256-GCM with a key derived from {@code password}.
     *
     * <p>If the password is wrong (or the ciphertext is tampered with), the GCM authentication
     * tag will fail and this method returns {@code "Wrong password"} instead of throwing.
     *
     * @param encryptedText the Base64-encoded string produced by {@link #encrypt}
     * @param password      the password used to derive the decryption key
     * @return the original plain-text string, or {@code "Wrong password"} if the password is invalid
     */
    public static String decrypt(String encryptedText, String password) {
        try {
            // Validate input is not null or empty
            if (encryptedText == null || encryptedText.trim().isEmpty()) {
                return "Error: Invalid encrypted data (empty)";
            }
            
            // Try to decode Base64 - this will fail if the input is not valid Base64
            byte[] packed;
            try {
                packed = Base64.getDecoder().decode(encryptedText.trim());
            } catch (IllegalArgumentException e) {
                return "Error: Invalid encrypted data (not Base64 encoded)";
            }
            
            // Validate minimum size
            if (packed.length < MIN_ENCRYPTED_SIZE) {
                return "Error: Invalid encrypted data (too short)";
            }

            // Unpack: salt | iv | ciphertext
            byte[] salt       = new byte[SALT_LENGTH_BYTES];
            byte[] iv         = new byte[GCM_IV_LENGTH_BYTES];
            byte[] cipherText = new byte[packed.length - CT_OFFSET];

            System.arraycopy(packed, SALT_OFFSET, salt,       0, SALT_LENGTH_BYTES);
            System.arraycopy(packed, IV_OFFSET,   iv,         0, GCM_IV_LENGTH_BYTES);
            System.arraycopy(packed, CT_OFFSET,   cipherText, 0, cipherText.length);

            // Derive the key with the extracted salt
            SecretKey secretKey = deriveKey(password, salt);

            // Decrypt — GCM will throw AEADBadTagException on a wrong password / tampered data
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] plainBytes = cipher.doFinal(cipherText);

            return new String(plainBytes, "UTF-8");

        } catch (javax.crypto.AEADBadTagException e) {
            // Authentication tag mismatch — wrong password or corrupted data
            return "Error: Wrong password";
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed: " + e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Derives a 256-bit AES {@link SecretKey} from the given {@code password} and {@code salt}
     * using PBKDF2 with HMAC-SHA-256.
     */
    private static SecretKey deriveKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KDF_ALGORITHM);
        KeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                KDF_ITERATIONS,
                AES_KEY_SIZE_BITS
        );
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }
}