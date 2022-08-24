package photsup.service.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import photsup.model.dto.Notification;
import java.io.IOException;
import java.util.function.Consumer;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void sendNotification(final Notification notification) {
        onlineUsers.entrySet().stream()
                .filter(entry -> entry.getKey().equals(notification.getReceiver()))
                .map(entry -> entry.getValue())
                .forEach(send(notification));
    }

   private Consumer<WebSocketSession> send(Notification notification){
        return session -> {
            try{
                String payload = this.objectMapper.writeValueAsString(notification);
                session.sendMessage(new TextMessage(payload));
            }
            catch (IOException e){
                log.error(e.getMessage());
            }
        };
   }
}
