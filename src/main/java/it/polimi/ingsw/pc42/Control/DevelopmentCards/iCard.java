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

    /**
     * Describes, for each decorator, the step that has to perform to undo a draw card move.
     *
     * @param move node of the move executed
     * @param fm family member placed in an action space that did a draw move
     */
    void undoDrawCard(JsonNode move, FamilyMember fm);

    /**
     * With some differences, based on the type of card decorator that overrides it, describes the sequence of controls
     * and actions that take place when drawing a card.
     *
     * @param move node of the move, eventually needed to check for particular immediate effect
     * @param fm family member placed in an action space that leads to a draw move
     * @throws ActionAbortedException re-throws from the callee if a certain point the action aborts
     */
    void drawCard (JsonNode move, FamilyMember fm) throws ActionAbortedException;

    /**
     * Applies the end game effect, if a card has one previously decorated.
     *
     * @param player player to whom apply the effect
     */
    void applyEndgameEffect(Player player);

    /**
     * Gets the JSON node of a single card object, saved during initialization and decoration.
     *
     * @return higher node of a single card object in the JSON of the cards
     */
    JsonNode getJSONDescriptionOfCards();

    void onHarvest (JsonNode move, FamilyMember fm) throws ActionAbortedException;

    void undoOnHarvest (JsonNode move, FamilyMember fm) throws ActionAbortedException;

    void onProduction (JsonNode move, FamilyMember fm) throws ActionAbortedException;

    void undoOnProduction (JsonNode move, FamilyMember fm) throws ActionAbortedException;

    void onAction (JsonNode move, FamilyMember fm, iActionSpace space);

    void undoOnAction (JsonNode move, FamilyMember fm, iActionSpace space);

    int getEra();

    String getName();

    Card.CardType getCardType();

    Board getBoard();

    int getActionValue();
}
