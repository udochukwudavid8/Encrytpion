package com.project.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DecryptedText {//This class hold the decrypted text
        private String recoveredText;
        private String decryptShiftedText;
        private String aesDecryptedText;
        private String rsaDecryptedText;


}
