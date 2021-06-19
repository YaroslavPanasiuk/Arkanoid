package sample;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Parent;

public abstract class BaseObject extends Parent{
    protected DoubleProperty widthProperty = new SimpleDoubleProperty(0);
    protected DoubleProperty heightProperty = new SimpleDoubleProperty(0);
    protected DoubleProperty xProperty = new SimpleDoubleProperty(0);
    protected DoubleProperty yProperty = new SimpleDoubleProperty(0);

    DoubleProperty widthProperty() {
        return  widthProperty;
    }
    double getWidth(){
        return widthProperty.get();
    }
    void setWidth(double width){
        this.widthProperty.set(width);
    }

    DoubleProperty heightProperty() {
        return  heightProperty;
    }
    double getHeight(){
        return heightProperty.get();
    }
    void setHeight(double height){
        this.heightProperty.set(height);
    }

    DoubleProperty xProperty() {
        return  xProperty;
    }
    double getX(){
        return xProperty.get();
    }
    void setX(double x){
        this.xProperty.set(x);
    }

    DoubleProperty yProperty() {
        return  yProperty;
    }
    double getY(){
        return yProperty.get();
    }
    void setY(double y){
        this.yProperty.set(y);
    }

    boolean isCollisionWith(BaseObject baseObject){
        return  getX() + getWidth() > baseObject.getX() &&
                getX() < baseObject.getX() + baseObject.getWidth() &&
                getY() + getHeight() > baseObject.getY() &&
                getY() < baseObject.getY() + baseObject.getHeight();
    }

}