import java.util.HashMap;
import java.util.Map;

public class PolyhedronNamesUtil {
    public static Map<Integer, String> map = new HashMap<Integer, String>() {
        {
            put(3, "Triangles");
            put(4, "Quadrangles");
            put(5, "Pentagons");
            put(6, "Hexagons");
            put(7, "Heptagons");
            put(8, "Octagons");
            put(9, "Nonagons");
            put(10, "Decagons");
            put(11, "Hendecagons");
            put(12, "Dodecagons");
        }
    };
}
