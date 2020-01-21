/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.IO;

import net.casnw.home.model.AbsComponent;
import net.casnw.home.model.SpatialContext;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDouble2DArray;

/**
 *
 * @author longyinping
 */
public class SpatialDownscaling extends AbsComponent {

    //IN
    public PoolDouble2DArray prec_array;
    public PoolDouble2DArray Ep_array;
    public PoolDouble2DArray temper_array;
    public PoolDouble2DArray Dr_grid;
    public PoolDouble2DArray Drw_grid;
    public PoolDouble2DArray LAI_array;
    //end of IN
    //Out
    public PoolDouble prec;
    public PoolDouble Ep;
    public PoolDouble temper;
    public PoolDouble Dr;
    public PoolDouble Drw;
    public PoolDouble LAI;
    private SpatialContext context;
    //end of Out

    @Override
    public void init() {
        context = (SpatialContext) this.getContext();
    }
    
    @Override
    public void run() {
        int row, col;
        row = context.getCurrentRowNum();
        col = context.getCurrentColNum();
        
        prec.setValue(prec_array.getCellValue(row, col));
        Ep.setValue(Ep_array.getCellValue(row, col));
        temper.setValue(temper_array.getCellValue(row, col));
        Dr.setValue(Dr_grid.getCellValue(row, col));
        Drw.setValue(Drw_grid.getCellValue(row, col));
        LAI.setValue(LAI_array.getCellValue(row, col));    
    }
}
