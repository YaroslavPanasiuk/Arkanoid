package sample;

import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Paddle extends BaseObject{
    private double previousPosition;

    double getSpeed(){
        double speed = getX() - previousPosition;
        previousPosition = getX();
        return speed;
    }

    Paddle(){
        Rectangle mRectangle = new Rectangle();
        mRectangle.widthProperty().bindBidirectional(widthProperty());
        mRectangle.heightProperty().bindBidirectional(heightProperty());
        mRectangle.xProperty().bindBidirectional(xProperty());
        mRectangle.yProperty().bindBidirectional(yProperty());
        mRectangle.setArcWidth(20);
        mRectangle.setArcHeight(20);
        mRectangle.setFill(Color.YELLOW);
        BoxBlur mBlur = new BoxBlur();
        mBlur.setWidth(5);
        mBlur.setHeight(5);
        mRectangle.setEffect(mBlur);
        setWidth(150);
        setHeight(25);
        getChildren().add(mRectangle);
        setX(0);
        previousPosition = 0;
    }

    void onMouseMove(MouseEvent event){
        if(event.getX() < getWidth()/2){
            setX(0);
        }
        if (event.getX() >= getWidth()/2 && event.getX() <= Main.roots.get(Main.roots.size()-1).getWidth() - getWidth()/2) {
            setX(event.getX() - getWidth()/2);
        }
        if(event.getX() > Main.roots.get(Main.roots.size()-1).getWidth() - getWidth()/2){
            setX(Main.roots.get(Main.roots.size()-1).getWidth() - getWidth());
        }

    }

}