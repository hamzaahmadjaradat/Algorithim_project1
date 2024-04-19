package com.example.algorithim_project1;
import javafx.scene.Group;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class CircleText extends Group {
    private Circle circle;
    private VBox text;

    public CircleText(double centerX, double centerY, double radius, VBox text) {
        circle = new Circle(centerX, centerY, radius);
        this.text = text;
        getChildren().addAll(circle, text);
    }

    public Circle getCircle() {
        return circle;
    }

    public VBox getText() {
        return text;
    }

    public void setCenterX(double centerX) {
        circle.setCenterX(centerX);
    }

    public void setCenterY(double centerY) {
        circle.setCenterY(centerY);
    }
}