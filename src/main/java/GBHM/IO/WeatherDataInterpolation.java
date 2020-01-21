/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.IO;

import net.casnw.home.io.Raster;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble2DArray;
import net.casnw.home.poolData.PoolObject;
import net.casnw.home.poolData.PoolObjectArray;
import GBHM.Utils.Interpolation;
import GBHM.Utils.ObjectUtil;
import GBHM.Utils.Point;

/**
 *
 * @author longyinping
 */
public class WeatherDataInterpolation extends AbsComponent {

    //In
    public PoolObjectArray precPoints;
    public PoolObjectArray tmeanPoints;
    public PoolObjectArray tmaxPoints;
    public PoolObjectArray tminPoints;
    public PoolObjectArray EpPoints;
    public PoolObject raster;
    //end of In
    //Out
    public PoolDouble2DArray prec_array;
    public PoolDouble2DArray Ep_array;
    public PoolDouble2DArray tmean_array;
    public PoolDouble2DArray tmax_array;
    public PoolDouble2DArray tmin_array;
    //end of OUT

    @Override
    public void run() {
        Point[] precPts;//nstation
        Point[] EpPts;//nstation
        Point[] tmaxPts;//nstation
        Point[] tminPts;//nstation
        Point[] tmeanPts;//nstation        
        precPts = ObjectUtil.toPoint(precPoints.getValue());
        EpPts = ObjectUtil.toPoint(EpPoints.getValue());
        tmeanPts = ObjectUtil.toPoint(tmeanPoints.getValue());
        tmaxPts = ObjectUtil.toPoint(tmaxPoints.getValue());
        tminPts = ObjectUtil.toPoint(tminPoints.getValue());
        Raster ascii = (Raster) raster.getValue();

        prec_array.setValue(Interpolation.DDW(ascii, precPts, 8, 130.0 * 1000.0, 400.0 * 1000.0, 4.0));
        Ep_array.setValue(Interpolation.IDW(ascii, EpPts, 8, 2));
        tmean_array.setValue(Interpolation.IDW(ascii, tmeanPts, 8, 2));
        tmax_array.setValue(Interpolation.IDW(ascii, tmaxPts, 8, 2));
        tmin_array.setValue(Interpolation.IDW(ascii, tminPts, 8, 2));

    }
}
