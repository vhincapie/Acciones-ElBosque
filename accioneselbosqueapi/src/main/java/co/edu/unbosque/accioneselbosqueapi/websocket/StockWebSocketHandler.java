package co.edu.unbosque.accioneselbosqueapi.websocket;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.StockDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class StockWebSocketHandler extends TextWebSocketHandler {

    private List<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    public void sendStockUpdates(List<StockDTO> stocks) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(stocks)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

}