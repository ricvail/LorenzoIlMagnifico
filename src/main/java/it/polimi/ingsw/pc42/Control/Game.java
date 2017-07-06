package it.polimi.ingsw.pc42.Control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Model.Player;
import it.polimi.ingsw.pc42.Utilities.ClientHandler;
import it.polimi.ingsw.pc42.Utilities.GameInitializer;
import it.polimi.ingsw.pc42.Utilities.MyTimer;
import it.polimi.ingsw.pc42.Utilities.Strings;

import java.util.ArrayList;

/**
 * Created by diego on 01/07/2017.
 */
public class Game {
    private int ID;
    private ArrayList<Player> playerArrayList;
    private ArrayList<ClientHandler> clients;
    private Board b;

    public Game(ArrayList<ClientHandler> clients) {
        this.clients = new ArrayList<ClientHandler>();
        for (ClientHandler c : clients){
            this.clients.add(c);
        }
    }
    public void start(){
        playerArrayList = new ArrayList<Player>();
        for (int i=0; i<clients.size(); i++){
            String color =Player.PlayerColor.values()[i].getPlayerColorString();
            Player player= Player.createPlayer(color);
            playerArrayList.add(player);
            clients.get(i).setPlayer(player);
            clients.get(i).setGame(this);
        }
        b = GameInitializer.initBaseGame(playerArrayList, false);//TODO shuffle
        for (ClientHandler client:clients){
            client.setBoard(b);
        }
        broadcastUpdate(Strings.MessageTypes.GAMESTARTED);
        timer = createTimer(b, getClient(b.getCurrentPlayer()));
        timer.startTimer();
    }

    public void switchClient(){
        timer.stopTimer();
        ClientHandler c = getClient(b.getCurrentPlayer());
        timer = createTimer(b, c);
        timer.startTimer();
    }

    public ClientHandler getClient (Player p){
        for (ClientHandler c : clients){
            if (c.getPlayer().getColor()== p.getColor()) {
                return c;
            }
        }
        try {
            throw new Exception();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public MyTimer timer;
    public MyTimer createTimer(Board b, ClientHandler client){
        Player p = b.getCurrentPlayer();
        return new MyTimer(MyTimer.getMoveTimeout(), new MyTimer.myTimerTask() {
            @Override
            public void onUpdate(int secondsLeft) {

            }
            @Override
            public void onEnd() {
                String timedOutPlayer = b.getCurrentPlayer().getColor().toString();
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode ghostNode = mapper.createObjectNode();
                ghostNode.put("servants", 0);
                ghostNode.put("slotID", 0);
                ghostNode.put("vatican", false);
                ArrayList<FamilyMember> fmList = p.getFamilyMembers();
                FamilyMember fm = null;
                int min = 100;
                for(int i=0; i<fmList.size(); i++){
                    if (fmList.get(i).getValue()< min &&!fmList.get(i).isUsed()){
                        min = fmList.get(i).getValue();
                        fm = fmList.get(i);
                    }
                }
                ghostNode.put("familyMember", fm.getDiceColor().getDiceColorString());
                try {
                    MoveManager.makeMove(b, p, ghostNode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ObjectNode payload =JsonNodeFactory.instance.objectNode();
                payload.put("timedOutPlayer", timedOutPlayer);
                broadcastUpdate(Strings.MessageTypes.MOVE_TIMEOUT, payload);
                switchClient();
            }
        });
    }
    public void broadcastUpdate(String messageType){
        broadcastUpdate(messageType, JsonNodeFactory.instance.objectNode());
    }

    public void broadcastUpdate(String messageType, ObjectNode payload){
        for (ClientHandler client:clients){
            payload.set("board", b.generateJsonDescription());
            payload.put("color", client.getPlayer().getColor().getPlayerColorString());
            if (client.getPlayer().getColor()==b.getCurrentPlayer().getColor()){
                payload.put("yourTurn", true);
            } else payload.put("yourTurn", false);
            client.sendMessage(messageType, payload);
        }
    }
}

