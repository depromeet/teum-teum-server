package net.teumteum.meeting.domain;

import net.teumteum.meeting.domain.response.ImageUploadResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ImageUpload {

    ImageUploadResponse upload(MultipartFile file, String path);

}
