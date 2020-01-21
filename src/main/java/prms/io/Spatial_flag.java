/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prms.io;

import net.casnw.home.io.Raster;
import net.casnw.home.io.RasterReader;
import net.casnw.home.meta.DatatypeEnum;
import net.casnw.home.meta.ModuleMeta;
import net.casnw.home.meta.VariableMeta;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.model.SpatialContext;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolString;

/**
 *
 * @author Administrator
 */
@ModuleMeta(moduleClass = "prms.io.Spatial_flag",
        name = "Spatial_flag",
        author = "hzfwhy@lzb.ac.cn",
        keyword = "Spatial_flag",
        description = "Spatial_flag")
public class Spatial_flag extends AbsComponent {

    public Raster hru_flagArray;
    @VariableMeta(name = "hru_flag",
            dataType = DatatypeEnum.PoolInteger,
            description = "hru_flag")
    public PoolInteger hru_flag = new PoolInteger();
    
    @VariableMeta(name = " hru_flagInputPath",
            dataType = DatatypeEnum.PoolString,
            description = " hru_flag file InputPath")
    public PoolString hru_flagInputPath =new PoolString();
    
    @VariableMeta(name = "cols",
            dataType = DatatypeEnum.PoolInteger,
            description = "cols")
    public PoolInteger cols = new PoolInteger();
    
    @VariableMeta(name = "rows",
            dataType = DatatypeEnum.PoolInteger,
            description = "rows")
    public PoolInteger rows = new PoolInteger();


    @Override
    public void init() throws Exception {

        hru_flagArray = new RasterReader(hru_flagInputPath.getValue()).readRaster();

    }

    @Override
    public void run() throws Exception {

     //   int cols = ((SpatialContext) this.getContext()).getCurrentColNum();
     //   int rows = ((SpatialContext) this.getContext()).getCurrentRowNum();
        hru_flag.setValue((int) hru_flagArray.getData()[rows.getValue()][cols.getValue()]);

    }

    @Override
    public void clear() throws Exception {

    }

}
