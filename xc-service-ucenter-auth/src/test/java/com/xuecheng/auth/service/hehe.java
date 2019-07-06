package com.xuecheng.auth.service;

import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

public class hehe {
    public static void main(String[] args) {


        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuHxj2js81+JdCSvCnzkGuf3UIPxwzGro61Hrv5nLLQRsBRe8dqdZP/YEunLrS6mP14QAmnTqRw+K7Nhi8G4Dgy5K2HGhI4JpT7hpvTX5L1twxnFcHoVC15y4fXE7C45/w+G2JxLeE5KpBDodgQ1Y0D/Ue9Lj2ep+pGKmAb3reU28opZsVD41+fijPE5gc/021eIUyceoJdsboaSc9i3Z3L8Xj5jkU9s6bQR7iBBeeW+BeA1DTpDpna18472waW7RE72bK4H/goYZKPmIMuU92XXWRtAbpGkarHGKc2jKJkBmG8kff7ihekCDwzh/QRaRuVmREDYYJYcYZHQU7eZx8wIDAQAB-----END PUBLIC KEY-----";
        String s = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOiIxIiwidXNlcnBpYyI6bnVsbCwidXNlcl9uYW1lIjoiaXRjYXN0Iiwic2NvcGUiOlsiYXBwIl0sIm5hbWUiOiJ0ZXN0MDIiLCJ1dHlwZSI6IjEwMTAwMiIsImlkIjoiNDkiLCJleHAiOjE1NjIwNzUyNTEsImF1dGhvcml0aWVzIjpbInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfYmFzZSIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfZGVsIiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9saXN0IiwiY291cnNlX2dldF9iYXNlaW5mbyIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfcGxhbiIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2UiLCJjb3Vyc2VfZmluZF9saXN0IiwieGNfdGVhY2htYW5hZ2VyIiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9tYXJrZXQiLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX3B1Ymxpc2giLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX2FkZCJdLCJqdGkiOiJjMTE0YzE0My1mYjdmLTQxZjktOGZiNy0yN2JlYWZlN2Q5MGEiLCJjbGllbnRfaWQiOiJYY1dlYkFwcCJ9.IXlNoZnEyxe70mU7c3K5bTMaDBm59r9a-Ijb0H8n5DoJ5leplS0LolKo2-izCf6TXt5t1P8GxB2-XggP9UgTbdqwUYuhAE-NSTe55HsE_mH6gHOkSh_nyg5SHih0cD7dM10slP02ubt67OAbcjnZzXecXgLiASdley7rxoTJPuu4wo8-gkHs-tcIJworvKzwil13OHMjLHLNdhp8tWWkFqijOhUYL9Iht_o8zwYvJ4DG3h42Co9csnwIT73bymi2IyRn6xHUmGKJxdJqZmc7yHBXMrNIBHZdPuNPcA33vLKxg45qAMej6EBMOoPHteVthO7w7lVtcsQwevC7xkdvlg";
//        Jwt decode = JwtHelper.decode(s);
        Jwt jwt = JwtHelper.decodeAndVerify(s, new RsaVerifier(publickey));
        String claims = jwt.getClaims();
        System.out.println(claims);
    }
}
