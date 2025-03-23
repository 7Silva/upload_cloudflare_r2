package github.com._silva.upload_cloudflare_r2.cdn_r2.infrastructure.controller;

import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import github.com._silva.upload_cloudflare_r2.cdn_r2.application.useCase.UploadToR2UseCase;
import github.com._silva.upload_cloudflare_r2.cdn_r2.domain.exceptions.InvalidFileException;
import github.com._silva.upload_cloudflare_r2.cdn_r2.infrastructure.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/cdn/v1")
public class UploadController {
    @Autowired
    private UploadToR2UseCase uploadToR2UseCase;

    private static final long MAX_FILE_SIZE = 30 * 1024 * 1024; // 30MB

    private static final Set<String> VALID_MIME_TYPES = ConcurrentHashMap.newKeySet();
    private static final Map<String, Set<String>> MIME_TYPE_CATEGORIES = new ConcurrentHashMap<>();

    static {
        MIME_TYPE_CATEGORIES.put("image", Set.of(
                "image/jpeg",
                "image/png",
                "image/gif",
                "image/webp",
                "image/svg+xml"));

        MIME_TYPE_CATEGORIES.put("video", Set.of(
                "video/mp4",
                "video/webm",
                "video/mpeg",
                "video/quicktime"));

        MIME_TYPE_CATEGORIES.put("audio", Set.of(
                "audio/mpeg",
                "audio/wav",
                "audio/ogg",
                "audio/mp4"));

        MIME_TYPE_CATEGORIES.put("application", Set.of(
                "application/pdf"));

        MIME_TYPE_CATEGORIES.values()
                .forEach(VALID_MIME_TYPES::addAll);
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> upload(@RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        validateFile(file);
        String fileName = uploadToR2UseCase.execute(file);
        return ResponseEntity.ok(ApiResponse.success(fileName, "Arquivo enviado com sucesso"));
    }

    private boolean isValidContentType(String contentType) {
        if (contentType == null) {
            return false;
        }

        if (VALID_MIME_TYPES.contains(contentType)) {
            return true;
        }

        String category = contentType.split("/")[0];
        Set<String> validTypesForCategory = MIME_TYPE_CATEGORIES.get(category);

        return validTypesForCategory != null && validTypesForCategory.contains(contentType);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Forneça um arquivo válido para ser enviado!");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException("O arquivo excede o tamanho máximo de 30MB!");
        }

        String contentType = file.getContentType();
        if (contentType == null || !isValidContentType(contentType)) {
            throw new InvalidFileException("Tipo de arquivo não suportado!");
        }
    }
}