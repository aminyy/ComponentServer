/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.Utils;

/**
 *
 * @author longyinping
 */
public class SoilLayerDepth {

    public static double[] calSoilLayerDepth(double SoilDepth) {
        double[] d1;
        double[] d;
        double tmp;
        int i;
        int layer;
        d1 = new double[10];
        d1[0] = 0.05;
        tmp = d1[0];
        for (i = 1; i < 10; i++) {
            if (i <= 3) {
                d1[i] = d1[i - 1] + 0.05;
            } else if (i == 4) {
                d1[i] = d1[i - 1] + 0.10;
            } else if (i == 5 || i == 6) {
                d1[i] = d1[i - 1] + 0.20;
            } else if (i == 7) {
                d1[i] = d1[i - 1] + 0.30;
            } else if (i == 8 || i == 9) {
                d1[i] = d1[i - 1] + 0.50;
            }
            tmp = tmp + d1[i];
            if (tmp >= SoilDepth) {
                break;
            }
        }
        d1[i] = d1[i] + SoilDepth - tmp;
        if (d1[i] < d1[i - 1] - 0.05) {
            d1[i - 1] = 0.5 * (d1[i] + d1[i - 1]);
            d1[i] = d1[i - 1];
        }
        layer = i + 1;
        d = new double[layer];
        for (i = 0; i < layer; i++) {
            d[i] = d1[i];
        }
        return d;
    }
}
