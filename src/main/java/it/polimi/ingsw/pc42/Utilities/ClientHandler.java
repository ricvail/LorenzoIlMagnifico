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

public class ClientHandler implements Runnable {

    private Board board;

    private Game game;
    private PrintWriter socketOut;

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
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void setGame(Game game) {
        this.game = game;
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
                String line = socketIn.nextLine();
                JsonNode jsonNode = null;
                try {
                    jsonNode = StreamMapper.fromStringToJson(line);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessageToClient(Strings.MessageTypes.CRITICAL_ERROR, "Unable to parse your move, please retry");
                    continue;
                }
                if (jsonNode.has("type")) {
                    String type = jsonNode.get("type").asText();
                    if (type.equalsIgnoreCase(Strings.MoveTypes.MOVE)){
                        try {
                            MoveManager.makeMove(board, player, jsonNode);
                            sendMessageToClient(Strings.MessageTypes.CONFIRM, "Your move was successful");
                            game.switchClient();
                            continue;
                        } catch (ActionAbortedException e){
                            if (!e.isValid){
                                sendMessageToClient(Strings.MessageTypes.ERROR, "Invalid move: "+
                                e.getMessage());
                                continue;
                            } else {
                                if (e.isComplete){
                                    sendMessageToClient(Strings.MessageTypes.CONFIRM, "Your move is valid");
                                } else {
                                    ObjectNode payload = JsonNodeFactory.instance.objectNode();
                                    payload.put("field", e.nextMoveField);
                                    payload.put("level", e.level);
                                    payload.set("options", e.availableChoices);
                                    sendMessageToClient(Strings.MessageTypes.WARNING, payload);
                                }
                                continue;
                            }
                        } catch (Exception e){
                            sendMessageToClient(Strings.MessageTypes.CRITICAL_ERROR, "Unable to parse your move, please retry");
                        }
                    }// end of MOVE type
                } else {
                    sendMessageToClient(Strings.MessageTypes.CRITICAL_ERROR, "Unable to parse your move, please retry");
                }

            } catch (Exception e){
                //Unhandled Exception
                e.printStackTrace();
                sendMessageToClient(Strings.MessageTypes.CRITICAL_ERROR, "Something went wrong, please repeat your move");
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

    public void sendMessageToClient(String type, String payload){
        JsonNodeFactory factory= JsonNodeFactory.instance;
        ObjectNode message = factory.objectNode();
        message.put("type", type);
        message.put("payload", payload);
        socketOut.print(message.toString());
        socketOut.flush();
    }
    public void sendMessageToClient(String type, JsonNode payload){
        sendMessageToClient(type, payload.toString());
    }
}
