package it.polimi.ingsw.pc42.Utilities;

import it.polimi.ingsw.pc42.Model.Player;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

public class Client {
    private Socket socket;
    private final static int PORT = 3000;
    private final static String IP="127.0.0.1";
    private Player player;

    public void setSocket (Socket socket){
        this.socket=socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void startClient() throws IOException {
        Socket socket = new Socket(IP, PORT);
        System.out.println("Connection Established");
        ExecutorService executor = Executors.newFixedThreadPool(4);
        executor.submit(new ClientInHandler(new Scanner(socket.getInputStream())));
        executor.submit((new ClientOutHandler(new PrintWriter(socket.getOutputStream()))));
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.startClient();
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
    }
}
