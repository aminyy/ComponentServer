/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prms.io;

import java.util.List;
import net.casnw.home.io.DataReader;
import net.casnw.home.meta.DatatypeEnum;
import net.casnw.home.meta.ModuleMeta;
import net.casnw.home.meta.VariableMeta;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble2DArray;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolString;

/**
 *
 * @author Administrator
 */
@ModuleMeta(moduleClass = "prms.io.Parameter_Thru",
        name = "Parameter_Thru",
        author = "hzfwhy@lzb.ac.cn",
        keyword = "parameter",
        description = "Parameter_Thru")
public class Parameter_Thru extends AbsComponent {

    DataReader parameterIO_Thru1;
    DataReader parameterIO_Thru2;
    DataReader parameterIO_Thru3;
    @VariableMeta(name = "rain_adj",
            dataType = DatatypeEnum.PoolDouble2DArray,
            description = "Monthly factor to adjust measured precipitation on each HRU to account for differences in elevation, etc")

    public PoolDouble2DArray rain_adj = new PoolDouble2DArray();

    @VariableMeta(name = "snow_adj",
            dataType = DatatypeEnum.PoolDouble2DArray,
            description = "Monthly factor to adjust measured precipitation on each HRU to account for differences in elevation, etc")
    public PoolDouble2DArray snow_adj = new PoolDouble2DArray();

    @VariableMeta(name = "strain_adj",
            dataType = DatatypeEnum.PoolDouble2DArray,
            description = "Monthly factor to adjust measured precipitation to  each HRU to account for differences in elevation,  etc. This factor is for the rain gage used for kinematic or storm routing")
    public PoolDouble2DArray strain_adj = new PoolDouble2DArray();
    
    @VariableMeta(name = "rain_adjInputPath1",
            dataType = DatatypeEnum.PoolString,
            description = "rain_adjInputPath1")
    public PoolString rain_adjInputPath1 = new PoolString();
    
    @VariableMeta(name = "snow_adjInputPath2",
            dataType = DatatypeEnum.PoolString,
            description = "snow_adjInputPath2")
    public PoolString snow_adjInputPath2 = new PoolString();
    
    @VariableMeta(name = "strain_adjInputPath3",
            dataType = DatatypeEnum.PoolString,
            description = "strain_adjInputPath3")
    public PoolString strain_adjInputPath3 = new PoolString();
    
    @VariableMeta(name = "nhru",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of HRUs.")
    public PoolInteger nhru = new PoolInteger();//xml

    @Override
    public void init() throws Exception {

        parameterIO_Thru1 = new DataReader(rain_adjInputPath1.getValue());
        parameterIO_Thru2 = new DataReader(snow_adjInputPath2.getValue());
        parameterIO_Thru3 = new DataReader(strain_adjInputPath3.getValue());
        List<Double> inputParameter;

//        rain_adj.setRowsNum(this.nhru.getValue());
//        rain_adj.setColsNum(12);
        rain_adj.setValue(new double[this.nhru.getValue()][12]);
        for (int k = 0; k < this.nhru.getValue(); k++) {
            if (parameterIO_Thru1.hasNext() == true) {
                inputParameter = parameterIO_Thru1.getNext();
                for (int i = 0; i < 12; i++) {
                    rain_adj.setCellValue(k, i, inputParameter.get(i));
                }
            }
        }
//        snow_adj.setRowsNum(this.nhru.getValue());
//        snow_adj.setColsNum(12);
        snow_adj.setValue(new double[this.nhru.getValue()][12]);
        for (int k = 0; k < this.nhru.getValue(); k++) {
            if (parameterIO_Thru2.hasNext() == true) {
                inputParameter = parameterIO_Thru2.getNext();
                for (int i = 0; i < 12; i++) {
                    snow_adj.setCellValue(k, i, inputParameter.get(i));
                }
            }
        }

//        strain_adj.setRowsNum(this.nhru.getValue());
//        strain_adj.setColsNum(12);
        strain_adj.setValue(new double[this.nhru.getValue()][12]);
        for (int k = 0; k < this.nhru.getValue(); k++) {
            if (parameterIO_Thru3.hasNext() == true) {
                inputParameter = parameterIO_Thru3.getNext();
                for (int i = 0; i < 12; i++) {
                    strain_adj.setCellValue(k, i, inputParameter.get(i));
                }
            }
        }

    }

    @Override
    public void run() throws Exception {
    }

    @Override
    public void clear() throws Exception {
    }
}
