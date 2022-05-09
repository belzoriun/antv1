package fr.florian.ants.antv1.util;

import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.util.option.OptionKey;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Class used as a game timer follower
 */
public class GameTimer {

    private static final int MIN_TICK_TIME = 5;

    private final ScheduledExecutorService executor;
    private ScheduledFuture<?> executorHandle;
    private final Runnable core;

    private static GameTimer instance = null;
    private long remainingTime;
    private final long totalTime;
    private boolean paused;
    private long tickTime;
    private double dayNightTime;
    private boolean transitToDay;

    private static final long DEFAULT_TICK_TIME = 50;
    private static final int DAY_DURATION = 2000;

    private GameTimer(long totalTime)
    {
        tickTime = DEFAULT_TICK_TIME;
        remainingTime = Application.options.getBoolean(OptionKey.INFINITE_SIMULATION) ? 0 : totalTime;
        this.totalTime=totalTime;
        paused = false;
        transitToDay = false;
        dayNightTime = 1;
        executor = Executors.newScheduledThreadPool(1);
        core = () -> {
            if (!paused) {
                TickWaiter.emitTick();
                if (transitToDay) {
                    dayNightTime += 1 / (double) DAY_DURATION;
                } else {
                    dayNightTime -= 1 / (double) DAY_DURATION;
                }
                if (dayNightTime <= 0) {
                    dayNightTime = 0;
                    transitToDay = true;
                } else if (dayNightTime >= 1) {
                    dayNightTime = 1;
                    transitToDay = false;
                }
                remainingTime += (Application.options.getBoolean(OptionKey.INFINITE_SIMULATION) ? 1 : -1) * 50;
            }
            if(remainingTime <= 0)
            {
                executorHandle.cancel(false);
            }
        };
    }

    public void start()
    {
        if(executorHandle != null)
        {
            executorHandle.cancel(false);
        }
        executorHandle = executor.scheduleAtFixedRate(core, 0, tickTime, TimeUnit.MILLISECONDS);
    }

    public boolean isDay()
    {
        return dayNightTime > 0.5;
    }

    public double getDayNightTime()
    {
        return dayNightTime;
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
        return remainingTime/(double)totalTime;
    }

    public double getTotalTime()
    {
        return totalTime;
    }

    public double getRemainingTime()
    {
        return remainingTime;
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
        if(tickTime < MIN_TICK_TIME) tickTime = MIN_TICK_TIME;
        this.tickTime = tickTime;
        executorHandle.cancel(false);
        executorHandle = executor.scheduleAtFixedRate(core, tickTime, tickTime, TimeUnit.MILLISECONDS);
    }

    public long getTickTime()
    {
        return tickTime;
    }

    public void setTickTimeDefault()
    {
        setTickTime(DEFAULT_TICK_TIME);
    }

}
