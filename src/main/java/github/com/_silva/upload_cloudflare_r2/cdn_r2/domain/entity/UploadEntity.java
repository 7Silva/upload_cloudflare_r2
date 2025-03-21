package github.com._silva.upload_cloudflare_r2.cdn_r2.domain.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "upload_logs")
public class UploadEntity {
    @Id
    private String id;
    
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String status;
    private String errorMessage;
    private String ipAddress;
    private LocalDateTime createdAt;
}
