package sample;

import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Ball extends BaseObject{
    private Circle mCircle;
    private final BoxBlur mBlur = new BoxBlur();
    private double dx;
    private double dy;
    private boolean isRed;
    private boolean deleted;
    AtomicBoolean powerUpIsActive = new AtomicBoolean(false);
    Scene scene = Main.roots.get(Main.roots.size()-1);

    boolean getIsRed(){
        return isRed;
    }
    boolean isDeleted(){
        return deleted;
    }

    void setDx(double dx){
        this.dx = dx;
    }
    void setDy(double dy){
        this.dy = dy;
    }
    void setIsRed(boolean b){
        isRed = b;
    }

    Ball(){
        initialize();
        setX(scene.getWidth()/2);
        setY(scene.getHeight()/2 + 4 * getHeight());
        double[] direction = randomDirection(100/(Main.fps + 0.0));
        setDx(direction[0]);
        setDy(-direction[1]);
    }

    Ball(double x, double y, double dx, double dy){
        initialize();
        setX(x);
        setY(y);
        setDx(dx);
        setDy(dy);
    }

    private void initialize(){
        mCircle = new Circle();
        mCircle.radiusProperty().bindBidirectional(widthProperty());
        mCircle.radiusProperty().bindBidirectional(heightProperty());
        mCircle.centerXProperty().bindBidirectional(xProperty());
        mCircle.centerYProperty().bindBidirectional(yProperty());
        mCircle.setFill(Color.WHITE);
        mBlur.setWidth(5);
        mBlur.setHeight(5);
        mCircle.setEffect(mBlur);
        isRed = false;
        deleted = false;
        powerUpIsActive.set(false);
        setWidth(10);
        setHeight(10);
        getChildren().add(mCircle);
    }

    void move() {
        setY(getY() + dy);
        setX(getX() + dx);

        final boolean atTopBorder = getY() <= 0 && dy < 0;
        final boolean atRightBorder = getX() + getWidth() >=
                getParent().getParent().getScene().getWidth() && dx > 0;

        final boolean atBottomBorder = getY() >=
                Main.roots.get(Main.roots.size()-1).getHeight() && dy > 0;

        final boolean atLeftBorder = getX() - getWidth() <= 0 && dx < 0;

        final boolean atPaddle = isCollisionWith(scene.game.getPaddle());
        if(atLeftBorder || atRightBorder){dx *= -1;}
        if(atTopBorder || (atPaddle && dy > 0)){dy *= -1;}
        double paddleSpeed = scene.game.getPaddle().getSpeed();
        if(atPaddle){
            scene.game.hitPaddleSound.stop();
            scene.game.hitPaddleSound.play();
            dx += paddleSpeed/10;
            final boolean atLeft = getX() < scene.game.getPaddle().getX();

            final boolean atRight = getX() + getWidth() >
                    scene.game.getPaddle().getX() + scene.game.getPaddle().getWidth();

            if((atLeft && dx > 0) || (atRight && dx < 0)){
                dx *= -1;
                dx += paddleSpeed/5;
            }
        }
        if(atBottomBorder){
            deleted = true;
        }
    }

    @Override
    boolean isCollisionWith(BaseObject baseObject){
        return  getX() + getWidth()/2 > baseObject.getX() &&
                getX() - getWidth()/2 < baseObject.getX() + baseObject.getWidth() &&
                getY() + getHeight()/2 > baseObject.getY() &&
                getY() - getHeight()/2 < baseObject.getY() + baseObject.getHeight();
    }

    void changeDirection(Brick brick) {
        System.out.println("is collision");
        final boolean atTop = getY() < brick.getY();
        final boolean atBottom = getY() > brick.getY() + brick.getHeight();
        final boolean atLeft = getX() < brick.getX();
        final boolean atRight = getX() > brick.getX() + brick.getWidth();
        if((atLeft && dx > 0) || (atRight && dx < 0)){
            dx *= -1;
        }
        if((atTop && dy > 0) || (atBottom && dy < 0)){
            dy *= -1;
        }
    }

    void accelerate(double acceleration) {
        if(dx > 0){
            dx += acceleration;
        }
        else{
            dx -= acceleration;
        }
        if(dy > 0){
            dy += acceleration;
        }
        else{
            dy -= acceleration;
        }
    }

    void setColor(Color color) {
        mCircle.setFill(color);
    }

    static double[] randomDirection(double speed){
        Random random = new Random();
        double x = -speed + 2 * speed * random.nextDouble();
        double y = - Math.sqrt(2 * speed * speed - x * x);
        return  new double[]{x, y};
    }

}
