package sample;

public final class Line {
    public Point start;
    public Point end;
    public final double k, b; // y = kx + b

    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;

        this.k = (start.y - end.y) / (start.x - end.x);
        this.b = end.y - k * end.x;

    }

    public boolean intersect(Line line) {
        double x = (line.b - this.b) / (this.k - line.k);
        double y = this.k * x + this.b;

        Point point = new Point(x, y);
        return  this.isInside(point) && line.isInside(point);
    }

    public boolean isInside(Point p) {
        double maxX = Math.max(start.x, end.x);
        double minX = Math.min(start.x, end.x);
        double maxY = Math.max(start.y, end.y);
        double minY = Math.min(start.y, end.y);

        return p.x > minX && p.x < maxX &&
               p.y > minY && p.y < maxY;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line line = (Line) o;

        if (Double.compare(line.k, k) != 0) return false;
        if (Double.compare(line.b, b) != 0) return false;
        if (start != null ? !start.equals(line.start) : line.start != null) return false;
        return end != null ? end.equals(line.end) : line.end == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        temp = Double.doubleToLongBits(k);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(b);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public double getMaxX() {
        return Math.max(start.x, end.x);
    }

    public double getMinX() {
        return Math.min(start.x, end.x);
    }

    public double getMaxY() {
        return Math.max(start.y, end.y);
    }
    public double getMinY() {
        return Math.min(start.y, end.y);
    }

    public double getLength() {
        return Math.sqrt(getMaxX() - getMinX() * (getMaxX() - getMinX()) +
                        (getMaxY() - getMinY()) * (getMaxY() - getMinY()));
    }
}