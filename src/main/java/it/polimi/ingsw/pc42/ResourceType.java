package it.polimi.ingsw.pc42;

public enum ResourceType{
    STONE("stone"), WOOD("wood"), SERVANT("servant"), COIN("coin"), FAITH("faith"), VICTORY("victory"), MILITARY("military");

    private String resourceType;
    ResourceType(String resourceType){
        this.resourceType = resourceType;
    }

    public String getRTString(){
        return resourceType;
    }

    public static ResourceType stringToResourceType(String rt) {
        for (ResourceType resourceType : ResourceType.values()) {
            if (resourceType.getRTString().equals(rt)) {
                return resourceType;
            }
        }
        throw new IllegalArgumentException(); //TODO more specific ex
    }


}
