package it.polimi.ingsw.pc42.Control;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.Player;
import it.polimi.ingsw.pc42.Utilities.Client;
import it.polimi.ingsw.pc42.Utilities.ClientHandler;
import it.polimi.ingsw.pc42.Utilities.GameInitializer;
import it.polimi.ingsw.pc42.Utilities.StreamMapper;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by diego on 01/07/2017.
 */
public class Game {
    private int ID;
    private ArrayList<Player> playerArrayList;
    private ArrayList<ClientHandler> clients;
    private Board b;

    public Game(ArrayList<ClientHandler> clients) {
        this.clients = clients;
    }
    public void start(){
        playerArrayList = new ArrayList<Player>();
        for (int i=0; i<clients.size(); i++){
            String color =Player.PlayerColor.values()[i].getPlayerColorString();
            Player player= Player.createPlayer(color);
            playerArrayList.add(player);
            clients.get(i).setPlayer(player);
        }
        Board b = GameInitializer.initBaseGame(playerArrayList, false);
        for (ClientHandler client:clients){
            client.setBoard(b);
            client.socketOut.println("Game started! You are player "+ client.getPlayer().getColor().getPlayerColorString());
            client.socketOut.print("Turn order: ");
            for (Player p : b.getPlayerArrayList()){
                client.socketOut.print(p.getColor().getPlayerColorString()+ " ");
            }
            client.socketOut.println("");
            client.socketOut.flush();
        }
    }
}

