package sample;

import javafx.beans.property.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Scene  extends Parent {
    private final DoubleProperty width = new SimpleDoubleProperty(0);
    private final DoubleProperty height = new SimpleDoubleProperty(0);

    Group menuScreen;
    Group gameScreen;
    Group pauseScreen;
    Group settingsScreen;
    Group victoryScreen;
    Group loseScreen;

    VBox menuScreenVB;
    VBox pauseScreenVB;
    VBox settingsScreenVB;
    VBox loseScreenVB;
    VBox victoryScreenVB;

    Group activeScreen = new Group();
    Group previousScreen = new Group();
    StringProperty goFullscreenText = new SimpleStringProperty();
    Game game;
    Engine engine;
    Stage stage;

    DoubleProperty widthProperty() {
        return  width;
    }
    double getWidth(){
        return width.get();
    }
    void setWidth(double width){
        this.width.set(width);
    }

    DoubleProperty heightProperty() {
        return  height;
    }
    double getHeight(){
        return height.get();
    }
    void setHeight(double height){
        this.height.set(height);
    }

    void setScreen(Group group){
        getChildren().clear();
        if(group == pauseScreen){
            getChildren().add(gameScreen);
        }
        previousScreen = activeScreen;
        activeScreen = group;
        game.setMusic(activeScreen);
        game.music.play();
        if(getChildren().contains(group)){
            getChildren().remove(group);
            getChildren().add(group);
            return;
        }
        getChildren().add(activeScreen);
    }

    Group getPreviousScreen(){
        if(previousScreen == menuScreen){
            return menuScreen;
        }
        if(previousScreen == gameScreen){
            return  gameScreen;
        }
        if(previousScreen == loseScreen){
            return  loseScreen;
        }
        if(previousScreen == victoryScreen){
            return  victoryScreen;
        }
        if(previousScreen == settingsScreen){
            return settingsScreen;
        }
        if(previousScreen == pauseScreen){
            return pauseScreen;
        }
        return null;
    }

    void initialize(){
        stage = (Stage)getScene().getWindow();
        game = new Game();
        engine = new Engine();
        initScreens();
        setWidthEvents(stage);
        bindKeys();
        sizeToStage(stage);
        updateWidth(getWidth());
        updateHeight(getHeight());
        game.initEventHandlers();
        setScreen(menuScreen);
    }

    void initScreens(){
        try {
            gameScreen = FXMLLoader.load(getClass().getResource("/resources/screens/GameScreen.fxml"));
            menuScreen = FXMLLoader.load(getClass().getResource("/resources/screens/menuScreen.fxml"));
            loseScreen = FXMLLoader.load(getClass().getResource("/resources/screens/loseScreen.fxml"));
            victoryScreen = FXMLLoader.load(getClass().getResource("/resources/screens/victoryScreen.fxml"));
            settingsScreen = FXMLLoader.load(getClass().getResource("/resources/screens/settingsScreen.fxml"));
            pauseScreen = FXMLLoader.load(getClass().getResource("/resources/screens/pauseScreen.fxml"));

            menuScreenVB = (VBox) menuScreen.getChildren().get(0);
            pauseScreenVB = (VBox) pauseScreen.getChildren().get(1);
            settingsScreenVB = (VBox) settingsScreen.getChildren().get(0);
            loseScreenVB = (VBox) loseScreen.getChildren().get(0);
            victoryScreenVB = (VBox) victoryScreen.getChildren().get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sizeToStage(Stage stage){
        if(stage.isFullScreen()){
            setWidth(stage.getWidth());
            setHeight(stage.getHeight());
        } else{
            setWidth(stage.getWidth() - Main.decorationWidth);
            setHeight(stage.getHeight() - Main.decorationHeight);
        }
    }

    private void setWidthEvents(Stage stage){
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("update width");
            if(newVal.doubleValue() == Screen.getPrimary().getBounds().getWidth()) {
                updateWidth((double) newVal);
            } else {
                updateWidth((double) newVal - Main.decorationWidth);
            }
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal.doubleValue() == Screen.getPrimary().getBounds().getHeight()) {
                goFullscreenText.set("Go Windowed");
                updateHeight((double) newVal);
            } else {
                updateHeight((double) newVal - Main.decorationHeight);
                goFullscreenText.set("Go Fullscreen");
            }
        });
    }

    private void bindKeys(){
        AtomicBoolean altPressed = new AtomicBoolean(false);
        AtomicBoolean enterPressed = new AtomicBoolean(false);
        Stage stage = (Stage)getScene().getWindow();
        getScene().setOnKeyPressed(press -> {
            if (press.getCode() == KeyCode.ALT) {
                altPressed.set(true);
            }
            if (press.getCode() == KeyCode.ENTER) {
                enterPressed.set(true);
            }
            if(altPressed.get() && enterPressed.get()) {
                stage.setFullScreen(!stage.isFullScreen());
            }
            if(press.getCode() == KeyCode.ESCAPE){
                if(activeScreen == pauseScreen){
                    game.play();
                } else if(activeScreen == gameScreen){
                    game.pause();
                }
            }
        });
        getScene().setOnKeyReleased(press -> {
            if (press.getCode() == KeyCode.ALT) {
                altPressed.set(false);
            }
            if (press.getCode() == KeyCode.ENTER) {
                enterPressed.set(false);
            }
        });
    }

    void updateWidth(double newWidth){
        game.updateWidth(newWidth);
        setWidth(newWidth);
        menuScreenVB.setPrefWidth(getWidth());
        loseScreenVB.setPrefWidth(getWidth());
        victoryScreenVB.setPrefWidth(getWidth());
        settingsScreenVB.setPrefWidth(getWidth());
        pauseScreenVB.setPrefWidth(getWidth());
    }

    void updateHeight(double newHeight){
        game.updateHeight(newHeight);
        setHeight(newHeight);
        menuScreenVB.setPrefHeight(getHeight());
        loseScreenVB.setPrefHeight(getHeight());
        victoryScreenVB.setPrefHeight(getHeight());
        settingsScreenVB.setPrefHeight(getHeight());
        pauseScreenVB.setPrefHeight(getHeight());
    }

    void run(){
        initialize();
        game.start();
    }
}
