package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.List;

public class App extends Application {

    private static final double SIZE = 1000;
    private static final double RADIUS = 3;
    private static final int COUNT = 5;



    @Override
    public void start(Stage primaryStage) throws Exception {
        Rectangle box = getMainPlane();

        Pane pane = new Pane();
        pane.getChildren().add(box);

        List<Point> randPoints = drawPointsAndGet(pane);
        drawLinesandGet(pane, randPoints);


        Scene value = new Scene(pane, SIZE, SIZE);
        primaryStage.setScene(value);
        primaryStage.show();
    }

    private List<Line> drawLinesandGet(Pane pane, List<Point> randPoints) {
        List<Line> allLines = LinesLogic.createLinesFrom(randPoints);
        List<Line> onlyShortLines = LinesLogic.onlyShortestAreleft(allLines);


        onlyShortLines.forEach(line -> {
            double x = line.start.x * SIZE;
            double y = line.start.y * SIZE;
            double x1 = line.end.x * SIZE;
            double y1 = line.end.y * SIZE;

            javafx.scene.shape.Line realLine = new javafx.scene.shape.Line(x, y, x1, y1);
            realLine.setStroke(Color.LIGHTBLUE);
            realLine.setTranslateZ(RADIUS / 2);
            pane.getChildren().add(realLine);
        });
        return onlyShortLines;
    }

    private List<Point> drawPointsAndGet(Pane pane) {
        List<Point> points = PointsLogic.getHaltonPoints(COUNT);
        double averageDistanceBetweenPoints = PointsLogic.getAverageDistanceBetweenPoints(points);
        List<Point> randomPoints = PointsLogic.generatePointWithDistance(averageDistanceBetweenPoints, COUNT);

        randomPoints.forEach(p -> {
            double x = p.x * SIZE;
            double y = p.y * SIZE;


            Circle cirlce = new Circle(RADIUS, Color.RED);
            cirlce.setTranslateX(x);
            cirlce.setTranslateY(y);
            cirlce.setTranslateZ(RADIUS / 4);

            pane.getChildren().add(cirlce);

        });

        return randomPoints;
    }

    private Rectangle getMainPlane() {
        Rectangle box = new Rectangle(SIZE, SIZE);
        return box;
    }
}
