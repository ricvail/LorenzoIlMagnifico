package it.polimi.ingsw.pc42.Control;


public interface iResourceWrapper extends iIntWrapper {

    /**
     * Returns the resource type that the class has wrapped.
     *
     * @return the resource type to which is tied
     */
    ResourceType getResourceType();

    /**
     * Adds the quantity passed to the resource that is wrapped.
     *
     * @param i quantity to be add
     * @throws IllegalArgumentException if at the end the operation the result is negative
     */
    void add(int i) throws IllegalArgumentException;

}
