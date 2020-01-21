/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.Utils;

/**
 *
 * @author longyinping
 */
public class Statistics {

    //求两点间的绝对距离
    public static double distance(double X1, double Y1, double X2, double Y2) {
        double distance;
        distance = Math.pow(X1 - X2, 2) + Math.pow(Y1 - Y2, 2);
        distance = Math.sqrt(distance);
        return distance;
    }

    public static double distance(Point p1, Point p2) {
        double distance;
        distance = Math.pow(p1.X - p2.X, 2) + Math.pow(p1.Y - p2.Y, 2);
        distance = Math.sqrt(distance);
        return distance;
    }

    public static double min(double[] value) {
        double min;
        min = 1.0e37;
        for (int i = 0; i < value.length; i++) {
            min = Math.min(min, value[i]);
        }
        return min;
    }

    public static int minNumber(double[] value) {
        int minNum = 0;
        double min;
        min = value[0];
        for (int i = 1; i < value.length; i++) {
            if (value[i] < min) {
                min = value[i];
                minNum = i;
            }
        }
        return minNum;
    }

    //升序排列
    public static double[] ascendOrder(double[] value) {
        int i, j;
        double t;

        for (i = 0; i < value.length; i++) {
            for (j = i + 1; j < value.length; j++) {
                if (value[j] < value[i]) {
                    t = value[i];
                    value[i] = value[j];
                    value[j] = t;
                }
            }
        }
        return value;
    }

    public static int[] ascendOrderNumber(double[] value) {

        double[] value1;
        double max = 1.0e37;
        value1 = new double[value.length];
        System.arraycopy(value, 0, value1, 0, value.length);
        int[] num = new int[value1.length];
        for (int i = 0; i < value1.length; i++) {
            num[i] = minNumber(value1);
            value1[num[i]] = max;
        }
        return num;
    }

    public static double blockMean(double[][] value, int row, int col, int radius, double nodata) {
        double mean = nodata;
        double sum = 0;
        int num = 0;
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                if (i != 0 && j != 0) {
                    if (value[row + i][col + j] != nodata && !new Double(value[row + i][col + j]).equals(new Double(Double.NaN))) {
                        sum = sum + value[row + i][col + j];
                        num++;
                    }
                }
            }
        }
        if (num > 0) {
            mean = sum / num;
        }
        return mean;
    }
}
