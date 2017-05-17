package it.polimi.ingsw.pc42.Utilities;


import it.polimi.ingsw.pc42.ResourceType;

public interface iResourceWrapper extends iIntWrapper {
    ResourceType getResourceType();
    void add(int i) throws IllegalArgumentException;

}
