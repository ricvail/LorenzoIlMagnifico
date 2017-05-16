package it.polimi.ingsw.pc42.ActionSpace;


import it.polimi.ingsw.pc42.FamilyMember;

import java.util.ArrayList;

public abstract class AbstractDecorator extends AbstractActionSpace{
    private AbstractActionSpace actionSpace;

    public AbstractDecorator(AbstractActionSpace actionSpace){
        this.actionSpace = actionSpace;
    }

    boolean canPlace(FamilyMember familyMember){
        return actionSpace.canPlace(familyMember);
    }
    void placeFamilyMember(FamilyMember familyMember){
        actionSpace.placeFamilyMember(familyMember);
    }
    void cleanup(){
        actionSpace.cleanup();
    }
    ArrayList<FamilyMember> getFamilyMembers(){
        return actionSpace.getFamilyMembers();
    }
    Area getArea(){
        return actionSpace.getArea();
    }
}
