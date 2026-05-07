# 🔐 JCipherTools

A full‑stack demo application built with **Spring Boot** (backend) and **Angular** (frontend) to showcase secure encryption and decryption workflows using modern cryptographic algorithms.

## 💡Rationale

In modern professional projects, it is common to encounter scenarios where sensitive data must be shared over the internet. To ensure confidentiality and integrity, such data requires protection through robust encryption and decryption mechanisms. Implementing a combination of cryptographic algorithms not only secures information against unauthorized access but also demonstrates adherence to industry best practices for handling sensitive data. So, this project demonstrate some of these algorithms.

---

## 🚀 Features
- **AES‑CBC‑256**: Symmetric encryption with initialization vector (IV) for confidentiality  
- **AES‑GCM‑256**: Authenticated encryption providing both confidentiality and integrity  
- **RSA-2048**: Asymmetric encryption for secure key exchange and digital signatures  
- **REST API**: Backend endpoints for encrypting/decrypting strings  
- **Angular UI**: Interactive interface to test encryption/decryption  

---

## 🛠️ Tech Stack
- **Backend**: Spring Boot, Java, Maven/Gradle  
- **Frontend**: Angular, TypeScript, Angular Material  
- **Security Libraries**: Java Cryptography Extension (JCE) .
- **Containerization**: Docker (planned)  

---

## 📂 Project Structure

```text
jciphertools/
│
├── jciphertools-backend/                # Spring Boot project
│   ├── src/main/java/...   # Controllers, services, crypto utils
│   ├── src/test/java/...   # Unit tests
│   └── pom.xml             # Maven config
│
├── jciphertools-frontend/               # Angular project
│   ├── src/app/...         # Components, services
│   └── package.json        # Dependencies
│
└── README.md               # Project overview
```

