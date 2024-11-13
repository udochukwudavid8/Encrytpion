package com.project.demo.service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

import com.project.demo.model.DecryptedText;
import com.project.demo.model.EncryptedText;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



@Service
@Slf4j
public class EncryptionService {
    private final String password = "Your16ByteEncryp";
    private KeyPair keyPair;

    IvParameterSpec iv;

    @PostConstruct
    public void init() throws Exception {
        byte[] ivBytes = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(ivBytes);
        iv = new IvParameterSpec(ivBytes);
        keyPair = generateKeyPair();
    }

    private String encryptShiftText(int otp, int shift) {  //This method is the start of the encryption it handles the shift key encryption
        log.info("shiftText/otp={}", otp);
        String newShift = "";
        String text=String.valueOf(otp);
        for (char character : text.toCharArray()) {
            int asciiValue = character;
            char newCharacter;
            asciiValue = asciiValue + shift;
            if (asciiValue <= 57) {
                newCharacter = (char) asciiValue;

            } else {
                asciiValue = asciiValue - 58;
                asciiValue = 48 + asciiValue;
                newCharacter = (char) asciiValue;

            }
            newShift += newCharacter;

            log.info(newShift);
        }
        return newShift;
    }

    private String aesEncryptText(String text) {// this method handles the aesEncryption  and collects the data from the shift key
        log.info("aesEncryptText/text={}", text);
        try {

            byte[] raw = password.getBytes(StandardCharsets.UTF_8);
            if (raw.length != 16) {
                throw new IllegalArgumentException("Invalid key size.");
            }

            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec,
                    new IvParameterSpec(new byte[16]));
            byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            System.err.println("Error during AES encryption: " + e.getMessage());
            return null;
        }
    }


    private String rsaEncryptText(String text) throws Exception { //this method handles the rsaEncryption and collects data from the aesEncryption
        log.info("rsaEncryptText/text={}", text);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        byte[] encryptedBytes = cipher.doFinal(text.getBytes());
        String rsaEncryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
        log.info("rsaEncryptText successful/text={}", rsaEncryptedText);
        return rsaEncryptedText;
    }

    private static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }


    private String decryptShiftText(String text, int shift) { // This handles the description of aesDecryption
        log.info("shiftText/text={}", text);
        String newShift = "";

        for (char character : text.toCharArray()) {
            int asciiValue = character;

            char newCharacter;
            asciiValue = asciiValue - shift;
            if (asciiValue >= 48) {
                newCharacter = (char) asciiValue;

            } else {
                asciiValue = (asciiValue + 58) - 48;
                newCharacter = (char) asciiValue;
            }

            newShift += newCharacter;
            log.info(newShift);
        }
        return newShift;
    }

    private String aesDecryptText(String text) {// this collects the data from the resDecryption and decrypts it
        log.info("aesDecryptText/text={}", text);
        try {
            byte[] raw = password.getBytes(StandardCharsets.UTF_8);
            if (raw.length != 16) {
                throw new IllegalArgumentException("Invalid key size.");
            }
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[16]));
            byte[] decodedEncryptedText = Base64.getDecoder().decode(text);
            byte[] original = cipher.doFinal(decodedEncryptedText);


            return new String(original);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error during AES decryption: " + e.getMessage());
            return null;
        }

    }

    private String rsaDecryptText(String text) throws Exception { // this collects the encrypted data and starts the description
        log.info("rsaDecryptTest/entering.....test={}", text);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(text));

        String rsaDecryptedText = new String(decryptedBytes);
        log.info("rsaDecryptTest/successful.....text={}", rsaDecryptedText);
        return rsaDecryptedText;
    }


    public EncryptedText encrypt(int otp, int shift) throws Exception { //this method has the container class of the encryption
        log.info("encrypt/text={}", otp);
        EncryptedText encryptedText = new EncryptedText();

        String encryptShiftText = encryptShiftText(otp, shift);
        encryptedText.setEncryptShiftedText(encryptShiftText);

        String aesText = aesEncryptText(encryptShiftText);
        encryptedText.setAesEncryptedText(aesText);

        String resText = rsaEncryptText(aesText);
        encryptedText.setRsaEncryptedText(resText);

        return encryptedText;

    }


    public DecryptedText decrypt(String resText, int shift) throws Exception {// this method handles the container class of the decryption
        log.info("decrypt/text={}", resText);
        DecryptedText decryptedText = new DecryptedText();

        String resDecryptText = rsaDecryptText(resText);
        decryptedText.setRsaDecryptedText(resDecryptText);

        String aesDecryptText = aesDecryptText(resDecryptText);
        decryptedText.setAesDecryptedText(aesDecryptText);

        String decryptShiftText = decryptShiftText(aesDecryptText, shift);
        decryptedText.setDecryptShiftedText(decryptShiftText);

        decryptedText.setRecoveredText(decryptShiftText);

        return decryptedText;

    }

}
