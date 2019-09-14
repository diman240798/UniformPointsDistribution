package sample;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LinesLogic {

    public static List<Line> createLinesFrom(List<Point> randPoints) {
        List<Line> result = new ArrayList<>();

        for (int i = 0; i < randPoints.size(); i++) {
            Point point = randPoints.get(i);
            for (int j = 0; j < randPoints.size(); j++) {
                if (i == j) continue;
                Point p = randPoints.get(j);
                checkLineAndAdd(point, p, result);
            }
        }

        return result;
    }

    private static void checkLineAndAdd(Point point, Point p, List<Line> result) {
        Line line = new Line(point, p);
        List<Line> possibleCoincidences = result.stream().filter(l ->
                (Math.round(l.k * 100000000) == Math.round(line.k * 100000000) && Math.round(l.b * 100000000) == Math.round(line.b * 100000000))  && (
                        (line.start == l.end || line.start == l.start) ||
                        (line.end == l.end || line.end == l.start)
                )
        ).collect(Collectors.toList());


        for (int i = 0; i < possibleCoincidences.size(); i++) {
            Line l = possibleCoincidences.get(i);
            if (line.k > 0 && l.k < 0 || line.k < 0 && l.k > 0) continue;
            boolean one = false, two = false;
            double k = line.k;
            if (k > 0) {
                one = l.getMinX() >= line.getMaxX() && l.getMinY() >= line.getMaxY();
                two = l.getMaxX() <= line.getMinX() && l.getMaxY() <= line.getMinY();
            } else if (k < 0) {
                one = l.getMaxX() <= line.getMinX() && l.getMinY() >= line.getMaxY();
                two = l.getMinX() >= line.getMaxX() && l.getMaxY() <= line.getMinY();
            } else {
                one = line.getMaxY() == l.getMaxY();
                two = l.getMaxX() < line.getMinX() || l.getMinX() > line.getMaxX();
            }
            if (!one && !two) return;
        }
        result.add(line);
    }

    public static List<Line> onlyShortestAreleft(List<Line> allLines) {
        Set<Line> shorts = getIntersected(allLines);
        return new ArrayList<>(shorts);
    }

    private static Set<Line> getIntersected(List<Line> allLines) {
        List<Line> shorts = new ArrayList<>();
        for (int i = 0; i < allLines.size(); i++) {
            Line line = allLines.get(i);
            for (int j = 0; j < allLines.size(); j++) {
                if (i == j) continue;
                Line l = allLines.get(j);
                boolean intersect = line.intersect(l);
                if (intersect) {
                    Line shortest = getShortestLine(line, l);
                    shorts.add(shortest);
                }
            }
        }
        return new HashSet<>(shorts);
    }

    private static Line getShortestLine(Line line, Line l) {
        return line.getLength() > l.getLength() ? l : line;
    }
}