/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.IO;

import GBHM.Modules1.Functions;
import GBHM.Utils.Statistics;
import net.casnw.home.io.Raster;
import net.casnw.home.io.RasterReader;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.model.SpatialContext;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDouble2DArray;
import net.casnw.home.poolData.PoolDoubleArray;
import net.casnw.home.poolData.PoolString;

/**
 *
 * @author longyinping
 */
public class SoilParameters extends AbsComponent {

    //@In
    public PoolString wsatFile;
    public PoolString wrsdFile;
    public PoolString alphaFile;
    public PoolString waternFile;
    public PoolString ksatFile;
    public PoolDouble elev;
    public PoolDouble2DArray data;
    //@Out   
    public PoolDouble Ds;
    public PoolDouble Dg;
    public PoolDoubleArray D;
    public PoolDoubleArray wsat;
    public PoolDoubleArray wfld;
    public PoolDoubleArray wrsd;
    public PoolDoubleArray alpha;
    public PoolDoubleArray watern;
    public PoolDoubleArray k0;
    public PoolDouble kg;
    public PoolDouble GWcs;
    private double[][][] wsat3d, wrsd3d, alpha3d, watern3d, ksat3d, layerDepth3d, wfld3d;
    private double[][] soilDepth;
    private int[][] nlayer;
    private SpatialContext context;

    @Override
    public void init() {
        //Just for soil water parameters from DaiYongjiu et al.
        Raster wsatR;
        Raster wrsdR;
        Raster alphaR;
        Raster waternR;
        Raster ksatR;
        double[][] wsat, wrsd, alpha, watern, ksat, layerDepth;
        int ncols, nrows;
        double nodata;
        final double[] D = {0.045, 0.046, 0.075, 0.123, 0.204, 0.336, 0.554};

        wsatR = new RasterReader(wsatFile.getValue() + "_" + Integer.toString(1) + ".asc").readRaster();
        ncols = wsatR.getCols();
        nrows = wsatR.getRows();
        nodata = Double.parseDouble(wsatR.getNDATA());


        soilDepth = new double[nrows][ncols];
        nlayer = new int[nrows][ncols];
        wsat3d = new double[nrows][ncols][D.length];
        wrsd3d = new double[nrows][ncols][D.length];
        alpha3d = new double[nrows][ncols][D.length];
        watern3d = new double[nrows][ncols][D.length];
        ksat3d = new double[nrows][ncols][D.length];
        layerDepth3d = new double[nrows][ncols][D.length];
        wfld3d = new double[nrows][ncols][D.length];


        for (int layer = 0; layer < D.length; layer++) {
            wsatR = new RasterReader(wsatFile.getValue() + "_" + Integer.toString(layer + 1) + ".asc").readRaster();
            wrsdR = new RasterReader(wrsdFile.getValue() + "_" + Integer.toString(layer + 1) + ".asc").readRaster();
            alphaR = new RasterReader(alphaFile.getValue() + "_" + Integer.toString(layer + 1) + ".asc").readRaster();
            waternR = new RasterReader(waternFile.getValue() + "_" + Integer.toString(layer + 1) + ".asc").readRaster();
            ksatR = new RasterReader(ksatFile.getValue() + "_" + Integer.toString(layer + 1) + ".asc").readRaster();

            wsat = wsatR.getData();
            wrsd = wrsdR.getData();
            alpha = alphaR.getData();
            watern = waternR.getData();
            ksat = ksatR.getData();

            for (int i = 0; i < nrows; i++) {
                for (int j = 0; j < ncols; j++) {
                    if (!new Double(wsat[i][j]).equals(new Double(Double.NaN))) {
                        soilDepth[i][j] = soilDepth[i][j] + D[layer];
                        nlayer[i][j] = nlayer[i][j] + 1;
                        wsat3d[i][j][layer] = wsat[i][j];
                        wrsd3d[i][j][layer] = wrsd[i][j];
                        alpha3d[i][j][layer] = alpha[i][j];
                        watern3d[i][j][layer] = watern[i][j];
                        ksat3d[i][j][layer] = ksat[i][j];
                        layerDepth3d[i][j][layer] = D[layer];
                        wfld3d[i][j][layer] = Functions.MoistureFromSuction_V(wsat[i][j], wrsd[i][j], watern[i][j], alpha[i][j], -1.02);//VG model
                    }
                }
            }

            //for nodata values in raster wsat, water body
            for (int i = 0; i < nrows; i++) {
                for (int j = 0; j < ncols; j++) {
                    if (!new Double(data.getCellValue(i, j)).equals(new Double(Double.NaN))) {
                        if (layer == 0) {
                            if (new Double(wsat[i][j]).equals(new Double(Double.NaN))) {
                                nlayer[i][j] = 1;
                                int radius = 1;
                                do {
                                    soilDepth[i][j] = Statistics.blockMean(soilDepth, i, j, radius, 0);
                                    radius = radius + 1;
                                } while (soilDepth[i][j] == 0);
                                radius = radius - 1;
                                wsat3d[i][j][0] = Statistics.blockMean(wsat, i, j, radius, 0);
                                wrsd3d[i][j][0] = Statistics.blockMean(wrsd, i, j, radius, 0);
                                alpha3d[i][j][0] = Statistics.blockMean(alpha, i, j, radius, 0);
                                watern3d[i][j][0] = Statistics.blockMean(watern, i, j, radius, 0);
                                ksat3d[i][j][0] = Statistics.blockMean(ksat, i, j, radius, 0);
                                wfld3d[i][j][0] = Functions.MoistureFromSuction_V(wsat3d[i][j][0], wrsd3d[i][j][0], watern3d[i][j][0], alpha3d[i][j][0], -1.02);
                                layerDepth3d[i][j][0] = soilDepth[i][j];
                            }
                        }
                    }
                }
            }
        }
        context = (SpatialContext) this.getContext();

    }

