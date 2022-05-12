package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.map.ChunkUpdateFeature;
import fr.florian.ants.antv1.map.ChunkUpdater;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.TickWaiter;
import fr.florian.ants.antv1.util.mod.ModLoader;
import fr.florian.ants.antv1.util.option.OptionKey;
import fr.florian.ants.antv1.util.pheromone.PheromoneManager;
import fr.florian.ants.antv1.util.resource.NoiseResourcePlacer;
import javafx.application.Platform;
import javafx.scene.input.KeyEvent;

import java.util.*;

public class DisplayStartController extends Thread {

    enum StartEvent{
        INIT_GAME, RESTART, END
    }

    private static final Object waiter = new Object();

    private volatile boolean running;
    private List<StartEvent> eventQueue;
    private Map<StartEvent, Runnable> executors;

    public DisplayStartController()
    {
        running = true;
        eventQueue = new ArrayList<>();
        executors = new HashMap<>();
        executors.put(StartEvent.INIT_GAME, ()->{
            try {
                Platform.runLater(() -> {
                    Application.showLoadingScreen("Initializing map");
                });
                Application.random = new Random(Application.seed);
                TickWaiter.lock();
                PheromoneManager.forceInit();
                fr.florian.ants.antv1.map.Map.getInstance().init(new NoiseResourcePlacer(Application.seed, ModLoader.loadModsResource()));
                for (ChunkUpdateFeature feature : ModLoader.loadModsUpdateFeatures()) {
                    ChunkUpdater.useUpdateFeature(feature);
                }
                Platform.runLater(() -> {
                    Application.showLoadingScreen("Initializing timer");
                });
                GameTimer.init(Application.options.getInt(OptionKey.SIMULATION_TIME) * 60000L);//2 minute
                GameTimer.getInstance().start();

                Platform.runLater(() -> {
                    Application.showLoadingScreen("Initializing view");

                    Application.main.init();

                    Application.switchToGameScreen();
                });
            }catch(Exception | OutOfMemoryError e)
            {
                Platform.runLater(() -> {
                    Thread t = new Thread(()->{
                        int remainingTime = 10;
                        while (remainingTime > 0) {
                            Application.showLoadingScreen("An error occurred while loading : " + e.getMessage() + " " + remainingTime);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ignored) {
                            }
                            remainingTime--;
                        }
                    });
                    t.start();
                    try {
                        t.join();
                    } catch (InterruptedException ignored) {
                    }
                    Application.switchToOptionScreen();
                });
            }
        });
        executors.put(StartEvent.END, ()->{

            TickWaiter.free();

            Platform.runLater(() -> {
                Application.showLoadingScreen("Removing ants");
            });
            System.out.print("killing ants ...");
            long time = System.currentTimeMillis();
            fr.florian.ants.antv1.map.Map.getInstance().killAll();
            System.out.println(" "+(System.currentTimeMillis()-time)+"ms");
            System.out.print("Stopping screen refresh ...");
            Platform.runLater(() -> {
                Application.showLoadingScreen("Stopping screen refresh");
            });
            time = System.currentTimeMillis();
            System.out.println(" "+(System.currentTimeMillis()-time)+"ms");
            try {
                Platform.runLater(() -> {
                    Application.showLoadingScreen("Stopping pheromone manager");
                });
                System.out.print("Stopping pheromone manager ...");
                time = System.currentTimeMillis();
                PheromoneManager.getInstance().stopExecution();
                PheromoneManager.getInstance().join();
                System.out.println(" "+(System.currentTimeMillis()-time)+"ms");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                Application.showLoadingScreen("Stopping timer");
            });
            System.out.print("Stopping timer ...");
            time = System.currentTimeMillis();
            GameTimer.getInstance().stopTime();
            System.out.println(" "+(System.currentTimeMillis()-time)+"ms");
            fr.florian.ants.antv1.map.Map.annihilate();
        });
        executors.put(StartEvent.RESTART, ()->{
            executors.get(StartEvent.END).run();
            Platform.runLater(()-> {
                System.out.println("restarting ...");
                Application.main.exit();
                Application.switchToOptionScreen();
            });
        });
    }

    public void emit(StartEvent event)
    {
        synchronized (waiter) {
            eventQueue.add(event);
            try {
                waiter.wait();
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void emitAsync(StartEvent event)
    {
        eventQueue.add(event);
    }

    public void run()
    {
        while(running)
        {
            if(!eventQueue.isEmpty())
            {
                StartEvent event = eventQueue.remove(0);
                Runnable executor = executors.get(event);
                if(executor != null)
                {
                    executor.run();
                    synchronized (waiter) {
                        waiter.notifyAll();
                    }
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
                running = false;
            }
        }
    }

    public void terminate()
    {
        running = false;
    }
}
