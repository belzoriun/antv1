package fr.florian.ants.antv1.util;

import fr.florian.ants.antv1.ui.Application;
import javafx.application.Platform;

public class GameTimer extends Thread{

    private static GameTimer instance = null;
    private long remainingTime;
    private long totalTime;
    private boolean paused;

    private GameTimer(long totalTime)
    {
        remainingTime = totalTime;
        this.totalTime=totalTime;
        paused = false;
    }

    public void run()
    {
        while(remainingTime > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                return;
            }
            if(!paused)
                remainingTime -= 10;
        }
        Platform.runLater(()->{
            Application.showEndMenu();
        });
    }

    public static void init(long totalTime)
    {
        if(instance == null)
        {
            instance = new GameTimer(totalTime);
        }
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

}
