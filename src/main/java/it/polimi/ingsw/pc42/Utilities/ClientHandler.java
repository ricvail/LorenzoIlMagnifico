package it.polimi.ingsw.pc42.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.Game;
import it.polimi.ingsw.pc42.Control.MoveManager;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientHandler extends MessageSender implements Runnable {

    private Board board;
    private Logger logger;
    private boolean isConnected;
    private boolean isInGame;
    private Game game;
    private Server server;
    private Player player;
    private Socket socket;
    Scanner socketIn;

    public void setBoard(Board b) {
        this.board = b;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public ClientHandler(Socket socket, Server server) {
        this.server=server;
        this.socket = socket;
        isInGame=false;
        isConnected=true;
        logger= LogManager.getLogger();
    }

    @Override
    public void sendMessage(String type, JsonNode payload) {
        if (isConnected) super.sendMessage(type, payload);
    }

    public void setGame(Game game) {
        this.game = game;
        isInGame = true;
    }

    public void run() {
        try {
            socketIn = new Scanner(socket.getInputStream());
            socketOut = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            logger.error(e);
            isConnected=false;
            return;
        }
        boolean continueLoop = true;

        //BEGIN MAIN LOOP____________________________________________________________________
        while (continueLoop) {
            try {
                loopBody();
            } catch (Exception e){
                logger.error(e);
                sendCriticalErrorMessage();
            }
            if (!isConnected) continueLoop=false;
        }
        //END MAIN LOOP____________________________________________________________________
        try {
            socketIn.close();
            socketOut.close();
            socket.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public void loopBody(){
        String line = "";
        try {
            line = socketIn.next();
        } catch(NoSuchElementException e){
            logger.error(e);
            isConnected=false;
            return;
        }
        JsonNode jsonNode = null;
        try {
            jsonNode = StreamMapper.fromStringToJson(line);
        } catch (Exception e) {
            logger.error(e);
            sendCriticalErrorMessage();
            return;
        }
        if (jsonNode.has("type")) {
            String type = jsonNode.get("type").asText();
            if (type.equalsIgnoreCase(Strings.MoveTypes.NEWGAME)&&!isInGame){
                server.addClientToLobby(this);
                return;
            } else if (type.equalsIgnoreCase(Strings.MoveTypes.JOINGAME)&&!isInGame){
                try {
                    Game g= server.getGame(jsonNode.get("payload").get("id").asInt());
                    ClientHandler cli =g.getClient(Player.PlayerColor.fromString(jsonNode.get("payload").get("color").asText()));
                    if (cli.isConnected){
                        throw new myException();
                    } else {
                        g.replaceClient(this, cli);
                    }
                } catch (Exception e) {
                    logger.error(e);
                    ObjectNode p = JsonNodeFactory.instance.objectNode();
                    sendMessage(Strings.MessageTypes.GAME_NOT_FOUND, p);
                }
            } else if (type.equalsIgnoreCase(Strings.MoveTypes.MOVE)){
                if (isInGame){
                    parseMoveType(jsonNode);
                } else {
                    sendCriticalErrorMessage();
                }
                return;
            } else if (type.equalsIgnoreCase(Strings.MoveTypes.QUERY)){
                ObjectNode payload = JsonNodeFactory.instance.objectNode();
                payload.set("board", board.generateJsonDescription());
                sendMessage(Strings.MessageTypes.UPDATE, payload);
            }else{
                sendCriticalErrorMessage();
            }
        } else {
            sendCriticalErrorMessage();
        }
    }


    public void parseMoveType(JsonNode jsonNode){
        try {
            MoveManager.makeMove(board, player, jsonNode.get("payload"));
            game.switchClient();
            ObjectNode payload = JsonNodeFactory.instance.objectNode();
            payload.put("playerCompleted", player.getColor().getPlayerColorString());
            game.broadcastUpdate(Strings.MessageTypes.MOVE_SUCCESSFUL, payload);
            if (board.isGameOver()){
                game.broadcastUpdate(Strings.MessageTypes.GAME_OVER);
            }
            return;
        } catch (ActionAbortedException e){
            logger.info(e);
            if (!e.isValid){
                ObjectNode payload = JsonNodeFactory.instance.objectNode();
                payload.put("message", e.getMessage());
                sendMessage(Strings.MessageTypes.MOVE_INVALID, payload);
                return;
            } else {
                if (e.isComplete){
                    ObjectNode payload = JsonNodeFactory.instance.objectNode();
                    sendMessage(Strings.MessageTypes.MOVE_COMPLETE, payload);
                } else {
                    ObjectNode payload = JsonNodeFactory.instance.objectNode();
                    payload.put("field", e.nextMoveField);
                    payload.put("level", e.level);
                    payload.put("isCardChoice", e.isCardChoice);
                    if (e.isCardChoice) payload.put("card", e.card);
                    payload.set("options", e.availableChoices);
                    sendMessage(Strings.MessageTypes.MOVE_INCOMPLETE, payload);
                }
                return;
            }
        } catch (Exception e){
            logger.error(e);
            sendCriticalErrorMessage();
        }
    }

    public void sendCriticalErrorMessage(){

        ObjectNode payload = JsonNodeFactory.instance.objectNode();
        sendMessage(Strings.MessageTypes.CRITICAL_ERROR, payload);
    }
}
