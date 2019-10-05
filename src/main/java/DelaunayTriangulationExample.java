import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;


/**
 * Simple implementation of an incremental 2D Delaunay triangulation algorithm
 * written in Java.
 * 
 * @author Johannes Diemke
 */
public class DelaunayTriangulationExample implements GLEventListener, MouseListener {
    private static final int COUNT = 30;
    private static final int SIZE = 1000;

    private static final Dimension DIMENSION = new Dimension(SIZE, SIZE);

    private static final Color COLOR_TRIANGLE_FILL = new Color(47, 47, 47);
    private static final Color COLOR_TRIANGLE_EDGES = new Color(5, 234, 234);
    private static final Color COLOR_TRIANGLE_BORDER = new Color(241, 241, 121);
    private static final Color COLOR_BACKGROUND = new Color(47, 47, 47);

    DelaunayTriangulator delaunayTriangulator;
    List<Vector2D> pointSet = new ArrayList<>();

    public static void main(String[] args) throws NotEnoughPointsException {
        Frame frame = new Frame("Delaunay Triangulation Example");
        frame.setResizable(false);

        GLCapabilities caps = new GLCapabilities(GLProfile.get("GL2"));
        caps.setSampleBuffers(true);
        caps.setNumSamples(8);

        GLCanvas canvas = new GLCanvas(caps);

        DelaunayTriangulationExample ex = new DelaunayTriangulationExample();
        MouseListener lister = ex;
        canvas.addGLEventListener(ex);
        canvas.setPreferredSize(DIMENSION);
        canvas.addMouseListener(lister);

        frame.add(canvas);

        final FPSAnimator animator = new FPSAnimator(canvas, 25);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });

        frame.setVisible(true);
        frame.pack();
        animator.start();


        /*List<MyPoint> myPoints = drawPointsAndGet();
        myPoints.forEach(p-> ex.pointSet.add(new Vector2D(p.x * SIZE, p.y * SIZE)));
        ex.delaunayTriangulator.triangulate();*/
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glDisable(GL.GL_CULL_FACE);
        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glClearColor(COLOR_BACKGROUND.getRed() / 255.0f, COLOR_BACKGROUND.getGreen() / 255.0f,
                COLOR_BACKGROUND.getBlue() / 255.0f, 1);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

        gl.setSwapInterval(1);
        gl.glDisable(GL2.GL_CULL_FACE);

        delaunayTriangulator = new DelaunayTriangulator(pointSet);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, DIMENSION.getWidth(), DIMENSION.getHeight(), 0, 1.0, -1.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, 0.0f);

        gl.glLineWidth(1.0f);
        gl.glColor3ub((byte) COLOR_TRIANGLE_FILL.getRed(), (byte) COLOR_TRIANGLE_FILL.getGreen(),
                (byte) COLOR_TRIANGLE_FILL.getBlue());
        gl.glBegin(GL.GL_TRIANGLES);

        for (int i = 0; i < delaunayTriangulator.getTriangles().size(); i++) {
            Triangle2D triangle = delaunayTriangulator.getTriangles().get(i);
            Vector2D a = triangle.a;
            Vector2D b = triangle.b;
            Vector2D c = triangle.c;

            gl.glVertex2d(a.x, a.y);

            gl.glVertex2d(b.x, b.y);
            gl.glVertex2d(c.x, c.y);

        }

        gl.glEnd();
        gl.glColor3ub((byte) COLOR_TRIANGLE_EDGES.getRed(), (byte) COLOR_TRIANGLE_EDGES.getGreen(),
                (byte) COLOR_TRIANGLE_EDGES.getBlue());
        gl.glBegin(GL.GL_LINES);

        for (int i = 0; i < delaunayTriangulator.getTriangles().size(); i++) {
            Triangle2D triangle = delaunayTriangulator.getTriangles().get(i);
            Vector2D a = triangle.a;
            Vector2D b = triangle.b;
            Vector2D c = triangle.c;

            gl.glVertex2d(a.x, a.y);
            gl.glVertex2d(b.x, b.y);
            gl.glVertex2d(b.x, b.y);
            gl.glVertex2d(c.x, c.y);
            gl.glVertex2d(c.x, c.y);
            gl.glVertex2d(a.x, a.y);
        }

        gl.glEnd();

        // draw all points
        gl.glPointSize(5.5f);
        gl.glColor3f(0.2f, 1.2f, 0.25f);

        gl.glColor3ub((byte) COLOR_TRIANGLE_BORDER.getRed(), (byte) COLOR_TRIANGLE_BORDER.getGreen(),
                (byte) COLOR_TRIANGLE_BORDER.getBlue());
        gl.glBegin(GL.GL_POINTS);

        for (Vector2D vector : pointSet) {
            gl.glVertex2d(vector.x, vector.y);
        }

        gl.glEnd();



        if (!delaunayTriangulator.getTriangles().isEmpty()) {
            Map<Integer, Integer> result = new HashMap<>();

            Map<Vector2D, List<Triangle2D>> generalMap = new HashMap<>();

            Map<Vector2D, List<Triangle2D>> mapa = delaunayTriangulator.getTriangles().stream().collect(Collectors.groupingBy(tr -> tr.a));
            Map<Vector2D, List<Triangle2D>> mapb = delaunayTriangulator.getTriangles().stream().collect(Collectors.groupingBy(tr -> tr.b));
            Map<Vector2D, List<Triangle2D>> mapc = delaunayTriangulator.getTriangles().stream().collect(Collectors.groupingBy(tr -> tr.c));

            fillGeneralMap(generalMap, mapa);
            fillGeneralMap(generalMap, mapb);
            fillGeneralMap(generalMap, mapc);

            generalMap.forEach((point, triangles) -> {
                if (triangles.size() < 4) return;

                Set<Vector2D> set = new HashSet<>();
                triangles.forEach(tr -> {
                    set.add(tr.a);
                    set.add(tr.b);
                    set.add(tr.c);
                });
                set.remove(point);

                boolean greaterX = set.stream().allMatch(p -> p.x > point.x);
                boolean smallerX = set.stream().allMatch(p -> p.x < point.x);
                boolean greaterY = set.stream().allMatch(p -> p.y > point.y);
                boolean smallerY = set.stream().allMatch(p -> p.y < point.y);

                if (!greaterX || !greaterY || !smallerX || !smallerY) {
                    int polyHedronType = triangles.size();
                    int res = result.getOrDefault(polyHedronType, 0);
                    res += 1;
                    result.put(polyHedronType, res);
                }

            });

            int a =6;
            int b = a;
        }
    }

    private void fillGeneralMap(Map<Vector2D, List<Triangle2D>> generalMap, Map<Vector2D, List<Triangle2D>> shortMap) {
        shortMap.forEach((point, triangles) -> {
            List<Triangle2D> list = generalMap.getOrDefault(point, new ArrayList<>());
            list.addAll(triangles);
            generalMap.put(point, list);
        });
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        List<MyPoint> myPoints = drawPointsAndGet();
        myPoints.forEach(p-> pointSet.add(new Vector2D(p.x * SIZE, p.y * SIZE)));
        try {
            delaunayTriangulator.triangulate();
        } catch (NotEnoughPointsException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        /*Point p = e.getPoint();
        pointSet.add(new Vector2D(p.x, p.y));

        try {
            delaunayTriangulator.triangulate();
        } catch (NotEnoughPointsException e1) {
        }*/
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }



    private static List<MyPoint> drawPointsAndGet() {
        List<MyPoint> points = PointsLogic.getHaltonPoints(COUNT);
        double averageDistanceBetweenPoints = PointsLogic.getAverageDistanceBetweenPoints(points);
        List<MyPoint> randomPoints = PointsLogic.generatePointWithDistance(averageDistanceBetweenPoints, COUNT);

        return randomPoints;
    }
}
