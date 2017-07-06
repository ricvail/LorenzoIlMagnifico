package it.polimi.ingsw.pc42.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.Game;
import it.polimi.ingsw.pc42.Control.MoveManager;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.Player;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler extends MessageSender implements Runnable {

    private Board board;


    private boolean isInGame;
    private Game game;
    private Server server;
    public void setBoard(Board b) {
        this.board = b;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    private Player player;

    private Socket socket;
    Scanner socketIn;
    public ClientHandler(Socket socket, Server server) {
        this.server=server;
        this.socket = socket;
        isInGame=false;
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
            e.printStackTrace();
        }
        boolean continueLoop = true;

        //BEGIN MAIN LOOP____________________________________________________________________
        while (continueLoop) {
            try {
                loopBody();
            } catch (Exception e){
                //Unhandled Exception
                e.printStackTrace();
                sendCriticalErrorMessage();
            }
        }
        //END MAIN LOOP____________________________________________________________________
        try {
            socketIn.close();
            socketOut.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loopBody(){
        String line = socketIn.nextLine();
        JsonNode jsonNode = null;
        try {
            jsonNode = StreamMapper.fromStringToJson(line);
        } catch (Exception e) {
            e.printStackTrace();
            sendCriticalErrorMessage();
            return;
        }
        if (jsonNode.has("type")) {
            String type = jsonNode.get("type").asText();
            if (type.equalsIgnoreCase(Strings.MoveTypes.NEWGAME)){
                server.addClientToLobby(this);
                return;
            } else if (type.equalsIgnoreCase(Strings.MoveTypes.JOINGAME)){
                //TODO
            } else if (type.equalsIgnoreCase(Strings.MoveTypes.MOVE)){
                if (isInGame){
                    parseMoveType(jsonNode);
                } else {
                    sendCriticalErrorMessage();
                }
                return;
            } else if (type.equalsIgnoreCase(Strings.MoveTypes.QUERY)){
                sendMessage(Strings.MessageTypes.UPDATE, board.generateJsonDescription());
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
            game.broadcastUpdate(Strings.MessageTypes.MOVE_SUCCESSFUL);
            return;
        } catch (ActionAbortedException e){
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
                    payload.set("options", e.availableChoices);
                    sendMessage(Strings.MessageTypes.MOVE_INCOMPLETE, payload);
                }
                return;
            }
        } catch (Exception e){
            sendCriticalErrorMessage();
        }
    }

    public void sendCriticalErrorMessage(){

        ObjectNode payload = JsonNodeFactory.instance.objectNode();
        sendMessage(Strings.MessageTypes.CRITICAL_ERROR, payload);
    }
}
