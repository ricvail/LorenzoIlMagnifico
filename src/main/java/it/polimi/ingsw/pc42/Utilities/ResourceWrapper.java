package it.polimi.ingsw.pc42.Utilities;

import it.polimi.ingsw.pc42.ResourceType;

/**
 * Created by RICVA on 16/05/2017.
 */
public class ResourceWrapper extends IntWrapper implements iResourceWrapper {

    ResourceType type;

    public ResourceWrapper(ResourceType t, int q){
        super (q);
        type=t;
    }
    public ResourceWrapper(ResourceType t){
        super ();
        type=t;
    }

    @Override
    public ResourceType getResourceType() {
        return type;
    }

    @Override
    public void add(int i) throws IllegalArgumentException {
        if (myInt+i<0) {
            throw new IllegalArgumentException();
        }
        myInt+=i;
    }

}
