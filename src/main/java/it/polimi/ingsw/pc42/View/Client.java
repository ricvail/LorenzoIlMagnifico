package it.polimi.ingsw.pc42.View;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.pc42.Model.Player;
import it.polimi.ingsw.pc42.Utilities.MessageSender;
import it.polimi.ingsw.pc42.Utilities.StreamMapper;
import it.polimi.ingsw.pc42.Utilities.Strings;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Client extends MessageSender {
    private Socket socket;
    private final static int PORT = 3000;
    private final static String IP="127.0.0.1";
    private JsonNode board;

    private Scanner socketIn;

    private boolean waitingForResponse;

    public void setSocket (Socket socket){
        this.socket=socket;
    }

    public Socket getSocket() {
        return socket;
    }
    boolean continueLoop;
    boolean isInGame;
    boolean isMyTurn;
    boolean moveComplete;
    JsonNode currentMove;
    ArrayList<moveBuildingState> moveStack;
    public void startClient() throws IOException {
        Socket socket = new Socket(IP, PORT);
        System.out.println("Connection Established");
        socketIn= new Scanner((socket.getInputStream()));
        socketOut=new PrintWriter(socket.getOutputStream());
        ExecutorService executor = Executors.newFixedThreadPool(2);
        continueLoop = true;
        isInGame=false;
        waitingForResponse=false;
        isMyTurn=false;
        moveComplete=false;
        executor.submit(userInputHandler);
        executor.submit(serverResponseHandler);
    }
    Runnable serverResponseHandler = ()->{
        while (continueLoop){
            String line = socketIn.nextLine();
            JsonNode payload = null;
            String type = "";
            try {
                JsonNode j = StreamMapper.fromStringToJson(line);
                payload = j.get("payload");
                type = j.get("type").asText();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            if (type.equalsIgnoreCase(Strings.MessageTypes.ADDED_TO_LOBBY)){
                System.out.println("You have been added to the lobby. Current number of players: " +
                        (4-payload.get("counter").asInt()));
            }

            if (type.equalsIgnoreCase(Strings.MessageTypes.OTHER_PLAYER_JOINED_LOBBY)){
                System.out.println("Another player has joined the lobby. Current number of players: " +
                        (4-payload.get("counter").asInt()));
            }

            if (type.equalsIgnoreCase(Strings.MessageTypes.GAMESTARTED)){
                System.out.println("Game started. You are player "+payload.get("color").asText().toUpperCase() + ". Press H for a list of commands");
                isInGame=true;
                waitingForResponse=false;
                moveStack=new ArrayList<moveBuildingState>();
            }

            if (type.equalsIgnoreCase(Strings.MessageTypes.MOVE_TIMEOUT)){
                if (isMyTurn){
                    System.out.println("Your time is up! It's now next player's turn");
                    isMyTurn=false;
                } else {
                    String timedOut = payload.get("timedOutPlayer").asText();
                    System.out.println("Player "+ timedOut.toUpperCase() + " has timed out.");
                }

            }

            if (type.equalsIgnoreCase(Strings.MessageTypes.MOVE_INCOMPLETE)){
                System.out.println(payload.get("field").asText());//TODO
                moveStack.add(0, new moveBuildingState(currentMove.deepCopy(),payload));
                waitingForResponse=false;
            }
            if (type.equalsIgnoreCase(Strings.MessageTypes.MOVE_COMPLETE)){
                System.out.println("Press E to execute move");
                moveComplete=true;
                waitingForResponse=false;
            }
            if (type.equalsIgnoreCase(Strings.MessageTypes.MOVE_INVALID)){
                System.out.println("Invalid move");
                moveComplete=false;
                waitingForResponse=false;
            }
            if (type.equalsIgnoreCase(Strings.MessageTypes.MOVE_SUCCESSFUL)){
                if (isMyTurn){
                    isMyTurn=false;
                    waitingForResponse=false;
                    resetMove();
                    System.out.println("Move successfully executed! It's now next player's turn");
                }else{
                    String playerCompleted = payload.get("playerCompleted").asText();
                    System.out.println("Player "+ playerCompleted.toUpperCase() + " has completed a move.");
                }

            }

            try {
                if (payload.has("board")){
                    board = payload.get("board");
                }
                if (payload.has("yourTurn")&&payload.get("yourTurn").asBoolean()){
                    if (!isMyTurn){
                        isMyTurn=true;
                        System.out.println("It's your turn. Press M to begin a new move");

                    }
                }
            }catch (Exception e){

            }

        }
    };

    public void resetMove(){
        moveComplete=false;
        moveStack=new ArrayList<moveBuildingState>();
    }

    Runnable userInputHandler= ()->{
        Scanner stdin = new Scanner(System.in);
        System.out.println("Write N to join a new game or R to reconnect to a previous one");
        while (continueLoop){
            String inputLine = stdin.next();
            if (!waitingForResponse) {
                if (!isInGame) {
                    if (inputLine.equalsIgnoreCase("n")) {
                        ObjectNode payload = JsonNodeFactory.instance.objectNode();
                        sendMessage(Strings.MoveTypes.NEWGAME, payload);
                        waitingForResponse=true;
                    } else if (inputLine.equalsIgnoreCase("r")) {
                        //read id
                        //read color
                        //send
                        //wait for response
                    } else{
                        System.out.println("Unable to parse input. Please retry.");
                    }
                } else {
                    gameMoveLoop(inputLine);
                }
            }
        }
    };



    public void gameMoveLoop(String in){
        if (in.equalsIgnoreCase("M")){
            currentMove=MoveBuilder.createBlankMove(true);
            waitingForResponse = true;
            sendMessage(Strings.MoveTypes.MOVE, currentMove);
        } else if (in.equalsIgnoreCase("E")){
            if (moveComplete){
                waitingForResponse = true;
                MoveBuilder.setChecking((ObjectNode)currentMove, false);
                sendMessage(Strings.MoveTypes.MOVE, currentMove);
            } else {
                System.out.println("Your move is not yet complete");
            }
        }  else if(moveStack.size()>0){
            MoveBuilder.addFiled((ObjectNode)currentMove, (ObjectNode)moveStack.get(0).serverResponse, in);waitingForResponse = true;
            sendMessage(Strings.MoveTypes.MOVE, currentMove);
            waitingForResponse= true;
        }
    }


    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.startClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class moveBuildingState{
        public moveBuildingState(JsonNode move, JsonNode serverResponse) {
            this.move = move;
            this.serverResponse = serverResponse;
        }
        public JsonNode move, serverResponse;
    }
}
