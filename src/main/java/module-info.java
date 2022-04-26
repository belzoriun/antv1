module fr.florian.ants.antv1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens fr.florian.ants.antv1 to javafx.fxml;
    exports fr.florian.ants.antv1;
}