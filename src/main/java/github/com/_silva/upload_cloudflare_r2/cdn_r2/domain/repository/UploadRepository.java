package github.com._silva.upload_cloudflare_r2.cdn_r2.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import github.com._silva.upload_cloudflare_r2.cdn_r2.domain.entity.UploadEntity;

public interface UploadRepository extends MongoRepository<UploadEntity, String> {}