package photsup.model.dto;

import lombok.Builder;
import photsup.model.entity.User;
import java.util.Date;

@Builder
public class PostSummary {
    private final Long postId;
    private final String content;
    private final String imageUrl;
    private final String awsKey;
    private final User author;
    private final Date created;
    private final int likeCount;
    private final boolean meLiked;
}
