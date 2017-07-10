package it.polimi.ingsw.pc42.Control;

import com.fasterxml.jackson.databind.JsonNode;

public class ActionAbortedException extends Exception{
    private boolean isComplete;
    private boolean isValid;

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getNextMoveField() {
        return nextMoveField;
    }

    public void setNextMoveField(String nextMoveField) {
        this.nextMoveField = nextMoveField;
    }

    public JsonNode getAvailableChoices() {
        return availableChoices;
    }

    public void setAvailableChoices(JsonNode availableChoices) {
        this.availableChoices = availableChoices;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isCardChoice() {
        return isCardChoice;
    }

    public void setCardChoice(boolean cardChoice) {
        isCardChoice = cardChoice;
    }

    public int getCard() {
        return card;
    }

    public void setCard(int card) {
        this.card = card;
    }

    private String nextMoveField;
    private transient JsonNode availableChoices;
    private int level;
    private boolean isCardChoice;
    private int card;

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