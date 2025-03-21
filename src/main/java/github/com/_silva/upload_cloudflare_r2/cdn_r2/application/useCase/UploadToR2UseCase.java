package github.com._silva.upload_cloudflare_r2.cdn_r2.application.useCase;

import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import github.com._silva.upload_cloudflare_r2.cdn_r2.domain.entity.UploadEntity;
import github.com._silva.upload_cloudflare_r2.cdn_r2.domain.repository.UploadRepository;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class UploadToR2UseCase {
    private static final String DEFAULT_FOLDER = "others/";

    @Autowired
    private S3Client s3Client;

    @Autowired
    private UploadRepository uploadRepository;

    @Value("${cloudflare.r2.bucket-name}")
    public String bucketName;

    public String execute(MultipartFile file) {
        String fileName = generateFileName(file.getOriginalFilename());
        String fileType = file.getContentType();
        String folderPrefix = getFolderPrefix(fileType);
        String storageKey = folderPrefix + fileName;

        UploadEntity uploadEntity = createUploadEntity(file, fileName, fileType);

        try {
            uploadFileToR2(storageKey, file);
            uploadEntity.setStatus("ENVIADO");
        } catch (IOException e) {
            handleUploadError(uploadEntity, e);
            return "Erro ao enviar o arquivo: " + e.getMessage();
        }

        uploadRepository.save(uploadEntity);
        return storageKey;
    }

    private String generateFileName(String originalFileName) {
    if (originalFileName == null) {
        return System.currentTimeMillis() + "-unnamed";
    }

    String normalized = Normalizer.normalize(originalFileName, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", ""); 

    String sanitizedFileName = normalized
            .replaceAll("[^a-zA-Z0-9.]", "_");
    return System.currentTimeMillis() + "_" + sanitizedFileName;
}

    private UploadEntity createUploadEntity(MultipartFile file, String fileName, String fileType) {
        UploadEntity uploadEntity = new UploadEntity();
        uploadEntity.setFileName(fileName);
        uploadEntity.setFileSize(file.getSize());
        uploadEntity.setFileType(fileType);
        uploadEntity.setCreatedAt(LocalDateTime.now());
        return uploadEntity;
    }

    private void uploadFileToR2(String key, MultipartFile file) throws IOException {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();
        RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
        s3Client.putObject(request, requestBody);
    }

    private String getFolderPrefix(String fileType) {
        if (fileType == null) {
            return DEFAULT_FOLDER;
        }

        String mimeTypeCategory = fileType.split("/")[0];
        return FileTypeFolder.fromMimeTypeCategory(mimeTypeCategory).getFolder();
    }

    private void handleUploadError(UploadEntity uploadEntity, IOException e) {
        uploadEntity.setStatus("ERRO");
        uploadEntity.setErrorMessage(e.getMessage());
    }

    private enum FileTypeFolder {
        IMAGE("image", "images/"),
        AUDIO("audio", "audios/"),
        VIDEO("video", "videos/"),
        DEFAULT("default", DEFAULT_FOLDER);

        private final String mimeTypeCategory;
        private final String folder;

        FileTypeFolder(String mimeTypeCategory, String folder) {
            this.mimeTypeCategory = mimeTypeCategory;
            this.folder = folder;
        }

        public String getFolder() {
            return folder;
        }

        public static FileTypeFolder fromMimeTypeCategory(String mimeTypeCategory) {
            for (FileTypeFolder type : values()) {
                if (type.mimeTypeCategory.equals(mimeTypeCategory)) {
                    return type;
                }
            }
            return DEFAULT;
        }
    }
}
