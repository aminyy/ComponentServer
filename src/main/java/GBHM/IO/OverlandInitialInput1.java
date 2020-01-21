/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.IO;

import java.io.IOException;
import net.casnw.home.io.Raster;
import net.casnw.home.io.RasterReader;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.model.SpatialContext;
import net.casnw.home.model.TemporalContext;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDouble2DArray;
import net.casnw.home.poolData.PoolDouble3DArray;
import net.casnw.home.poolData.PoolDoubleArray;
import net.casnw.home.poolData.PoolString;

/**
 *
 * @author longyinping
 */
public class OverlandInitialInput1 extends AbsComponent {

    //IN
    public PoolString moisturePath;//optional
    public PoolString dglPath;//optional
    public PoolString GWstPath;//optional
    public PoolString CstPath;//optional
    public PoolString snowPath;//optional
    public PoolString sstPath;//optional
    public PoolDouble3DArray w_array;
    public PoolDouble2DArray dgl_array;
    public PoolDouble2DArray GWst_array;
    public PoolDouble2DArray Cst_array;
    public PoolDouble2DArray snowdepth_array;
    public PoolDouble2DArray sst_array;
    //end of IN
    //Parameter
    public PoolDouble Ds;
    public PoolDoubleArray D;
    public PoolDouble Dg;
    public PoolDouble GWcs;
    public PoolDoubleArray wrsd;
    public PoolDoubleArray wsat;
    //end of Parameter
    //OUT
    public PoolDoubleArray w;
    public PoolDouble dgl;
    public PoolDouble GWst;
    public PoolDouble Cst;
    public PoolDouble snowdepth;
    public PoolDouble sst;
    //end of OUT
    int nlayer;
    private SpatialContext spatialcontext;
    private TemporalContext temporalContext;

    @Override
    public void init() throws IOException {
        spatialcontext = (SpatialContext) this.getContext();
        temporalContext = (TemporalContext) this.getContext().getContext().getContext();
        int nrows = spatialcontext.getRowsNum();
        int ncols = spatialcontext.getColsNum();

        w_array.setValue(new double[nrows][ncols][]);
        dgl_array.setValue(new double[nrows][ncols]);
        GWst_array.setValue(new double[nrows][ncols]);
        Cst_array.setValue(new double[nrows][ncols]);
        snowdepth_array.setValue(new double[nrows][ncols]);
        sst_array.setValue(new double[nrows][ncols]);

        nlayer = 10;
        if (moisturePath.getValue() != null && !moisturePath.getValue().equals("")) {
            w_array.setValue(readMoisture());
        }

        if (dglPath.getValue() != null && !dglPath.getValue().equals("")) {
            dgl_array.setValue(read(dglPath.getValue()));
        }

        if (GWstPath.getValue() != null && !GWstPath.getValue().equals("")) {
            GWst_array.setValue(read(GWstPath.getValue()));
        }

        if (CstPath.getValue() != null && !CstPath.getValue().equals("")) {
            Cst_array.setValue(read(CstPath.getValue()));
        } else {
            Cst_array.setValue(generate());
        }

        if (snowPath.getValue() != null && !snowPath.getValue().equals("")) {
            snowdepth_array.setValue(read(snowPath.getValue()));
        } else {
            snowdepth_array.setValue(generate());
        }

        if (sstPath.getValue() != null && !sstPath.getValue().equals("")) {
            sst_array.setValue(read(sstPath.getValue()));
        } else {
            sst_array.setValue(generate());
        }

    }

    @Override
    public void run() {
        int row, col;
        row = spatialcontext.getCurrentRowNum();
        col = spatialcontext.getCurrentColNum();

        if (temporalContext.getCurrentTime().getTime().compareTo(temporalContext.getStartTime().getTime()) == 0) {//initialize
            if (moisturePath.getValue() != null && !moisturePath.getValue().equals("")) {
                w.setValue(w_array.getValue(row, col));
            } else {
                w.setValue(generateMoisture(Ds.getValue(), D.getValue(), wrsd.getValue(), wsat.getValue()));
            }
            if (dglPath.getValue() != null && !dglPath.getValue().equals("")) {
                dgl.setValue(dgl_array.getCellValue(row, col));
            } else {
                dgl.setValue(Ds.getValue());
            }
            if (GWstPath.getValue() != null && !GWstPath.getValue().equals("")) {
                GWst.setValue(GWst_array.getCellValue(row, col));
            } else {
                GWst.setValue((Ds.getValue() + Dg.getValue() - dgl.getValue()) * GWcs.getValue());               
            }
        } else {//read from the pool
            w.setValue(w_array.getValue(row, col));
            dgl.setValue(dgl_array.getCellValue(row, col));
            GWst.setValue(GWst_array.getCellValue(row, col));
            Cst.setValue(Cst_array.getCellValue(row, col));
            snowdepth.setValue(snowdepth_array.getCellValue(row, col));
            sst.setValue(sst_array.getCellValue(row, col));
        }
    }

    private double[][][] readMoisture() throws IOException {
        double[][][] array;
        Raster raster;
        int nrows, ncols;
        double nodata;
        RasterReader moisture;
        double[][][] w1;//nlayer,nrows,ncols
        double[] w2;//nlayer;
        double[] w3;
        w1 = new double[nlayer][][];
        w2 = new double[nlayer];


        moisture = new RasterReader(moisturePath.getValue() + Integer.toString(1) + ".asc");
        raster = moisture.readRaster();
        nrows = raster.getRows();
        ncols = raster.getCols();
        nodata = Double.parseDouble(raster.getNDATA());
        array = new double[nrows][ncols][];

        for (int ilayer = 0; ilayer < nlayer; ilayer++) {
            moisture = new RasterReader(moisturePath.getValue() + Integer.toString(ilayer + 1) + ".asc");
            w1[ilayer] = moisture.readRaster().getData();
        }

        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                if (w1[0][row][col] != nodata) {

                    int num = 0;//计算土壤层数
                    for (int ilayer = 0; ilayer < nlayer; ilayer++) {
                        w2[ilayer] = w1[ilayer][row][col];
                        if (w2[ilayer] != nodata) {
                            num++;
                        }
                    }

                    w3 = new double[num];
                    /* for (int ilayer = 0; ilayer < num; ilayer++) {
                     w3[ilayer] = w2[ilayer];
                     }*/
                    System.arraycopy(w2, 0, w3, 0, num);

                    array[row][col] = w3;

                } else {
                    array[row][col] = null;
                }
            }
        }
        return array;
    }

    private double[] generateMoisture(double Ds, double[] D, double wrsd[], double wsat[]) {
        double[] array;
        double D0;
        double tmp = 0;
        array = new double[D.length];        
        for (int i = 0; i < D.length; i++) {
            D0 = Ds * wrsd[i] / (wsat[i] - wrsd[i]);
            tmp = tmp + D[i];
            array[i] = wsat[i] * (D0 + tmp) / (D0 + Ds);
        }
        return array;
    }

    private double[][] read(String FileName) throws IOException {
        double[][] array;
        RasterReader DGL = new RasterReader(FileName);
        array = DGL.readRaster().getData();
        return array;
    }

    private double[][] generate() {
        int nrows, ncols;
        double[][] array;
        nrows = spatialcontext.getRowsNum();
        ncols = spatialcontext.getColsNum();
        array = new double[nrows][ncols];

        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                array[i][j] = 0;
            }
        }
        return array;
    }
}
