/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.Utils;

/**
 *
 * @author longyinping
 */
public class DataTypeConversion {

    public static int[][] integerFromDouble(double[][] value) {

        int[][] value2;
        int nrows = value.length;
        int ncols = value[0].length;
        value2 = new int[nrows][ncols];
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                if (!new Double(value[i][j]).equals(new Double(Double.NaN))) {
                    value2[i][j] = (int) value[i][j];
                } else {
                    value2[i][j] = -9999;
                }
            }
        }
        return value2;
    }
}
