package photsup.service.notification;

import org.springframework.web.socket.WebSocketSession;
import photsup.model.dto.Notification;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface NotificationService {
    Map<String, WebSocketSession> onlineUsers =
            new ConcurrentHashMap<>(32);

    default Map<String, WebSocketSession> getOnlineUsers(){
        return onlineUsers;
    }


    void sendNotification(Notification notification);
}