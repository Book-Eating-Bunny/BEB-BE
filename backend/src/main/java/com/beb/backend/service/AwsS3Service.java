package com.beb.backend.service;

import com.beb.backend.exception.AwsS3Exception;
import com.beb.backend.exception.AwsS3ExceptionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3Service {

    private final S3Client awsS3Client;

    public void uploadFile(String bucketName, String filePath, MultipartFile multipartFile){
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .contentType(multipartFile.getContentType())
                    .build();
            RequestBody requestBody = RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize());

            awsS3Client.putObject(putObjectRequest, requestBody);
            log.info("Successfully uploaded file {} to S3 bucket {}", filePath, bucketName);
        } catch (Exception e) {
            log.error("Failed to upload file {} to S3 bucket {}", filePath, bucketName, e);
            throw new AwsS3Exception(AwsS3ExceptionInfo.S3_FILE_UPLOAD_FAILED);
        }
    }

    public void deleteFile(String bucketName, String filePath) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();

            awsS3Client.deleteObject(deleteObjectRequest);
            log.info("Successfully deleted file {} from S3 bucket {}", filePath, bucketName);
        } catch (Exception e) {
            log.error("Failed to delete file {} from S3 bucket {}", filePath, bucketName, e);
            throw new AwsS3Exception(AwsS3ExceptionInfo.S3_FILE_DELETE_FAILED);
        }
    }
}
