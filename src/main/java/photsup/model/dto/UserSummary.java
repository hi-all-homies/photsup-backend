package photsup.model.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserSummary {
    private final Long userId;
    private final String username;
    private final String uniqueKey;
    private final String avatarUrl;
    private final long postsSent;
    private final String status;
    private final long likesReceived;
}
