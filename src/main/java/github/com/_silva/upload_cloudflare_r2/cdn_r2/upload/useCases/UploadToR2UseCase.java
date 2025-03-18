package github.com._silva.upload_cloudflare_r2.cdn_r2.upload.useCases;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import github.com._silva.upload_cloudflare_r2.cdn_r2.upload.UploadEntity;
import github.com._silva.upload_cloudflare_r2.cdn_r2.upload.UploadRepository;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class UploadToR2UseCase {
    @Autowired
    private S3Client s3Client;

    @Autowired
    private UploadRepository uploadRepository;

    @Value("${cloudflare.r2.bucket-name}")
    public String bucketName;

    public String execute(MultipartFile file) {
        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();

        UploadEntity uploadEntity = new UploadEntity();
        uploadEntity.setFileName(fileName);
        uploadEntity.setFileSize(file.getSize());
        uploadEntity.setFileType(file.getContentType());
        uploadEntity.setCreatedAt(LocalDateTime.now());

        try {
            uploadFileToR2(fileName, file);

            uploadEntity.setStatus("ENVIADO");
        } catch (IOException e) {
            uploadEntity.setStatus("ERRO");
            uploadEntity.setErrorMessage(e.getMessage());
            return "Erro ao enviar o arquivo: " + e.getMessage();
        }
        
        uploadRepository.save(uploadEntity);
        return fileName;
    }

    private void uploadFileToR2(String fileName, MultipartFile file) throws IOException {
        PutObjectRequest request = PutObjectRequest
                .builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
        s3Client.putObject(request, requestBody);
    }
}
