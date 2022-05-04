package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.TickAwaiter;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.HashMap;

public class AntNumberGraphDisplay extends Pane implements Runnable{

    private AreaChart<Number, Number> chart;
    private NumberAxis time;
    private NumberAxis score;
    private double previousMin = 0;
    java.util.Map<Long, XYChart.Series<Number, Number>> series;

    public AntNumberGraphDisplay()
    {
        series = new HashMap<>();
        time = new NumberAxis("time", 0, 1, 0.5);
        time.setAutoRanging(false);
        score = new NumberAxis("ant number", 0, 100, 10);
        chart = new AreaChart<Number, Number>(time, score);
        getChildren().add(chart);
        chart.setMaxWidth(250);
        for(AntHillTile hill : Map.getInstance().getAntHills())
        {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            this.series.put(hill.getUniqueId(), series);
            chart.getData().add(this.series.get(hill.getUniqueId()));


            Node fill = series.getNode().lookup(".chart-series-area-fill"); // only for AreaChart
            Node line = series.getNode().lookup(".chart-series-area-line");

            Color color = hill.getColor(); // or any other color

            String rgb = String.format("%d, %d, %d",
                    (int) (color.getRed() * 255),
                    (int) (color.getGreen() * 255),
                    (int) (color.getBlue() * 255));

            fill.setStyle("-fx-fill: rgba(" + rgb + ", 0.15);");
            line.setStyle("-fx-stroke: rgba(" + rgb + ", 1.0);");
        }
        chart.setTitle("Number of ants over time");
        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);
    }

    @Override
    public void run() {
        try {
            while (TickAwaiter.isLocked()) {
                TickAwaiter.waitTick();
                Platform.runLater(()-> {
                    double ctime = (GameTimer.getInstance().getTotalTime() - GameTimer.getInstance().getRemainingTime())/1000;
                    if(ctime < 10) {
                        time.setLowerBound(0);
                    }
                    else
                    {
                        time.setLowerBound(ctime-10);
                    }
                    time.setUpperBound(ctime);
                    time.setTickUnit(1);
                    if(Map.getInstance().getAntHills().size() > 0) {
                        double min = Map.getInstance().getAntsOf(Map.getInstance().getAntHills().get(0).getUniqueId()).size();
                        double max = min;
                        for (AntHillTile hill : Map.getInstance().getAntHills()) {
                            if(series.containsKey(hill.getUniqueId())) {
                                int nb = Map.getInstance().getAntsOf(hill.getUniqueId()).size();
                                if (nb > max) max = nb;
                                if (nb < min) min = nb;
                                series.get(hill.getUniqueId()).getData().add(new XYChart.Data<Number, Number>(ctime, nb));
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
        }catch(Exception e)
        {
        }
    }
}
