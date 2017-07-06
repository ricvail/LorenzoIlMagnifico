package it.polimi.ingsw.pc42.Control;

public enum ResourceType{
    STONE("stone"), WOOD("wood"), SERVANT("servants"), COIN("coins"), FAITHPOINTS("faithPoints"), VICTORYPOINTS("victoryPoints"), MILITARYPOINTS("militaryPoints");

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
        throw new IllegalArgumentException();
    }

}
