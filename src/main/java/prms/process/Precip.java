package prms.process;

import java.util.logging.*;
import net.casnw.home.meta.DatatypeEnum;
import net.casnw.home.meta.ModuleMeta;
import net.casnw.home.meta.VariableMeta;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDouble2DArray;
import net.casnw.home.poolData.PoolDoubleArray;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolIntegerArray;

//从此模块开始修改循环条件，之前的模块回头改造！！！！！！！！！！！！！

/*  Description
 ("Precipitation form and distribution." +
 "This component determines whether measured precipitation" +
 "is rain or snow and distributes it to the HRU's.")
 */

/* Keywords
 ("Precipitation")
 */
@ModuleMeta(moduleClass = "prms.process.Precip",
        name = "Precip",
        author = "hzfwhy@lzb.ac.cn",
        keyword = "Precipitation",
        description = "Precipitation form and distribution."
        + "This component determines whether measured precipitation"
        + "is rain or snow and distributes it to the HRU's.")
public class Precip extends AbsComponent {

    private static final Logger log = Logger.getLogger("HOME.model." + Precip.class.getSimpleName());
    // private fields
    private double[] tmax;
    private double[] tmin;
    private int[] istack;
    // Input Parameter
    /* Role(PARAMETER)
     Description("Number of temperature stations.")
     */
    @VariableMeta(name = "ntemp",
            dataType = DatatypeEnum.PoolInteger,
            description = "the number of temperature observation")
    public PoolInteger ntemp= new PoolInteger();

    /* Role(PARAMETER)
     Description("Number of precipitation stations.")
     */
    @VariableMeta(name = "nrain",
            dataType = DatatypeEnum.PoolInteger,
            description = "the number of rain observation")
    public PoolInteger nrain= new PoolInteger();

