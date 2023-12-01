package com.technology.user.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileHelper {
    private static final Logger logger = LoggerFactory.getLogger(FileHelper.class);

    public static String calculateHash(MultipartFile file) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            InputStream is = file.getInputStream();
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = is.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            byte[] digest = md.digest();
            StringBuilder hash = new StringBuilder();
            for (byte b : digest) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("NO SUCH ALGORITHM EXCEPTION OCCURRED DURING HASH CALCULATION");
        } catch (IOException e) {
            logger.error("IO EXCEPTION OCCURRED DURING HASH CALCULATION");
        }
        return "";
    }

}
