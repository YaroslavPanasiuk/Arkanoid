package sample;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.effect.MotionBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("rawtypes")
public class Game {
    private final Paddle mainBrick = new Paddle();
    private ArrayList<Brick> bricksList = new ArrayList<>();
    private final ArrayList<Ball> ballsList = new ArrayList<>();
    private final Group bricksGroup = new Group();
    private final Group ballsGroup = new Group();
    private MediaPlayer soundtrack;
    private MediaPlayer menuMusic;
    private MediaPlayer breakSound;
    private MediaPlayer hitSound;
    MediaPlayer music;
    MediaPlayer hitPaddleSound;
    private MediaPlayer victorySound;
    private MediaPlayer loseSound;
    final DoubleProperty musicVolume = new SimpleDoubleProperty(0.5);
    final DoubleProperty soundVolume = new SimpleDoubleProperty(0.5);
    AtomicBoolean powerUpIsActive = new AtomicBoolean(false);
    final Timeline loop = new Timeline();
    private final ArrayList<Timeline> timer = new ArrayList();
    EventHandler<MouseEvent> mouseMovedEventHandler;
    Scene scene = Main.roots.get(Main.roots.size()-1);

    Paddle getPaddle(){return mainBrick;}

    void initEventHandlers(){
        mouseMovedEventHandler = e -> {
            if(loop.getStatus() == Animation.Status.RUNNING && scene.activeScreen == scene.gameScreen){
                mainBrick.onMouseMove(e);
            }
        };
        scene.setOnMouseMoved(mouseMovedEventHandler);
    }

    Game(){
        initSounds();
    }

    Group initGameScreen(){
        initEventHandlers();
        Group gameObjects = new Group();
        for (Timeline t: timer) {
            t.playFrom(t.getTotalDuration());
        }

        Rectangle background = new Rectangle(0,0,scene.getWidth(),scene.getHeight());
        background.setFill(Color.BLACK);
        background.widthProperty().bindBidirectional(scene.widthProperty());
        background.heightProperty().bindBidirectional(scene.heightProperty());

        bricksGroup.getChildren().clear();
        ballsList.clear();
        ballsGroup.getChildren().clear();

        bricksList = Brick.generateBricks(10, 10);
        bricksGroup.getChildren().addAll(bricksList);

        Ball ball = new Ball();
        ballsGroup.getChildren().add(ball);
        ballsList.add(ball);

        mainBrick.setY(scene.getHeight() - mainBrick.getHeight() - 3);

        gameObjects.getChildren().addAll(background, bricksGroup, mainBrick, ballsGroup);

        return gameObjects;
    }

    void pause() {
        if (loop.getStatus() == Animation.Status.STOPPED) {
            return;
        }
        scene.setScreen(scene.pauseScreen);
        MotionBlur blur = new MotionBlur(90, 15);
        scene.gameScreen.setEffect(blur);
        loop.stop();
        for (Timeline t : timer){
            t.stop();
        }
    }

    void play(){
        if(loop.getStatus() != Animation.Status.STOPPED) {
            return;
        }
        scene.setScreen(scene.gameScreen);
        scene.gameScreen.setEffect(null);
        loop.play();
        for (Timeline t : timer) {
            t.play();
        }
    }

    void update() {
        if(bricksList.size() > 0 && bricksList.get(bricksList.size() - 1).getY() > scene.getHeight()){
            scene.setScreen(scene.loseScreen);
            return;
        }
        for(int i = 0; i < ballsList.size(); i ++){
            ballsList.get(i).move();
            if(ballsList.get(i).isDeleted()){
                ballsGroup.getChildren().remove(i);
                ballsList.remove(i);
                if(ballsList.isEmpty()){
                    scene.setScreen(scene.loseScreen);
                    return;
                }
                break;
            }
            for(int j = 0; j < bricksList.size(); j++){
                bricksList.get(j).move(1/(bricksList.size() + 20.0)/ballsList.size());
                checkCollision(ballsList.get(i), bricksList.get(j));
            }
        }
    }

    private void checkCollision(Ball ball, Brick brick){
        if(ball.isCollisionWith(brick)) {
            if (!ball.powerUpIsActive.get()) {
                powerUp(ball);
            }
            if (!ball.getIsRed()) {
                ball.changeDirection(brick);
                ball.accelerate(0.03);
            }
            brick.subtractHitPoint();
            if (brick.getHitPoints() == 0) {
                bricksGroup.getChildren().remove(bricksList.indexOf(brick));
                bricksList.remove(brick);
                breakSound.stop();
                breakSound.play();
                if (bricksList.isEmpty()) {
                    scene.setScreen(scene.victoryScreen);
                }
            } else {
                hitSound.stop();
                hitSound.play();
            }
        }
    }