    /* Role(PARAMETER)
     Description("Number of HRUs.")
     */
    @VariableMeta(name = "nhru",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of HRUs.")
    public PoolInteger nhru= new PoolInteger();

    /* Role(PARAMETER)
     Description("Number of storms.")
     */
    @VariableMeta(name = "nstorm",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of storms.")
    public PoolInteger nstorm= new PoolInteger();

    /* Role(PARAMETER)
     Description("Adjustment factor for rain in a rain/snow mix Monthly factor to adjust rain proportion in a mixed  rain/snow event")
     Unit("decimal fraction")
     Bound ("nmonths")
     */
    @VariableMeta(name = "adjmix_rain",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Adjustment factor for rain in a rain/snow mix Monthly factor to adjust rain proportion in a mixed  rain/snow event")
    public PoolDoubleArray adjmix_rain= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Units for measured precipitation Units for measured precipitation (0=inches; 1=mm)")
     */
    @VariableMeta(name = "precip_units",
            dataType = DatatypeEnum.PoolInteger,
            description = "Units for measured precipitation Units for measured precipitation (0=inches; 1=mm)")
    public PoolInteger precip_units= new PoolInteger();

    /* Role(PARAMETER)
     Description("Area of each HRU")
     Unit("acres")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_area",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Area of each HRU")
    public PoolDoubleArray hru_area= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Monthly factor to adjust measured precipitation on each HRU to account for differences in elevation, etc")
     Unit("decimal fraction")
     Bound ("nmonths,nhru")
     */
    @VariableMeta(name = "rain_adj",
            dataType = DatatypeEnum.PoolDouble2DArray,
            description = "Monthly factor to adjust measured precipitation on each HRU to account for differences in elevation, etc")
    public PoolDouble2DArray rain_adj= new PoolDouble2DArray();

    /* Role(PARAMETER)
     Description("Monthly factor to adjust measured precipitation on each HRU to account for differences in elevation, etc")
     Unit("decimal fraction")
     Bound ("nmonths,nhru")
     */
    @VariableMeta(name = "snow_adj",
            dataType = DatatypeEnum.PoolDouble2DArray,
            description = "Monthly factor to adjust measured precipitation on each HRU to account for differences in elevation, etc")
    public PoolDouble2DArray snow_adj= new PoolDouble2DArray();

    /* Role(PARAMETER)
     Description("Monthly factor to adjust measured precipitation to  each HRU to account for differences in elevation,  etc. This factor is for the rain gage used for kinematic or storm routing")
     Unit("decimal fraction")
     Bound ("nmonths,nhru")
     */
    @VariableMeta(name = "strain_adj",
            dataType = DatatypeEnum.PoolDouble2DArray,
            description = "Monthly factor to adjust measured precipitation to  each HRU to account for differences in elevation,  etc. This factor is for the rain gage used for kinematic or storm routing")
    public PoolDouble2DArray strain_adj= new PoolDouble2DArray();

    /* Role(PARAMETER)
     Description("Units for measured temperature (0=Fahrenheit; 1=Celsius)")
     */
    @VariableMeta(name = "temp_units",
            dataType = DatatypeEnum.PoolInteger,
            description = "Units for measured temperature (0=Fahrenheit; 1=Celsius)")
    public PoolInteger temp_units= new PoolInteger();

    /* Role(PARAMETER)
     Description("Index of the base precipitation station used for lapse rate calculations for each HRU.")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_psta",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Index of the base precipitation station used for lapse rate calculations for each HRU")
    public PoolIntegerArray hru_psta= new PoolIntegerArray();

    /* Role(PARAMETER)
     Description("If maximum temperature of an HRU is greater than or equal to this value (for each month, January to December),  precipitation is assumed to be rain,  in deg C or F, depending on units of data")
     Unit("degrees")
     Bound ("nmonths")
     */
    @VariableMeta(name = "tmax_allrain",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "If maximum temperature of an HRU is greater than or equal to this value (for each month, January to December),  precipitation is assumed to be rain")
    public PoolDoubleArray tmax_allrain= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("If HRU maximum temperature is less than or equal to this  value, precipitation is assumed to be snow,  in deg C or F, depending on units of data")
     Unit("degrees")
     */
    @VariableMeta(name = "tmax_allsnow",
            dataType = DatatypeEnum.PoolDouble,
            description = "If HRU maximum temperature is less than or equal to this  value, precipitation is assumed to be snow,  in deg C or F, depending on units of data")
    public PoolDouble tmax_allsnow= new PoolDouble();

    /* Role(PARAMETER)
     Description("Adjustment factor for each storm")
     Unit("percent")
     Bound ("nstorm")
     */
    @VariableMeta(name = "storm_scale_factor",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Adjustment factor for each storm")
    public PoolDoubleArray storm_scale_factor= new PoolDoubleArray();
    // Input vars

    /* Description("Inverse of total basin area as sum of HRU areas")
     Unit("1/acres")
     */
    @VariableMeta(name = "basin_area_inv",
            dataType = DatatypeEnum.PoolDouble,
            description = "Inverse of total basin area as sum of HRU areas")
    public PoolDouble basin_area_inv= new PoolDouble();

    /* Description("Observed precipitation at each measurement station. [obs]")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "precip",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "(precip)")
    public PoolDoubleArray precip= new PoolDoubleArray();

    /* Description("HRU adjusted temperature for timestep < 24")
     Unit("deg C")
     Bound ("nhru")
     */
    @VariableMeta(name = "tempc",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "HRU adjusted temperature for timestep < 24")
    public PoolDoubleArray tempc= new PoolDoubleArray();

    /* Description("HRU adjusted temperature for timestep < 24")
     Unit("deg F")
     Bound ("nhru")
     */
    @VariableMeta(name = "tempf",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "HRU adjusted temperature for timestep < 24")
    public PoolDoubleArray tempf= new PoolDoubleArray();

    /*Description("Kinematic routing switch (0=daily; 1=storm period)")*/
    @VariableMeta(name = "route_on",
            dataType = DatatypeEnum.PoolInteger,
            description = "Kinematic routing switch (0=daily; 1=storm period)")
    public PoolInteger route_on= new PoolInteger();

    /* Description("Maximum HRU temperature. [temp]")
     @Unit("deg C")
     @Bound ("nhru")
     */
    @VariableMeta(name = "tmaxc",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Maximum HRU temperature. [temp]")
    public PoolDoubleArray tmaxc= new PoolDoubleArray();

    /* Description("Maximum HRU temperature. [temp]")
     Unit("deg F")
     Bound ("nhru")
     */
    @VariableMeta(name = "tmaxf",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Maximum HRU temperature. [temp]")
    public PoolDoubleArray tmaxf= new PoolDoubleArray();

    /* Description("Minimum HRU temperature. [temp]")
     Unit("deg C")
     Bound ("nhru")
     */
    @VariableMeta(name = "tminc",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Minimum HRU temperature. [temp]")
    public PoolDoubleArray tminc= new PoolDoubleArray();

    /* Description("Minimum HRU temperature. [temp]")
     Unit("deg F")
     Bound ("nhru")
     */
    @VariableMeta(name = "tminf",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Minimum HRU temperature. [temp]")
    public PoolDoubleArray tminf= new PoolDoubleArray();

    /*Description("Basin daily maximum temperature for use with solrad radiation component")*/
    @VariableMeta(name = "solrad_tmax",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin daily maximum temperature adjusted to elevation of solar radiation station")
    public PoolDouble solrad_tmax= new PoolDouble();

    /*Description("Number of active HRUs")*/
    @VariableMeta(name = "active_hrus",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of active HRUs")
    public PoolInteger active_hrus= new PoolInteger();

    /* Description("Routing order for HRUs")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_route_order",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Routing order for HRUs")
    public PoolIntegerArray hru_route_order= new PoolIntegerArray();

    /* Description("Date of the current time step")
     Unit("yyyy mm dd hh mm ss")
     */
    /**
     * **********时间格式处理***************
     */
    /*  public Calendar date */
    @VariableMeta(name = "date_year",
            dataType = DatatypeEnum.PoolInteger,
            description = " Calendar date)")
    public PoolInteger date_year= new PoolInteger();

    @VariableMeta(name = "date_mo",
            dataType = DatatypeEnum.PoolInteger,
            description = "Calendar.MONTH)")
    public PoolInteger date_mo= new PoolInteger();
    @VariableMeta(name = "date_day",
            dataType = DatatypeEnum.PoolInteger,
            description = " Calendar.DAY_OF_MONTH)")
    public PoolInteger date_day= new PoolInteger();
    // Output Vars
    /* Description("Adjusted precipitation on each HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_ppt",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Precipitation on HRU, rain and snow. [precip]")
    public PoolDoubleArray hru_ppt= new PoolDoubleArray();

    /* Description("Computed rain on each HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_rain",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Rain on HRU. [precip]")
    public PoolDoubleArray hru_rain= new PoolDoubleArray();

    /* Description("Computed snow on each HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_snow",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Snow on HRU. [precip]")
    public PoolDoubleArray hru_snow= new PoolDoubleArray();

    /* Description("New snow on HRU (0=no; 1=yes)")
     Bound ("nhru")
     */
    @VariableMeta(name = "newsnow",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "New snow on HRU (0=no; 1=yes)")
    public PoolIntegerArray newsnow= new PoolIntegerArray();

    /* Description("Precipitation mixture (0=no; 1=yes)")
     Bound ("nhru")
     */
    @VariableMeta(name = "pptmix",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Precipitation is mixture of rain and snow (0=no; 1=yes)")
    public PoolIntegerArray pptmix= new PoolIntegerArray();

    /* Description("Proportion of rain in a mixed event")
     Unit("decimal fraction")
     Bound ("nhru")
     */
    @VariableMeta(name = "prmx",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Proportion of rain in a mixed even")
    public PoolDoubleArray prmx= new PoolDoubleArray();

    /* Description("Area weighted adjusted average rain for basin")
     Unit("inches")
     */
    @VariableMeta(name = "basin_rain",
            dataType = DatatypeEnum.PoolDouble,
            description = "Area weighted adjusted average rain for basin")
    public PoolDouble basin_rain= new PoolDouble();

    /* Description("Area weighted adjusted average snow for basin")
     Unit("inches")
     */
    @VariableMeta(name = "basin_snow",
            dataType = DatatypeEnum.PoolDouble,
            description = "Area weighted adjusted average snow for basin")
    public PoolDouble basin_snow= new PoolDouble();

    /* Description("Area weighted measured average precip for basin")
     Unit("inches")
     */
    @VariableMeta(name = "basin_obs_ppt",
            dataType = DatatypeEnum.PoolDouble,
            description = "Area-weighted measured average precipitation for basin. [precip]")
    public PoolDouble basin_obs_ppt= new PoolDouble();

    /* Description("Area weighted adjusted average precip for basin")
     Unit("inches")
     */
    @VariableMeta(name = "basin_ppt",
            dataType = DatatypeEnum.PoolDouble,
            description = "basin_ppt")
    public PoolDouble basin_ppt = new PoolDouble();
    // private define  
    //xml parameter IO
    private int nrain_pri;
    private int nstorm_pri;
    private double[] adjmix_rain_pri;
    private int precip_units_pri;
    private double[][] rain_adj_pri;
    private double[][] snow_adj_pri;
    private double[][] strain_adj_pri;
    private int[] hru_psta_pri;
    private double[] tmax_allrain_pri;
    private double tmax_allsnow_pri;
    private double[] storm_scale_factor_pri;
    //input 
    private int ntemp_pri;
    private int nhru_pri;
    private double[] hru_area_pri;
    private int temp_units_pri;
    private double basin_area_inv_pri;
    private double tempc_pri;
    private double tempf_pri;
    private int route_on_pri;//obs读模块中获取
    private double tmaxc_pri;
    private double tmaxf_pri;
    private double tminc_pri;
    private double tminf_pri;
    private double solrad_tmax_pri;
    private int active_hrus_pri;
    private int[] hru_route_order_pri;
    // time series IO;
    private double[] precip_pri;
    /**
     * ***********time**************
     */
    //out
    private double[] hru_ppt_pri;
    private double[] hru_rain_pri;
    private double[] hru_snow_pri;
    private int[] newsnow_pri;
    private int[] pptmix_pri;
    private double[] prmx_pri;
    private double basin_rain_pri;
    private double basin_snow_pri;
    private double basin_obs_ppt_pri;
    private double basin_ppt_pri;
    //loop
    private int j;
    private double sum_obs;
    private int iform;
    private int year;
    private int mo;
    private int day;
    private int ss = 0;

    @Override
    public void init() throws Exception {
        nhru_pri = this.nhru.getValue();
        hru_ppt_pri = new double[nhru_pri];
        hru_rain_pri = new double[nhru_pri];
        hru_snow_pri = new double[nhru_pri];
        newsnow_pri = new int[nhru_pri];
        pptmix_pri = new int[nhru_pri];
        prmx_pri = new double[nhru_pri];

        tmax = new double[nhru_pri];
        tmin = new double[nhru_pri];
        nrain_pri = this.nrain.getValue();
        istack = new int[nrain_pri];

        temp_units_pri = this.temp_units.getValue();
        route_on_pri = this.route_on.getValue();

        hru_route_order_pri = this.hru_route_order.getValue();
        hru_area_pri = this.hru_area.getValue();

        nstorm_pri = this.nstorm.getValue();
        adjmix_rain_pri = this.adjmix_rain.getValue();
        precip_units_pri = this.precip_units.getValue();
        rain_adj_pri = this.rain_adj.getValue();
        snow_adj_pri = this.snow_adj.getValue();
        strain_adj_pri = this.strain_adj.getValue();
        hru_psta_pri = this.hru_psta.getValue();
        tmax_allrain_pri = this.tmax_allrain.getValue();
        tmax_allsnow_pri = this.tmax_allsnow.getValue();
        storm_scale_factor_pri = this.storm_scale_factor.getValue();

        ntemp_pri = this.ntemp.getValue();
        basin_area_inv_pri = this.basin_area_inv.getValue();
        active_hrus_pri = this.active_hrus.getValue();

        for (int i = 0; i < nhru_pri; i++) {
            if (hru_psta_pri[i] < 1) {      //TODO maybe this should throw an exception instead
                hru_psta_pri[i] = 1;
            }
        }
        if (nstorm_pri > 0) {
            for (int i = 0; i < nstorm_pri; i++) {
                storm_scale_factor_pri[i] = (100.0 + storm_scale_factor_pri[i]) / 100.0;
            }
            for (int ii = 0; ii < active_hrus_pri; ii++) {
                int i = hru_route_order_pri[ii];
                for (int k = 0; k < 12; k++) {
                    strain_adj_pri[k][i] *= storm_scale_factor_pri[0];
                }
            }
        }

        j = -1;
    }

    @Override
    public void run() throws Exception {
        // dafine value
        ss++;

        for (int j = 0; j < active_hrus_pri; j++) {
        int i = hru_route_order_pri[j];

        tempc_pri = this.tempc.getValue(i);
        tempf_pri = this.tempf.getValue(i);
        tmaxc_pri = this.tmaxc.getValue(i);
        tmaxf_pri = this.tmaxf.getValue(i);
        tminc_pri = this.tminc.getValue(i);
        tminf_pri = this.tminf.getValue(i);
        solrad_tmax_pri = this.solrad_tmax.getValue();

        if (j == 0) {

            if (hru_ppt_pri == null) {
                init();
            }
            basin_ppt_pri = 0.;
            basin_rain_pri = 0.;
            basin_snow_pri = 0.;
            precip_pri = precip.getValue();

            if (solrad_tmax_pri < -50.00) {
                System.out.println("bad temperature data, using previous time "
                        + "step values" + solrad_tmax_pri + " " + mo);
                // load tmax and tmin with appropriate observed values
            }

            for (int ii = 0; ii < nrain_pri; ii++) {
                istack[ii] = 0;
            }

            sum_obs = 0.0;

            iform = 0;

            if (precip_units_pri == 1) {
                for (int ii = 0; ii < nrain_pri; ii++) {
                    precip_pri[ii] /= 25.4;  // inch -> mm
                }
            }
//        if (nform > 0) {
//            iform = form_data[0];
//        } else {
//            iform = 0;
//        }

            /*
             year = date.get(Calendar.YEAR);
             int mo = date.get(Calendar.MONTH);
             int day = date.get(Calendar.DAY_OF_MONTH);
             */
            /**
             * ******从读模块获取**********************
             */
            year = this.date_year.getValue();
            mo = this.date_mo.getValue();

            day = this.date_day.getValue();
         //   System.out.println("day===" + day);
//            if (ss == 266) {
//                ss =0;
//            }

        }

//        if (solrad_tmax_pri < -50.00) {
//            System.out.println("bad temperature data, using previous time "
//                    + "step values" + solrad_tmax_pri + " " + mo);
//            // load tmax and tmin with appropriate observed values
//        } else 
        if (temp_units_pri == 0) {
            if (route_on_pri == 1) {
                // rsr, warning, tempf needs to be set in temperature module
                //              for (int j = 0; j < nhru; j++) {

                if (j == i) {
                    tmax[j] = tempf_pri;
                    tmin[j] = tmax[j];
                }
                //               }
            } else {
                if (j == i) //                for (int j = 0; j < nhru; j++) {
                {
                    tmax[j] = tmaxf_pri;
                    tmin[j] = tminf_pri;
                }
//                }
            }
        } else if (route_on_pri == 1) {
            // rsr, warning, tempc needs to be set in temperature module
            //           for (int j = 0; j < nhru; j++) {
            if (j == i) {
                tmax[j] = tempc_pri;
                tmin[j] = tmax[j];
            }
            //           }
        } else {

            if (j == i) {
                //        for (int j = 0; j < nhru; j++) {
                tmax[j] = tmaxc_pri;
                tmin[j] = tminc_pri;
            }
            //        }
        }

        /**
         * *******************************end**********************************
         */
        /**
         * ***************************loop active hru**************************
         */
        if (j < this.active_hrus.getValue()) {
//        for (int ii = 0; ii < active_hrus; ii++) {
            //           i = hru_route_order[j];
            pptmix_pri[i] = 0;
            hru_ppt_pri[i] = 0.0;
            hru_rain_pri[i] = 0.0;
            hru_snow_pri[i] = 0.0;
            newsnow_pri[i] = 0;
            prmx_pri[i] = 0.0;
            int ip = hru_psta_pri[i] - 1;
            double ppt = precip_pri[ip];
            if (ppt < 0.0) {
                if (istack[ip] == 0) {
                    System.out.println("warning, bad precipitation value: " + ppt
                            + "; precip station: " + ip + "; time: " + year + " " + mo + 1 + " "
                            + day + " ; value set to 0.0");
                    istack[ip] = 1;
                }
                ppt = 0.0;
            }

            if (ppt >= 1.0e-06) {

                sum_obs += (ppt * hru_area_pri[i]);

//******if within storm period for kinematic routing, adjust precip
//******by storm adjustment factor
                if (route_on_pri == 1) {
                    double pcor = strain_adj_pri[i][mo];
                    hru_ppt_pri[i] = ppt * pcor;
                    hru_rain_pri[i] = hru_ppt_pri[i];
                    prmx_pri[i] = 1.0;
                } //******if observed temperature data are not available or if observed
                //******form data are available and rain is explicitly specified then
                //******precipitation is all rain.
                else if (solrad_tmax_pri < -50.0 || solrad_tmax_pri > 150.0 || iform == 2) {
                    if ((solrad_tmax_pri > -998 && solrad_tmax_pri < -50.0) || solrad_tmax_pri > 150.0) {
                        System.out.println("warning, bad solrad_tmax " + solrad_tmax + " " + year + " " + mo + 1 + " " + day);
                    }
                    double pcor = rain_adj_pri[i][mo];
                    hru_ppt_pri[i] = ppt * pcor;
                    hru_rain_pri[i] = hru_ppt_pri[i];
                    prmx_pri[i] = 1.0;
                } //******if form data are available and snow is explicitly specified or if
                //******maximum temperature is below or equal to the base temperature for
                //******snow then precipitation is all snow
                else if (iform == 1 || tmax[i] <= tmax_allsnow_pri) {
                    double pcor = snow_adj_pri[i][mo];
                    hru_ppt_pri[i] = ppt * pcor;
                    hru_snow_pri[i] = hru_ppt_pri[i];
                    newsnow_pri[i] = 1;
                } //******if minimum temperature is above base temperature for snow or
                //******maximum temperature is above all_rain temperature then
                //******precipitation is all rain
                else if (tmin[i] > tmax_allsnow_pri || tmax[i] >= tmax_allrain_pri[mo]) {

                    double pcor = rain_adj_pri[i][mo];
                    hru_ppt_pri[i] = ppt * pcor;
                    hru_rain_pri[i] = hru_ppt_pri[i];
                    prmx_pri[i] = 1.0;
                } //******otherwise precipitation is a mixture of rain and snow
                else {
                    prmx_pri[i] = (((tmax[i] - tmax_allsnow_pri) / (tmax[i] - tmin[i])) * adjmix_rain_pri[mo]);

//******unless mixture adjustment raises the proportion of rain to
//******greater than or equal to 1.0 in which case it all rain
                    if (prmx_pri[i] >= 1.0) {  //rsr changed > to ge 1/8/2006
                        double pcor = rain_adj_pri[i][mo];
                        hru_ppt_pri[i] = ppt * pcor;
                        hru_rain_pri[i] = hru_ppt_pri[i];
                        prmx_pri[i] = 1.0;
                    } //******if not, it is a rain/snow mixture
                    else {
                        double pcor = snow_adj_pri[i][mo];
                        pptmix_pri[i] = 1;
                        hru_ppt_pri[i] = ppt * pcor;
                        hru_rain_pri[i] = prmx_pri[i] * hru_ppt_pri[i];
                        hru_snow_pri[i] = hru_ppt_pri[i] - hru_rain_pri[i];
                        newsnow_pri[i] = 1;
                    }
                }
//            System.out.println("mo, day, hru, pcor, hruppt " + mo + " " + day + " " + i + " " + pcor + " " + hru_ppt[i]);
                basin_ppt_pri += hru_ppt_pri[i] * hru_area_pri[i];
                basin_rain_pri += hru_rain_pri[i] * hru_area_pri[i];
                basin_snow_pri += hru_snow_pri[i] * hru_area_pri[i];

            }

            this.hru_ppt.setValue(i, hru_ppt_pri[i]);
            this.hru_rain.setValue(i, hru_rain_pri[i]);
            this.hru_snow.setValue(i, hru_snow_pri[i]);
            this.newsnow.setValue(i, newsnow_pri[i]);
            this.pptmix.setValue(i, pptmix_pri[i]);
            this.prmx.setValue(i, prmx_pri[i]);

        }
        //       }  // end hru loop
        /**
         * *************************************** end hru
         * loop***************************
         */
        if (j + 1 == this.active_hrus.getValue()) {
            basin_obs_ppt_pri = sum_obs * basin_area_inv_pri;
            basin_ppt_pri *= basin_area_inv_pri;
            basin_rain_pri *= basin_area_inv_pri;
            basin_snow_pri *= basin_area_inv_pri;

            if (log.isLoggable(Level.INFO)) {
             //   log.info("Precip " + basin_rain_pri + " " + basin_ppt_pri + " " + basin_snow_pri);
            }

            this.basin_ppt.setValue(basin_ppt_pri);
            this.basin_snow.setValue(basin_snow_pri);
            this.basin_obs_ppt.setValue(basin_obs_ppt_pri);
            this.basin_rain.setValue(basin_rain_pri);

        }

       
        }

    }

    @Override
    public void clear() throws Exception {
    }
}
