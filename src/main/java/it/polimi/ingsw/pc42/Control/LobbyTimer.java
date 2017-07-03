package it.polimi.ingsw.pc42.Control;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.pc42.Utilities.Server;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by diego on 01/07/2017.
 */

public class LobbyTimer {
    private Server server;
    private int secondToStart;
    Timer myTimer;
    TimerTask timerTask= new TimerTask(){
        public void run(){
            secondToStart--;
            System.out.println(" seconds to start: "+ secondToStart);
            if (secondToStart==0){
                System.out.println("game is starting...");
                server.createGame();
            }
        }
    };

    public LobbyTimer(Server server){
        this.server=server;
        secondToStart=this.timeParse();
        myTimer = new Timer();
    }

    public int timeParse(){
        int secondsToStart = 0;
        File timeoutJSON = new File("src/res/timeout.json");
        ObjectMapper mapper = new ObjectMapper();
        try {
            secondsToStart = mapper.readTree(timeoutJSON).get("lobbyTimeoutSeconds").asInt();
        } catch (Exception e){
            e.printStackTrace();
        }
        return secondsToStart;
    }

    public void stopTimer(){
        myTimer.cancel();
    }

    public void start(){
        myTimer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }


}
