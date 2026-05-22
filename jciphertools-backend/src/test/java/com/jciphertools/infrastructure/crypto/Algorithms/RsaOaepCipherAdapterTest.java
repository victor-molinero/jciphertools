package com.jciphertools.infrastructure.crypto.Algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.jciphertools.domain.Algorithm;
import com.jciphertools.domain.CipherRequest;
import com.jciphertools.domain.CipherResponse;
import com.jciphertools.infrastructure.config.properties.RsaProperties;

class RsaOaepCipherAdapterTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldEncryptAndDecryptRoundtrip() throws Exception {
        KeyPair keys = generateRsaKeyPair();
        Path publicKeyPath = writePublicKeyPem(keys, tempDir.resolve("public.pem"));
        Path privateKeyPath = writePrivateKeyPem(keys, tempDir.resolve("private.pem"));
        RsaOaepCipherAdapter adapter = new RsaOaepCipherAdapter(
            new RsaProperties(publicKeyPath.toString(), privateKeyPath.toString()));

        CipherResponse encrypted = adapter.encrypt(new CipherRequest("hello world", Algorithm.RSA_OAEP));
        CipherResponse decrypted = adapter.decrypt(new CipherRequest(encrypted.result(), Algorithm.RSA_OAEP));

        assertNotEquals("hello world", encrypted.result());
        assertEquals("hello world", decrypted.result());
    }

    @Test
    void shouldThrowWhenKeyFilesDoNotExist() {
        Path missingPublic = tempDir.resolve("missing-public.pem");
        Path missingPrivate = tempDir.resolve("missing-private.pem");

        assertThrows(Exception.class,
            () -> new RsaOaepCipherAdapter(new RsaProperties(missingPublic.toString(), missingPrivate.toString())));
    }

    @Test
    void shouldThrowWhenCiphertextIsInvalidBase64() throws Exception {
        KeyPair keys = generateRsaKeyPair();
        Path publicKeyPath = writePublicKeyPem(keys, tempDir.resolve("public.pem"));
        Path privateKeyPath = writePrivateKeyPem(keys, tempDir.resolve("private.pem"));
        RsaOaepCipherAdapter adapter = new RsaOaepCipherAdapter(
            new RsaProperties(publicKeyPath.toString(), privateKeyPath.toString()));

        CipherRequest invalid = new CipherRequest("%%%", Algorithm.RSA_OAEP);

        assertThrows(IllegalArgumentException.class, () -> adapter.decrypt(invalid));
    }

    @Test
    void shouldThrowWhenPlaintextExceedsRsaLimit() throws Exception {
        KeyPair keys = generateRsaKeyPair();
        Path publicKeyPath = writePublicKeyPem(keys, tempDir.resolve("public.pem"));
        Path privateKeyPath = writePrivateKeyPem(keys, tempDir.resolve("private.pem"));
        RsaOaepCipherAdapter adapter = new RsaOaepCipherAdapter(
            new RsaProperties(publicKeyPath.toString(), privateKeyPath.toString()));
        String oversizedInput = "x".repeat(400);

        assertThrows(GeneralSecurityException.class,
                () -> adapter.encrypt(new CipherRequest(oversizedInput, Algorithm.RSA_OAEP)));
    }

    private static KeyPair generateRsaKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    private static Path writePublicKeyPem(KeyPair keys, Path path) throws Exception {
        String content = toPem("PUBLIC KEY", keys.getPublic().getEncoded());
        Files.writeString(path, content);
        return path;
    }

    private static Path writePrivateKeyPem(KeyPair keys, Path path) throws Exception {
        String content = toPem("PRIVATE KEY", keys.getPrivate().getEncoded());
        Files.writeString(path, content);
        return path;
    }

    private static String toPem(String type, byte[] encoded) {
        String body = Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(encoded);
        return "-----BEGIN " + type + "-----\n"
                + body
                + "\n-----END " + type + "-----\n";
    }
}