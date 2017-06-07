package it.polimi.ingsw.pc42.Control;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionSpace.ActionSpace;

import java.util.ArrayList;

/**
 * Created by RICVA on 06/06/2017.
 */
public class ActionAbortedException extends Exception{
    public boolean isComplete;
    public boolean isValid;
    public String nextMoveField;
    public JsonNode availableChoices;
    public ActionAbortedException(boolean isValid){
        this.isComplete=true;
        this.isValid =isValid; //depends on whether the action was aborted by the user or because it was not valid
    }
    public ActionAbortedException (String nextMoveField, JsonNode availableChoices){
        this.nextMoveField=nextMoveField;
        this.availableChoices = availableChoices;
        isComplete=false;
        isValid=true;
    }


}