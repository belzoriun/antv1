package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.TickWaiter;
import fr.florian.ants.antv1.util.Vector;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Class representing the pause menu
 */
public class PauseMenu extends BorderPane {

    private final Button continueBtn;
    private Vector clickPoint;
    private Label title;
    private Button step;

    public PauseMenu(double initialTranslateX, double initialTranslateY)
    {
        title = new Label("Game paused");
        title.setFont(new Font(15));
        title.setStyle("-fx-font-weight: bold");
        title.setUnderline(true);
        setTop(title);
        title.setAlignment(Pos.CENTER);
        title.setPadding(new Insets(10));
        VBox main = new VBox();
        this.setCenter(main);
        Button restart = new Button("restart");
        step = new Button("step simulation");
        main.setPrefWidth(120);
        main.setSpacing(5);
        setPadding(new Insets(10));
        Button exit = new Button("exit");
        exit.setOnAction((ActionEvent e)-> Application.switchToMenuScreen());
        continueBtn = new Button("continue");
        continueBtn.setOnAction((ActionEvent e)-> playGame());
        main.getChildren().add(continueBtn);
        main.getChildren().add(step);
        main.getChildren().add(restart);
        main.getChildren().add(exit);
        setPrefWidth(130);
        restart.setOnAction((ActionEvent e) -> {
            Application.restart();
            continueBtn.setDisable(false);
        });
        step.setOnAction(e->{
            TickWaiter.emitTick();
        });
        continueBtn.setMinWidth(main.getPrefWidth());
        restart.setMinWidth(main.getPrefWidth());
        exit.setMinWidth(main.getPrefWidth());

        setTranslateX(initialTranslateX);
        setTranslateY(initialTranslateY);

        setOnMousePressed(e->{
            if(e.getButton() == MouseButton.PRIMARY)
            {
                clickPoint = new Vector(e.getScreenX(), e.getScreenY());
            }
        });

        setOnMouseDragged(e->{
            Vector point = new Vector(e.getScreenX(), e.getScreenY());
            if(e.isPrimaryButtonDown())
            {
                if(clickPoint != null) {
                    Vector v = point.add(clickPoint.multi(-1));
                    setTranslateX(getTranslateX() + v.getX());
                    setTranslateY(getTranslateY() + v.getY());
                }
                clickPoint = point;
            }
        });

        setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5), null)));
        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));

        this.setVisible(false);
    }

    public void pauseGame()
    {
        this.setVisible(true);
        GameTimer.getInstance().pause();
    }

    public void playGame()
    {
        this.setVisible(false);
        GameTimer.getInstance().play();
    }

    /**
     * Set this menu to "game end" mode
     */
    public void setEndMenu() {
        continueBtn.setVisible(false);
        step.setVisible(false);
        title.setText("Game end");
    }
}
