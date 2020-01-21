/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.IO;

import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDouble2DArray;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolInteger2DArray;
import net.casnw.home.poolData.PoolInteger3DArray;
import net.casnw.home.poolData.PoolIntegerArray;

/**
 *
 * @author longyinping
 */
public class RiverToGrid extends AbsComponent {

    //IN
    public PoolDouble2DArray Drw;//nsub,nflow
    public PoolDouble2DArray Dr;//nsub,nflow
    public PoolInteger nsub;
    public PoolInteger3DArray row;//nsub,nflow,ngrid
    public PoolInteger3DArray col;//nsub,nflow,ngrid
    public PoolInteger2DArray ngrid;//nsub,nflow
    public PoolIntegerArray nflow;//nsub    
    public PoolInteger nrows;
    public PoolInteger ncols;
    public PoolDouble nodata;
    //end of IN
    //OUT
    public PoolDouble2DArray Drw_grid;//nrows,ncols
    public PoolDouble2DArray Dr_grid;//nrows,ncols
    //end of OUT

    @Override
    public void init() {
        Dr_grid.setValue(this.convert(Dr.getValue(), nrows.getValue(), ncols.getValue(), nodata.getValue(), nsub.getValue(), nflow.getValue(), ngrid.getValue(), row.getValue(), col.getValue()));
    }

    @Override
    public void run() {
        Drw_grid.setValue(this.convert(Drw.getValue(), nrows.getValue(), ncols.getValue(), nodata.getValue(), nsub.getValue(), nflow.getValue(), ngrid.getValue(), row.getValue(), col.getValue()));
    }

    public double[][] convert(double[][] array, int nrows, int ncols, double nodata, int nsub, int[] nflow, int[][] ngrid, int[][][] row, int[][][] col) {
        double[][] array1;
        array1 = new double[nrows][ncols];

        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                array1[i][j] = nodata;
            }
        }
        for (int i = 0; i < nsub; i++) {
            for (int j = 0; j < nflow[i]; j++) {
                for (int k = 0; k < ngrid[i][j]; k++) {
                    array1[row[i][j][k]][col[i][j][k]] = array[i][j];
                }
            }
        }
        return array1;
    }
}
