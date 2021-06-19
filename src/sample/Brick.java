package sample;

import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class Brick extends BaseObject{
    private final Rectangle mRectangle = new Rectangle();
    private final BoxBlur mBlur = new BoxBlur();
    private int hitPoints;
    final int maxHitPoints = 6;
    Brick(){
        mRectangle.widthProperty().bindBidirectional(widthProperty());
        mRectangle.heightProperty().bindBidirectional(heightProperty());
        mRectangle.xProperty().bindBidirectional(xProperty());
        mRectangle.yProperty().bindBidirectional(yProperty());
        mRectangle.setArcWidth(20);
        mRectangle.setArcHeight(20);
        mRectangle.setFill(getRandomColor());
        mBlur.setWidth(1);
        mBlur.setHeight(1);
        mRectangle.setEffect(mBlur);
        hitPoints = 1;
        mRectangle.setStroke(hpColor());
        mRectangle.setStrokeWidth(1);
        getChildren().add(mRectangle);
    }

    int getHitPoints(){
        return hitPoints;
    }

    private void setHitPoints(int rowNumber){
        Random random = new Random();
        int i = random.nextInt(rowNumber * maxHitPoints + 2);
        if(i == 0){
            hitPoints++;
            mRectangle.setStrokeWidth(mRectangle.getStrokeWidth() + 2);
            mRectangle.setStroke(hpColor());
        }
    }

    void subtractHitPoint(){
        hitPoints--;
        mRectangle.setStrokeWidth(mRectangle.getStrokeWidth() - 2);
        if(hitPoints != 0){
            mRectangle.setStroke(hpColor());
        }
    }

    static ArrayList<Brick> generateBricks(int columns, int rows){
        ArrayList<Brick> bricks = new ArrayList<>();
        for(int i  = 0; i < rows; i++){
            for(int j = 0; j < columns; j ++){
                Brick brick = new Brick();
                brick.setWidth((Main.roots.get(Main.roots.size()-1).getWidth())/columns -
                        brick.maxHitPoints-1);
                brick.setHeight((Main.roots.get(Main.roots.size()-1).getHeight()/2)/rows -
                        brick.maxHitPoints-1);
                brick.setX(j * (brick.getWidth() + brick.maxHitPoints+1) + brick.maxHitPoints/2.0);
                brick.setY(i * (brick.getHeight() + brick.maxHitPoints+1));
                for(int k = 1; k < brick.maxHitPoints; k++){
                    brick.setHitPoints(i);
                }
                bricks.add(brick);
            }
        }
        return  bricks;
    }

    void move(double speed){
        setY(getY() + speed);
    }

    private Color hpColor(){
        return Color.rgb(
                255,
                255 - 255/maxHitPoints * (hitPoints - 1),
                255 - 255/maxHitPoints * (hitPoints - 1)
        );
    }

    private Color getRandomColor(){
        Random random = new Random();
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        return Color.rgb(r, g, b);
    }

}
