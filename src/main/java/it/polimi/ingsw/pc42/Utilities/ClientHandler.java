package it.polimi.ingsw.pc42.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.MoveManager;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.Player;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    private Board board;

    public PrintWriter socketOut;

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

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            Scanner socketIn = new Scanner(socket.getInputStream());
            socketOut = new PrintWriter(socket.getOutputStream());
            boolean continueLoop = true;
            while (continueLoop) {
                String line = socketIn.nextLine();
                try {
                    JsonNode jsonNode = StreamMapper.fromStringToJson(line);
                    if (jsonNode.has("familyMember")) {

                        MoveManager.makeMove(board, player, jsonNode);
                    }
                    if (jsonNode.has("getDescription"))
                        socketOut.println(getPlayer().generateJsonDescription().asText());
                    socketOut.flush();
                    //System.out.println(client.getPlayer().generateJsonDescription().asText());
                    //TODO come generare tutte le descrizioni possibili
                } catch (ActionAbortedException e) {
                    e.printStackTrace();
                    if (e.isValid && !e.isComplete) {
                        socketOut.println("Missing field: " + e.nextMoveField);
                        socketOut.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    socketOut.println("Exception: " + e.getMessage());
                    socketOut.flush();
                }

            }
            socketIn.close();
            socketOut.close();
            socket.close();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
