package it.polimi.ingsw.pc42.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.FamilyMember;
import it.polimi.ingsw.pc42.PrivilegeManager;



public class privilegesActionSpaceDecorator extends AbstractDecorator {

    //TODO

    private int quantity;

    public privilegesActionSpaceDecorator(int q, iActionSpace actionSpace) {
        super(actionSpace);
        this.quantity=q;
    }

    @Override
    public void placeFamilyMember(FamilyMember familyMember, JsonNode json) {

        PrivilegeManager.applyDifferentPrivileges(familyMember.owner, json.get("privileges"));
        super.placeFamilyMember(familyMember, json);
    }
}
