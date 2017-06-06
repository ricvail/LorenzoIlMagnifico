package it.polimi.ingsw.pc42.Control;


public interface iResourceWrapper extends iIntWrapper {
    ResourceType getResourceType();
    void add(int i) throws IllegalArgumentException;

}
