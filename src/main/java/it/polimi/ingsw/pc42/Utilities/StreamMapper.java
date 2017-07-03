package it.polimi.ingsw.pc42.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.Player;


/**
 * Created by diego on 28/06/2017.
 */
public class StreamMapper {
    public static JsonNode fromStringToJson(String message) {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return node;
    }

    public static void main(String[] args) {
        String message = "{\n" +
                "      \"DESCRIZIONE\": \"turno giocatore rosso, va a piazzarsi nel consiglio con l'arancione, sceglie 2 servants e 1 coin\",\n" +
                "      \"familyMember\": \"orange\",\n" +
                "      \"servants\": 0,\n" +
                "      \"slotID\": 17,\n" +
                "      \"privileges\": [1]\n" +
                "    }";

        JsonNode node = fromStringToJson(message);
        Board b = GameInitializer.initBaseGame(false);
        try {
            b.makeMove(node);
        } catch (ActionAbortedException ae){
            System.out.println(ae.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.SERVANT).get());


    }
}
