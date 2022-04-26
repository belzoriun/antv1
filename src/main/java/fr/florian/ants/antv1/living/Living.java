package fr.florian.ants.antv1.living;

public abstract class Living implements Runnable{

    private float actionThreshold; //in ms
    private boolean alive;

    private long lastTimeAct;

    protected Living(float actionThreshold)
    {
        this.actionThreshold = actionThreshold;
        this.alive= true;
        lastTimeAct = 0L;
    }

    protected abstract void executeAction();

    @Override
    public void run() {
        while(this.alive)
        {
            long time = System.currentTimeMillis();
            if(time - lastTimeAct > actionThreshold)
            {
                lastTimeAct = time;
                executeAction();
            }
        }
    }

}
