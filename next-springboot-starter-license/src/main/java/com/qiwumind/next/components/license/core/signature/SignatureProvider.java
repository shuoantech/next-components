/*
 * MIT License
 *
 * Copyright (c) 2026 qiwumind
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.  Author: liks
 * Email: 307039176@qq.com
 */

package com.qiwumind.next.components.license.core.signature;

import com.qiwumind.next.components.license.autoconfigure.LicenseProperties;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
public class SignatureProvider {

    private final LicenseProperties properties;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Signature signature;

    public SignatureProvider(LicenseProperties properties) {
        this.properties = properties;
    }

    public void init() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        loadKeys();
        initSignature();
    }

    private void loadKeys() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        var signing = properties.getSigning();
        
        Path privateKeyPath = Paths.get(signing.getPrivateKeyPath());
        Path publicKeyPath = Paths.get(signing.getPublicKeyPath());

        if (Files.exists(privateKeyPath)) {
            String privateKeyPem = Files.readString(privateKeyPath);
            this.privateKey = decodePrivateKey(privateKeyPem);
            log.info("私钥加载成功");
        } else {
            log.warn("私钥文件不存在: {}", privateKeyPath);
        }

        if (Files.exists(publicKeyPath)) {
            String publicKeyPem = Files.readString(publicKeyPath);
            this.publicKey = decodePublicKey(publicKeyPem);
            log.info("公钥加载成功");
        } else {
            throw new IllegalStateException("公钥文件不存在: " + publicKeyPath);
        }
    }

    private void initSignature() throws NoSuchAlgorithmException, NoSuchProviderException {
        var signing = properties.getSigning();
        this.signature = Signature.getInstance(signing.getAlgorithm(), signing.getProvider());
    }

    public byte[] sign(byte[] data) throws SignatureException, InvalidKeyException {
        if (privateKey == null) {
            throw new IllegalStateException("私钥未加载，无法签名");
        }
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    public boolean verify(byte[] data, byte[] signatureBytes) throws SignatureException, InvalidKeyException {
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(signatureBytes);
    }

    public String signBase64(byte[] data) throws SignatureException, InvalidKeyException {
        byte[] signatureBytes = sign(data);
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    public boolean verifyBase64(byte[] data, String signatureBase64) throws SignatureException, InvalidKeyException {
        byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
        return verify(data, signatureBytes);
    }

    private PrivateKey decodePrivateKey(String pem) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String cleanPem = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                             .replace("-----END PRIVATE KEY-----", "")
                             .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(cleanPem);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(spec);
    }

    private PublicKey decodePublicKey(String pem) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String cleanPem = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                             .replace("-----END PUBLIC KEY-----", "")
                             .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(cleanPem);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }

    public void generateKeyPair(String privateKeyPath, String publicKeyPath) throws Exception {
        var signing = properties.getSigning();
        
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", signing.getProvider());
        keyGen.initialize(signing.getKeySize());
        KeyPair pair = keyGen.generateKeyPair();

        Path privPath = Paths.get(privateKeyPath);
        Path pubPath = Paths.get(publicKeyPath);

        Files.createDirectories(privPath.getParent());
        
        String privatePem = "-----BEGIN PRIVATE KEY-----\n" +
                Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded()) +
                "\n-----END PRIVATE KEY-----";
        Files.writeString(privPath, privatePem);

        String publicPem = "-----BEGIN PUBLIC KEY-----\n" +
                Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()) +
                "\n-----END PUBLIC KEY-----";
        Files.writeString(pubPath, publicPem);

        log.info("密钥对生成成功: {} / {}", privateKeyPath, publicKeyPath);
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }


    public static void main(String[] args) {

    }
}