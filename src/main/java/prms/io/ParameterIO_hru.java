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
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolIntegerArray;
import net.casnw.home.poolData.PoolString;

/**
 * Description parameter input
 *
 */
/*
 * keywords:parameter
 */
@ModuleMeta(moduleClass = "prms.io.ParameterIO_hru",
        name = "ParameterIO_hru",
        author = "hzfwhy@lzb.ac.cn",
        keyword = "parameter",
        description = "parameter input")
public class ParameterIO_hru extends AbsComponent {

    DataReader parameterIO;
    @VariableMeta(name = "carea_max",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Maximum contributing area Maximum possible area contributing to surface runoff  expressed as a portion of the HRU area")
    public PoolDoubleArray carea_max= new PoolDoubleArray();

    @VariableMeta(name = "cov_type",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Vegetation cover type designation for HRU (0=bare soil; 1=grasses; 2=shrubs; 3=trees")
    public PoolIntegerArray cov_type = new PoolIntegerArray();

    @VariableMeta(name = "covden_sum",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Summer vegetation cover density for the major vegetation type on each HRU. [intcp]")
    public PoolDoubleArray covden_sum = new PoolDoubleArray();

    @VariableMeta(name = "covden_win",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Winter vegetation cover density for the major vegetation type on each HRU")
    public PoolDoubleArray covden_win = new PoolDoubleArray(); 

    @VariableMeta(name = "dprst_pct_open",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Decimal fraction of depression storage area that can flow to a stream channel. Amount of flow is a  function of storage.")

    public PoolDoubleArray dprst_pct_open = new PoolDoubleArray();

    @VariableMeta(name = "frozen",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Flag for frozen ground (0=no; 1=yes).")
    public PoolIntegerArray frozen = new PoolIntegerArray();

    @VariableMeta(name = " groundmelt",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Amount of snowpack-water that melts each day to soils")
    public PoolDoubleArray groundmelt = new PoolDoubleArray();

    @VariableMeta(name = "hru_area",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Amount of snowpack-water that melts each day to soils")
    public PoolDoubleArray hru_area = new PoolDoubleArray();

    @VariableMeta(name = "hru_deplcrv",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "  Index number for the snowpack areal depletion curve associated with an HRU")
    public PoolIntegerArray hru_deplcrv = new PoolIntegerArray();

    @VariableMeta(name = "hru_elev",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "  Mean elevation for each HRU  ")
    public PoolDoubleArray hru_elev = new PoolDoubleArray();

    @VariableMeta(name = "hru_deplcrv",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Index of groundwater reservoir assigned to HRU Index of groundwater reservoir receiving excess soil  water from each HRU")

    public PoolIntegerArray hru_gwres = new PoolIntegerArray();

    @VariableMeta(name = "hru_percent_dprst",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "HRU depression storage area as a decimal percent of the total HRU area")
    public PoolDoubleArray hru_percent_dprst = new PoolDoubleArray();

    @VariableMeta(name = "hru_percent_imperv",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Proportion of each HRU area that is impervious")
    public PoolDoubleArray hru_percent_imperv = new PoolDoubleArray();

    @VariableMeta(name = "hru_psta",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Index of the base precipitation station used for lapse rate calculations for each HRU")
    public PoolIntegerArray hru_psta = new PoolIntegerArray();

    @VariableMeta(name = "hru_radpl",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Index of the radiation plane used to compute solar radiation for a given HRU")
    public PoolIntegerArray hru_radpl = new PoolIntegerArray();

    @VariableMeta(name = "hru_solsta",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Index of solar radiation station associated with each HRU")
    public PoolIntegerArray hru_solsta = new PoolIntegerArray();

    @VariableMeta(name = "hru_ssres",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Index of subsurface reservoir receiving excess water from HRU soil zone")
    public PoolIntegerArray hru_ssres = new PoolIntegerArray();

    @VariableMeta(name = "hru_tsta",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Index of the base temperature station used for lapse  rate calculations")
    public PoolIntegerArray hru_tsta = new PoolIntegerArray();

    @VariableMeta(name = "hru_type",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = " HRU maximum impervious area retention storage Maximum impervious area retention storage for each HRU")

    public PoolIntegerArray hru_type = new PoolIntegerArray();

    @VariableMeta(name = "imperv_stor_max",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = " HRU maximum impervious area retention storage Maximum impervious area retention storage for each HRU")

    public PoolDoubleArray imperv_stor_max = new PoolDoubleArray();

    @VariableMeta(name = "jh_coef_hru",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = " Jensen-Haise Air temperature coefficient used in Jensen-Haise potential  evapotranspiration \" +\n"
            + "     \"computations for each HRU.  See PRMS  manual for calculation method")
    public PoolDoubleArray jh_coef_hru = new PoolDoubleArray();

    @VariableMeta(name = "pkwater_equiv",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = " Snowpack water equivalent on an HRU. [snow]")
    public PoolDoubleArray pkwater_equiv = new PoolDoubleArray();

    @VariableMeta(name = "rad_trncf",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "inputParameter rad_trncf")
    public PoolDoubleArray rad_trncf = new PoolDoubleArray();

    @VariableMeta(name = "smidx_coef",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Coefficient in contributing area computations Coefficient in non-linear contributing area algorithm.  Equation used is: contributing area = smidx_coef *  10.**(smidx_exp*smidx) where smidx is soil_moist +  .5 * ppt_net\")"
            + "Unit(\"decimal fraction)")
    public PoolDoubleArray smidx_coef = new PoolDoubleArray();

    @VariableMeta(name = "smidx_exp",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Exponent in contributing area computations Exponent in non-linear contributing area algorithm.  Equation used is: contributing area = smidx_coef *  10.**(smidx_exp*smidx) where smidx is soil_moist +  .5 * ppt_net")

    public PoolDoubleArray smidx_exp = new PoolDoubleArray();
    @VariableMeta(name = "snarea_thresh",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "If maximum temperature of an HRU is less than or equal to this value, precipitation is assumed to be snow")

    public PoolDoubleArray snarea_thresh = new PoolDoubleArray();
    @VariableMeta(name = "snow_intcp",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Snow interception storage capacity for the major vegetation type in each HRU")
    public PoolDoubleArray snow_intcp = new PoolDoubleArray();

    @VariableMeta(name = "snowinfil_max",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Maximum snow infiltration per day Maximum snow infiltration per day")
    public PoolDoubleArray snowinfil_max = new PoolDoubleArray();

    @VariableMeta(name = "soil2gw_max",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Maximum snow infiltration per day Maximum snow infiltration per day")
    public PoolDoubleArray soil2gw_max = new PoolDoubleArray();

    @VariableMeta(name = "soil_moist_init",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Initial value of available water in soil profile")
    public PoolDoubleArray soil_moist_init = new PoolDoubleArray();

    @VariableMeta(name = "soil_moist_max",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Maximum available water holding capacity of soil profile.  Soil profile is surface to bottom of rooting zone")
    public PoolDoubleArray soil_moist_max = new PoolDoubleArray();

    @VariableMeta(name = "soil_moist_srunoff",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "soil_moist_srunoff")
    public PoolDoubleArray soil_moist_srunoff = new PoolDoubleArray();

    @VariableMeta(name = "soil_rechr_init",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Initial value for soil recharge zone (upper part of  soil_moist).  Must be less than or equal to soil_moist_init")
    public PoolDoubleArray soil_rechr_init = new PoolDoubleArray();

    @VariableMeta(name = "soil_rechr_max",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Maximum value for soil recharge zone (upper portion  of soil_moist where losses occur as both evaporation  and transpiration")
    public PoolDoubleArray soil_rechr_max = new PoolDoubleArray();
    @VariableMeta(name = "soil_type",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "HRU soil type (1=sand; 2=loam; 3=clay)")
    public PoolIntegerArray soil_type = new PoolIntegerArray();

    @VariableMeta(name = "srain_intcp",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Winter rain interception storage capacity for the major vegetation type in the HRU")
    public PoolDoubleArray srain_intcp = new PoolDoubleArray();

    @VariableMeta(name = "tmax_adj",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Adjustment to maximum temperature for each HRU, estimated  based on slope and aspect")
    public PoolDoubleArray tmax_adj = new PoolDoubleArray();

    @VariableMeta(name = "tmin_adj",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Adjustment to minimum temperature for each HRU, estimated  based on slope and aspect")
    public PoolDoubleArray tmin_adj = new PoolDoubleArray();

    @VariableMeta(name = "transp_beg",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Month to begin summing tmaxf for each HRU; when sum is  >= to transp_tmax, transpiration begins")
    public PoolIntegerArray transp_beg = new PoolIntegerArray();

    @VariableMeta(name = "transp_end",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Month to stop transpiration computations;  transpiration is computed thru end of previous month")
    public PoolIntegerArray transp_end = new PoolIntegerArray();

    @VariableMeta(name = "transp_tmax",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Temperature index to determine the specific date of the  start of the transpiration)")
    public PoolDoubleArray transp_tmax = new PoolDoubleArray();

    @VariableMeta(name = "wrain_intcp",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Winter rain interception storage capacity for the major vegetation type in the HRU")
    public PoolDoubleArray wrain_intcp = new PoolDoubleArray();

    @VariableMeta(name = "hruparameterInputPath",
            dataType = DatatypeEnum.PoolString,
            description = "hruparameterInputPath")
    public PoolString hruparameterInputPath = new PoolString();

    @VariableMeta(name = "nhru",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of HRUs.")
    public PoolInteger nhru = new PoolInteger();//xml

    @Override
    public void init() throws Exception {
        parameterIO = new DataReader(hruparameterInputPath.getValue());
        List<Double> inputParameter;
        for (int i = 0; i < this.nhru.getValue(); i++) {

            if (parameterIO.hasNext() == true) {

                inputParameter = parameterIO.getNext();

                hru_percent_dprst.setValue(i, inputParameter.get(0));
                hru_area.setValue(i, inputParameter.get(1));
                hru_type.setValue(i, inputParameter.get(2).intValue());
                dprst_pct_open.setValue(i, inputParameter.get(3));
                hru_ssres.setValue(i, inputParameter.get(4).intValue());
                hru_gwres.setValue(i, inputParameter.get(5).intValue());
                hru_elev.setValue(i, inputParameter.get(6));
                tmax_adj.setValue(i, inputParameter.get(7));
                tmin_adj.setValue(i, inputParameter.get(8));
                hru_psta.setValue(i, inputParameter.get(9).intValue());
                hru_radpl.setValue(i, inputParameter.get(10).intValue());
                hru_solsta.setValue(i, inputParameter.get(11).intValue());
                jh_coef_hru.setValue(i, inputParameter.get(12));
                transp_beg.setValue(i, inputParameter.get(13).intValue());
                transp_end.setValue(i, inputParameter.get(14).intValue());
                transp_tmax.setValue(i, inputParameter.get(15));
                pkwater_equiv.setValue(i, inputParameter.get(16));
                snow_intcp.setValue(i, inputParameter.get(17));
                srain_intcp.setValue(i, inputParameter.get(18));
                wrain_intcp.setValue(i, inputParameter.get(19));
                covden_sum.setValue(i, inputParameter.get(20));
                covden_win.setValue(i, inputParameter.get(21));
                snarea_thresh.setValue(i, inputParameter.get(22));
                groundmelt.setValue(i, inputParameter.get(23));
                imperv_stor_max.setValue(i, inputParameter.get(24));
                smidx_coef.setValue(i, inputParameter.get(25));
                smidx_exp.setValue(i, inputParameter.get(26));
                soil_moist_max.setValue(i, inputParameter.get(27));
                soil_moist_init.setValue(i, inputParameter.get(28));
                carea_max.setValue(i, inputParameter.get(29));
                snowinfil_max.setValue(i, inputParameter.get(30));
                cov_type.setValue(i, inputParameter.get(31).intValue());
                hru_deplcrv.setValue(i, inputParameter.get(32).intValue());
                hru_percent_imperv.setValue(i, inputParameter.get(33).doubleValue());
                hru_tsta.setValue(i, inputParameter.get(34).intValue());
                rad_trncf.setValue(i, inputParameter.get(35).doubleValue());
                soil2gw_max.setValue(i, inputParameter.get(36).doubleValue());
                soil_rechr_init.setValue(i, inputParameter.get(37).doubleValue());
                soil_rechr_max.setValue(i, inputParameter.get(38).doubleValue());
                soil_type.setValue(i, inputParameter.get(39).intValue());
                soil_moist_srunoff.setValue(i, inputParameter.get(40).doubleValue());
                frozen.setValue(i, inputParameter.get(41).intValue());

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
