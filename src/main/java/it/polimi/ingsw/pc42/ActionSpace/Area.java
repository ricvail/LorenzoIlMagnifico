package it.polimi.ingsw.pc42.ActionSpace;


public enum Area {
    TERRITORY("territory"), BUILDING("building"), CHARACTER("character"), VENTURE("venture"),
    COUNCIL("council"), MARKET("market"), HARVEST("harvest"), PRODUCTION("production");

    private String area;

    Area(String area){this.area=area;}

    public String getAreaString(){
        return area;
    }

    public static Area fromString(String a){
        for (Area area : Area.values()) {
            if (area.getAreaString().equalsIgnoreCase(a)) {
                return area;
            }
        }
        throw new IllegalArgumentException();//TODO throw more specific exception
    }
}
