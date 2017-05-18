package it.polimi.ingsw.pc42.DevelopmentCards;

import it.polimi.ingsw.pc42.Player;
import it.polimi.ingsw.pc42.ResourceType;

import java.util.ArrayList;

public class ImmediateBonusChoice extends AbstractDecorator {

    private int choice=0; //To be passed as a parameter, this is a temporary solution for testing

    public final ArrayList<iCard> choices;

    public ImmediateBonusChoice(iCard c) {
        super(c);
        choices=new ArrayList<>();
        //choices.add(card);
    }

    public void addChoice(){
        choices.add(card);
    }


    @Override
    public void applyDrawEffect(Player player) {
        choices.get(choice).applyDrawEffect(player);

    }

}
