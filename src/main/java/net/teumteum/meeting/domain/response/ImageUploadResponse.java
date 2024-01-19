package net.teumteum.meeting.domain.response;

import lombok.Builder;

@Builder
public record ImageUploadResponse(
    String fileName,
    String originalFileName,
    String contentType,
    String filePath
) {

}
