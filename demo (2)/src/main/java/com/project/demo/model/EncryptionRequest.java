package com.project.demo.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class EncryptionRequest {// this class holds the Request body of the encryption
    private int otp;
    private int shift = 3;
}
