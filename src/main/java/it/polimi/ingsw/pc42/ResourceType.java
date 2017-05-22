package it.polimi.ingsw.pc42;

public enum ResourceType{
    STONE("stone"), WOOD("wood"), SERVANT("servant"), COIN("coin"), FAITH("faith"), VICTORY("victory"), MILITARY("military");

    private String resourceType;
    ResourceType(String resourceType){
        this.resourceType = resourceType;
    }

    public String getString(){
        return resourceType;
    }

    public static ResourceType fromString(String rt) {
        for (ResourceType resourceType : ResourceType.values()) {
            if (resourceType.getString().equalsIgnoreCase(rt)) {
                return resourceType;
            }
        }
        throw new IllegalArgumentException();//TODO throw more specific exception
    }

}
