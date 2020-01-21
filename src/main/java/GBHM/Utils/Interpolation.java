/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.Utils;

import net.casnw.home.io.Raster;

/**
 *
 * @author longyinping
 */
public class Interpolation {

    //value[] is the to be interpolated elements at different stations;
    //stationXY[][] is the coordinates of the stations,the size is nstation*2, the first collomn of which is X, and the second is Y ;
    //ascii is a ASCII object
    //nearestPointNum is the number of nearest points to be selected
    public static double[][] IDW(Raster raster, double[] value, double[][] stationXY, int nearestPointNum, double p) {
        Point[] pt = new Point[value.length];
        for (int i = 0; i < value.length; i++) {
            pt[i].X = stationXY[i][0];
            pt[i].Y = stationXY[i][1];
            pt[i].value = value[i];
        }
        return IDW(raster, pt, nearestPointNum, p);
    }

    public static double[][] IDW(Raster raster, Point[] pt, int nearestPointNum, double p) {
        int row, col;
        double[][] value;

        int nrows = raster.getRows();
        int ncols = raster.getCols();
        double xll = raster.getXll();
        double yll = raster.getYll();
        double cellsize = raster.getCellsize();
        double[][] data = raster.getData();
        double nodata = Double.parseDouble(raster.getNDATA());

        value = new double[nrows][ncols];

        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                if (new Double(data[row][col]).equals((new Double(Double.NaN)))) {
                    value[row][col] = nodata;
                } else {
                    Point pt0 = new Point();
                    pt0.X = xll + col * cellsize;
                    pt0.Y = yll + (nrows - 1 - row) * cellsize;
                    value[row][col] = IDW(pt0, pt, nearestPointNum, p);
                }
            }
        }

        return value;
    }

    //pt0：插值点
    //pt:采样点
    //nearestPointNum：距离最近的nearestPointNum个点
    //p:距离的幂
    public static double IDW(Point pt0, Point[] pt, int nearestPointNum, double p) {
        //参考文献：龙银平等：SWAT模型水文过程模拟的数据不确定性分析——以青海湖布哈河流域为例

        double[] distance;
        int[] order;//距离由近及远，Point的序号

        nearestPointNum = Math.min(pt.length, nearestPointNum);

        distance = new double[pt.length];
        for (int i = 0; i < pt.length; i++) {
            distance[i] = Statistics.distance(pt0, pt[i]);
        }

        order = Statistics.ascendOrderNumber(distance);

        double sum = 0;//分母
        for (int i = 0; i < nearestPointNum; i++) {
            sum = sum + Math.pow(distance[order[i]], -p);
        }

        double sum1 = 0;//分子
        for (int i = 0; i < nearestPointNum; i++) {
            sum1 = sum1 + Math.pow(distance[order[i]], -p) * pt[order[i]].value;
        }

        return sum1 / sum;
    }

    public static double IDW(Point pt0, Point[] pt, int nearestPointNum) {
        double p = 2;
        return IDW(pt0, pt, nearestPointNum, p);
    }

    public static double IDW(Point pt0, Point[] pt) {
        int nearestPointNum = 8;
        double p = 2;
        return IDW(pt0, pt, nearestPointNum, p);
    }

    public static double[][] DDW(Raster raster, Point[] pts, int nearestPointNum, double maxDistance, double x0, double m) {
        double[][] value;
        int nrows = raster.getRows();
        int ncols = raster.getCols();
        double xll = raster.getXll();
        double yll = raster.getYll();
        double cellsize = raster.getCellsize();
        double[][] data = raster.getData();
        double nodata = Double.parseDouble(raster.getNDATA());

        value = new double[nrows][ncols];
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                if (new Double(data[row][col]).equals((new Double(Double.NaN)))) {
                    value[row][col] = nodata;
                } else {
                    Point pt0 = new Point();
                    pt0.X = xll + col * cellsize;
                    pt0.Y = yll + (nrows - 1 - row) * cellsize;
                    pt0.ELVE = data[row][col];
                    value[row][col] = DDW(pt0, pts, nearestPointNum, maxDistance, x0, m);
                }
            }
        }
        return value;
    }

    //Directional Distance Weighted method
    //参考《水资源综合评价模型及其在黄河流域的应用》，杨大文
    //pt0:插值点
    //pts:采样点
    //x0:控制空间衰减程度的基于经验的衰减距离
    //m:调节系数，在1至8之间，一般取4
    public static double DDW(Point pt0, Point[] pts, int nearestPointNum, double maxDistance, double x0, double m) {
        double[] distance;
        int[] order;//距离由近及远，Point的序号
        double[] wk, ak, Wk;
        int sign1, sign2;
        double sum_wl, sum_Wk, sum_W_P;
        double sta, sta1, sta2;
        double value;


        nearestPointNum = Math.min(pts.length, nearestPointNum);

        distance = new double[pts.length];
        for (int i = 0; i < pts.length; i++) {
            distance[i] = Statistics.distance(pt0, pts[i]);
        }
        order = Statistics.ascendOrderNumber(distance);

        wk = new double[nearestPointNum];
        ak = new double[nearestPointNum];
        Wk = new double[nearestPointNum];

        for (int i = 0; i < nearestPointNum; i++) {
            if (distance[order[i]] > maxDistance) {
                wk[i] = 0;
            } else {
                wk[i] = Math.exp(-distance[order[i]] * m / x0);
            }
        }

        for (int i = 0; i < nearestPointNum; i++) {
            ak[i] = 0;
            sum_wl = 0;
            for (int j = 0; j < nearestPointNum; j++) {
                if (j != i) {
                    if (pts[order[j]].Y >= pt0.Y) {
                        sign1 = 1;
                    } else {
                        sign1 = -1;
                    }
                    if (pts[order[i]].Y >= pt0.Y) {
                        sign2 = 1;
                    } else {
                        sign2 = -1;
                    }
                    sta1 = (pts[order[j]].X - pt0.X) / distance[order[j]];
                    sta2 = (pts[order[i]].X - pt0.X) / distance[order[i]];
                    sta = sign1 * Math.acos(sta1) - sign2 * Math.acos(sta2);
                    ak[i] = ak[i] + wk[j] * (1 - Math.cos(sta));
                    sum_wl = sum_wl + wk[j];
                }
            }
            if (sum_wl > 0) {
                ak[i] = ak[i] / sum_wl;
            } else {
                ak[i] = 0;
            }
        }
        sum_Wk = 0;
        sum_W_P = 0;
        for (int i = 0; i < nearestPointNum; i++) {
            Wk[i] = wk[i] * (1 + ak[i]);
            sum_Wk = sum_Wk + Wk[i];
            sum_W_P = sum_W_P + Wk[i] * pts[order[i]].value;
        }
        value = sum_W_P / sum_Wk;

        if (pt0.ELVE > 3000) {
            value = value * (1 + (pt0.ELVE - 1000) * 0.04 / 1000);
        }
        return value;
    }
}
