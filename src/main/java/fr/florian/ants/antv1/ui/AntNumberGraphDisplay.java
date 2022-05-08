package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.TickWaiter;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.HashMap;

/**
 * Class used to display the ant number as a graph
 */
public class AntNumberGraphDisplay extends Pane implements Runnable{

    private final NumberAxis time;
    private final NumberAxis score;
    private double previousMin = 0;
    private final java.util.Map<Long, XYChart.Series<Number, Number>> series;

    public AntNumberGraphDisplay()
    {
        series = new HashMap<>();
        time = new NumberAxis("time", 0, 1, 0.5);
        score = new NumberAxis("ant number", 0, 100, 10);
        LineChart<Number, Number> chart = new LineChart<>(time, score);
        getChildren().add(chart);
        chart.setMaxWidth(250);
        for(AntHillTile hill : Map.getInstance().getAntHills())
        {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            this.series.put(hill.getUniqueId(), series);
            chart.getData().add(this.series.get(hill.getUniqueId()));

            Node line = series.getNode().lookup(".chart-series-line");

            Color color = hill.getColor(); // or any other color

            String rgb = String.format("%d, %d, %d",
                    (int) (color.getRed() * 255),
                    (int) (color.getGreen() * 255),
                    (int) (color.getBlue() * 255));

            line.setStyle("-fx-stroke: rgba(" + rgb + ", 1.0); -fx-stroke-width: 1px;");
        }
        chart.setTitle("Number of ants over time");
        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);
    }

    @Override
    public void run() {
        try {
            while (TickWaiter.isLocked()) {
                TickWaiter.waitTick();
                Platform.runLater(()-> {
                    double time = (GameTimer.getInstance().getTotalTime() - GameTimer.getInstance().getRemainingTime())/1000;
                    if(time < 10) {
                        this.time.setLowerBound(0);
                    }
                    else
                    {
                        this.time.setLowerBound(time-10);
                    }
                    this.time.setUpperBound(time);
                    this.time.setTickUnit(1);
                    if(Map.getInstance().getAntHills().size() > 0) {
                        double min = Map.getInstance().getAntsOf(Map.getInstance().getAntHills().get(0).getUniqueId()).size();
                        double max = min;
                        for (AntHillTile hill : Map.getInstance().getAntHills()) {
                            if(series.containsKey(hill.getUniqueId())) {
                                int nb = Map.getInstance().getAntsOf(hill.getUniqueId()).size();
                                if (nb > max) max = nb;
                                if (nb < min) min = nb;
                                series.get(hill.getUniqueId()).getData().add(new XYChart.Data<>(time, nb));
                            }
                        }
                        score.setUpperBound(max + 10);
                        if (previousMin < 100) {
                            score.setLowerBound(0);
                        } else {
                            score.setLowerBound(previousMin - 100);
                        }
                        previousMin = min;
                    }
                });
            }
        }catch(Exception ignored)
        {
        }
    }
}
