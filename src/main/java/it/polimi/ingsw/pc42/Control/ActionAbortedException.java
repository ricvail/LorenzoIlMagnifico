package it.polimi.ingsw.pc42.Control;

/**
 * Created by RICVA on 06/06/2017.
 */
public class ActionAbortedException extends Exception{
    public boolean isComplete;
    public boolean isValid;
    public String nextMoveField;
    public ActionAbortedException(boolean isComplete, boolean isValid){
        this.isComplete=isComplete;
        this.isValid =isValid;
    }

}