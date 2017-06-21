package it.polimi.ingsw.pc42.Control;

/**
 * Created by RICVA on 16/05/2017.
 */
public class ResourceWrapper extends IntWrapper implements iResourceWrapper {

    ResourceType type;

    public CostBonus activeBonus;


    public ResourceWrapper(ResourceType t, int q){
        super (q);
        type=t;
    }
    public ResourceWrapper(ResourceType t){
        super ();
        type=t;
    }

    public void resetBonus(){
        activeBonus=new CostBonus(0);
    }

    public CostBonus getBonus(){
        return activeBonus;
    }

    public void addBonus(int bonus){
        if (activeBonus ==null){
            activeBonus =new CostBonus(0);
        }
        CostBonus b = activeBonus;
        b.initialBonus+= bonus;
        b.remainingBonus+=bonus;
    }

    public void addUsingBonus(int q){
        if (activeBonus ==null){
            activeBonus =new CostBonus(0);
        }
        if (q<0){
            q*=-1;
            if (activeBonus.remainingBonus>q){
                activeBonus.remainingBonus-=q;
            } else {
                int toSubtract=q- activeBonus.remainingBonus;
                activeBonus.remainingBonus=0;
                add(toSubtract*-1);
            }
        } else {
            add(q);
        }
    }

    public void abortAddUsingBonus(int q){
        if (q<0&& activeBonus.remainingBonus< activeBonus.initialBonus){
            q*=-1;
            int bonusUsed= activeBonus.initialBonus- activeBonus.remainingBonus;
            if (q<=bonusUsed){
                activeBonus.remainingBonus+=q;
            }else {
                activeBonus.remainingBonus= activeBonus.initialBonus;
                add(q-bonusUsed);
            }
        } else {
            add(q*-1);
        }
    }

    public void undoAddUsingBonus(int q){
        if (activeBonus ==null){
            activeBonus =new CostBonus(0);
        }
        if (q<0){
            q*=-1;
            if (activeBonus.remainingBonus>q){
                activeBonus.remainingBonus-=q;
            } else {
                int toSubtract=q- activeBonus.remainingBonus;
                activeBonus.remainingBonus=0;
                add(toSubtract);
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

    public class CostBonus {
        public CostBonus(int initialBonus) {
            this.initialBonus = initialBonus;
            this.remainingBonus = initialBonus;
        }

        int initialBonus, remainingBonus;
    }

}
