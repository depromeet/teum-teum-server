package net.teumteum.meeting.infra;

import lombok.RequiredArgsConstructor;
import net.teumteum.meeting.domain.ImageUpload;
import net.teumteum.meeting.domain.response.ImageUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageUploadService implements ImageUpload {

    private final S3Client s3Client;
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Override
    public ImageUploadResponse upload(MultipartFile file, String path) {
        String originalFilename = Optional.ofNullable(file.getOriginalFilename())
                .orElseThrow(() -> new IllegalArgumentException("파일 이름이 없습니다."));
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString();
        String destination = path + "/" + fileName + fileExtension;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(destination)
                .build();

        try (var inputStream = file.getInputStream()) {
            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
        }

        return ImageUploadResponse.builder()
                .fileName(fileName)
                .originalFileName(originalFilename)
                .contentType(file.getContentType())
                .filePath(destination)
                .build();
    }
}
