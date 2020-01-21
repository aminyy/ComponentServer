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
import net.casnw.home.poolData.PoolDoubleArray;
import net.casnw.home.poolData.PoolString;

/**
 *
 * @author Administrator
 */
@ModuleMeta(moduleClass = "prms.io.ParameterIO_time",
        name = "ParameterIO_time",
        author = "hzfwhy@lzb.ac.cn",
        keyword = "parameter",
        description = "ParameterIO_time")
public class ParameterIO_time extends AbsComponent {

    DataReader parameterIO_T;
    @VariableMeta(name = "adjmix_rain",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Adjustment factor for rain in a rain/snow mix Monthly factor to adjust rain proportion in a mixed  rain/snow event")
    public PoolDoubleArray adjmix_rain = new PoolDoubleArray() ;

    @VariableMeta(name = "cecn_coef",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Convection condensation energy coefficient, varied monthly")
    public PoolDoubleArray cecn_coef = new PoolDoubleArray();

    @VariableMeta(name = "dday_intcp",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Intercept in temperature / degree-day relationship. Intercept in relationship: dd-coef =  dday_intcp + dday_slope*(tmax)+1.")

    public PoolDoubleArray dday_intcp = new PoolDoubleArray();
    @VariableMeta(name = "dday_slope",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Slope in temperature / degree-day relationship. Coefficient in relationship: dd-coef =  dday_intcp + dday_slope*(tmax)+1.")
    public PoolDoubleArray dday_slope = new PoolDoubleArray();

    public PoolDoubleArray epan_coef = new PoolDoubleArray();

    public PoolDoubleArray jh_coef = new PoolDoubleArray();

    @VariableMeta(name = "ppt_rad_adj",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "If basin precipitation exceeds this value, solar radiation is mutiplied by the summer or winter precip adjustment factor, depending on the season. ")
    public PoolDoubleArray ppt_rad_adj = new PoolDoubleArray();

    @VariableMeta(name = "tmax_allrain",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "If maximum temperature of an HRU is greater than or equal to this value (for each month, January to December),  precipitation is assumed to be rain")
    public PoolDoubleArray tmax_allrain = new PoolDoubleArray();

    @VariableMeta(name = "tmax_index",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Index temperature, by month, used to determine precipitation adjustments to solar radiation, in deg F or C depending  on units of data")
    public PoolDoubleArray tmax_index = new PoolDoubleArray();

    @VariableMeta(name = "tmax_lapse",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "rray of twelve values representing the change in maximum temperature per 1000 elev_units of elevation change for each month, January to December")
    public PoolDoubleArray tmax_lapse = new PoolDoubleArray();

    @VariableMeta(name = "tmin_lapse",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Array of twelve values representing the change in minimum temperture per 1000 elev_units of  elevation change for each month, January to December")
    public PoolDoubleArray tmin_lapse= new PoolDoubleArray();

    @VariableMeta(name = "tstorm_mo",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Monthly indicator for prevalent storm type (0=frontal  storms prevalent; 1=convective storms prevalent)")
    public PoolDoubleArray tstorm_mo = new PoolDoubleArray();

    @VariableMeta(name = "TparameterInputPath",
            dataType = DatatypeEnum.PoolString,
            description = "Temperature data parameter InputPath")
    public PoolString TparameterInputPath = new PoolString();

    @Override
    public void init() throws Exception {

        parameterIO_T = new DataReader(TparameterInputPath.getValue());
        List<Double> inputParameter;
        for (int i = 0; i < 12; i++) {

            if (parameterIO_T.hasNext() == true) {

                inputParameter = parameterIO_T.getNext();
                tmax_lapse.setValue(i, inputParameter.get(0));
                tmin_lapse.setValue(i, inputParameter.get(1));
                adjmix_rain.setValue(i, inputParameter.get(2));
                tmax_allrain.setValue(i, inputParameter.get(3));
                dday_intcp.setValue(i, inputParameter.get(4));
                dday_slope.setValue(i, inputParameter.get(5));
                ppt_rad_adj.setValue(i, inputParameter.get(6));
                tmax_index.setValue(i, inputParameter.get(7));
                jh_coef.setValue(i, inputParameter.get(8));
                epan_coef.setValue(i, inputParameter.get(9));
                tstorm_mo.setValue(i, inputParameter.get(10));
                cecn_coef.setValue(i, inputParameter.get(11));

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
