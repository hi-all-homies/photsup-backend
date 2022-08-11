package photsup.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Getter
@Setter
public class PostRequest {
    private Long postId;
    private String content;
    private MultipartFile image;
}
