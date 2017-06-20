package it.polimi.ingsw.pc42.Control;

import java.util.ArrayList;

/**
 * Created by RICVA on 16/05/2017.
 */
public class ResourceWrapper extends IntWrapper implements iResourceWrapper {

    ResourceType type;

    CostBonus active;

    ArrayList<CostBonus> costBonuses;

    public ResourceWrapper(ResourceType t, int q){
        super (q);
        type=t;
        costBonuses=new ArrayList<>();
    }
    public ResourceWrapper(ResourceType t){
        super ();
        type=t;
        costBonuses=new ArrayList<>();
    }

    public void enableBonus(int actionID){
        active=getBonus(actionID);
    }
    public CostBonus getBonus(int actionID){
        for (CostBonus b : costBonuses){
            if (b.actionID==actionID){
                return b;
            }
        }
        CostBonus b =new CostBonus(0, 0, actionID);
        costBonuses.add(b);
        return b;
    }

    public void addBonus(int actionID, int bonus){
        CostBonus b = getBonus(actionID);
        b.initialBonus+= bonus;
        b.remainingBonus+=bonus;
    }

    public void addUsingBonus(int q){
        if (active==null){
            active=new CostBonus(0, 0, -1);
        }
        if (q<0){
            q*=-1;
            if (active.remainingBonus>q){
                active.remainingBonus-=q;
            } else {
                int toSubtract=q-active.remainingBonus;
                active.remainingBonus=0;
                add(toSubtract*-1);
            }
        } else {
            add(q);
        }
    }

    public void undoAddUsingBonus(int q){
        if (q<0&&active.remainingBonus<active.initialBonus){
            q*=-1;
            int bonusUsed=active.initialBonus-active.remainingBonus;
            if (q<=bonusUsed){
                active.remainingBonus+=q;
            }else {
                active.remainingBonus=active.initialBonus;
                add(q-bonusUsed);
            }
        } else {
            add(q*-1);
        }


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

    class CostBonus {
        public CostBonus(int initialBonus, int remainingBonus, int actionID) {
            this.initialBonus = initialBonus;
            this.remainingBonus = remainingBonus;
            this.actionID = actionID;
        }

        int initialBonus, remainingBonus, actionID;
    }

}
