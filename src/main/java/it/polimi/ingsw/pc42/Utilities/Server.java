package it.polimi.ingsw.pc42.Utilities;

import it.polimi.ingsw.pc42.Control.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static ExecutorService executor = Executors.newCachedThreadPool();

    private ArrayList<ClientHandler> clients;
    private ArrayList<Game> games;
    private int counter=4;
    private MyTimer timer;

    private final static int PORT = 3000;
    public void startServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server socket ready on port: " + PORT);
        System.out.println("Server ready");
        clients=new ArrayList<ClientHandler>();
        games= new ArrayList<Game>();
        boolean end =false;
        while (!end) {
            try {
                Socket socket = serverSocket.accept();
                ClientHandler client = new ClientHandler(socket);
                executor.submit(client);
                clients.add(client);
                counter--;
                System.out.println("you are waiting for " + counter+ " players");
                if (clients.size()==2){
                    timer = createTimer();
                    timer.startTimer();
                }
                if (clients.size()==4){
                    this.createGame();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException npe){npe.printStackTrace();}
        }
        executor.shutdown();
        serverSocket.close();
    }

    public void createGame(){
        Game game=new Game(clients);
        games.add(game);
        game.start();
        this.cleanClients();
        timer.stopTimer();
        System.out.print(games.size());
    }

    public void cleanClients(){
        try {
            clients.clear();
            counter = 4;
        } catch (Exception e ){
            e.printStackTrace();
        }
    }

    public MyTimer createTimer(){
        return new MyTimer(MyTimer.getLobbyTimeout(), new MyTimer.myTimerTask() {
            @Override
            public void onUpdate(int secondsLeft) {
                System.out.println(" seconds to start: "+ secondsLeft);
            }
            @Override
            public void onEnd() {
                System.out.println("game is starting...");
                createGame();
            }
        });
    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
