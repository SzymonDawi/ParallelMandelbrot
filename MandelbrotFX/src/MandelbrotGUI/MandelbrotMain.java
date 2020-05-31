package MandelbrotGUI;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MandelbrotMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MandelbrotGUI.fxml"));
        primaryStage.setTitle("Mandelbrot Paralellisation Techniques");
        primaryStage.setScene(new Scene(root, 1280, 800));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}

