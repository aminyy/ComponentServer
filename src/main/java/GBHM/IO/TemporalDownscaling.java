/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.IO;

import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDate;
import net.casnw.home.poolData.PoolDouble2DArray;
import GBHM.Utils.TemporalDownscale;

/**
 *
 * @author longyinping
 */
public class TemporalDownscaling extends AbsComponent {

    //In
    public PoolDate currentDate;
    public PoolDouble2DArray prec_daily;//nrows,ncols
    public PoolDouble2DArray tmin_daily;//nrows,ncols
    public PoolDouble2DArray tmax_daily;//nrows,ncols
    public PoolDouble2DArray ep_daily;//nrows,ncols
    //Out
    public PoolDouble2DArray prec_hourly;//24,nrows,ncols
    public PoolDouble2DArray temper_hourly;//24,nrows,ncols
    public PoolDouble2DArray ep_hourly;//24,nrows,ncols
    private double[][][] prec;
    private double[][][] temper;
    private double[][][] ep;
    private int time;//[0,23]

    @Override
    public void init() {
        time = 0;
    }

    @Override
    public void run() {       

        if (time == 0) {
            int nrows = prec_daily.getValue().length;
            int ncols = prec_daily.getValue()[0].length;

            double[][][] prec1 = new double[nrows][ncols][24];
            double[][][] temper1 = new double[nrows][ncols][24];
            double[][][] ep1 = new double[nrows][ncols][24];

            prec = new double[24][nrows][ncols];
            temper = new double[24][nrows][ncols];
            ep = new double[24][nrows][ncols];

            for (int i = 0; i < nrows; i++) {
                for (int j = 0; j < ncols; j++) {
                    prec1[i][j] = TemporalDownscale.RainTemporalDownscale(currentDate.getValue(), prec_daily.getValue()[i][j]);
                    temper1[i][j] = TemporalDownscale.TemperatureTemporalDownscale(tmax_daily.getValue()[i][j], tmin_daily.getValue()[i][j]);
                    ep1[i][j] = TemporalDownscale.PotentialEvapTemporalDownscale(ep_daily.getValue()[i][j]);
                }
            }

            for (int idh = 0; idh < 24; idh++) {
                for (int i = 0; i < nrows; i++) {
                    for (int j = 0; j < ncols; j++) {
                        prec[idh][i][j] = prec1[i][j][idh];
                        temper[idh][i][j] = temper1[i][j][idh];
                        ep[idh][i][j] = ep1[i][j][idh];
                    }
                }
            }
        }

        prec_hourly.setValue(prec[time]);
        temper_hourly.setValue(temper[time]);
        ep_hourly.setValue(ep[time]);

        time++;
        if (time > 23) {
            time = 0;
        }
    }
}
