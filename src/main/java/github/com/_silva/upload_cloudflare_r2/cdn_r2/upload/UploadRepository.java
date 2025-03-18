package github.com._silva.upload_cloudflare_r2.cdn_r2.upload;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UploadRepository extends MongoRepository<UploadEntity, String> {}