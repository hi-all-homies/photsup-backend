package photsup.service.post;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import java.io.IOException;
import java.util.Map;


@RequiredArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {
    private final S3Client s3Client;

    @Value("${bucket.name:photsup}")
    private String bucket;

    @Override
    public Map<String, String> storeImage(MultipartFile image) throws IOException {

        final String key = System.currentTimeMillis() + image.getOriginalFilename();

        s3Client.putObject(builder ->
                builder.bucket(bucket)
                        .key(key)
                        .contentType(image.getContentType())
                        .build(), RequestBody.fromBytes(image.getBytes()));


        String url = s3Client.utilities()
                .getUrl(builder -> builder.bucket(bucket)
                        .key(key)
                        .build())
                .toExternalForm();

        return Map.of(KEY, key, URL, url);
    }

    @Override
    public void deleteImage(String key) {
        this.s3Client.deleteObject(
                builder -> builder.bucket(bucket)
                        .key(key)
                        .build());
    }
}
