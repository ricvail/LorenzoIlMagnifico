package it.polimi.ingsw.pc42.Control;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.pc42.Control.ActionSpace.iActionSpace;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Model.Player;

import java.io.File;
import java.io.IOException;

/**
 * Created by RICVA on 13/06/2017.
 */
public class MoveManager {
    public static void makeMove (Board b, JsonNode move) throws Exception {
        makeMove (b, b.getCurrentPlayer(), move);

        if (move.has("checking")&&move.get("checking").asBoolean()){
            //nothing
        } else {
        }
    }



    public static void undoMove(Board b, Player p, JsonNode move) throws Exception {
        undoGetFamilyMemberFromJson(b, move, p);
    }

    public static void makeMove(Board b, Player p, JsonNode move) throws Exception {
        if (!b.isPlayerTurn(p)){
            throw new Exception("it's not this player's turn");
        }
        if (b.isVatican()){
            makeVaticanChoice(b, p, move);
            b.endVaticanPlayerTurn();
        } else {
            getFamilyMemberFromJson(b, move, p);
            undoMove(b, b.getCurrentPlayer(), move);
            getFamilyMemberFromJson(b, move, p);
            if (move.has("checking") && move.get("checking").asBoolean()) {
                undoMove(b, b.getCurrentPlayer(), move);
                System.out.print("checking");
                throw new ActionAbortedException(true, "checking move finished");
            } else {
                b.endPlayerTurn();

            }
        }
    }

    public static void makeVaticanChoice(Board b, Player p, JsonNode move) throws Exception {
        if (move.has("vaticanChoice")){
            if (move.get("vaticanChoice").asBoolean()){
                if (enoughFaithPoints(p, b)){
                    Board.giveUpFaithPoints(p);
                } else{
                    throw new ActionAbortedException(false, "You don't have enough faith points to avoid the excommunication");
                }
            } else {
                //TODO give Excommunication
            }
        }else {
            throw new ActionAbortedException("vaticanChoice", null);
        }
    }

    private static void undoGetFamilyMemberFromJson(Board b, JsonNode move, Player p) throws ActionAbortedException {
        FamilyMember fm=null;
        try {
            fm  = p.getFamilyMemberFromColor(move.get("familyMember").asText());
        } catch (Exception e) {
            e.printStackTrace();
        }
        undoGetActionSpaceFromJson(b, move, fm);
    }

    private static void getFamilyMemberFromJson(Board b, JsonNode move, Player p) throws ActionAbortedException {
        if (!move.has("familyMember")){
            throw new ActionAbortedException("familyMember", p.getUnusedFamilyMembersList());
        }
        FamilyMember fm;
        try {
            fm  = p.getFamilyMemberFromColor(move.get("familyMember").asText());
        } catch (Exception e) {
            throw new ActionAbortedException(false, "Wrong Family Member's Color");
        }
        if (fm.isUsed()){
            throw new ActionAbortedException(false, "This Family Member is used");
        }else {
            getActionSpaceFromJson(b, move, fm);
        }
    }


