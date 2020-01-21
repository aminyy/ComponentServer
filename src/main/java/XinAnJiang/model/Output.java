/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package XinAnJiang.model;

import net.casnw.home.io.DataWriter;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolString;

/**
 *
 * @author longyinping
 */
public class Output extends AbsComponent {

    public PoolString evapFile;
    public PoolString moistureFile;
    public PoolString runoffFile;
    public PoolDouble E;
    public PoolDouble EU;
    public PoolDouble EL;
    public PoolDouble ED;
    public PoolDouble W;
    public PoolDouble WU;
    public PoolDouble WL;
    public PoolDouble WD;
    public PoolDouble R;
    public PoolDouble RS;
    public PoolDouble RG;
    DataWriter evapWriter;
    DataWriter moistureWriter;
    DataWriter runoffWriter;

    @Override
    public void init() throws Exception {
        evapWriter = new DataWriter(evapFile.getValue());
        moistureWriter = new DataWriter(moistureFile.getValue());
        runoffWriter = new DataWriter(runoffFile.getValue());
        evapWriter.writeLine("E, EU, EL, ED");
        moistureWriter.writeLine("W, WU, WL, WD");
        runoffWriter.writeLine("R, RS, RG");
    }

    @Override
    public void run() {
        evapWriter.writeLine(String.format("%1$16.3f  %2$16.3f  %3$16.3f  %4$16.3f",
                E.getValue(), EU.getValue(), EL.getValue(), ED.getValue()));
        moistureWriter.writeLine(String.format("%1$16.3f  %2$16.3f  %3$16.3f  %4$16.3f",
                W.getValue(), WU.getValue(), WL.getValue(), WD.getValue()));
        runoffWriter.writeLine(String.format("%1$16.3f  %2$16.3f  %3$16.3f",
                R.getValue(), RS.getValue(), RG.getValue()));

    }

    @Override
    public void clear() {
        evapWriter.close();
        moistureWriter.close();
        runoffWriter.close();
    }
}
