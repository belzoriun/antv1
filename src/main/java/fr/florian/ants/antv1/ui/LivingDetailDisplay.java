package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.living.Living;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class LivingDetailDisplay extends VBox {

    private Living displayFor;

    private Label position;
    private TitledPane detail;

    public LivingDetailDisplay()
    {
        setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5), null)));
        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(1))));
        Label title = new Label("Ant detail");
        title.setUnderline(true);
        title.setFont(new Font(15));
        getChildren().add(title);
        position = new Label();
        getChildren().add(position);
        setPadding(new Insets(10));
        setSpacing(15);
        detail = new TitledPane();
        detail.setText("Detail");
        getChildren().add(detail);
    }

    public void displayFor(Living l)
    {
        this.displayFor = l;
    }

    public void update()
    {
        if(displayFor == null) return;
        position.setText("Position : "+displayFor.getPosition());
        detail.setContent(displayFor.getDetailDisplay());
    }
}
