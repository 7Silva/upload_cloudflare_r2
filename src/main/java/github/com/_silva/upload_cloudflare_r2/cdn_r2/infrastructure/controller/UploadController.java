package github.com._silva.upload_cloudflare_r2.cdn_r2.infrastructure.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import github.com._silva.upload_cloudflare_r2.cdn_r2.application.useCase.UploadToR2UseCase;
import github.com._silva.upload_cloudflare_r2.cdn_r2.infrastructure.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/cdn/v1")
public class UploadController {
    @Autowired
    private UploadToR2UseCase uploadToR2UseCase;

    @GetMapping("/upload/health")
    public ResponseEntity<String> uploadHealth() {
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Forneça um arquivo para ser enviado!"));
            }

            String ipAddress = request.getRemoteAddr();

            String fileName = uploadToR2UseCase.execute(file, ipAddress);
            return ResponseEntity.ok(
                    ApiResponse.success(fileName, "Arquivo enviado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erro ao enviar o arquivo: " + e.getMessage()));
        }
    }
}