/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prms.io;

import net.casnw.home.io.DataWriter;
import net.casnw.home.meta.DatatypeEnum;
import net.casnw.home.meta.ModuleMeta;
import net.casnw.home.meta.VariableMeta;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDoubleArray;
import net.casnw.home.poolData.PoolString;

/**
 *
 * @author Administrator
 */
@ModuleMeta(moduleClass = "prms.io.WriteOut",
        name = "WriteOut",
        author = "hzfwhy@lzb.ac.cn",
        keyword = "WriteOut",
        description = "WriteOut")
public class WriteOut extends AbsComponent {

    @VariableMeta(name = "potet",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Potential evapotranspiration for each HRU. [potet]")
    public PoolDoubleArray potet;
    @VariableMeta(name = "tavgf",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Average HRU temperature. [temp]")

    public PoolDoubleArray tavgf;
    @VariableMeta(name = "swrad",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "The computed solar radiation for each HRU [solrad]")
    public PoolDoubleArray swrad;

    DataWriter DataWrite;
    public int i;
    @VariableMeta(name = "outputPath",
            dataType = DatatypeEnum.PoolString,
            description = "output file Path")
    public PoolString outputPath;
    @VariableMeta(name = "basin_stflow",
            dataType = DatatypeEnum.PoolDouble,
            description = "Sum of basin_sroff, basin_ssflow and basin_gwflow for  timestep")

    public PoolDouble basin_stflow;
    @VariableMeta(name = " basin_cms",
            dataType = DatatypeEnum.PoolDouble,
            description = "Sum of basin_sroff, basin_ssflow and basin_gwflow for  timestep")
    public PoolDouble basin_cms;
    @VariableMeta(name = "basin_sroff_cfs",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin surface runoff for timestep")
    public PoolDouble basin_sroff_cfs;
    @VariableMeta(name = "basin_ssflow_cfs",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin subsurface flow for timestep")
    public PoolDouble basin_ssflow_cfs;
    @VariableMeta(name = "basin_gwflow_cfs",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin ground-water flow for timestep")
    public PoolDouble basin_gwflow_cfs;
    @VariableMeta(name = "basin_cfs",
            dataType = DatatypeEnum.PoolDouble,
            description = "Streamflow from basin")
    public PoolDouble basin_cfs;
    private double potet_pri;
    private double tavgf_pri;
    private double swrad_pri;

    @Override
    public void init() throws Exception {

        DataWrite = new DataWriter(outputPath.getValue());
        DataWrite.writeLine("tavgf" + "      " + "potet" + "      " + "basin_cfs" + "      " + " swrad " + "      " + "\n");

        i = -1;

    }

    @Override
    public void run() throws Exception {

    	for (i = 0; i < 23; i++) {
        potet_pri = this.potet.getValue(i);
        tavgf_pri = this.tavgf.getValue(i);
        swrad_pri = this.swrad.getValue(i);
        DataWrite.writeLine(Double.toString(tavgf_pri) + "    " + Double.toString(potet_pri) + "    " + Double.toString(basin_cfs.getValue()) + "    " + Double.toString(swrad_pri) + "    " + "\n");

        
    	}


    }

    @Override
    public void clear() throws Exception {

    }

}
