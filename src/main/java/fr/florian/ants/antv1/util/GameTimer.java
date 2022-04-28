package fr.florian.ants.antv1.util;

import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.ui.Application;
import javafx.application.Platform;

public class GameTimer extends Thread{

    private static GameTimer instance = null;
    private long remainingTime;
    private long totalTime;
    private boolean paused;
    private long tickTime;

    private static final long DEFAULT_TICK_TIME = 50;

    private GameTimer(long totalTime)
    {
        tickTime = DEFAULT_TICK_TIME;
        remainingTime = totalTime;
        this.totalTime=totalTime;
        paused = false;
    }

    public void run()
    {
        while(remainingTime > 0) {
            try {
                Thread.sleep(tickTime);
            } catch (InterruptedException e) {
                return;
            }
            if(!paused) {
                TickAwaiter.emitTick();
                remainingTime -= 50;
            }
        }
        Platform.runLater(()->{
            Application.showEndMenu();
        });
    }

    public void stopTime()
    {
        this.remainingTime = 0;
    }

    public static void init(long totalTime)
    {
        instance = new GameTimer(totalTime);
    }

    public static GameTimer getInstance()
    {
        return instance;
    }

    public double getRemainingTimeFraction()
    {
        return remainingTime/totalTime;
    }

    public boolean isPaused()
    {
        return paused;
    }

    public void pause()
    {
        paused=true;
    }

    public void play()
    {
        paused = false;
    }

    public void setTickTime(long tickTime)
    {
        this.tickTime = tickTime;
        if(this.tickTime < 1) this.tickTime = 1;
    }

    public long getTickTime()
    {
        return tickTime;
    }

    public void setTickTimeDefault()
    {
        this.tickTime = DEFAULT_TICK_TIME;
    }

}
