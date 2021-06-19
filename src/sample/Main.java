package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import java.io.File;
import java.util.ArrayList;

public class Main extends Application {
    static final int decorationWidth = 14;
    static final int decorationHeight = 37;
    static final int fps = 60;
    static ArrayList<sample.Scene> roots = new ArrayList<>();

    static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        generateStage(primaryStage);
        sample.Scene root = new sample.Scene();
        Scene startingScreen = new Scene(root);
        primaryStage.setScene(startingScreen);
        primaryStage.show();
        roots.add(root);
        root.run();
    }

    private void generateStage(Stage stage){
        stage.setWidth(900);
        stage.setHeight(600);
        stage.setMinHeight(400);
        stage.setMinWidth(600);
        stage.setTitle("Arkanoid game");

        stage.getIcons().add(new Image(new File("src/resources/icon64.jpg").toURI().toString()));
    }
}