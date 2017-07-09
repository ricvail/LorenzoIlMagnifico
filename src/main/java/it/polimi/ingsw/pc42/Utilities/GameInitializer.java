package it.polimi.ingsw.pc42.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import it.polimi.ingsw.pc42.Control.ActionSpace.iActionSpace;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Model.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import static it.polimi.ingsw.pc42.Utilities.CardParser.createCard;

public class GameInitializer {
    private static Logger logger= LogManager.getLogger();

    /**
     * Takes the file path as parameter and returns the mapper of the JSON object at the root node.
     *
     * @param path relative path
     * @return the root node of the newly mapped JSON object
     */
    public static JsonNode readFile(String path){
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(path);
            return mapper.readTree(file);
        } catch (Exception e){
            logger.error(e);
            return null;
        }
    }

    /**
     * Read the file for default bonus tile and returns a mapped object.
     *
     * @return mapper of bonus tile
     */
    public static JsonNode getDefaultBonusTileJson(){
        return readFile("src/res/personalBonuses.json");
    }

    /**
     * Read the file for default action spaces and returns a mapped object.
     *
     * @return root node for the list of action spaces
     */
    public static JsonNode getDefaultActionSpacesJson(){
        JsonNode root=null;
        JsonNode sent;
        try {
            root=readFile("src/res/actionsSpaces.json").get("action_spaces");
        } catch (Exception e){
            logger.error(e);
        }
        sent=root;
        return sent;
    }

    /**
     * Read the file for default development cards and returns a mapped object.
     *
     * @return root node for development cards
     */
    private static JsonNode getDefaultCardsJson(){
        return readFile("src/res/developmentCards.json");
    }

    /**
     * Read the file for default privileges and returns a mapped object.
     *
     * @return root node for the list of privileges
     */
    public static JsonNode getDefaultPrivileges() {
        JsonNode root=null;
        JsonNode sent;
        try {
            root=readFile("src/res/privileges.json").get("privileges");
        } catch (Exception e){
            logger.error(e);
        }
        sent=root;
        return sent;
    }

    /**
     *Checks if the the list of players that want to start a new game is valid.
     *
     * @param playerList node of the list of players ready to play
     * @return <code>true</code> if is valid
     */
    private static boolean isBasicPlayerListJsonValid(JsonNode playerList){
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
                logger.error(e);
                return false;
            }
            for (int j = 0; j<i; j++){
                String otherPlayerColor= array.get(j).get("color").asText();
                if (otherPlayerColor.equalsIgnoreCase(playerColor)) return false;
            }
        }
        return true;
    }

    /**
     * takes a <code>boolean</code> to init the game with shuffled cards or not, then delegates the creation itself.
     *
     * @param shuffle <code>true</code> for shuffled development cards
     * @return reference a to board ready to start a game
     */
    public static Board initBaseGame(boolean shuffle){
        return initBaseGame(readFile("src/res/prova_playerInit.json"), shuffle);
    }

    /**
     *
     * Overloaded method that takes a mapped JSON that contains the players who wants to start a game, too.
     *
     * @param players node of the list of players ready to play
     * @param shuffle <code>true</code> for shuffled development cards
     * @return reference a to board ready to start a game
     */
    public static Board initBaseGame(JsonNode players, boolean shuffle){
        Board b=null;
        try {
            b= initGame(false,
                    players,
                    getDefaultActionSpacesJson(),
                    getDefaultCardsJson(),
                    shuffle, getDefaultPrivileges());
        } catch (Exception e) {
            logger.error(e);
        }
        return b;
    }

    /**
     * Overloaded method that takes a list of the players who wants to start a game, too.
     *
     * @param players list of players ready to play
     * @param shuffle <code>true</code> for shuffled development cards
     * @return reference a to board ready to start a game
     */
    public static Board initBaseGame(ArrayList<Player> players, boolean shuffle){
        Board b=null;
        try {
            b= initGame(false,
                    players,
                    getDefaultActionSpacesJson(),
                    getDefaultCardsJson(),
                    shuffle, getDefaultPrivileges());
        } catch (Exception e) {
            logger.error(e);
        }
        return b;
    }

    /**
     * Delegates the initialization of the list of  cards, and checks if it has to shuffle the cards, then returns them.
     *
     * @param cardList node of JSON object of the card array
     * @param bp a wrapper for a board object
     * @param shuffle <code>true</code> for shuffled development cards
     * @return list of objects that implement the card interface (already decorated)
     */
    private static ArrayList<iCard> readCards(JsonNode cardList,BoardProvider bp, boolean shuffle){
        ArrayList<iCard> cards = readCards(cardList, bp);
        if (shuffle){
            Collections.shuffle(cards);
        }
        return cards;
    }

    /**
     * Initializes a list of action spaces and adds them, after delegating the creation and decoration.
     *
     * @param spacesList node of JSON object of the action spaces array
     * @param boardProvider a wrapper for a board object
     * @return list of objects that implement the action space interface (already decorated)
     * @throws Exception re-throws exception from the creator of action spaces
     */
    private static ArrayList<iActionSpace> readActionSpaces(JsonNode spacesList, BoardProvider boardProvider) throws Exception {
        ArrayList<iActionSpace> actionSpaceList = new ArrayList<>();
        Iterator<JsonNode> actionSpacesIterator= spacesList.iterator();
        while (actionSpacesIterator.hasNext()) {
            ActionSpaceParser.actionSpace(actionSpacesIterator.next(), boardProvider, actionSpaceList);
        }
        return actionSpaceList;
    }

    /**
     * Iterates through the array of cards in the JSON file and delegates the initialization and decoration of a card.
     *
     * @param cardList node of JSON object of the card array
     * @param bp a wrapper for a board object
     * @return list of objects that implement the card interface
     */
    private static ArrayList<iCard> readCards(JsonNode cardList, BoardProvider bp){
        ArrayList<iCard> cards = new ArrayList<>();
        Iterator<JsonNode> jsonNodeIterator = cardList.get("developmentCards").elements();
        while (jsonNodeIterator.hasNext()){
            iCard c;
            c = createCard(jsonNodeIterator.next(), bp);
            cards.add(c);
        }
        return cards;
    }

    /**
     * Read from file the players who want to start a game, initializes them and eventually shuffles them.
     * If the specifications in the JSON file are not valid throws exception.
     *
     * @param playerList node of the list of players ready to play
     * @param shuffle <code>true</code> for shuffled players
     * @return a list of players
     * @throws Exception if the the list of players that want to start a new game is not valid
     */
    private static ArrayList<Player> initBasicPlayers(JsonNode playerList, boolean shuffle) throws Exception {
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

    /*
     * Things required to initialize a game:
     * -Game mode (advanced or basic)
     * -Player information
     *      -Color
     *      -Advanced rules
     *          -Leader cards
     *          -Production and Harvest bonus
     * -Action spaces list
     * -Cards list
     * -Excommunication tiles list */

    /**
     *  Takes all the parameters needed to initialize the game, initializes the list of players and delegates the rest.
     * Then returns a board ready to start the game.
     *
     * @param advanced <code>true</code> for advanced game mode
     * @param playerList  node of the list of players ready to play
     * @param actionSpaces node of JSON object of the action spaces array
     * @param cards node of JSON object of the card array
     * @param shuffle <code>true</code> for shuffled players and cards
     * @param privileges root node of JSON object for the list of privileges
     * @return reference a to board ready to start a game
     * @throws Exception re-throws exception from the creation of players, cards, action spaces or privileges
     */
    private static Board initGame(boolean advanced, JsonNode playerList, JsonNode actionSpaces,
                                 JsonNode cards, boolean shuffle, JsonNode privileges) throws Exception {

        ArrayList<Player> players = initBasicPlayers(playerList, shuffle);

        return initGame(advanced, players, actionSpaces, cards, shuffle, privileges);
    }

    /**
     * Calls the methods for the creation of cards, action spaces, council and board. Then delegates the cleaning and
     * checking in order to be ready to take the first move and start thee game.
     *
     * @param advanced <code>true</code> for advanced game mode
     * @param players list of initialized players
     * @param actionSpaces node of JSON object of the action spaces array
     * @param cards node of JSON object of the card array
     * @param shuffle <code>true</code> for shuffled players and cards
     * @param privileges root node of JSON object for the list of privileges
     * @return reference a to board ready to start a game
     * @throws Exception re-throws exception from the creation of cards, action spaces or privileges
     */
    private static Board initGame(boolean advanced, ArrayList<Player> players, JsonNode actionSpaces,
                                 JsonNode cards, boolean shuffle, JsonNode privileges) throws Exception {

        BoardProvider boardProvider = new BoardProvider();
        ArrayList<iCard> cardList = readCards(cards,boardProvider,shuffle);
        ArrayList<iActionSpace> actionSpaceList=readActionSpaces(actionSpaces, boardProvider);
        int councilID=ActionSpaceParser.getCouncilID(actionSpaces);
        Board b =new Board(players, cardList, actionSpaceList, (ArrayNode) actionSpaces, shuffle, privileges);
        b.setCouncilID(councilID);
        boardProvider.setBoard(b);
        b.firstCleanup();

        return b;
    }
    
}