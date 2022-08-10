package photsup.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Getter
@Setter
public class PostRequest {
    private Long authorId;
    private Long postId;
    private String content;
    private String imageUrl;
    private String awsKey;
    private MultipartFile image;
}
