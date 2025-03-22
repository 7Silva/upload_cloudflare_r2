package github.com._silva.upload_cloudflare_r2.cdn_r2.infrastructure.controller;

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

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            throw new InvalidFileException("Forneça um arquivo válido para ser enviado!");
        }

        String fileName = uploadToR2UseCase.execute(file);
        return ResponseEntity.ok(ApiResponse.success(fileName, "Arquivo enviado com sucesso"));
    }
}