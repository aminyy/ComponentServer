/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.IO;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.casnw.home.io.Raster;
import net.casnw.home.io.RasterWriter;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDate;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDouble2DArray;
import net.casnw.home.poolData.PoolDouble3DArray;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolString;

/**
 *
 * @author longyinping
 */
public class DailyOutput extends AbsComponent {

    //In   
    public PoolDate currentDate;
    public PoolDouble2DArray baseArray;
    public PoolString precOutPath;
    public PoolString eactOutPath;
    public PoolString runoffOutPath;
    public PoolString snowmeltOutPath;
    public PoolString MoistureOutPath;
    public PoolString dglOutPath;
    public PoolString GWstOutPath;
    public PoolString CstOutPath;
    public PoolString snowOutPath;
    public PoolString sstOutPath;
    public PoolString soilWaterDepthOutPath;
    public PoolDouble2DArray prec;
    public PoolDouble2DArray Eact;//实现蒸散发//nrows,ncols
    public PoolDouble2DArray qin;//坡面产流//nrows,ncols
    public PoolDouble2DArray snowmelt;
    public PoolDouble3DArray w;
    public PoolDouble2DArray dgl;
    public PoolDouble2DArray GWst;//nrows,ncols
    public PoolDouble2DArray Cst;//nrows,ncols
    public PoolDouble2DArray snowdepth;//nrows,ncols
    public PoolDouble2DArray sst;//nrows,ncols
    public PoolDouble2DArray soilWaterDepth;
    public PoolInteger ncols;
    public PoolInteger nrows;
    public PoolDouble xllcorner;
    public PoolDouble yllcorner;
    public PoolDouble cellsize;
    public PoolDouble nodata;
    //end of In
    private Raster raster;
    private SimpleDateFormat format;
    private Date Date;

    @Override
    public void init() {
        raster = new Raster();
        raster.setRows(nrows.getValue());
        raster.setCols(ncols.getValue());
        raster.setXll(xllcorner.getValue());
        raster.setYll(yllcorner.getValue());
        raster.setCellsize(cellsize.getValue());
        raster.setNDATA(Double.toString(nodata.getValue()));
        format = new SimpleDateFormat("yyyyMMdd");

    }

    @Override
    public void run() throws IOException {
        Date = currentDate.getValue();
        asciiOutput(precOutPath.getValue(), prec.getValue());
        asciiOutput(eactOutPath.getValue(), Eact.getValue());
        asciiOutput(runoffOutPath.getValue(), qin.getValue());
        asciiOutput(snowmeltOutPath.getValue(), snowmelt.getValue());
        asciiOutput(dglOutPath.getValue(), dgl.getValue());
        asciiOutput(GWstOutPath.getValue(), GWst.getValue());
        asciiOutput(CstOutPath.getValue(), Cst.getValue());
        asciiOutput(snowOutPath.getValue(), snowdepth.getValue());
        asciiOutput(sstOutPath.getValue(), sst.getValue());
        asciiOutput(soilWaterDepthOutPath.getValue(), soilWaterDepth.getValue());
        moistureOutput(MoistureOutPath.getValue(), w.getValue());
    }

    public void asciiOutput(String path, double[][] array) throws IOException {
        if (path != null && !path.equals("")) {

            double[][] array1 = new double[array.length][array[0].length];

            DecimalFormat formatter = new DecimalFormat("######.000");

            for (int i = 0; i < array.length; i++) {
                for (int j = 0; j < array[0].length; j++) {
                    Double cellValue = new Double(baseArray.getCellValue(i, j));
                    if (cellValue.equals(new Double(Double.NaN))) {
                        array1[i][j] = nodata.getValue();
                    } else {
                        array1[i][j] = Double.valueOf(formatter.format(array[i][j]));
                    }
                }
            }
            RasterWriter writer = new RasterWriter(path + format.format(Date) + ".asc");
            raster.setData(array1);
            writer.writeRaster(raster);
        }
    }

    public void moistureOutput(String path, double[][][] moistureArray) throws IOException {
        if (path != null && !path.equals("")) {
            int nlayer = 10;
            double[][][] array;
            array = new double[nlayer][nrows.getValue()][ncols.getValue()];
            for (int i = 0; i < nrows.getValue(); i++) {
                for (int j = 0; j < ncols.getValue(); j++) {
                    for (int k = 0; k < nlayer; k++) {
                        Double cellValue = new Double(baseArray.getCellValue(i, j));
                        if (!cellValue.equals(new Double(Double.NaN))) {
                            if (k < moistureArray[i][j].length) {
                                array[k][i][j] = moistureArray[i][j][k];
                            } else {
                                array[k][i][j] = nodata.getValue();
                            }
                        } else {
                            array[k][i][j] = nodata.getValue();
                        }
                    }
                }
            }

            for (int i = 0; i < nlayer; i++) {
                RasterWriter writer = new RasterWriter(path + format.format(Date) + "_" + Integer.toString(i + 1) + ".asc");
                raster.setData(array[i]);
                writer.writeRaster(raster);
            }
        }
    }
}
