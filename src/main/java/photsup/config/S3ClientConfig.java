package photsup.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import java.net.URI;

@Configuration
public class S3ClientConfig {
    @Value("${AWS_KEY}")
    private String awsKey;

    @Value("${AWS_SECRET}")
    private String awsSecretKey;

    @Bean
    public S3Client getS3Client(){
        AwsCredentials credentials = AwsBasicCredentials.create(awsKey,awsSecretKey);

        return S3Client.builder()
                .credentialsProvider(() -> credentials)
                .endpointOverride(URI.create("https://storage.yandexcloud.net"))
                .region(Region.of("ru-central1"))
                .build();
    }
}
