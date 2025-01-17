package com.beb.backend.service;

import com.beb.backend.exception.ProfileImgException;
import com.beb.backend.exception.ProfileImgExceptionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileImgService {

    private final AwsS3Service awsS3Service;

    @Value("${cloud.aws.region}")
    private String REGION;

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String BUCKET_NAME = "test-beb-bucket-01";

    public static final String DEFAULT_PROFILE_IMG_KEY = "profile-images/default";

    private void validateProfileImgFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ProfileImgException(ProfileImgExceptionInfo.NULL_OR_EMPTY_FILE);
        }
        validateProfileImgSize(file.getSize());
        validateProfileImgExtension(file.getOriginalFilename());
    }

    private void validateProfileImgExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new ProfileImgException(ProfileImgExceptionInfo.INVALID_FILE_EXTENSION);
        }

        String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ProfileImgException(ProfileImgExceptionInfo.INVALID_FILE_EXTENSION);
        }
    }

    private void validateProfileImgSize(long fileSize) {
        if (fileSize > MAX_FILE_SIZE) {
            throw new ProfileImgException(ProfileImgExceptionInfo.FILE_SIZE_LIMIT_EXCEEDED);
        }
    }

    public String generateProfileImgUrl(String profileImgKey) {
        if (profileImgKey == null) {
            profileImgKey = DEFAULT_PROFILE_IMG_KEY;
        }
        return "https://" + BUCKET_NAME + ".s3." + REGION + ".amazonaws.com/" + profileImgKey;
    }

    public String uploadProfileImage(MultipartFile profileImg, long memberId) {
        // 회원 가입 시, 회원 정보 수정 시 필요
        // 회원 가입 시 -> memberId가 나와야 profileImgKey 설정 가능
        // 회원 정보 수정 시 -> 파일이 없으면 기존 파일 있었을 경우 삭제해야 함.
        validateProfileImgFile(profileImg);

        String profileImgKey = "profile-images/" + memberId;
        awsS3Service.uploadFile(BUCKET_NAME, profileImgKey, profileImg);
        return profileImgKey;   // profileImgKey를 Member에 저장. 프론트 측에 제공할 때에는 URL로 바꿔서 제공
    }

    public String deleteProfileImage(String profileImgKey) {
        awsS3Service.deleteFile(BUCKET_NAME, profileImgKey);
        return DEFAULT_PROFILE_IMG_KEY;
    }
}
