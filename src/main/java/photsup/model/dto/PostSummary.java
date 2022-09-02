package photsup.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import photsup.model.entity.Comment;
import photsup.model.entity.User;
import java.time.LocalDateTime;
import java.util.Set;


@Builder
@Getter
public class PostSummary {
    private final Long postId;
    private final String content;
    private final String imageUrl;
    private final User author;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private final LocalDateTime created;
    private final int likeCount;
    private final boolean meLiked;
    private final int commentCount;

    private final Set<Comment> comments;
}