    public static void undoGetActionSpaceFromJson(Board b, JsonNode move, FamilyMember fm) throws ActionAbortedException {
        iActionSpace space=null;
        try {
            space = b.getActionSpaceByID(move.get("slotID").asInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
        undoApplyPlayerPermanentBonus(move, fm, space);
    }
    public static void getActionSpaceFromJson(Board b, JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (!move.has("slotID")){
            //TODO generate list of possible action spaces
            throw new ActionAbortedException("slotID", null);
            //throw new ActionAbortedException(false);//temp
        }
        iActionSpace space;
        try {
            space = b.getActionSpaceByID(move.get("slotID").asInt());
        } catch (Exception e) {
            throw new ActionAbortedException(false, "This Action Space does not exist or the ID is not an integer");
        }
        if (fm.canBePlacedInArea(space.getArea())&&
                b.getNumberOfPlayers()>=space.getMinimumNumberOfPlayers()){
            applyPlayerPermanentBonus(move, fm, space);
        } else {
            throw new ActionAbortedException(false, "Family Member can't be place in this Area"
                                                    +" or this Action Space is not active in "+b.getNumberOfPlayers()+" players game-mode");
        }
    }

    private static void undoApplyPlayerPermanentBonus(JsonNode move, FamilyMember fm, iActionSpace space) throws ActionAbortedException {
        undoApplyServants(move, fm, space);
        for (ResourceWrapper w:fm.owner.resources) {
            w.resetBonus();
        }
    }
    private static void applyPlayerPermanentBonus(JsonNode move, FamilyMember fm, iActionSpace space) throws ActionAbortedException {
        /**
         * skipped for the moment
         *      card must have an "apply Permanent CostBonus" method (params familyMember and ActionSpace)
         *          change value of FamilyMember
         *          Enable resourceWrapper CostBonus
         *      Also an undo permanent CostBonus (for the catch segment)
         */

        applyServants(move, fm, space);
        for (ResourceWrapper w:fm.owner.resources) {
            w.resetBonus();
        }
    }

    private static void undoApplyServants(JsonNode move, FamilyMember fm, iActionSpace space) throws ActionAbortedException {
        int servants = move.get("servants").asInt();
        fm.owner.getResource(ResourceType.SERVANT).add(servants);
        fm.setValue(fm.getValue()-servants);
        undoCheckActionValue(move, fm, space);
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
                throw new ActionAbortedException(false, "Not enough servants");
            }
        } else{
            throw new ActionAbortedException(false, "Servants must be integer");
        }
    }

    private static void undoCheckActionValue(JsonNode move, FamilyMember fm, iActionSpace space) throws ActionAbortedException {
        undoAction(move, fm, space);
    }

    private static void checkActionValue(JsonNode move, FamilyMember fm, iActionSpace space) throws ActionAbortedException {
        int required = space.getMinimumActionValue(fm);
        if (fm.getValue()>=required){
            performAction(move, fm, space);
        } else {
            throw new ActionAbortedException(false, "Family Member's Action Value lower than requirement");
        }
    }

    private static void undoAction(JsonNode move, FamilyMember fm, iActionSpace space) throws ActionAbortedException {
        fm.setUsed(false);
        space.undoAction(move, fm);
        space.getFamilyMembers().remove(fm);
    }

    private static void performAction(JsonNode move, FamilyMember fm, iActionSpace space) throws ActionAbortedException {
        fm.setUsed(true);
        space.getFamilyMembers().add(fm);
        try {
            space.performAction(move, fm);
        } catch (ActionAbortedException e){
            fm.setUsed(false);
            space.getFamilyMembers().remove(fm);
            throw e;
        }
    }


    private static int getRequiredServants(JsonNode move, FamilyMember fm, iActionSpace space){
        int required = space.getMinimumActionValue(fm);
        if (fm.getValue()>=required) return 0;
        else return (required-fm.getValue());
    }

    private static boolean enoughFaithPoints(Player player, Board board){
        File faithPointsJson = new File("src/res/faithPoints.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root=null;
        try {
            root = mapper.readTree(faithPointsJson);
        } catch (IOException e){
            e.printStackTrace();
        }
        JsonNode excommunicationRequests = root.get("excommunicationRequests");
        int minFaithPoints = excommunicationRequests.get(board.getEra()-2).asInt();
        if (player.getResource(ResourceType.FAITHPOINTS).get()<minFaithPoints){
            return false;
        }
        return true;
    }

    public static JsonNode nodeGhostMove(String familyMember){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode ghostNode = mapper.createObjectNode();
        ghostNode.put("DESCRIZIONE", "ghost");
        ghostNode.put("familyMember", familyMember);
        ghostNode.put("servants", 0);
        ghostNode.put("slotID", 0);
        return ghostNode;
    }
}