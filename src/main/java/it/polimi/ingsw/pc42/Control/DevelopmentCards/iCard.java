package it.polimi.ingsw.pc42.Control.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.ActionSpace.iActionSpace;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Model.Player;

public interface iCard {

    //boolean drawRequirementCheck(Player player);

    //void applyDrawEffect(Player player, JsonNode json);

    void undoDrawCard(JsonNode move, FamilyMember fm);

    void drawCard (JsonNode move, FamilyMember fm) throws ActionAbortedException;

    void applyEndgameEffect(Player player);

    JsonNode getJSONDescriptionOfCards();

    int getEra();

    String getName();

    Card.CardType getCardType();

    Board getBoard();

}
