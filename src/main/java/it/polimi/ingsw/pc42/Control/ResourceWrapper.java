package it.polimi.ingsw.pc42.Control;


public class ResourceWrapper extends IntWrapper implements iResourceWrapper {

    ResourceType type;

    public CostBonus getActiveBonus() {
        return activeBonus;
    }

    public void setActiveBonus(CostBonus activeBonus) {
        this.activeBonus = activeBonus;
    }

    private CostBonus activeBonus;

    private boolean accumulatorEnabled;
    private int accumulated;


    public CostBonus getBonus(){
        return activeBonus;
    }

    /**
     * Class constructor. Takes a resources type, creates a wrapper around it with an attribute for quantity that can
     * be modified flexibly.
     *
     * @param t resource type to wrap
     * @param q quantity to be set
     */
    public ResourceWrapper(ResourceType t, int q){
        super (q);
        type=t;
        accumulatorEnabled=false;
        accumulated=0;
    }

    /**
     * Class constructor. Takes a resources type and creates a wrapper around it.
     *
     * @param t resource type to wrap
     */
    public ResourceWrapper(ResourceType t){
        super ();
        type=t;
    }

    /**
     * Sets the attribute to zero, passing it to the constructor of this one.
     */
    public void resetBonus(){
        activeBonus=new CostBonus(0);
    }

    /**
     * Adds a quantity to the bonus attribute.
     *
     * @param bonus quantity to be added
     */
    public void addBonus(int bonus){
        if (activeBonus ==null){
            activeBonus =new CostBonus(0);
        }
        CostBonus b = activeBonus;
        b.initialBonus+= bonus;
        b.remainingBonus+=bonus;
    }



    /**
     * Integrates the mechanism of discount bonus for card cost or card immediate effect.
     *
     * @param q quantity of the discount
     */
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
        if (!accumulatorEnabled||i<0) {
            myInt += i;
            if (myInt < 0) {
                throw new IllegalArgumentException();
            }
        } else {
            accumulated+=i;
        }
    }

    public void setAccumulatorEnabled(boolean acc){
        accumulatorEnabled=acc;
        if (acc==false){
            myInt+=accumulated;
            accumulated=0;
        }
    }

    public class CostBonus {

        int initialBonus, remainingBonus;

        /**
         * Class constructor. Takes a quantity and set equally an initial and remaining bonus attributes.
         *
         * @param initialBonus quantity to set
         */
        public CostBonus(int initialBonus) {
            this.initialBonus = initialBonus;
            this.remainingBonus = initialBonus;
        }
    }

}
