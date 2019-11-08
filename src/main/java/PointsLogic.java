
import org.apache.commons.math3.random.HaltonSequenceGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PointsLogic {

    public static List<MyPoint> getHaltonPoints(int count) {
        HaltonSequenceGenerator gen = new HaltonSequenceGenerator(2);

        List<MyPoint> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double[] nextVector = gen.nextVector();
            list.add(new MyPoint(nextVector[0], nextVector[1]));
        }
        return list;
    }

    public static double getAverageDistanceBetweenPoints(int size, int pointsCount) {
        return size * Math.sqrt(2 / (pointsCount * Math.sqrt(3))) / size;
    }

    public static double getDistBtwTwoPoints(MyPoint p0, MyPoint p1) {
        return Math.sqrt(
                (p0.x - p1.x) * (p0.x - p1.x) +
                        (p0.y - p1.y) * (p0.y - p1.y)
        );
    }

    public static List<MyPoint> generatePointWithDistance(double minDist, int count) {
        List<MyPoint> myPoints = new ArrayList<MyPoint>();

        Random random = new Random();

        while (myPoints.size() < count) {
            double x = random.nextDouble();
            double y = random.nextDouble();
            MyPoint myPoint = new MyPoint(x, y);

            if (myPoints.size() < 1) {
                myPoints.add(myPoint);
            } else {
                if (checkPoint(myPoints, myPoint, minDist)) {
                    myPoints.add(myPoint);
                }
            }
        }
        return myPoints;

    }

    private static boolean checkPoint(List<MyPoint> myPoints, MyPoint myPoint, double minDist) {
        for (int i = 0; i < myPoints.size(); i++) {
            MyPoint p = myPoints.get(i);
            double distBtwTwoPoints = getDistBtwTwoPoints(p, myPoint);
            if (distBtwTwoPoints < minDist / Math.sqrt(2))
                return false;
        }
        return true;
    }
}
