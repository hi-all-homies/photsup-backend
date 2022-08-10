package photsup.service.post;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

public interface ImageService {
    String KEY = "key";
    String URL = "url";

    Map<String, String> storeImage(MultipartFile image) throws IOException;

    boolean deleteImage(String key);
}
