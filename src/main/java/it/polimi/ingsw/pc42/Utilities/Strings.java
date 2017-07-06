package it.polimi.ingsw.pc42.Utilities;

/**
 * Created by RICVA on 04/07/2017.
 */
public class Strings {
    public static class MessageTypes {
        public static String GAMEJOINED = "GAMEJOINED", ADDED_TO_LOBBY = "ADDED_TO_LOBBY",
                OTHER_PLAYER_JOINED_LOBBY="OTHER_PLAYER_JOINED_LOBBY", GAMESTARTED= "GAMESTARTED",
                MOVE_TIMEOUT="MOVE_TIMEOUT", MOVE_INVALID ="MOVE_INVALID", MOVE_SUCCESSFUL= "MOVE_SUCCESSFUL",
                MOVE_INCOMPLETE= "MOVE_INCOMPLETE", MOVE_COMPLETE= "MOVE_COMPLETE";

        public static String CRITICAL_ERROR = "CRITICAL_ERROR", PLAIN = "PLAIN",
                 CONFIRM = "CONFIRM", WARNING = "WARNING", INFO = "INFO", UPDATE="UPDATE";
    }
    public static class MoveTypes{
        public static String MOVE = "MOVE", NEWGAME = "NEWGAME", JOINGAME = "JOINGAME", QUERY="QUERY";
    }
}
