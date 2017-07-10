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
import it.polimi.ingsw.pc42.Utilities.myException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class MoveManager {

    private static Logger logger= LogManager.getLogger();
    /**
     *  Starts the process to execute the move, taking the current player and delegating the rest.
     *
     * @param b board of the current game
     * @param move higher node of a JSON object that describes the move to be executed
     * @throws Exception re-throws from the callee
     */
    public static void makeMove (Board b, JsonNode move) throws myException, ActionAbortedException {
        makeMove (b, b.getCurrentPlayer(), move);

        if (move.has("checking")&&move.get("checking").asBoolean()){
            //nothing
        } else {
        }
    }

    /**
     * Starts the process to undo the move, taking the board, the player and the node of the move.
     *
     * @param b board of the current game
     * @param p the player that has to make the move
     * @param move higher node of a JSON object that describes the move to be reverted
     * @throws Exception re-throws from the callee
     */
    public static void undoMove(Board b, Player p, JsonNode move) throws myException, ActionAbortedException {
        undoGetFamilyMemberFromJson(b, move, p);
    }

    /**
     * Checks if is the correct player that try to make the move, if it is the vatican phase, then execute the move
     * delegating the single sub-parts of it.
     *
     * @param b board of the current game
     * @param p the player that has to make the move
     * @param move higher node of a JSON object that describes the move to be executed
     * @throws Exception if was a checking move, to not end the turn, or re-throws
     */
    public static void makeMove(Board b, Player p, JsonNode move) throws myException, ActionAbortedException {
        if (!b.isPlayerTurn(p)){
            throw new myException("it's not this player's turn");
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

    /**
     * Takes the choice of the player for the Vatican phase, then checks if has enough resources and make the move,
     * delegating them. If the player doesn't have enough resources or the JSON of the move is not valid throws exception.
     *
     * @param b board of the current game
     * @param p the player that has to make the move
     * @param move higher node of a JSON object that describes the move to be executed
     * @throws Exception if the player can't do that choice in the Vatican phase or if the JSON of the move is not valid
     */
    public static void makeVaticanChoice(Board b, Player p, JsonNode move) throws myException, ActionAbortedException {
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

    /**
     * Continues the undo move, re-picking the family member and passing it to the callee, that deals with the action
     * space.
     *
     * @param b board of the current game
     * @param move higher node of a JSON object that describes the move to be reverted
     * @param p the player that has to make the move
     * @throws ActionAbortedException re-throws from the callee
     */
    private static void undoGetFamilyMemberFromJson(Board b, JsonNode move, Player p) throws ActionAbortedException {
        FamilyMember fm=null;
        try {
            fm  = p.getFamilyMemberFromColor(move.get("familyMember").asText());
        } catch (Exception e) {
            logger.error(e);
        }
        undoGetActionSpaceFromJson(b, move, fm);
    }

    /**
     * Checks the specifications to get the proper family family member and delegates the control on the action space.
     * Throws exception if the field is missing or the tied specification doesn't pass the control.
     *
     * @param b board of the current game
     * @param move higher node of a JSON object that describes the move to be executed
     * @param p the player that has to make the move
     * @throws ActionAbortedException if there isn't the family member field in the JSON of the move, if it is used or
     * if the color is wrong
     */
    private static void getFamilyMemberFromJson(Board b, JsonNode move, Player p) throws ActionAbortedException {
        if (!move.has("familyMember")){
            throw new ActionAbortedException("familyMember", p.getUnusedFamilyMembersList());
        }
        FamilyMember fm;
        try {
            fm  = p.getFamilyMemberFromColor(move.get("familyMember").asText());
        } catch (Exception e) {
            logger.info(e);
            throw new ActionAbortedException(false, "Wrong Family Member's Color");
        }
        if (fm.isUsed()){
            throw new ActionAbortedException(false, "This Family Member is used");
        }else {
            getActionSpaceFromJson(b, move, fm);
        }
    }

    /**
     * Continues the undo move, re-picking the action spaces to be freed and passing it to the callee that undo the
     * permanent bonus.
     *
     * @param b board of the current game
     * @param move higher node of a JSON object that describes the move to be reverted
     * @param fm family member that was placed
     * @throws ActionAbortedException re-throws from the callee
     */
    public static void undoGetActionSpaceFromJson(Board b, JsonNode move, FamilyMember fm) throws ActionAbortedException {
        iActionSpace space=null;
        try {
            space = b.getActionSpaceByID(move.get("slotID").asInt());
        } catch (Exception e) {
            logger.error(e);
        }
        undoApplyPlayerPermanentBonus(move, fm, space);
    }

    /**
     * Checks the specifications to get the proper action space and checks if the family member can be placed in it,
     * then delegates the control on the permanent bonus. Throws exception if the field is missing, the tied
     * specification doesn't pass the control or the "placing" move is illegal.
     *
     * @param b board of the current game
     * @param move higher node of a JSON object that describes the move to be executed
     * @param fm family member that has to be placed
     * @throws ActionAbortedException if field is missing, the specification doesn't pass the control or the "placing"
     * move is illegal
     */
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
            logger.info(e);
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

    /**
     * Continues the undo move, delegating the removal of the bonus value from the servants. Then reset the bonuses in
     * the wrappers of the player's resources.
     *
     * @param move higher node of a JSON object that describes the move to be reverted
     * @param fm family member that was placed
     * @param space action space that held the family member
     * @throws ActionAbortedException re-throws from the callee
     */
    private static void undoApplyPlayerPermanentBonus(JsonNode move, FamilyMember fm, iActionSpace space) throws ActionAbortedException {
        fm.owner.applyPermanentBonuses(move, fm, space);
        undoApplyServants(move, fm, space);
        fm.owner.undoApplyPermanentBonuses(move, fm, space);
    }

    /**
     * ATM delegates the application of servants to a callee, that continues long the "chain" to execute the move. Then
     * reset the bonuses in the wrappers of the player's resources.
     *
     * @param move higher node of a JSON object that describes the move to be executed
     * @param fm family member that has to be placed
     * @param space action space that has to hold the family member
     * @throws ActionAbortedException re-throws from the callee
     */
    private static void applyPlayerPermanentBonus(JsonNode move, FamilyMember fm, iActionSpace space) throws ActionAbortedException {
        /*
         * skipped for the moment
         *      card must have an "apply Permanent CostBonus" method (params familyMember and ActionSpace)
         *          change value of FamilyMember
         *          Enable resourceWrapper CostBonus
         *      Also an undo permanent CostBonus (for the catch segment)
         */
        fm.owner.applyPermanentBonuses(move, fm, space);
        try {
            applyServants(move, fm, space);
        } catch (ActionAbortedException e){
            fm.owner.undoApplyPermanentBonuses(move, fm, space);
            throw e;
        }
        fm.owner.undoApplyPermanentBonuses(move, fm, space);
    }

    /**
     * Continues the undo move, re-adds the servants to the player, re-sets the action value and delegates the
     * "climb" upward of the move to the callee.
     *
     * @param move higher node of a JSON object that describes the move to be reverted
     * @param fm family member that was placed
     * @param space action space that held the family member
     * @throws ActionAbortedException re-throws from the callee
     */
    private static void undoApplyServants(JsonNode move, FamilyMember fm, iActionSpace space) throws ActionAbortedException {
        undoCheckActionValue(move, fm, space);
        int servants = move.get("servants").asInt();
        fm.owner.getResource(ResourceType.SERVANT).add(servants);
        fm.setValue(fm.getValue()-servants);
    }

    /**
     * Checks for the servants in the move node and, eventually, increments the action value of the family member and
     * remove the correspondent servants from the player. Then delegates to the callee the checking of the value to
     * execute the move. Throws exception if the field is missing, if the player don't have enough servants, the
     * specification is of the wrong type or re-throws it from the callee.
     *
     * @param move higher node of a JSON object that describes the move to be executed
     * @param fm family member that has to be placed
     * @param space action space that has to hold the family member
     * @throws ActionAbortedException if the field is missing, if the player don't have enough servants, the
     * specification is of the wrong type or re-throws it from the callee
     */
    private static void applyServants(JsonNode move, FamilyMember fm, iActionSpace space) throws ActionAbortedException {
        if (!move.has("servants")){
            JsonNodeFactory factory=JsonNodeFactory.instance;
            ArrayNode list=factory.arrayNode();
            list.add( getRequiredServants(fm, space));
            throw new ActionAbortedException("servants",list);
        }
        if (move.get("servants").isInt()&&move.get("servants").asInt()>=0){
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

    /**
     * Continues the undo move, delegates the undo of the performed action to the callee.
     *
     * @param move higher node of a JSON object that describes the move to be reverted
     * @param fm family member that was placed
     * @param space action space that held the family member
     * @throws ActionAbortedException re-throws it from the callee
     */
    private static void undoCheckActionValue(JsonNode move, FamilyMember fm, iActionSpace space) throws ActionAbortedException {
        undoAction(move, fm, space);
    }

    /**
     *Checks the min value to place the family member and in the case delegates the performing of the action. Throws
     * exception if family member's action value is lower than requirement or re-throws it from the callee.
     *
     * @param move higher node of a JSON object that describes the move to be executed
     * @param fm family member that has to be placed
     * @param space action space that has to hold the family member
     * @throws ActionAbortedException if family member's action value is lower than requirement or re-throws it from the
     * callee
     */
    private static void checkActionValue(JsonNode move, FamilyMember fm, iActionSpace space) throws ActionAbortedException {
        int required = space.getMinimumActionValue(fm);
        if (fm.getValue()>=required){
            performAction(move, fm, space);
        } else {
            throw new ActionAbortedException(false, "Family Member's Action Value lower than requirement");
        }
    }

    /**
     * Continues the undo move, sets unused the family member delegates the undo of the placing of the family member
     * to the callee.
     *
     * @param move higher node of a JSON object that describes the move to be reverted
     * @param fm family member that was placed
     * @param space action space that held the family member
     * @throws ActionAbortedException re-throws it from the callee
     */
    private static void undoAction(JsonNode move, FamilyMember fm, iActionSpace space) throws ActionAbortedException {
        fm.setUsed(false);
        space.undoAction(move, fm);
        space.getFamilyMembers().remove(fm);
    }

    /**
     * Performs the action tied to the action space or re-throws an exception.
     *
     * @param move higher node of a JSON object that describes the move to be executed
     * @param fm family member placed
     * @param space action space that holds the family member
     * @throws ActionAbortedException re-throws it from the callee
     */
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

    /**
     * Checks if a family member action value matches the requirement of an action space, if it doesn't return the
     * difference, that is the required servants to perform the placing.
     *
     * @param fm family member that has to be placed
     * @param space action space that has to hold the family member
     * @return 0 if the action value matches the requirement, else the difference between the values
     */
    private static int getRequiredServants(FamilyMember fm, iActionSpace space){
        int required = space.getMinimumActionValue(fm);
        if (fm.getValue()>=required) return 0;
        else return (required-fm.getValue());
    }

    /**
     * Checks, according to the game rule, if the the player has enough faith points to avoid the excommunication in
     * the current era.
     *
     * @param player current player of the Vatican phase
     * @param board  board of the current game
     * @return <code>true</code> if the player has enough resource to avoid excommunication
     */
    private static boolean enoughFaithPoints(Player player, Board board){
        File faithPointsJson = new File("src/res/faithPoints.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root=null;
        JsonNode json;
        try {
            root = mapper.readTree(faithPointsJson);
        } catch (IOException e){
            logger.error(e);
        }
        json=root;
        if (json!=null){
            int minFaithPoints = json.get("excommunicationRequests").get(board.getEra()-2).asInt();
            if (player.getResource(ResourceType.FAITHPOINTS).get()<minFaithPoints){
                return false;
            }
        }
        return true;
    }

    /**
     *Creates a JSON and maps it, returning the higher node, that represent the null move to be executed by some
     * family member.
     *
     * @param familyMember family member color
     * @return the higher node for the JSON object that describe the null move
     */
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