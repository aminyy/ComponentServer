/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.IO;

import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble2DArray;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolIntegerArray;

/**
 *
 * @author longyinping
 */
public class Results extends AbsComponent {

    //In
    public PoolDouble2DArray Eact_array;//实际蒸散发//nrows,ncols
    public PoolDouble2DArray qin_array;//坡面产流//nrows,ncols
    public PoolDouble2DArray snowmelt_array;
    public PoolDouble2DArray discharge;
    public PoolInteger nsub;
    public PoolIntegerArray nflow;
    //end of In
    //Out
    public PoolDouble2DArray Eact_daily;
    public PoolDouble2DArray qin_daily;
    public PoolDouble2DArray snowmelt_daily;
    public PoolDouble2DArray discharge_daily;//nsub,nflow
    private int time;
    private int nrows;
    private int ncols;

    @Override
    public void init() {
        time = 0;
        nrows = Eact_array.getValue().length;
        ncols = Eact_array.getValue()[0].length;
    }

    @Override
    public void run() {
        if (time == 0) {
            Eact_daily.setValue(new double[nrows][ncols]);
            qin_daily.setValue(new double[nrows][ncols]);
            snowmelt_daily.setValue(new double[nrows][ncols]);
            discharge_daily.setValue(new double[nsub.getValue()][100]);
        }
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                Eact_daily.setCellValue(i, j, Eact_daily.getCellValue(i, j) + Eact_array.getCellValue(i, j));
                qin_daily.setCellValue(i, j, qin_daily.getCellValue(i, j) + qin_array.getCellValue(i, j) / 24);
                snowmelt_daily.setCellValue(i, j, snowmelt_daily.getCellValue(i, j) + snowmelt_array.getCellValue(i, j));
            }
        }
        for (int i = 0; i < nsub.getValue(); i++) {
            for (int j = 0; j < nflow.getValue(i); j++) {
                discharge_daily.setCellValue(i, j, discharge_daily.getCellValue(i, j) + discharge.getCellValue(i, j) / 24);
            }
        }
        System.out.print(time+"  ");
        System.out.println(discharge.getCellValue(nsub.getValue()-1, nflow.getValue(nsub.getValue()-1)-1));

        time++;
        if (time > 23) {
            time = 0;
        }

    }
}
