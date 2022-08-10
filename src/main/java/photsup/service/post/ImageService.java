package photsup.service.post;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

public interface ImageService {

    Map<String, String> storeImage(MultipartFile image) throws IOException;

    boolean deleteImage(String key);
}
