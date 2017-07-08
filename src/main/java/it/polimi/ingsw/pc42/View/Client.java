package it.polimi.ingsw.pc42.View;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.pc42.Utilities.GameInitializer;
import it.polimi.ingsw.pc42.Utilities.MessageSender;
import it.polimi.ingsw.pc42.Utilities.StreamMapper;
import it.polimi.ingsw.pc42.Utilities.Strings;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
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
    String userQuery;
    JsonNode currentMove;
    ArrayList<moveBuildingState> moveStack;
    public String playerColor;
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
                System.out.println("Game started. ID: "+payload.get("id").asInt()+"\nYou are player "+payload.get("color").asText().toUpperCase() + ". Press H for a list of commands");
                playerColor=payload.get("color").asText().toUpperCase();
                isInGame=true;
                waitingForResponse=false;
                moveStack=new ArrayList<moveBuildingState>();
            }
            if (type.equalsIgnoreCase(Strings.MessageTypes.GAME_NOT_FOUND)){
                System.out.println("Can't join this game");
                System.out.println("Write N to join a new game or R to reconnect to a previous one");
                isInGame=false;
                waitingForResponse=false;
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
                if (payload.get("field").asText().equalsIgnoreCase("immediateEffect")){
                    MoveBuilder.addInner((ObjectNode)currentMove, (ObjectNode)payload);
                    waitingForResponse = true;
                    sendMessage(Strings.MoveTypes.MOVE, currentMove);
                } else if (payload.get("field").asText().equalsIgnoreCase("cardChoices")){
                    MoveBuilder.addCardChoice((ObjectNode)currentMove, (ObjectNode)payload);
                    waitingForResponse = true;
                    sendMessage(Strings.MoveTypes.MOVE, currentMove);
                } else {
                    printNextFieldInstructions(payload);
                    moveStack.add(0, new moveBuildingState(currentMove.deepCopy(), payload));
                    waitingForResponse = false;
                }
            }
            if (type.equalsIgnoreCase(Strings.MessageTypes.MOVE_COMPLETE)){
                System.out.println("Press E to execute move");
                moveComplete=true;
                waitingForResponse=false;
            }
            if (type.equalsIgnoreCase(Strings.MessageTypes.MOVE_INVALID)){
                System.out.println("Invalid move: "+payload.get("message"));
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

            if (type.equalsIgnoreCase(Strings.MessageTypes.CRITICAL_ERROR)){
                System.out.println("Error, invalid input");
                waitingForResponse=false;
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
            if (type.equalsIgnoreCase(Strings.MessageTypes.UPDATE)){
                printStatus();
            }
            if (type.equalsIgnoreCase(Strings.MessageTypes.GAME_OVER)){
                isInGame=false;
                isMyTurn=false;
                continueLoop=false;
                System.out.println(OutputStringGenerator.ArrayToString(OutputStringGenerator.theWinnerIs(board)));
            }
        }
    };

    public void printStatus(){
        waitingForResponse=false;
        ArrayList<String> gen= new ArrayList<>();
        if (userQuery.equalsIgnoreCase("B")){
            gen=OutputStringGenerator.generateOutputStringOf_B(board);
        }
        if (userQuery.equalsIgnoreCase("TT")){
            try {
                gen=OutputStringGenerator.generateOutputStringOf_A(board, "TERRITORY");
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
        }
        if (userQuery.equalsIgnoreCase("CT")){
            try {
                gen = OutputStringGenerator.generateOutputStringOf_A(board, "CHARACTER");
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
        }
        if (userQuery.equalsIgnoreCase("BT")){
            try {
                gen=OutputStringGenerator.generateOutputStringOf_A(board, "BUILDING");
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
        }
        if (userQuery.equalsIgnoreCase("VT")){
            try {
                gen=OutputStringGenerator.generateOutputStringOf_A(board, "VENTURE");
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
        }
        if (userQuery.equalsIgnoreCase("C")){
            try {
                gen=OutputStringGenerator.generateOutputStringOf_A(board, "COUNCIL");
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
        }
        if (userQuery.equalsIgnoreCase("P")){
            try {
                gen=OutputStringGenerator.generateOutputStringOf_A(board, "PRODUCTION");
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
        }
        if (userQuery.equalsIgnoreCase("HV")){
            try {
                gen=OutputStringGenerator.generateOutputStringOf_A(board, "HARVEST");
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
        }
        if (userQuery.equalsIgnoreCase("MK")){
            try {
                gen=OutputStringGenerator.generateOutputStringOf_A(board, "MARKET");
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
        }
        if (userQuery.equalsIgnoreCase("RP")){
            try {
                gen=OutputStringGenerator.getPlayerStatus(board, "red");
            }catch (Exception e){
                System.out.println("Red player doesn't exist");
                return;
            }
        }
        if (userQuery.equalsIgnoreCase("BP")){
            try {
                gen=OutputStringGenerator.getPlayerStatus(board, "blue");
            }catch (Exception e){
                System.out.println("Blue player doesn't exist");
                return;
            }
        }
        if (userQuery.equalsIgnoreCase("YP")){
            try {
                gen=OutputStringGenerator.getPlayerStatus(board, "yellow");
            }catch (Exception e){
                System.out.println("Yellow player doesn't exist");
                return;
            }
        }
        if (userQuery.equalsIgnoreCase("GP")){
            try {
                gen=OutputStringGenerator.getPlayerStatus(board, "green");
            }catch (Exception e){
                System.out.println("Green player doesn't exist");
                return;
            }
        }
        System.out.print(OutputStringGenerator.ArrayToString(gen));

    }

    public void printNextFieldInstructions(JsonNode payload){
        String field = payload.get("field").asText();
        //ArrayNode options = (ArrayNode) payload.get("options");
        String out=field;
        if ("familyMember".equalsIgnoreCase(field)) {
            ArrayNode options = (ArrayNode) payload.get("options");
            out="Chose which family member you want use. Press ";
            //System.out.print(options.toString());
            int counter;
            for (counter=0; counter<options.size(); counter++) {
                out+=counter;
                out+=" for "+options.get(counter).asText() + " (value: ";
                if ("neutral".equalsIgnoreCase(options.get(counter).asText())){
                    out+="0)";
                } else {
                    Iterator<JsonNode> dies = board.get("dices").elements();
                    while (dies.hasNext()) {
                        JsonNode die = dies.next();
                        if (die.get("color").asText().equalsIgnoreCase(options.get(counter).asText())) {
                            out += die.get("value").asInt() + ")";
                        }
                    }
                }
                if (counter<options.size()-1){
                    out+=", ";
                }
            }
        }
        if ("slotID".equalsIgnoreCase(field)){
            out="Chose the slot ID in which you want put your family member";
        }
        if ("servants".equalsIgnoreCase(field)){
            out="Chose how many servants you want to use. " +
                    "Press A to select the minimum number of servants necessary to complete your move";
        }
        if ("privileges".equalsIgnoreCase(field)){
            ArrayNode option = (ArrayNode) payload.get("options");
            out="Chose your privilege \n\t";
            Iterator<JsonNode> privilegeList = option.elements();
            JsonNode privileges= GameInitializer.getDefaultPrivileges();
            int counter=0;
            while (privilegeList.hasNext()){
                int privilege = privilegeList.next().asInt();
                JsonNode privilegeDetails=privileges.get(privilege);
                out+=counter+" --> ";
                out+=OutputStringGenerator.ArrayToString(OutputStringGenerator.parseResources(privilegeDetails))+"\t";
                counter++;
            }
        }
        if ("paymentChoice".equalsIgnoreCase(field)){
            out="Chose a payment option: 0 for the first, 1 for the second";
        }
        if ("optionToActivate".equalsIgnoreCase(field)){
            try {
                out= OutputStringGenerator.ArrayToString(OutputStringGenerator.getProducionChoice(board, playerColor, payload.get("card").asInt()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if ("vaticanChoice".equalsIgnoreCase(field)){
            out="Write Y to support vatican oe N to keep your faith points";
        }
        System.out.println(out);
    }

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
                        boolean flag = true;
                        int id=0;
                        while (flag){
                            try {
                                System.out.println("Please insert the game ID: ");
                                id = Integer.parseInt(stdin.next());
                                flag=false;
                            } catch (Exception e){

                            }
                        }
                        flag=true;
                        String color= "";
                        while (flag){
                            try {
                                System.out.println("Please insert the player color: ");
                                color = stdin.next();
                                flag=false;
                            } catch (Exception e){
                            }
                        }
                        ObjectNode node = JsonNodeFactory.instance.objectNode();
                        node.put("id", id);
                        node.put("color", color);
                        waitingForResponse=true;
                        sendMessage(Strings.MoveTypes.JOINGAME, node);
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
        if(in.equalsIgnoreCase("H")) {
            ArrayList<String> gen=OutputStringGenerator.generateMenuCommands();
            System.out.print(OutputStringGenerator.ArrayToString(gen));
        }
        else if (in.equalsIgnoreCase("M")){
            currentMove=MoveBuilder.createBlankMove(true);
            waitingForResponse = true;
            sendMessage(Strings.MoveTypes.MOVE, currentMove);
        }else if (in.equalsIgnoreCase("U")){
            if (moveStack.size()>1){
                moveStack.remove(0);
                currentMove=moveStack.get(0).move.deepCopy();
                printNextFieldInstructions(moveStack.get(0).serverResponse);
            }
        } else if (in.equalsIgnoreCase("E")){
            if (moveComplete){
                waitingForResponse = true;
                MoveBuilder.setChecking((ObjectNode)currentMove, false);
                sendMessage(Strings.MoveTypes.MOVE, currentMove);
            } else {
                System.out.println("Your move is not yet complete");
            }
        }  else if (in.equalsIgnoreCase("B")||in.equalsIgnoreCase("TT")||
                in.equalsIgnoreCase("CT")||in.equalsIgnoreCase("BT")||
                in.equalsIgnoreCase("VT")||in.equalsIgnoreCase("C")||
                in.equalsIgnoreCase("P")||in.equalsIgnoreCase("HV")||
                in.equalsIgnoreCase("MK")||in.equalsIgnoreCase("RP")||
                in.equalsIgnoreCase("BP")|| in.equalsIgnoreCase("YP")||
                in.equalsIgnoreCase("GP")){
            userQuery=in;
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            waitingForResponse=true;
            sendMessage(Strings.MoveTypes.QUERY, node);
        }
        else if(moveStack.size()>0){
            MoveBuilder.addField((ObjectNode)currentMove, (ObjectNode)moveStack.get(0).serverResponse, in);waitingForResponse = true;
            sendMessage(Strings.MoveTypes.MOVE, currentMove);
            waitingForResponse= true;
        }
    }


    public static void main(String[] args) {
        Client client = new Client();
        boolean asd = true;

        while (asd){
            try {
                client.startClient();
                asd=false;
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }

    public class moveBuildingState{
        public moveBuildingState(JsonNode move, JsonNode serverResponse) {
            this.move = move;
            this.serverResponse = serverResponse;
        }
        public JsonNode move, serverResponse;
    }

    public void printCommandsMenu(){

    }
}