    private void powerUp(Ball ball){
        Duration powerDuration = Duration.seconds(10);
        KeyFrame resetPower = null;
        Random random = new Random();
        int i = random.nextInt(40);
        switch (i) {
            case 0: {
                if(powerUpIsActive.get()){
                    break;
                }
                mainBrick.setWidth(mainBrick.getWidth() * 2);
                powerUpIsActive.set(true);
                resetPower = new KeyFrame(powerDuration, e -> {
                    mainBrick.setWidth(mainBrick.getWidth() / 2);
                    powerUpIsActive.set(false);
                });
                break;
            }
            case 1: {
                double deltaWidth = ball.getWidth()/15;
                Timeline bigBall = new Timeline(new KeyFrame(Duration.millis(1000/30.0), e -> ball.setWidth(ball.getWidth() + deltaWidth)));
                bigBall.setCycleCount(30);
                bigBall.play();
                ball.setColor(Color.GREEN);
                ball.powerUpIsActive.set(true);
                resetPower = new KeyFrame(powerDuration,
                        e -> {
                            Timeline resetBall = new Timeline(new KeyFrame(Duration.millis(1000/30.0), f -> ball.setWidth(ball.getWidth() - deltaWidth)));
                            resetBall.setCycleCount(30);
                            resetBall.setOnFinished(g -> {
                                ball.setColor(Color.WHITE);
                                ball.powerUpIsActive.set(false);
                            });
                            resetBall.play();
                        });
                break;
            }
            case 2: {
                ball.setIsRed(true);
                ball.setColor(Color.RED);
                ball.powerUpIsActive.set(true);
                resetPower = new KeyFrame(powerDuration,
                        e -> {
                            ball.setIsRed(false);
                            ball.setColor(Color.WHITE);
                            ball.powerUpIsActive.set(false);
                        });
                break;
            }
            case 3: {
                double[] direction = Ball.randomDirection(100/(Main.fps + 0.0));
                Ball newBall = new Ball(mainBrick.getX() + mainBrick.getWidth()/2,
                        scene.getHeight() - 40, direction[0], direction[1]);
                ballsList.add(newBall);
                ballsGroup.getChildren().add(newBall);
                break;
            }
        }

        if(resetPower != null) {
            Timeline t = new Timeline(resetPower);
            t.play();
            t.setOnFinished(e -> {
                t.getKeyFrames().clear();
                timer.remove(t);
            });
            timer.add(t);
        }
    }

    void setMusic(Group screen){
        if(screen == scene.menuScreen && scene.previousScreen != scene.settingsScreen){
            music.stop();
            music = menuMusic;
            return;
        }
        if(screen == scene.pauseScreen){
            music.volumeProperty().unbindBidirectional(musicVolume);
            music.volumeProperty().set(musicVolume.get()/4);
            return;
        }
        if(scene.previousScreen == scene.pauseScreen){
            music.volumeProperty().bindBidirectional(musicVolume);
            return;
        }
        music.pause();
        if(screen == scene.gameScreen){
            music.stop();
            music = soundtrack;
            return;
        }
        if(screen == scene.loseScreen){
            music = loseSound;
            return;
        }
        if(screen == scene.victoryScreen){
            music = victorySound;
        }
    }

    private void initSounds(){
        soundtrack = new MediaPlayer(new Media(new File(
                "src/resources/music/soundtrack.mp3").toURI().toString()));
        soundtrack.setCycleCount(MediaPlayer.INDEFINITE);
        soundtrack.volumeProperty().bindBidirectional(musicVolume);

        menuMusic = new MediaPlayer(new Media(new File(
                "src/resources/music/Menu.mp3").toURI().toString()));
        menuMusic.setCycleCount(MediaPlayer.INDEFINITE);
        menuMusic.volumeProperty().bindBidirectional(musicVolume);

        breakSound = new MediaPlayer(new Media(new File(
                "src/resources/music/breakSound.wav").toURI().toString()));
        breakSound.setOnEndOfMedia(() -> breakSound.stop());
        breakSound.volumeProperty().bindBidirectional(soundVolume);

        hitSound = new MediaPlayer(new Media(new File(
                "src/resources/music/hitSound.wav").toURI().toString()));
        hitSound.setOnEndOfMedia(() -> hitSound.stop());
        hitSound.volumeProperty().bindBidirectional(soundVolume);

        hitPaddleSound = new MediaPlayer(new Media(new File(
                "src/resources/music/hitPaddle.wav").toURI().toString()));
        hitPaddleSound.setOnEndOfMedia(() -> hitPaddleSound.stop());
        hitPaddleSound.volumeProperty().bindBidirectional(soundVolume);

        victorySound = new MediaPlayer(new Media(new File(
                "src/resources/music/Victory.mp3").toURI().toString()));
        victorySound.setOnEndOfMedia(() -> victorySound.stop());
        victorySound.volumeProperty().bindBidirectional(soundVolume);

        loseSound = new MediaPlayer(new Media(new File(
                "src/resources/music/Lose.wav").toURI().toString()));
        loseSound.setOnEndOfMedia(() -> loseSound.stop());
        loseSound.volumeProperty().bindBidirectional(soundVolume);

        music = menuMusic;
    }

    void updateWidth(double newWidth){
        double k = newWidth/scene.getWidth();
        for(Brick brick: bricksList){
            brick.setWidth(brick.getWidth() * k);
            brick.setX(brick.getX() * k);
        }
        for(Ball ball: ballsList){
            ball.setX(ball.getX() * k);
        }
        mainBrick.setX(mainBrick.getX() * k);
    }

    void updateHeight(double newHeight){
        double k = newHeight/scene.getHeight();
        for(Brick brick: bricksList){
            brick.setHeight(brick.getHeight() * k);
            brick.setY(brick.getY() * k);
        }
        for(Ball ball: ballsList){
            ball.setY(ball.getY() * k);
        }
        mainBrick.setY(newHeight - mainBrick.getHeight() - 3);
    }

    void start(){
        Duration frameTime = Duration.millis(1000/(Main.fps + 0.0));
        KeyFrame frame = new KeyFrame(frameTime, (e) -> {
            if(scene.activeScreen == scene.gameScreen){
                update();
            }
        });

        loop.setCycleCount(Animation.INDEFINITE);
        loop.getKeyFrames().add(frame);
        loop.play();
    }
}
