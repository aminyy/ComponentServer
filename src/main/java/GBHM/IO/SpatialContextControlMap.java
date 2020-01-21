/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.IO;

import net.casnw.home.io.Raster;
import net.casnw.home.io.RasterReader;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble2DArray;
import net.casnw.home.poolData.PoolObject;
import net.casnw.home.poolData.PoolString;

/**
 *
 * @author longyinping
 */
public class SpatialContextControlMap extends AbsComponent {

    public PoolString baseRasterFile;
    public PoolDouble2DArray data;
    public PoolObject raster;
    private RasterReader reader;
    private double[][] baseArray;

    @Override
    public void init() {
        Raster r;
        reader = new RasterReader(baseRasterFile.getValue());
        r=reader.readRaster();
        baseArray = r.getData();
        data.setValue(baseArray);        
        raster.setValue(r);
    }
}
