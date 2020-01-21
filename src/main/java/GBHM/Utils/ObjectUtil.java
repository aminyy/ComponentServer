/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.Utils;

/**
 *
 * @author longyinping
 */
public class ObjectUtil {

    public static Object toObject(Point point) {
        Object obj = (Object) point;
        return obj;
    }

    public static Object[] toObject(Point[] points) {
        int num = points.length;
        Object[] objs;
        objs = new Object[num];
        for (int i = 0; i < num; i++) {
            objs[i] = toObject(points[i]);
        }
        return objs;
    }

    public static Point toPoint(Object obj) {
        Point point = (Point) obj;
        return point;
    }

    public static Point[] toPoint(Object[] objs) {
        int num = objs.length;
        Point[] points = new Point[num];
        for (int i = 0; i < num; i++) {
            points[i] = toPoint(objs[i]);
        }
        return points;
    }
}
