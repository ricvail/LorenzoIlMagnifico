package it.polimi.ingsw.pc42.Control;

import com.fasterxml.jackson.databind.JsonNode;

public class ActionAbortedException extends Exception{
    private boolean isComplete;
    private boolean isValid;

    public boolean isComplete() {
        return isComplete;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getNextMoveField() {
        return nextMoveField;
    }

    public JsonNode getAvailableChoices() {
        return availableChoices;
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
     * Class constructor for invalid or complete moves.
     *
     * @param isValid <code>true</code> if the move is valid until this point
     * @param message detailed message of the cause
     */
    public ActionAbortedException(boolean isValid, String message) {
        super(message);
        this.isComplete = isValid;
        this.isValid = isValid;
        isCardChoice=false;
    }

    /**
     *Class constructor for incomplete moves.
     *
     * @param nextMoveField next fieldname of thee move
     * @param availableChoices node of the list of available choices
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