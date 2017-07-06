package it.polimi.ingsw.pc42.Control;

public interface iIntWrapper {

    int get();

    /**
     * Sets an attribute equal to the parameter or throws an exception if negative.
     *
     * @param i quantity to set
     * @throws IllegalArgumentException if the quantity is negative
     */
    void set(int i) throws IllegalArgumentException;
}
