package sample;

import javafx.animation.Animation;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Engine {

    Scene scene = Main.roots.get(Main.roots.size()-1);
    Stage stage = (Stage) scene.getScene().getWindow();

    @FXML
    Button toggleFullscreen = new Button();
    @FXML
    Slider musicSlider = new Slider();
    @FXML
    Slider soundSlider = new Slider();
    @FXML
    Rectangle pauseScreen = new Rectangle();

    @FXML
    public void initialize(){
        if(stage.isFullScreen()){
            scene.goFullscreenText.set("Go Windowed");
        }
        else {
            scene.goFullscreenText.set("Go Fullscreen");
        }
        toggleFullscreen.textProperty().bind(scene.goFullscreenText);
        musicSlider.valueProperty().bindBidirectional(scene.game.musicVolume);
        soundSlider.valueProperty().bindBidirectional(scene.game.soundVolume);

        pauseScreen.widthProperty().bindBidirectional(scene.widthProperty());
        pauseScreen.heightProperty().bindBidirectional(scene.heightProperty());


    }

    @FXML
    void restartEventMethod(){
        scene.game.play();
        scene.setScreen(scene.gameScreen);
        scene.gameScreen.getChildren().add(scene.game.initGameScreen());
        System.out.println("Restart");
    }

    @FXML
    void resumeEventMethod(){
        scene.game.play();
        System.out.println("Resume");
    }

    @FXML
    void settingsEventMethod(){
        scene.setScreen(scene.settingsScreen);
        System.out.println("Settings");
    }

    @FXML
    void goToMenuEventMethod(){
        scene.setScreen(scene.menuScreen);
        System.out.println("Go to menu");
    }

    @FXML
    void goFullScreenEventMethod(){
        stage.setFullScreen(!stage.isFullScreen());
        System.out.println("Fullscreen");
    }

    @FXML
    void goBackEventMethod(){
        scene.setScreen(scene.getPreviousScreen());
        System.out.println("Back");
    }

    @FXML
    void closeEventMethod(){
        stage.close();
    }
}
