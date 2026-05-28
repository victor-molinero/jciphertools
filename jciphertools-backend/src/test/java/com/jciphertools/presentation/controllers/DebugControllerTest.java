package com.jciphertools.presentation.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jciphertools.infrastructure.config.properties.AesCbcProperties;
import com.jciphertools.infrastructure.config.properties.AesGcmProperties;
import com.jciphertools.infrastructure.config.properties.CorsProperties;
import com.jciphertools.infrastructure.config.properties.RsaProperties;

class DebugControllerTest {

    private DebugController controller;
    private String aesCbcKey;
    private String aesCbcIv;
    private String aesGcmKey;
    private String aesGcmIv;
    private String rsaPublicKeyPem;
    private String rsaPrivateKeyPem;

    @BeforeEach
    void setUp() throws Exception {
        aesCbcKey = base64Bytes(32);
        aesCbcIv = base64Bytes(16);
        aesGcmKey = base64Bytes(32);
        aesGcmIv = base64Bytes(12);

        RsaProperties rsaProperties = createRsaProperties();
        rsaPublicKeyPem = rsaProperties.publicKeyPem();
        rsaPrivateKeyPem = rsaProperties.privateKeyPem();

        controller = new DebugController(
                new AesCbcProperties(aesCbcKey, aesCbcIv),
                new AesGcmProperties(aesGcmKey, aesGcmIv),
                rsaProperties,
                new CorsProperties(List.of("http://localhost:4200")));
    }

    @Test
    void shouldReturnOkWhenAllEnvironmentValuesAreValid() {
        var response = controller.checkEnvironment();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().allValid());
        assertTrue(response.getBody().checks().values().stream().allMatch(Boolean::booleanValue));
    }

    @Test
    void shouldReturnInternalServerErrorWhenAesKeyIsInvalid() throws Exception {
        controller = new DebugController(
                new AesCbcProperties("invalid", base64Bytes(16)),
                new AesGcmProperties(base64Bytes(32), base64Bytes(12)),
                createRsaProperties(),
                new CorsProperties(List.of("http://localhost:4200")));

        var response = controller.checkEnvironment();

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().allValid());
        assertFalse(response.getBody().checks().get("AES_CBC_KEY"));
        assertTrue(response.getBody().summary().contains("AES_CBC_KEY"));
    }

    @Test
    void shouldNeverExposeSecretValuesInResponse() {
        var response = controller.checkEnvironment().getBody();

        assertNotNull(response);
        String responseText = response.summary() + response.checks().toString();
        assertFalse(responseText.contains(aesCbcKey));
        assertFalse(responseText.contains(aesCbcIv));
        assertFalse(responseText.contains(aesGcmKey));
        assertFalse(responseText.contains(aesGcmIv));
        assertFalse(responseText.contains(rsaPublicKeyPem));
        assertFalse(responseText.contains(rsaPrivateKeyPem));
    }

    private static String base64Bytes(int length) {
        return Base64.getEncoder().encodeToString(new byte[length]);
    }

    private static RsaProperties createRsaProperties() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        return new RsaProperties(toPem("PUBLIC KEY", keyPair.getPublic()), toPem("PRIVATE KEY", keyPair.getPrivate()));
    }

    private static String toPem(String type, PublicKey key) {
        return "-----BEGIN " + type + "-----\n"
                + Base64.getMimeEncoder(64, new byte[] {'\n'}).encodeToString(key.getEncoded())
                + "\n-----END " + type + "-----";
    }

    private static String toPem(String type, PrivateKey key) {
        return "-----BEGIN " + type + "-----\n"
                + Base64.getMimeEncoder(64, new byte[] {'\n'}).encodeToString(key.getEncoded())
                + "\n-----END " + type + "-----";
    }
}