package com.turgho.investsim.infrastructure.crypto;

import com.dfiney.mvn.spring.commons.service.CryptoService;
import org.springframework.stereotype.Service;

@Service
public class CryptoAdapter {

    private final CryptoService cryptoService;

    public CryptoAdapter(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    public String encrypt(String plainText) {
        return cryptoService.encrypt(plainText);
    }

    public String decrypt(String cipherText) {
        return cryptoService.decrypt(cipherText);
    }
}
