package com.technology.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AWSService {
    @Value("${aws.bucket.name}")
    private String bucketName;
    private final AmazonS3 s3Client;

    public String uploadFile(MultipartFile file) {
        File fileObject = convertMultipartFileToFile(file);
        String fileName = System.currentTimeMillis() + "_"+file.getOriginalFilename();
        PutObjectResult result = s3Client.putObject(
                new PutObjectRequest(bucketName,fileName,fileObject));
        String fileUrl = s3Client.getUrl(bucketName,fileName).toString();
        fileObject.delete();
        return fileUrl;
    }

    public void deleteFile(String fileUrl){
        try {
            URL url = new URL(fileUrl);
            String host = url.getHost();
            String bucket = host.substring(0,host.indexOf("."));
            String key = url.getPath().substring(1);
            s3Client.deleteObject(bucketName,key);
        } catch (MalformedURLException e) {
            log.error("ERROR PARSING URL",e);
        }
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);){
            fileOutputStream.write(multipartFile.getBytes());
        } catch (FileNotFoundException e) {
            log.error("ERROR FINDING MULTIPART FILE TO RILE", e);
        } catch (IOException e) {
            log.error("ERROR CONVERTING MULTIPART FILE TO RILE", e);
        }
        return file;
    }
}
