package it.polimi.ingsw.pc42.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import it.polimi.ingsw.pc42.Control.ActionSpace.iActionSpace;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Model.Player;
import it.polimi.ingsw.pc42.Control.ResourceType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import static it.polimi.ingsw.pc42.Utilities.CardParser.createCard;

/**
 * Created by RICVA on 30/05/2017.
 */
public class GameInitializer {

    public static JsonNode readFile(String path){
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(path);
            return mapper.readTree(file);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static JsonNode getDefaultBonusTileJson(){
        return readFile("src/res/personalBonuses.json");
    }

    public static JsonNode getDefaultActionSpacesJson(){
        return readFile("src/res/actionsSpaces.json").get("action_spaces");
    }

    public static JsonNode getDefaultCardsJson(){
        return readFile("src/res/developmentCards.json");
    }


    private static JsonNode getDefaultPrivileges() {
        return readFile("src/res/privileges.json").get("privileges");
    }

    public static boolean isBasicPlayerListJsonValid(JsonNode playerList){
        if (!(playerList.has("players")&&playerList.get("players").isArray())){
            return false;
        }
        ArrayNode array = (ArrayNode) playerList.get("players");
        if (array.size()<2) return false;
        for (int i = 0; i<array.size(); i++){
            if (!array.get(i).has("color"))
                return false;
            String playerColor=array.get(i).get("color").asText();
            try {
                Player.PlayerColor.fromString(playerColor);
            } catch (Exception e){
                e.printStackTrace();
                return false;
            }
            for (int j = 0; j<i; j++){
                String otherPlayerColor= array.get(j).get("color").asText();
                if (otherPlayerColor.equalsIgnoreCase(playerColor)) return false;
            }
        }
        return true;
    }

    public static void main(String args []){
        System.out.print(
            initBaseGame(true)
        );

    }

    public static Board initBaseGame(boolean shuffle){
        return initBaseGame(readFile("src/res/prova_playerInit.json"), shuffle);
    }
    public static Board initBaseGame(JsonNode players, boolean shuffle){
        Board b=null;
        try {
            b= initGame(false,
                    players,
                    getDefaultActionSpacesJson(),
                    getDefaultCardsJson(),
                    shuffle, getDefaultPrivileges());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }
    public static Board initBaseGame(ArrayList<Player> players, boolean shuffle){
        Board b=null;
        try {
            b= initGame(false,
                    players,
                    getDefaultActionSpacesJson(),
                    getDefaultCardsJson(),
                    shuffle, getDefaultPrivileges());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    public static ArrayList<iCard> readCards(JsonNode cardList,BoardProvider bp, boolean shuffle){
        ArrayList<iCard> cards = readCards(cardList, bp);
        if (shuffle){
            Collections.shuffle(cards);
        }
        return cards;
    }

    public static ArrayList<iActionSpace> readActionSpaces(JsonNode spacesList, BoardProvider boardProvider) throws Exception {
        ArrayList<iActionSpace> actionSpaceList = new ArrayList<>();
        Iterator<JsonNode> actionSpacesIterator= spacesList.iterator();
        while (actionSpacesIterator.hasNext()) {
            ActionSpaceParser.actionSpace(actionSpacesIterator.next(), boardProvider, actionSpaceList);
        }
        return actionSpaceList;
    }


        public static ArrayList<iCard> readCards(JsonNode cardList, BoardProvider bp){
        ArrayList<iCard> cards = new ArrayList<>();
        Iterator<JsonNode> jsonNodeIterator = cardList.get("developmentCards").elements();
        while (jsonNodeIterator.hasNext()){
            iCard c;
            c = createCard(jsonNodeIterator.next(), bp);
            cards.add(c);
        }
        return cards;
    }

    public static ArrayList<Player> initBasicPlayers(JsonNode playerList, boolean shuffle) throws Exception {
        if (!isBasicPlayerListJsonValid(playerList)){
            throw new Exception("Invalid PlayerInit Json");
        }
        ArrayList<Player> players = new ArrayList<>();
        Iterator<JsonNode> playerIterator =playerList.get("players").iterator();
        while (playerIterator.hasNext()){
            players.add(Player.createPlayer(playerIterator.next().get("color").asText()));
        }
        if (shuffle){
            Collections.shuffle(players);
        }
        return players;
    }


    /**
     * Things required to initialize a game:
     * -Game mode (advanced or basic)
     * -Player information
     *      -Color
     *      -Advanced rules
     *          -Leader cards
     *          -Production and Harvest bonus
     * -Action spaces list
     * -Cards list
     * -Excommunication tiles list
     *
     * @param actionSpaces
     * @return
     */
    public static Board initGame(boolean advanced, JsonNode playerList, JsonNode actionSpaces,
                                 JsonNode cards, boolean shuffle, JsonNode privileges) throws Exception {
        ArrayList<Player> players = initBasicPlayers(playerList, shuffle);

        return initGame(advanced, players, actionSpaces, cards, shuffle,privileges);
    }
    public static Board initGame(boolean advanced, ArrayList<Player> players, JsonNode actionSpaces,
                                 JsonNode cards, boolean shuffle, JsonNode privileges) throws Exception {
        BoardProvider boardProvider = new BoardProvider();
        ArrayList<iCard> cardList = readCards(cards,boardProvider,shuffle);
        ArrayList<iActionSpace> actionSpaceList=readActionSpaces(actionSpaces, boardProvider);
        int councilID=ActionSpaceParser.getCouncilID(actionSpaces);

        Board b =new Board(players, cardList, actionSpaceList, shuffle, privileges);
        b.setCouncilID(councilID);
        boardProvider.setBoard(b);

        b.firstCleanup();

        return b;
    }
    
}