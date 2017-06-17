package it.polimi.ingsw.pc42.Control;

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
        myInt+=i;
        if (myInt<0) {
            throw new IllegalArgumentException();
        }
    }

}
