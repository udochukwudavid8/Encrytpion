package com.project.demo.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
@Data
public class DecryptionRequest {// this class holds the Request body of the decryption


        private String text;
        private int shift = 3;
}
