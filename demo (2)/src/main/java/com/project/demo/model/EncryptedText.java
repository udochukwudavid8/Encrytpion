package com.project.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EncryptedText {//This class hold the encrypted text
    private String encryptShiftedText;
    private String aesEncryptedText;
    private String rsaEncryptedText;

}
