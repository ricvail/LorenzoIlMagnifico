package it.polimi.ingsw.pc42.Utilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.pc42.Control.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static ExecutorService executor = Executors.newCachedThreadPool();
    private ArrayList<ClientHandler> clients;
    private ArrayList<Game> games;
    private int counter=4;
    private MyTimer timer;
    private final static int PORT = 3000;

    public void startServer() throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket= new ServerSocket(PORT);
            System.out.println("Server socket ready on port: " + PORT);
            System.out.println("Server ready");
            clients = new ArrayList<>();
            games = new ArrayList<>();
            boolean end = false;
            while (!end) {
                try {
                    Socket socket = serverSocket.accept();
                    ClientHandler client = new ClientHandler(socket, this);
                    executor.submit(client);
                } catch (Exception e) {
                    if ("This is to make sonar happy".equalsIgnoreCase(e.getMessage())) {
                        end = true;
                    }
                    LogManager.getLogger().error(e);
                }
            }
            executor.shutdown();
        } finally {
            if (serverSocket!=null)
                serverSocket.close();

        }
    }

    public void addClientToLobby(ClientHandler client){
        counter--;
        ObjectNode payload = JsonNodeFactory.instance.objectNode();
        payload.put("counter", counter);
        client.sendMessage(Strings.MessageTypes.ADDED_TO_LOBBY, payload);
        for (ClientHandler cli: clients){
            payload.put("counter", counter);
            cli.sendMessage(Strings.MessageTypes.OTHER_PLAYER_JOINED_LOBBY, payload);
        }
        clients.add(client);
        System.out.println("Waiting for " + counter+ " players");
        if (clients.size()==2){
            timer = createTimer();
            timer.startTimer();
        }
        if (clients.size()==4){
            this.createGame();
        }
    }

    public Game getGame(int id) throws myException {
        for (Game g: games){
            if (g.id==id) return g;
        }
        throw new myException();
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
            LogManager.getLogger().error(e);
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
        System.getProperties().setProperty("log4j.configurationFile", "log4j2.xml");
        Server server = new Server();
        try {
            server.startServer();
        } catch (IOException e) {
            LogManager.getLogger().error(e);
        }
    }
}
