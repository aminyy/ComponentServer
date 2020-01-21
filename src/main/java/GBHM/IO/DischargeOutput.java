/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.IO;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import net.casnw.home.io.DataWriter;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDate;
import net.casnw.home.poolData.PoolDouble2DArray;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolIntegerArray;
import net.casnw.home.poolData.PoolString;

/**
 *
 * @author longyinping
 */
public class DischargeOutput extends AbsComponent {

    //In
    public PoolDate currentDate;
    public PoolString dischargeOutputPath;
    public PoolDouble2DArray discharge;
    public PoolInteger nsub;
    public PoolIntegerArray nflow;
    //End of In
    private DataWriter w;
    private SimpleDateFormat format;

    @Override
    public void init() throws FileNotFoundException {
        w = new DataWriter(dischargeOutputPath.getValue());
        format = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public void run() {
        int isub, iflow;
        isub = nsub.getValue() - 1;
        iflow = nflow.getValue(isub) - 1;
        w.writeLine(format.format(currentDate.getValue()) + "  "
                + String.format("%1$8.3f", discharge.getCellValue(isub, iflow)));
    }

    @Override
    public void clear() {
        w.close();
    }
}
