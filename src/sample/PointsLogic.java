package sample;

import org.apache.commons.math3.random.HaltonSequenceGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PointsLogic {

    public static List<Point> getHaltonPoints(int count) {
        HaltonSequenceGenerator gen = new HaltonSequenceGenerator(2);

        List<Point> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double[] nextVector = gen.nextVector();
            list.add(new Point(nextVector[0], nextVector[1]));
        }
        return list;
    }

    public static double getAverageDistanceBetweenPoints(List<Point> points) {
        List<Double> avDistances = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            Point p0 = points.get(i);
            for (int j = 0; j < points.size(); j++) {
                if (i == j) continue;
                Point p1 = points.get(j);
                double distBtwTwoPoints = getDistBtwTwoPoints(p0, p1);
                if (distBtwTwoPoints < 3.65 / points.size())
                    avDistances.add(distBtwTwoPoints);
            }
        }
        Double sum = avDistances.stream().reduce(Double::sum).get();
        return sum / avDistances.size();
    }

    public static double getDistBtwTwoPoints(Point p0, Point p1) {
        return Math.sqrt(
                (p0.x - p1.x) * (p0.x - p1.x) +
                        (p0.y - p1.y) * (p0.y - p1.y)
        );
    }

    public static List<Point> generatePointWithDistance(double minDist, int count) {
        List<Point> points = new ArrayList<Point>();

        Random random = new Random();

        while (points.size() < count) {
            double x = random.nextDouble();
            double y = random.nextDouble();
            Point point = new Point(x, y);

            if (points.size() < 1) {
                points.add(point);
            } else {
                if (checkPoint(points, point, minDist)) {
                    points.add(point);
                }
            }
        }
        return points;

    }

    private static boolean checkPoint(List<Point> points, Point point, double minDist) {
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            double distBtwTwoPoints = getDistBtwTwoPoints(p, point);
            if (distBtwTwoPoints < minDist / Math.sqrt(2))
                return false;
        }
        return true;
    }
}
