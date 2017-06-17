package it.polimi.ingsw.pc42.Control;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import it.polimi.ingsw.pc42.Model.Player;

import java.util.Iterator;

/**
 * Created by RICVA on 21/05/2017.
 */
public class PrivilegeManager {

    private JsonNode privileges;

    /**
     * Expects a JsonArray of privileges
     * @param privileges
     */
    public PrivilegeManager(JsonNode privileges){
        this.privileges=privileges;
    }

    JsonNode getPrivileges(){
        return privileges;
    }

    public void applyPrivileges(Player p, JsonNode move, int q) throws ActionAbortedException {
        if ((!isPrivilegesChoiceLengthCorrect(move, q))){
            throw new ActionAbortedException("privileges", getListOfUnusedPrivileges(move));
        }
        if (!areAllPrivilegesDifferent(move.get("privileges"))){
            throw new ActionAbortedException(false);
        }
        try {
            applyDifferentPrivileges(p, move);
        } catch (Exception e) {
            //this should never happen
            e.printStackTrace();
        }

    }

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

    private void applyDifferentPrivileges(Player p, JsonNode j) throws Exception {
        int i = 0;
        while (j.get("privileges").has(i)){
            applyPrivilege(p, j.get("privileges").get(i).asInt(), getPrivileges());
            i++;
        }
    }

    private boolean isPrivilegesChoiceLengthCorrect(JsonNode json, int quantity){
        if (!(json.has("privileges")&&json.get("privileges").isArray())) {
            return false;
        }
        int len = json.get("privileges").size();
        return (len==quantity);
    }

    public void undoPrivileges(Player p, JsonNode j) throws Exception {
        int i=0;
        while (j.has(i)){
            undoPrivilege(p, j.get(i).asInt(), getPrivileges());
            i++;
        }
    }


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

    private void applyPrivilege(Player p,int i, JsonNode privileges) throws Exception {
        usePrivilege(p, i, privileges, false);
    }
    private void undoPrivilege(Player p,int i, JsonNode privileges) throws Exception {
        usePrivilege(p, i, privileges, true);
    }

    private void usePrivilege(Player p,int i, JsonNode privileges, boolean subtract) throws Exception {
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
