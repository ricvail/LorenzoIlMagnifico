package it.polimi.ingsw.pc42.Control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Model.Player;
import it.polimi.ingsw.pc42.Utilities.ClientHandler;
import it.polimi.ingsw.pc42.Utilities.GameInitializer;
import it.polimi.ingsw.pc42.Utilities.MyTimer;

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
            //client.socketOut.println("Game started! You are player "+ client.getPlayer().getColor().getPlayerColorString());
            //client.socketOut.print("Turn order: ");
            for (Player p : b.getPlayerArrayList()){
                //client.socketOut.print(p.getColor().getPlayerColorString()+ " ");
            }
            //client.socketOut.println("");
            //client.socketOut.flush();
        }
        timer = createTimer(b, getClient(b.getCurrentPlayer()));
        timer.startTimer();
    }

    public void switchClient(){
        timer.stopTimer();
        ClientHandler c = getClient(b.getCurrentPlayer());
        timer = createTimer(b, c);
        //c.socketOut.println("It is now your turn");
        //c.socketOut.flush();
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
                if (secondsLeft==5){
                    //client.socketOut.println("Five seconds left!");
                    //client.socketOut.flush();
                }
            }
            @Override
            public void onEnd() {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode ghostNode = mapper.createObjectNode();
                ghostNode.put("servants", 0);
                ghostNode.put("slotID", 0);
                ArrayList<FamilyMember> fmList = p.getFamilyMembers();
                FamilyMember fm = fmList.get(0);
                int min = fmList.get(0).getValue();
                for(int i=1; i<fmList.size(); i++){
                    if (fmList.get(i).getValue()< min){
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
                //client.socketOut.println("Time is up, null move executed");
                //client.socketOut.flush();
                switchClient();
            }
        });
    }
}

