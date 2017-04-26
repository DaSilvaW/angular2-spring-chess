package com.dynadrop.chess;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.google.gson.Gson;
import com.dynadrop.chess.model.Game;
import com.dynadrop.chess.model.Board;
import com.dynadrop.chess.model.Player;
import com.dynadrop.chess.websocket.bean.Message;
import java.util.ArrayList;
import java.io.IOException;


@Component
public class GameHandler extends TextWebSocketHandler {

    //TODO REFACTOR ALL THIS SHIT
    static ArrayList<WebSocketSession> sessions;
    static ArrayList<Game> games;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
      if (games == null) {
        System.out.println("game list null, initializing");
        games = new ArrayList<Game>();
      }
      System.out.println("Connection established");
      if (this.sessions == null) {
        this.sessions = new ArrayList<WebSocketSession>();
      }
      this.sessions.add(session);
      //this.session = session;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage)
            throws Exception {
        try {
          System.out.println(textMessage.getPayload());//.substring(1, textMessage.getPayload().length()-1)
          Gson gson = new Gson();
          Message message = gson.fromJson(textMessage.getPayload(), Message.class);
          if ("CLOSE".equalsIgnoreCase(textMessage.getPayload())) {
            //session.close();
          }else if ("newGame".equals(message.action)) {
            Player player = new Player();
            Game game = new Game(player, message.gameUUID);
            games.add(game);
            sendMessageToAllSessions(new TextMessage(gson.toJson(game)));
            //session.sendMessage(new TextMessage(gson.toJson(game)));
          }else if ("move".equals(message.action)){
            Game game = this.getGameByUUID(message.gameUUID);
            Board board = game.getBoard();
            if (!board.movePiece(message.movement)){
              System.out.println("invalid move");
            }
            sendMessageToAllSessions(new TextMessage(gson.toJson(game)));
          }else if ("requestUpdate".equals(message.action)) {
            Game game = this.getGameByUUID(message.gameUUID);
            //session.sendMessage(new TextMessage(gson.toJson(game)));
            sendMessageToAllSessions(new TextMessage(gson.toJson(game)));
          }
        }catch (Exception e) {
          e.printStackTrace();
        }
    }

    private void sendMessageToAllSessions(TextMessage message) throws IOException {
      //TODO send only for sessions with same geme uuid
      for (WebSocketSession session: this.sessions) {
        session.sendMessage(message);
      }
    }

    private Game getGameByUUID(String uuid) {
      System.out.println("searching game with uuid: " + uuid);
      for(Game game: this.games) {
        System.out.println("game: " + game.getUUID());
        if (game.getUUID().equals(uuid)) {
          System.out.println("game found.");
          return game;
        }
      }
      return null;
    }

}
