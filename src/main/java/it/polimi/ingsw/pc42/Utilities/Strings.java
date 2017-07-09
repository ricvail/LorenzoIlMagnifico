package it.polimi.ingsw.pc42.Utilities;

public class Strings {
    public static class MessageTypes {

        public static final String GAMEJOINED = "GAMEJOINED", ADDED_TO_LOBBY = "ADDED_TO_LOBBY",
                OTHER_PLAYER_JOINED_LOBBY="OTHER_PLAYER_JOINED_LOBBY", GAMESTARTED= "GAMESTARTED",
                GAME_NOT_FOUND= "GAME_NOT_FOUND",
                MOVE_TIMEOUT="MOVE_TIMEOUT", MOVE_INVALID ="MOVE_INVALID", MOVE_SUCCESSFUL= "MOVE_SUCCESSFUL",
                MOVE_INCOMPLETE= "MOVE_INCOMPLETE", MOVE_COMPLETE= "MOVE_COMPLETE", GAME_OVER="GAME_OVER";

        public static final String CRITICAL_ERROR = "CRITICAL_ERROR", UPDATE="UPDATE";
    }

    public static class MoveTypes{

        public static final String MOVE = "MOVE", NEWGAME = "NEWGAME", JOINGAME = "JOINGAME", QUERY="QUERY";
    }
}
