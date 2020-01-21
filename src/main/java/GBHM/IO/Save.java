/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.IO;

import net.casnw.home.model.AbsComponent;
import net.casnw.home.model.SpatialContext;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDouble2DArray;
import net.casnw.home.poolData.PoolDouble3DArray;
import net.casnw.home.poolData.PoolDoubleArray;

/**
 *
 * @author longyinping
 */
public class Save extends AbsComponent {

    //OUT
    public PoolDouble3DArray w_array;
    public PoolDouble2DArray dgl_array;
    public PoolDouble2DArray GWst_array;
    public PoolDouble2DArray Cst_array;
    public PoolDouble2DArray snowdepth_array;
    public PoolDouble2DArray sst_array;
    public PoolDouble2DArray Eact_array;//实际蒸散发//nrows,ncols
    public PoolDouble2DArray qin_array;//坡面产流//nrows,ncols
    public PoolDouble2DArray snowmelt_array;
    public PoolDouble2DArray soilWaterDepth;//mm
    //end of OUT
    //IN
    public PoolDoubleArray w;
    public PoolDouble dgl;
    public PoolDouble GWst;
    public PoolDouble Cst;
    public PoolDouble snowdepth;
    public PoolDouble sst;
    public PoolDouble Eact;
    public PoolDouble snowmelt;
    public PoolDouble q_hillslope;
    public PoolDouble qsub;
    public PoolDoubleArray D;
    //end of IN
    private SpatialContext context;

    @Override
    public void init() {
        context = (SpatialContext) this.getContext();
        int nrows = context.getRowsNum();
        int ncols = context.getColsNum();
        Eact_array.setValue(new double[nrows][ncols]);
        qin_array.setValue(new double[nrows][ncols]);
        snowmelt_array.setValue(new double[nrows][ncols]);
        soilWaterDepth.setValue(new double[nrows][ncols]);
    }

    @Override
    public void run() {
        int row, col;
        row = context.getCurrentRowNum();
        col = context.getCurrentColNum();
        w_array.setValue(row, col, w.getValue());
        dgl_array.setCellValue(row, col, dgl.getValue());
        GWst_array.setCellValue(row, col, GWst.getValue());
        Cst_array.setCellValue(row, col, Cst.getValue());
        snowdepth_array.setCellValue(row, col, snowdepth.getValue());
        sst_array.setCellValue(row, col, sst.getValue());
        Eact_array.setCellValue(row, col, Eact.getValue());
        qin_array.setCellValue(row, col, q_hillslope.getValue() + qsub.getValue());
        snowmelt_array.setCellValue(row, col, snowmelt.getValue());

        double waterdepth = 0;
        for (int i = 0; i < D.getValue().length; i++) {
            waterdepth = waterdepth + w.getValue(i) * D.getValue(i) * 1000;
        }
        soilWaterDepth.setCellValue(row, col, waterdepth);
    }
}
