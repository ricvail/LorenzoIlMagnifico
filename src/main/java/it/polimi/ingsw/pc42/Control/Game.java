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
import it.polimi.ingsw.pc42.View.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by diego on 01/07/2017.
 */
public class Game {
    private ArrayList<Player> playerArrayList;
    private ArrayList<ClientHandler> clients;
    private Board b;
    private Logger logger;

    public static int nextID = 0;

    public int id;

    public Game(ArrayList<ClientHandler> clients) {
        this.id = nextID;
        nextID++;
        this.clients = new ArrayList<ClientHandler>();
        for (ClientHandler c : clients){
            this.clients.add(c);
        }
        logger= LogManager.getLogger();
    }
    public void start(){
        playerArrayList = new ArrayList<Player>();
        Collections.shuffle(clients);
        for (int i=0; i<clients.size(); i++){
            String color =Player.PlayerColor.values()[i].getPlayerColorString();
            Player player= Player.createPlayer(color);
            playerArrayList.add(player);
            clients.get(i).setPlayer(player);
            clients.get(i).setGame(this);
        }
        Collections.shuffle(playerArrayList);
        b = GameInitializer.initBaseGame(playerArrayList, true, true);
        for (ClientHandler client:clients){
            client.setBoard(b);
        }
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("id", id);
        broadcastUpdate(Strings.MessageTypes.GAMESTARTED, node);
        timer = createTimer(b, getClient(b.getCurrentPlayer()));
        timer.startTimer();
    }

    public void replaceClient(ClientHandler newCli, ClientHandler oldCli){
        newCli.setPlayer(oldCli.getPlayer());
        newCli.setGame(this);
        newCli.setBoard(b);
        clients.add(newCli);
        ObjectNode payload = JsonNodeFactory.instance.objectNode();
        payload.put("id", id);
        payload.set("board", b.generateJsonDescription());
        payload.put("color", newCli.getPlayer().getColor().getPlayerColorString());
        if (newCli.getPlayer().getColor()==b.getCurrentPlayer().getColor()){
            payload.put("yourTurn", true);
        } else payload.put("yourTurn", false);
        newCli.sendMessage(Strings.MessageTypes.GAMESTARTED, payload);
    }

    public void switchClient(){
        timer.stopTimer();
        ClientHandler c = getClient(b.getCurrentPlayer());
        timer = createTimer(b, c);
        timer.startTimer();
    }

    public ClientHandler getClient (Player p){
        return getClient(p.getColor());
    }

    public ClientHandler getClient (Player.PlayerColor color){
        for (ClientHandler c : clients){
            if (c.getPlayer().getColor()== color) {
                return c;
            }
        }
        try {
            throw new Exception();
        } catch (Exception e){
            logger.error(e);
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
                try{
                    ghostNode.put("familyMember", fm.getDiceColor().getDiceColorString());
                } catch (Exception e){
                    logger.error(e);
                }
                try {
                    MoveManager.makeMove(b, p, ghostNode);
                } catch (Exception e) {
                    logger.error(e);
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

