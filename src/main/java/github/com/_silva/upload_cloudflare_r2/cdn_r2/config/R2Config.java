package github.com._silva.upload_cloudflare_r2.cdn_r2.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class R2Config {
    @Value("${cloudflare.r2.access-key-id}")
    private String accessKeyId;

    @Value("${cloudflare.r2.secret-access-key}")
    private String secretAccessKey;

    @Value("${cloudflare.r2.endpoint}")
    private String endpoint;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        S3Configuration s3Config = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .checksumValidationEnabled(false)
                .build();

        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(endpoint))
                .serviceConfiguration(s3Config)
                .region(Region.of("auto"))
                .build();
    }
}
