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
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        String fileName = uploadToR2UseCase.execute(file);
           return ResponseEntity.ok()
                    .body("Arquivo enviado com sucesso: " + fileName);
    }
}