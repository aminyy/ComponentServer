/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package XinAnJiang.model;

import java.util.List;

import net.casnw.home.io.DataReader;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolString;

/**
 *
 * @author longyinping
 */
public class Climate extends AbsComponent {

    public PoolString weatherFile;
    public PoolDouble pP;
    public PoolDouble pEm;
    //  public PoolInteger count;
    public PoolInteger num;
    DataReader climate;

    @Override
    public void init() throws Exception {
        climate = new DataReader(weatherFile.getValue());
        num.setValue(-1);
    }

    /**
     *
     * @throws Exception
     */
    @Override
    public void run() throws Exception {
        List<Double> Vars;
        if (climate.hasNext()) {
            Vars = climate.getNext();
            pP.setValue(Vars.get(0));
            pEm.setValue(Vars.get(1));
            num.setValue(num.getValue() + 1);
            System.out.println("num=" + Integer.toString(num.getValue()));
        }
    }
}
