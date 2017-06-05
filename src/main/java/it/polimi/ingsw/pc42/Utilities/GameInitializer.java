package it.polimi.ingsw.pc42.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import it.polimi.ingsw.pc42.ActionSpaceParser;
import it.polimi.ingsw.pc42.Board;
import it.polimi.ingsw.pc42.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Player;
import it.polimi.ingsw.pc42.ResourceType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import static it.polimi.ingsw.pc42.CardParser.createCard;

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

    public static JsonNode getDefaultActionSpacesJson(){
        return readFile("src/res/actionsSpaces.json");
    }

    public static JsonNode getDefaultCardsJson(){
        return readFile("src/res/developmentCards.json");
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
        Board b=null;
        try {
            b= initGame(false,
                    readFile("src/res/prova_playerInit.json"),
                    getDefaultActionSpacesJson(),
                    getDefaultCardsJson(),
                    shuffle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    public static ArrayList<iCard> readCards(JsonNode cardList, boolean shuffle){
        ArrayList<iCard> cards = readCards(cardList);
        if (shuffle){
            Collections.shuffle(cards);
        }
        return cards;
    }


        public static ArrayList<iCard> readCards(JsonNode cardList){
        ArrayList<iCard> cards = new ArrayList<>();
        Iterator<JsonNode> jsonNodeIterator = cardList.get("developmentCards").elements();
        while (jsonNodeIterator.hasNext()){
            iCard c;
            c = createCard(jsonNodeIterator.next());
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
            players.add(Player.fromColorString(playerIterator.next().get("color").asText()));
        }
        if (shuffle){
            Collections.shuffle(players);
        }
        for (int i =0; i<players.size(); i++){
            Player p = players.get(i);
            /*
            p.getResource(ResourceType.WOOD).set(2);
            p.getResource(ResourceType.STONE).set(2);
            p.getResource(ResourceType.SERVANT).set(3);
            */
            p.getResource(ResourceType.COIN).set(5+i);
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
    public static Board initGame(boolean advanced, JsonNode playerList, JsonNode actionSpaces, JsonNode cards, boolean shuffle) throws Exception {

        ArrayList<Player> players = initBasicPlayers(playerList, shuffle); //TODO PersonalBonusTiles
        ArrayList<iCard> cardList = readCards(cards, shuffle);

        Board b =new Board(players, cardList);

        Iterator<JsonNode> actionSpacesIterator= actionSpaces.get("action_spaces").iterator();
        while (actionSpacesIterator.hasNext()) {
            ActionSpaceParser.actionSpace(actionSpacesIterator.next(), b);
        }

        return b;
    }



}
