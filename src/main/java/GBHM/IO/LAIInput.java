/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.IO;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.casnw.home.io.RasterReader;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDate;
import net.casnw.home.poolData.PoolDouble2DArray;
import net.casnw.home.poolData.PoolString;

/**
 *
 * @author longyinping
 */
public class LAIInput extends AbsComponent {

    //In
    public PoolDate currentDate;
    public PoolString LAIPath;
    //@Out
    public PoolDouble2DArray LAI;

    @Override
    public void init() {
    }

    @Override
    public void run() throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyyDDD");
        /*       double[][] NDVI;
         Date date = new Date();
         date.setTime(Date.getTime());
         if (date.getDate() <= 10) {
         date.setDate(1);
         } else if (date.getDate() <= 20) {
         date.setDate(11);
         } else {
         date.setDate(21);
         }
        
         ASCIIReader ndvi = new ASCIIReader("data/ndvi/" + format.format(date) + ".asc");
         LAI = ndvi.getArray();*/

        Date date = new Date();
        date.setTime(currentDate.getValue().getTime());
        date.setMonth(0);
        SimpleDateFormat format1 = new SimpleDateFormat("DDD");
        int day = Integer.parseInt(format1.format(date));
        if (day % 8 == 0) {
            day = (day / 8 - 1) * 8 + 1;
        } else {
            day = (day / 8) * 8 + 1;
        }
        date.setDate(day);
        RasterReader lai = new RasterReader(LAIPath.getValue() + "lai" + format.format(date) + ".asc");
        LAI.setValue(lai.readRaster().getData());
    }

    @Override
    public void clear() {
    }
}
