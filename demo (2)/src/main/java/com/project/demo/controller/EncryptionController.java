package com.project.demo.controller;


import com.project.demo.model.DecryptedText;
import com.project.demo.model.DecryptionRequest;
import com.project.demo.model.EncryptedText;
import com.project.demo.model.EncryptionRequest;
import com.project.demo.service.EncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class EncryptionController {

    @Autowired
    private EncryptionService encryptionService;



    @PostMapping("/encrypt")//url endpoint for the encrypt
    public EncryptedText getEncryption(@RequestBody EncryptionRequest encryptionRequest) throws Exception {
        log.info("getEncryption/request={}",encryptionRequest);

        return encryptionService.encrypt(encryptionRequest.getOtp(), encryptionRequest.getShift());
    }

    @PostMapping("/decrypt")//url endpoint for the decrypt
    public DecryptedText getDecryption(@RequestBody DecryptionRequest decryptionRequest) throws Exception {
        log.info("getDecryption/request={}",decryptionRequest);


        return encryptionService.decrypt(decryptionRequest.getText(),decryptionRequest.getShift());
    }
}
