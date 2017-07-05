package it.polimi.ingsw.pc42.View;

import it.polimi.ingsw.pc42.Model.Player;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Client {
    private Socket socket;
    private final static int PORT = 3000;
    private final static String IP="127.0.0.1";
    private Player player;

    private Scanner socketIn;
    private PrintWriter socketOut;

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
    boolean continueLoop=true;

    public void startClient() throws IOException {
        Socket socket = new Socket(IP, PORT);
        System.out.println("Connection Established");
        socketIn= new Scanner((socket.getInputStream()));
        socketOut=new PrintWriter(socket.getOutputStream());
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(userInputHandler);
        executor.submit(serverResponseHandler);
    }
    Runnable serverResponseHandler = ()->{
        while (continueLoop){
            String line = socketIn.nextLine();
            System.out.println(line);
        }
    };
    Runnable userInputHandler= ()->{
        while (continueLoop){
            Scanner stdin = new Scanner(System.in);
            while (continueLoop) {
                String inputLine = stdin.nextLine();
                socketOut.println(inputLine);
                socketOut.flush();
            }
        }
    };

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.startClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
