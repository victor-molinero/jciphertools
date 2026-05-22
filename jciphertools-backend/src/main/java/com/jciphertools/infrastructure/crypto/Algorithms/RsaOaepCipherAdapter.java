package com.jciphertools.infrastructure.crypto.Algorithms;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jciphertools.domain.CipherRequest;
import com.jciphertools.domain.CipherResponse;
import com.jciphertools.infrastructure.config.properties.RsaProperties;
import com.jciphertools.infrastructure.crypto.CipherAdapter;

@Component
public class RsaOaepCipherAdapter implements CipherAdapter {

    private static final String TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final OAEPParameterSpec OAEP_PARAMS = new OAEPParameterSpec(
            "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
    private static final Logger log = LoggerFactory.getLogger(RsaOaepCipherAdapter.class);


    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public RsaOaepCipherAdapter(RsaProperties properties) throws Exception {
        String publicKeyPath = properties.publicKeyPath();
        String privateKeyPath = properties.privateKeyPath();
        this.publicKey = loadPublicKey(Path.of(publicKeyPath));
        this.privateKey = loadPrivateKey(Path.of(privateKeyPath));
        log.info("RSA-OAEP adapter initialized with keys successfully loaded");
    }

    @Override
    public CipherResponse encrypt(CipherRequest request) throws GeneralSecurityException {
        log.debug("RSA-OAEP encryption: initializing cipher");
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, OAEP_PARAMS);
        byte[] encrypted = cipher.doFinal(request.input().getBytes(StandardCharsets.UTF_8));
        log.debug("RSA-OAEP encryption successful, encrypted size: {} bytes", encrypted.length);
        return new CipherResponse(Base64.getEncoder().encodeToString(encrypted));
    }

    @Override
    public CipherResponse decrypt(CipherRequest request) throws GeneralSecurityException {
        log.debug("RSA-OAEP decryption: initializing cipher with private key");
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey, OAEP_PARAMS);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(request.input()));
        log.debug("RSA-OAEP decryption successful");
        return new CipherResponse(new String(decrypted, StandardCharsets.UTF_8));
    }

    private static PublicKey loadPublicKey(Path path) throws Exception {
        String pem = Files.readString(path)
                .replaceAll("-----[A-Z ]+-----", "")
                .replaceAll("\\s", "");
        byte[] der = Base64.getDecoder().decode(pem);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(new X509EncodedKeySpec(der));
    }

    private static PrivateKey loadPrivateKey(Path path) throws Exception {
        String pem = Files.readString(path)
                .replaceAll("-----[A-Z ]+-----", "")
                .replaceAll("\\s", "");
        byte[] der = Base64.getDecoder().decode(pem);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(new PKCS8EncodedKeySpec(der));
    }
}