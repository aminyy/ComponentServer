/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.IO;

import net.casnw.home.io.Raster;
import net.casnw.home.io.RasterReader;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.model.SpatialContext;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDoubleArray;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolString;

/**
 *
 * @author longyinping
 */
public class BaseMaps extends AbsComponent {

    //@In    
    public PoolString demFile;
    public PoolString cellareaFile;
    public PoolString slopeFile;
    public PoolString slopeLengthFile;
    public PoolString soilDepthFile;
    public PoolString soilTypesFile;
    public PoolString landuseFile;
    public PoolString LAIFile;
    //@Out
    public PoolDouble area;
    public PoolDouble slp;
    public PoolDouble length;
    public PoolDouble Ds;
    public PoolInteger soilcode;
    public PoolInteger landcode;
    public PoolDouble Dg;
    public PoolDoubleArray D;
    public PoolInteger nrows;
    public PoolInteger ncols;
    public PoolDouble xllcorner;
    public PoolDouble yllcorner;
    public PoolDouble cellsize;
    public PoolDouble nodata;
    Raster cellarea;
    Raster slope;
    Raster slopeLength;
    Raster soilDepth;
    Raster soilTypes;
    Raster land;
    Raster dem;
    SpatialContext context;

    @Override
    public void init() throws Exception {
        dem = new RasterReader(demFile.getValue()).readRaster();
        cellarea = new RasterReader(cellareaFile.getValue()).readRaster();
        slope = new RasterReader(slopeFile.getValue()).readRaster();
        slopeLength = new RasterReader(slopeLengthFile.getValue()).readRaster();
        soilDepth = new RasterReader(soilDepthFile.getValue()).readRaster();
        soilTypes = new RasterReader(soilTypesFile.getValue()).readRaster();
        land = new RasterReader(landuseFile.getValue()).readRaster();


        context = (SpatialContext) this.getContext();

        nrows.setValue(cellarea.getRows());
        ncols.setValue(cellarea.getCols());
        xllcorner.setValue(cellarea.getXll());
        yllcorner.setValue(cellarea.getYll());
        cellsize.setValue(cellarea.getCellsize());
        nodata.setValue(Double.parseDouble(cellarea.getNDATA()));

    }

    @Override
    public void run() throws Exception {
        int col, row;
        row = context.getCurrentRowNum();
        col = context.getCurrentColNum();
        
        area.setValue(cellarea.getData()[row][col] * cellarea.getCellsize() * cellarea.getCellsize());
        slp.setValue(slope.getData()[row][col] / 100.0);
        length.setValue(slopeLength.getData()[row][col]);
        Ds.setValue(soilDepth.getData()[row][col]);
        soilcode.setValue((int) soilTypes.getData()[row][col]);
        landcode.setValue((int) land.getData()[row][col]);

        double dg;
        dg = Ds.getValue() * 10.0;
        if (dem.getData()[row][col] >= 3500) {
            dg = Math.min(2.0, Ds.getValue());
        }
        Dg.setValue(dg);
        D.setValue(calSoilLayerDepth(Ds.getValue()));
    }

    @Override
    public void clear() throws Exception {
    }

    public double[] calSoilLayerDepth(double SoilDepth) {
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
