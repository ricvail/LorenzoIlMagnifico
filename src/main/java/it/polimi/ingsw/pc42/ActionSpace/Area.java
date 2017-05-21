package it.polimi.ingsw.pc42.ActionSpace;


public enum Area {
    TERRITORY("territory"), BUILDING("building"), CHARACTER("character"), VENTURE("venture"),
    COUNCIL("council"), MARKET("market"), HARVEST("harvest"), PRODUCTION("production");

    private String area;

    Area(String area){this.area=area;}

    public String getAreaString(){
        return area;
    }
}