    @Override
    public void run() {
        int i, j;
        double[] wsat1d, wrsd1d, alpha1d, watern1d, ksat1d, layerDepth1d, wfld1d;
        i = context.getCurrentRowNum();
        j = context.getCurrentColNum();
        if (i == 169 && j == 170) {
            System.out.println();
        }
        wsat1d = new double[nlayer[i][j]];
        wrsd1d = new double[nlayer[i][j]];
        alpha1d = new double[nlayer[i][j]];
        watern1d = new double[nlayer[i][j]];
        ksat1d = new double[nlayer[i][j]];
        layerDepth1d = new double[nlayer[i][j]];
        wfld1d = new double[nlayer[i][j]];
        for (int layer = 0; layer < nlayer[i][j]; layer++) {
            wsat1d[layer] = wsat3d[i][j][layer];
            wrsd1d[layer] = wrsd3d[i][j][layer];
            alpha1d[layer] = alpha3d[i][j][layer];
            watern1d[layer] = watern3d[i][j][layer];
            ksat1d[layer] = ksat3d[i][j][layer] * 10.0 / 24.0; //cm/d ---> mm/hour
            layerDepth1d[layer] = layerDepth3d[i][j][layer];
            wfld1d[layer] = wfld3d[i][j][layer];
        }
        this.wsat.setValue(wsat1d);
        this.wrsd.setValue(wrsd1d);
        this.alpha.setValue(alpha1d);
        this.watern.setValue(watern1d);
        this.k0.setValue(ksat1d);
        this.wfld.setValue(wfld1d);
        this.D.setValue(layerDepth1d);
        this.Ds.setValue(soilDepth[i][j]);
        double dg;
        dg = Ds.getValue();
        if (elev.getValue() >= 3500) {
            dg = Math.min(2.0, Ds.getValue());
        }
        this.Dg.setValue(dg);
        this.kg.setValue(ksat1d[nlayer[i][j] - 1] / 2);
        this.GWcs.setValue(0.1);

    }
}
