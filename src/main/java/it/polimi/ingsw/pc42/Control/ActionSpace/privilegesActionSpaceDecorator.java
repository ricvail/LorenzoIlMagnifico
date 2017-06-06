package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Control.PrivilegeManager;



public class privilegesActionSpaceDecorator extends AbstractDecorator {

    //TODO

    private int quantity;

    public privilegesActionSpaceDecorator(int q, iActionSpace actionSpace) {
        super(actionSpace);
        this.quantity=q;
    }

    private boolean checkPrivilegesChoiceLength(JsonNode json){
        if (!(json.has("privileges")&&json.get("privileges").isArray())) {
            return false;
        }
        int len = ((ArrayNode) json.get("privileges")).size();
        return (len==quantity);
    }

    @Override
    public void placeFamilyMember(FamilyMember familyMember, JsonNode json) {
        PrivilegeManager.applyDifferentPrivileges(familyMember.owner, json.get("privileges"));
        super.placeFamilyMember(familyMember, json);
    }
}
