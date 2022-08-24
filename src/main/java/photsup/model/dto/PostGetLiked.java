package photsup.model.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PostGetLiked implements Notification {
    private final String receiver;
    private final String liker;
    private final long postId;

    @Override
    public String getReceiver() {
        return this.receiver;
    }
}
