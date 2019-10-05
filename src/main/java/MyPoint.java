public class MyPoint {
    public double x;
    public double y;

    public MyPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public MyPoint() {
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyPoint myPoint = (MyPoint) o;

        if (Double.compare(myPoint.x, x) != 0) return false;
        return Double.compare(myPoint.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
