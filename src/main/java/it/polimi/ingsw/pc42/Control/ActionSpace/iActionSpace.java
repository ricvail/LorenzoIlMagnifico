package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.FamilyMember;


import java.util.ArrayList;

public interface iActionSpace {

    /**
     * Describes, for each decorator, the step that has to perform to undo the placing of a family member and the
     * consequent actions.
     *
     * @param move node of the move
     * @param fm family member that was placed
     */
    void undoAction (JsonNode move, FamilyMember fm);

    /**
     * With some differences, based on the type of action space decorator that overrides it, describes the sequence of
     * controls and actions that happens when placing a family member.
     *
     * @param move node of the move
     * @param fm family member that has to be placed
     * @throws ActionAbortedException re-throws from the callee if a certain point the action aborts
     */
    void performAction(JsonNode move, FamilyMember fm) throws ActionAbortedException;

    /**
     * At the end of round, clean the action space from the family member, if placed, and from the card, if decorated
     * with one.
     */
    void cleanup();

    ArrayList<FamilyMember> getFamilyMembers();

    Area getArea();

    int getID();

    /**
     * Returns the number of family members, placed in an action space, that are: visible, not the same color and not
     * of the same player as the one passed as parameter.
     *
     * @param fm family member that has to be placed
     * @return the number of family members that pass the control
     */
    int getNumberOfVisibleFamilyMembers(FamilyMember fm);

    Board getBoard();

    int getMinimumActionValue(FamilyMember fm);

    int getMinimumNumberOfPlayers();

    /**
     * Creates a JSON description of the state of an action space: like player, family members placed, a card
     * if is decorated with one. Then adds it to the node passed as parameter and returns the updated node.
     *
     * @param node node to be updated
     * @return the updated node
     */
    ObjectNode updateDescription (ObjectNode node);

}
