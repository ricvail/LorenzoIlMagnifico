package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Model.Player;

/**
 * Created by RICVA on 13/06/2017.
 */
public class MoveManager {
    public static void makeMove (Board b, JsonNode move) throws Exception {
        makeMove (b, b.getCurrentPlayer(), move);
        b.endPlayerTurn();
    }

    public static void makeMove(Board b, Player p, JsonNode move) throws Exception {
        if (!b.isPlayerTurn(p)){
            throw new Exception("it's not this player's turn");
        }

        getFamilyMemberFromJson(b, move, p);
    }
    private static void getFamilyMemberFromJson(Board b, JsonNode move, Player p) throws ActionAbortedException {
        if (!move.has("familyMember")){
            throw new ActionAbortedException("familyMember", p.getUnusedFamilyMembersList());
        }
        FamilyMember fm;
        try {
            fm  = p.getFamilyMemberFromColor(move.get("familyMember").asText());
        } catch (Exception e) {
            throw new ActionAbortedException(false);
        }
        if (fm.isUsed()){
            throw new ActionAbortedException(false);
        }else {
            getActionSpaceFromJson(b, move, fm);
        }
    }


    private static void getActionSpaceFromJson(Board b, JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (!move.has("slotID")){
            //TODO generate list of possible action spaces
            //throw new ActionAbortedException("slotID", b.getPossibleSlotList(fm));
            throw new ActionAbortedException(false);//temp
        }
        iActionSpace space;
        try {
            space = b.getActionSpaceByID(move.get("slotID").asInt());
        } catch (Exception e) {
            throw new ActionAbortedException(false);
        }
        if (fm.canBePlacedInArea(space.getArea())&&
                b.getNumberOfPlayers()>=space.getMinimumNumberOfPlayers()){
            applyPlayerPermanentBonus(move, fm, space);
        } else {
            throw new ActionAbortedException(false);
        }
    }

    private static void applyPlayerPermanentBonus(JsonNode move, FamilyMember fm, iActionSpace space) throws ActionAbortedException {
        /**
         * skipped for the moment
         *      card must have an "apply Permanent bonus" method (params familyMember and ActionSpace)
         *          change value of FamilyMember
         *          Enable resourceWrapper bonus
         *      Also an undo permanent bonus (for the catch segment)
         */
        applyServants(move, fm, space);
    }

    private static void applyServants(JsonNode move, FamilyMember fm, iActionSpace space) throws ActionAbortedException {
        if (!move.has("servants")){
            JsonNodeFactory factory=JsonNodeFactory.instance;
            ArrayNode list=factory.arrayNode();
            list.add( getRequiredServants(move, fm, space));
            throw new ActionAbortedException("servants",list);
        }
        if (move.get("servants").isInt()){
            int servants = move.get("servants").asInt();
            if (fm.owner.getResource(ResourceType.SERVANT).get()>=servants){
                fm.owner.getResource(ResourceType.SERVANT).add(servants*-1);
                fm.setValue(fm.getValue()+servants);
                try {
                    checkActionValue(move, fm, space);
                } catch (ActionAbortedException e){
                    fm.owner.getResource(ResourceType.SERVANT).add(servants);
                    fm.setValue(fm.getValue()-servants);
                    throw e;
                }
            } else{
                throw new ActionAbortedException(false);
            }
        } else{
            throw new ActionAbortedException(false);
        }
    }

    private static void checkActionValue(JsonNode move, FamilyMember fm, iActionSpace space) throws ActionAbortedException {
        int required = space.getMinimumActionValue(fm);
        if (fm.getValue()>=required){
            space.performAction(move, fm);
        } else {
            throw new ActionAbortedException(false);
        }
    }

    private static int getRequiredServants(JsonNode move, FamilyMember fm, iActionSpace space){
        int required = space.getMinimumActionValue(fm);
        if (fm.getValue()>=required) return 0;
        else return (required-fm.getValue());
    }
}