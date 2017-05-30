package it.polimi.ingsw.pc42;


import it.polimi.ingsw.pc42.ActionSpace.iActionSpace;
import it.polimi.ingsw.pc42.DevelopmentCards.iCard;

import java.util.ArrayList;

public interface iBoard {

    ArrayList<iActionSpace> getActionSpaces();

    int getEra();

    ArrayList<iCard> getCards();

    //ArrayList<Player> turnOrder;

}