package photsup.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import photsup.oauth2.UserPrincipal;

@Component
@RequiredArgsConstructor
public class NotificationHandler extends TextWebSocketHandler {
    private final NotificationService notificationService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        var oauthToken =(OAuth2AuthenticationToken) session.getPrincipal();
        var userPrincipal = (UserPrincipal)oauthToken.getPrincipal();

        this.notificationService.getOnlineUsers()
                .put(userPrincipal.getUniqueKey(), session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        var oauthToken =(OAuth2AuthenticationToken) session.getPrincipal();
        var userPrincipal = (UserPrincipal)oauthToken.getPrincipal();

        this.notificationService.getOnlineUsers()
                .remove(userPrincipal.getUniqueKey());
    }
}
