package it.polimi.ingsw.pc42.Control;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import it.polimi.ingsw.pc42.Model.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;

public class PrivilegeManager {

    private JsonNode privileges;
    private Logger logger;

    JsonNode getPrivileges(){
        return privileges;
    }

    /**
     * Class constructor. Expects a JSON object node of an array of privileges and sets the reference.
     *
     * @param privileges node of an array of privileges
     */
    public PrivilegeManager(JsonNode privileges){
        logger= LogManager.getLogger();
        this.privileges=privileges;
    }

    /**
     * Checks if the JSON of the privileges length is correct and if they are all different from each other, then
     * applies it to the player.
     *
     * @param p player to whom apply the privileges
     * @param move node of an array of privileges in a move
     * @param q  value to be compared to the array length
     * @throws ActionAbortedException if the array length is not correct and if there are two privileges that are equal
     */
    public void applyPrivileges(Player p, JsonNode move, int q) throws ActionAbortedException {
        if ((!isPrivilegesChoiceLengthCorrect(move, q))){
            throw new ActionAbortedException("privileges", getListOfUnusedPrivileges(move));
        }
        if (!areAllPrivilegesDifferent(move.get("privileges"))){
            throw new ActionAbortedException(false, "You've chosen the same privilege more than once");
        }
        try {
            applyDifferentPrivileges(p, move);
        } catch (Exception e) {
            logger.error(e);
        }

    }

    /**
     *  Returns an array node of only the unused privileges, maybe because aborted during the application.
     *
     * @param move node of an array of privileges in a move
     * @return array node of the unused privileges
     */
    private JsonNode getListOfUnusedPrivileges(JsonNode move){
        JsonNodeFactory factory=JsonNodeFactory.instance;
        ArrayNode list=factory.arrayNode();
        for (int i = 0; i<getPrivileges().size(); i++){
            if (!(move.has("privileges")&&move.get("privileges").isArray())) {
                list.add(i);
            } else {
                boolean used = false;
                Iterator<JsonNode> iterator = move.get("privileges").iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().asInt() == i) {
                        used = true;
                    }
                }
                if (!used) {
                    list.add(i);
                }
            }
        }
        return list;
    }

    /**
     * Iterates through the array of privileges and applies each one (must be already controlled) to the player.
     *
     * @param p player to whom apply the privileges
     * @param j node of an array of privileges in a move
     * @throws Exception re-throws from the callee that applies the privileges
     */
    private void applyDifferentPrivileges(Player p, JsonNode j) throws Exception {
        int i = 0;
        while (j.get("privileges").has(i)){
            applyPrivilege(p, j.get("privileges").get(i).asInt(), getPrivileges());
            i++;
        }
    }

    /**
     * Accesses the array node of privileges and checks its length, then compares it to the value taken as parameter.
     *
     * @param json node of an array of privileges in a move
     * @param quantity value to be compared to the array length
     * @return <code>true</code> if the length is correct
     */
    private boolean isPrivilegesChoiceLengthCorrect(JsonNode json, int quantity){
        if (!(json.has("privileges")&&json.get("privileges").isArray())) {
            return false;
        }
        int len = json.get("privileges").size();
        return (len==quantity);
    }

    /**
     * Iterates through the privileges and delegates the "undo" application for each one in the array.
     *
     * @param p player to whom subtract the privileges previously applied
     * @param j node of an array of privileges in a move
     * @throws Exception  re-throws from the callee
     */
    public void undoPrivileges(Player p, JsonNode j) throws Exception {
        int i=0;
        while (j.get("privileges").has(i)){
            undoPrivilege(p, j.get("privileges").get(i).asInt(), getPrivileges());
            i++;
        }
    }

    /**
     * Compares all the privileges to check if are all different from each other.
     *
     * @param j node of an array of privileges
     * @return <code>true</code> if they are all different
     */
    private boolean areAllPrivilegesDifferent(JsonNode j){
        int i =0;
        while (j.has(i)){
            if (!privileges.has(j.get(i).asInt())){
                return false;
            }
            int k =i+1;
            while (j.has(k)) {
                if (j.get(i).asInt() == j.get(k).asInt()){
                    return false;
                }
                k++;
            }
            i++;
        }
        return true;
    }

    /**
     * Delegates the real application of the single privilege, passes to the callee a <code>boolean</code> to specify
     * that is the "apply" privilege move.
     *
     * @param p player to whom apply the privileges
     * @param i index of the privilege in the array
     * @param privileges node of the array of privileges of the game
     * @throws Exception re-throws exception from the callee that tries to apply the privilege
     */
    private void applyPrivilege(Player p,  int i, JsonNode privileges) throws Exception {
        usePrivilege(p, i, privileges, false);
    }

    /**
     * Delegates the real subtraction of resources, passes to the callee a <code>boolean</code> to specify that is the
     * "undo" privileges move.
     *
     * @param p player to whom subtract the privileges previously applied
     * @param i index of the privilege in the array
     * @param privileges node of the array of privileges of the game
     * @throws Exception re-throws exception from the callee that tries to undo the privilege application
     */
    private void undoPrivilege(Player p, int i, JsonNode privileges) throws Exception {
        usePrivilege(p, i, privileges, true);
    }

    /**
     * Searches a match for the index passed in the privileges of the game, if finds it: adds or subtracts the
     * correspondent resources to the player, based on the <code>boolean</code> passed as parameter that specifies if it
     * is an undo move or not.
     *
     * @param p player to whom apply the privileges
     * @param i index of the privilege in the array
     * @param privileges node of the array of privileges of the game
     * @param subtract <code>true</code> if is an undo privilege move
     * @throws Exception if the privilege of the specified index does not exist
     */
    private void usePrivilege(Player p, int i, JsonNode privileges, boolean subtract) throws Exception {
        if (privileges.has(i)){
            Iterator<String> iterator = privileges.get(i).fieldNames();
            while (iterator.hasNext()){
                String key = iterator.next();
                ResourceType type = ResourceType.fromString(key);
                int q = privileges.get(i).get(key).asInt();
                if (subtract){
                    p.getResource(type).add(q*-1);
                } else{
                    p.getResource(type).add(q);
                }
            }
        } else{
            throw new Exception("A privilege with this number does not exist: "+ i);
        }
    }

}
