package it.polimi.ingsw.pc42.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by RICVA on 03/07/2017.
 */
public class MyTimer {

    int seconds;
    Timer t;
    myTimerTask task;
    private static Logger logger=LogManager.getLogger();

    public MyTimer(int seconds, myTimerTask task){
        this.seconds=seconds;
        t = new Timer();
        this.task= task;
        logger= LogManager.getLogger();
    }

    public void startTimer (){
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                seconds--;
                if (seconds >0){
                    task.onUpdate(seconds);
                } else {
                    task.onEnd();
                    t.cancel();
                }
            }
        }, 1000, 1000);
    }

    public void stopTimer(){
        t.cancel();
    }

    public interface myTimerTask{
        void onUpdate(int secondsLeft);
        void onEnd();
    }

    public static JsonNode timerSettings = null;

    public static JsonNode getTimerSettings(){
        if (timerSettings!=null) return timerSettings;
        File timeoutJSON = new File("src/res/timeout.json");
        ObjectMapper mapper = new ObjectMapper();
        try {
            timerSettings= mapper.readTree(timeoutJSON);
            return timerSettings;
        } catch (Exception e){
            logger.error(e);
            return null;
        }
    }

    public static int getLobbyTimeout(){
        int i=getTimerSettings().get("lobbyTimeoutSeconds").asInt();
        return i;
    }
    public static int getMoveTimeout(){
        int i=getTimerSettings().get("moveTimeoutSeconds").asInt();
        return i;
    }

}
