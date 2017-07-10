package it.polimi.ingsw.pc42.Control;

import com.fasterxml.jackson.databind.JsonNode;

public class ActionAbortedException extends Exception{
    public boolean isComplete;
    public boolean isValid;
    public String nextMoveField;
    public transient JsonNode availableChoices;
    public int level;
    public boolean isCardChoice;
    public int card;

    /**
     * Class constructor. The <code>boolean</code> parameter indicates if the action that aborts is completed.
     *
     * @param isValid
     * @param message
     */
    public ActionAbortedException(boolean isValid, String message) {
        super(message); //detailed message of the cause
        this.isComplete = isValid;
        this.isValid = isValid;
        isCardChoice=false;
    }

    /**
     *TODO javadoc4
     * @param nextMoveField
     * @param availableChoices
     */
    public ActionAbortedException (String nextMoveField, JsonNode availableChoices){
        this.nextMoveField=nextMoveField;
        this.availableChoices = availableChoices;
        isComplete=false;
        isValid=true;
        level = 0;
        isCardChoice=false;
    }


}