package prms.process;

import java.util.Calendar;
import java.util.logging.*;
import net.casnw.home.meta.DatatypeEnum;
import net.casnw.home.meta.ModuleMeta;
import net.casnw.home.meta.VariableMeta;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDoubleArray;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolIntegerArray;


/*  Description
 ("Soil moisture accounting." +
 "This module does soil moisture accounting, including addition" +
 "of infiltration, computation of actual evapotranspiration, and" +
 "seepage to subsurface and groundwater reservoirs.")
 */

/*   Keywords
 ("Soilwater")
 */
@ModuleMeta(moduleClass = "prms.process.Smbal",
        name = "Smbal",
        author = "hzfwhy@lzb.ac.cn",
        keyword = "Soilwater",
        description = "Soil moisture accounting."
        + "This module does soil moisture accounting, including addition"
        + "of infiltration, computation of actual evapotranspiration, and"
        + "seepage to subsurface and groundwater reservoirs.")
public class Smbal extends AbsComponent {

    private static final Logger log = Logger.getLogger("HOME.model." + Smbal.class.getSimpleName());
    // private fields
    private static final double NEARZERO = 1.0e-10;
    private static final double ONETHIRD = 1.0 / 3.0;
    private static final double TWOTHIRDS = 2.0 / 3.0;
    private double last_soil_moist;
    private int soil2gw[];
    // Input Params
    /* Role(PARAMETER)
     Description("Number of HRUs.")
     */
    @VariableMeta(name = "nhru",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of HRUs.")
    public PoolInteger nhru= new PoolInteger();

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
     Description("Total basin area. [basin]")
     Unit("acres")
     */
    @VariableMeta(name = "basin_area",
            dataType = DatatypeEnum.PoolDouble,
            description = "Total basin area")
    public PoolDouble basin_area= new PoolDouble();

    /* Role(PARAMETER)
     Description("HRU soil type (1=sand; 2=loam; 3=clay)")
     Bound ("nhru")
     */
    @VariableMeta(name = "soil_type",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "HRU soil type (1=sand; 2=loam; 3=clay)")
    public PoolIntegerArray soil_type= new PoolIntegerArray();

    /* Role(PARAMETER)
     Description("Vegetation cover type designation for HRU (0=bare soil; 1=grasses; 2=shrubs; 3=trees)")
     Bound ("nhru")
     */
    @VariableMeta(name = "cov_type",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Vegetation cover type designation for each HRU (0=bare soil; 1=grasses; 2=shrubs; 3=trees)")
    public PoolIntegerArray cov_type= new PoolIntegerArray();

    /* Role(PARAMETER)
     Description("Selection flag for depression storage computation. 0=No; 1=Yes")
     */
    @VariableMeta(name = "dprst_flag",
            dataType = DatatypeEnum.PoolInteger,
            description = "Selection flag for depression storage computation. 0=No; 1=Yes")
    public PoolInteger dprst_flag= new PoolInteger();

    /* Role(PARAMETER)
     Description("Initial value for soil recharge zone (upper part of  soil_moist).  Must be less than or equal to soil_moist_init")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "soil_rechr_init",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Initial value for soil recharge zone (upper part of  soil_moist).  Must be less than or equal to soil_moist_init")

    public PoolDoubleArray soil_rechr_init= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Initial value of available water in soil profile")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "soil_moist_init",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Initial value of available water in soil profile")
    public PoolDoubleArray soil_moist_init= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Maximum available water holding capacity of soil profile.  Soil profile is surface to bottom of rooting zone")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "soil_moist_max",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Maximum available water holding capacity of soil profile.  Soil profile is surface to bottom of rooting zone")
    public PoolDoubleArray soil_moist_max= new PoolDoubleArray();

    /*Role(PARAMETER)
     Description("Maximum value for soil recharge zone (upper portion  of soil_moist where losses occur as both evaporation  and transpiration).  Must be less than or equal to  soil_moist")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "soil_rechr_max",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Maximum value for soil recharge zone (upper portion  of soil_moist where losses occur as both evaporation  and transpiration")

    public PoolDoubleArray soil_rechr_max= new PoolDoubleArray();

    /*Role(PARAMETER)
     Description("The maximum amount of the soil water excess for an HRU that is routed directly to the associated groundwater  reservoir each day")
     Unit(" inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "soil2gw_max",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Maximum snow infiltration per day Maximum snow infiltration per day")
    public PoolDoubleArray soil2gw_max= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Type of each HRU (0=inactive; 1=land; 2=lake; 3=swale)")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_type",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Type of each HRU (0=inactive; 1=land; 2=lake; 3=swale)")
    public PoolIntegerArray hru_type= new PoolIntegerArray();

    /* Role(PARAMETER)
     Description("Flag for frozen ground (0=no; 1=yes)")
     Bound ("nhru")
     */
    @VariableMeta(name = "frozen",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Flag for frozen ground (0=no; 1=yes).")
    public PoolIntegerArray frozen= new PoolIntegerArray();
    // Input vars

    /* Description("Date of the current time step")
     Unit("yyyy mm dd hh mm ss")
     */
    /**
     * **************时间格式*******************************
     */
    public Calendar date;
    @VariableMeta(name = "date_mo",
            dataType = DatatypeEnum.PoolInteger,
            description = "Calendar.MONTH)")
    public PoolInteger date_mo= new PoolInteger();
    @VariableMeta(name = "date_day",
            dataType = DatatypeEnum.PoolInteger,
            description = " Calendar.DAY_OF_MONTH)")
    public PoolInteger date_day= new PoolInteger();
    /* Description("Length of the time step")
     Unit("hours")
     */
    @VariableMeta(name = "deltim",
            dataType = DatatypeEnum.PoolDouble,
            description = "Length of the time step)")
    public PoolDouble deltim= new PoolDouble();

    /*Description("Number of active HRUs")*/
    @VariableMeta(name = "active_hrus",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of active HRUs")
    public PoolInteger active_hrus= new PoolInteger();

    /* Description("Kinematic routing switch (0=daily; 1=storm period)")*/
    @VariableMeta(name = "route_on",
            dataType = DatatypeEnum.PoolInteger,
            description = "Kinematic routing switch (0=daily; 1=storm period)")
    public PoolInteger route_on= new PoolInteger();

    /* Description("Ground-melt of snowpack, goes to soil")
     Unit(" inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "gmelt_to_soil",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Ground-melt of snowpack, goes to soi")
    public PoolDoubleArray gmelt_to_soil= new PoolDoubleArray();

    /* Description("HRU pervious area. [basin]")
     Unit("acres")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_perv",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Pervious area of each HRU")
    public PoolDoubleArray hru_perv= new PoolDoubleArray();

    /* Description("Infiltration for each HRU. [sroff]")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "infil",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Infiltration for each HRU. [sroff]")
    public PoolDoubleArray infil= new PoolDoubleArray();

    /* Description("Snow-covered area on an HRU, in decimal fraction of total HRU area. [snow]")
     Unit("decimal fraction")
     Bound ("nhru")
     */
    @VariableMeta(name = "snowcov_area",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Snow-covered area on an HRU, in decimal fraction of total HRU area. [snow]")
    public PoolDoubleArray snowcov_area= new PoolDoubleArray();

    /* Description("Indicator for whether transpiration is occurring. [potet]")
     Bound ("nhru")
     */
    @VariableMeta(name = "transp_on",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Indicator for whether transpiration is occurring, 0=no, 1=yes. [potet]")
    public PoolIntegerArray transp_on= new PoolIntegerArray();

    /* Description("Potential evapotranspiration for each HRU. [potet]")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "potet",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Potential evapotranspiration for each HRU. [potet]")
    public PoolDoubleArray potet= new PoolDoubleArray();

    /* Description("Evaporation and sublimation from snowpack. [snow]")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "snow_evap",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Evaporation and sublimation from snowpack. [snow]")
    public PoolDoubleArray snow_evap= new PoolDoubleArray();

    /* Description("Adjusted precipitation on each HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_ppt",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Precipitation on HRU, rain and snow. [precip]")
    public PoolDoubleArray hru_ppt= new PoolDoubleArray();

    /* Description("Routing order for HRUs")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_route_order",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Routing order for HRUs")
    public PoolIntegerArray hru_route_order= new PoolIntegerArray();

    /* Description("Evaporation from interception on each HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_intcpevap",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Evaporation from interception on each HR")
    public PoolDoubleArray hru_intcpevap= new PoolDoubleArray();
    /* Description("Evaporation from impervious area for each HRU")
     Unit("inches")
     */
    @VariableMeta(name = "hru_impervevap",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Evaporation from impervious area for each HRU")
    public PoolDoubleArray hru_impervevap= new PoolDoubleArray();

    /* Description("Proportion of each HRU area that is pervious")
     Unit("decimal fraction")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_percent_perv",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Proportion of each HRU area that is perviou")
    public PoolDoubleArray hru_percent_perv= new PoolDoubleArray();

    /* Description("Inverse of total basin area as sum of HRU areas")
     Unit("1/acres")
     */
    @VariableMeta(name = "basin_area_inv",
            dataType = DatatypeEnum.PoolDouble,
            description = "Inverse of total basin area as sum of HRU areas")
    public PoolDouble basin_area_inv= new PoolDouble();

    /* Description("Evaporation from depression storage for each HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "dprst_evap_hru",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Evaporation from depression storage for each HRU")
    public PoolDoubleArray dprst_evap_hru= new PoolDoubleArray();
    // Output vars
    /* Description("Basin area weighted average of hru_actet for land HRUs")
     Unit("inches")
     */
    @VariableMeta(name = "basin_actet",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area weighted average of hru_actet for land HRUs")
    public PoolDouble basin_actet= new PoolDouble();

    /* Description("Basin area weighted average for soil_rechr")
     Unit("inches")
     */
    @VariableMeta(name = "basin_soil_rechr",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area weighted average for soil_rechr")
    public PoolDouble basin_soil_rechr= new PoolDouble();

    /*Description("Basin area weighted average of pervious area ET")
     Unit("inches")
     */
    @VariableMeta(name = "basin_perv_et",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area weighted average of pervious area ET")
    public PoolDouble basin_perv_et= new PoolDouble();

    /* Description("Basin average excess soil water that flows directly to  groundwater reservoirs")
     Unit("inches")
     */
    @VariableMeta(name = "basin_soil_to_gw",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin average excess soil water that flows directly to  groundwater reservoirs")
    public PoolDouble basin_soil_to_gw= new PoolDouble();

    /* Description("Basin area weighted average of lake evaporation")
     Unit("inches")
     */
    @VariableMeta(name = "basin_lakeevap",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area weighted average of lake evaporation")
    public PoolDouble basin_lakeevap= new PoolDouble();

    /* Description("Basin area weighted average for soil_moist")
     Unit("inches")
     */
    @VariableMeta(name = "basin_soil_moist",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Basin area weighted average for soil_moist")
    public PoolDoubleArray basin_soil_moist= new PoolDoubleArray();

    /* Description("Basin area weighted average of glacier melt to soil")
     Unit("inches")
     */
    @VariableMeta(name = "basin_gmelt2soil",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area weighted average of glacier melt to soil")
    public PoolDouble basin_gmelt2soil= new PoolDouble();

    /* Description("Current moisture content of soil recharge zone, ie, the  portion of the soil profile from which evaporation can  take place")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "soil_rechr",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Current moisture content of soil recharge zone, ie, the  portion of the soil profile from which evaporation can  take place")
    public PoolDoubleArray soil_rechr= new PoolDoubleArray();

    /* Description("Current moisture content of soil profile to the depth  of the rooting zone of the major vegetation type on the  HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "soil_moist",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Current moisture content of soil profile to the depth  of the rooting zone of the major vegetation type on the  HRU")
    public PoolDoubleArray soil_moist= new PoolDoubleArray();

    /* Description("Portion of excess soil water from an HRU that flows to  its associated groundwater reservoir")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "soil_to_gw",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "The amount of water transferred from the soil zone to a groundwater reservoir for each HRU. [smbal]")

    public PoolDoubleArray soil_to_gw= new PoolDoubleArray();

    /* Description("Portion of excess soil water from an HRU that flows to  its associated subsurface reservoir")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "soil_to_ssr",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "The amount of water transferred from the soil zone to a subsurface reservoir for each HRU. [smbal]")
    public PoolDoubleArray soil_to_ssr= new PoolDoubleArray();

    /* Description("Actual evapotranspiration from pervious areas of HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "perv_actet",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Actual evapotranspiration from pervious areas of HRU")

    public PoolDoubleArray perv_actet= new PoolDoubleArray();

    /* Description("Actual evapotranspiration on HRU, pervious + impervious")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_actet",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Actual evapotranspiration on HRU, pervious + impervious")
    public PoolDoubleArray hru_actet= new PoolDoubleArray();
    //private define 
    //xml parameter IO
    private int[] soil_type_pri;
    private int dprst_flag_pri;
    private double[] soil_rechr_init_pri;
    private double[] soil_moist_max_pri;
    private double[] soil_rechr_max_pri;
    private double[] soil2gw_max_pri;
    private int[] frozen_pri;
    //input
    private int nhru_pri;
    private double[] hru_area_pri;
    private double basin_area_pri;
    private int[] cov_type_pri;
    private double[] soil_moist_init_pri;
    private int[] hru_type_pri;
    private double deltim_pri;
    private int active_hrus_pri;
    private int route_on_pri;
    private double gmelt_to_soil_pri;
    private double hru_perv_pri;
    private double infil_pri;
    private double snowcov_area_pri;
    private int transp_on_pri;
    private double potet_pri;
    private double snow_evap_pri;
    private double hru_ppt_pri;
    private int[] hru_route_order_pri;
    private double hru_intcpevap_pri;
    private double hru_impervevap_pri;
    private double hru_percent_perv_pri;
    private double basin_area_inv_pri;
    private double dprst_evap_hru_pri;
    //time series
    private int mo;
    private int day;
    //out
    private double basin_actet_pri;
    private double basin_soil_rechr_pri;
    private double basin_perv_et_pri;
    private double basin_soil_to_gw_pri;
    private double basin_lakeevap_pri;
    private double[] basin_soil_moist_pri;
    private double basin_gmelt2soil_pri;
    private double[] soil_rechr_pri;
    private double[] soil_moist_pri;
    private double[] soil_to_gw_pri;
    private double[] soil_to_ssr_pri;
    private double[] perv_actet_pri;
    private double[] hru_actet_pri;
    //pricate  define loop
    private int k;
    private double avail_potet, td;
    private double basin_s2ss, basin_infil;

    @Override
    public void init() throws Exception {
        nhru_pri = this.nhru.getValue();
        basin_soil_moist_pri = new double[1];
        soil_rechr_pri = new double[nhru_pri];
        soil_moist_pri = new double[nhru_pri];
        soil_to_gw_pri = new double[nhru_pri];
        soil_to_ssr_pri = new double[nhru_pri];
        perv_actet_pri = new double[nhru_pri];
        hru_actet_pri = new double[nhru_pri];
        this.soil_to_ssr.setValue(soil_to_ssr_pri);
        soil2gw = new int[nhru_pri];

        //getvalue parameter
        soil_type_pri = this.soil_type.getValue();
        dprst_flag_pri = this.dprst_flag.getValue();
        soil_rechr_init_pri = this.soil_rechr_init.getValue();
        soil_moist_max_pri = this.soil_moist_max.getValue();
        soil_rechr_max_pri = this.soil_rechr_max.getValue();
        soil2gw_max_pri = this.soil2gw_max.getValue();
        frozen_pri = this.frozen.getValue();
        nhru_pri = this.nhru.getValue();
        hru_area_pri = this.hru_area.getValue();
        basin_area_pri = this.basin_area.getValue();
        cov_type_pri = this.cov_type.getValue();
        hru_type_pri = this.hru_type.getValue();
        deltim_pri = this.deltim.getValue();
        active_hrus_pri = this.active_hrus.getValue();
        route_on_pri = this.route_on.getValue();
        hru_route_order_pri = this.hru_route_order.getValue();
        basin_area_pri = this.basin_area.getValue();
        active_hrus_pri = this.active_hrus.getValue();

        hru_area_pri = this.hru_area.getValue();
        basin_area_pri = this.basin_area.getValue();
        cov_type_pri = this.cov_type.getValue();
        hru_type_pri = this.hru_type.getValue();
        deltim_pri = this.deltim.getValue();
        active_hrus_pri = this.active_hrus.getValue();
        route_on_pri = this.route_on.getValue();
        hru_route_order_pri = this.hru_route_order.getValue();
        basin_area_pri = this.basin_area.getValue();
        active_hrus_pri = this.active_hrus.getValue();
        active_hrus_pri = this.active_hrus.getValue();
        soil_moist_init_pri = this.soil_moist_init.getValue();

        //  do only once so restart uses saved values
        for (int i = 0; i < nhru_pri; i++) {
            soil_rechr_pri[i] = soil_rechr_init_pri[i];
            soil_moist_pri[i] = soil_moist_init_pri[i];
        }

        basin_soil_moist_pri[0] = 0.0;

        for (k = 0; k < active_hrus_pri; k++) {
            int i = hru_route_order_pri[k];
            if (soil_rechr_max_pri[i] > soil_moist_max_pri[i]) {
                System.out.println("hru: " + i + " " + soil_rechr_max_pri[i]
                        + " " + soil_moist_max_pri[i] + " "
                        + " soil_rechr_max > soil_moist_max, soil_rechr_max set to soil_moist_max");
                soil_rechr_max_pri[i] = soil_moist_max_pri[i];
            }
            if (soil_rechr_pri[i] > soil_rechr_max_pri[i]) {
                System.out.println("  soil_rechr > soil_rechr_max,  "
                        + "soil_rechr set to soil_rechr_max " + i + " "
                        + soil_rechr_pri[i] + "  " + soil_rechr_max_pri[i]);
                soil_rechr_pri[i] = soil_rechr_max_pri[i];
            }
            if (soil_moist_pri[i] > soil_moist_max_pri[i]) {
                System.out.println(" soil_moist > soil_moist_max, "
                        + "soil_moist set to soil_moist_max " + i + " "
                        + soil_moist_pri[i] + " " + soil_moist_max_pri[i]);
                soil_moist_pri[i] = soil_moist_max_pri[i];
            }
            if (soil_rechr_pri[i] > soil_moist_pri[i]) {
                System.out.println("hru: " + i + " " + soil_rechr_pri[i]
                        + " " + soil_moist_pri[i] + " "
                        + " soil_rechr > soil_moist, soil_rechr set to soil_moist");
                soil_rechr_pri[i] = soil_moist_pri[i];
            }

            //rsr, hru_perv must be > 0.0
            //   if ( hru_type[i] == 2 || hru_perv[i] < nearzero ) {
            if (hru_type_pri[i] == 2) {
                soil_rechr_pri[i] = 0.0;
                soil_moist_pri[i] = 0.0;
            }
            if (soil2gw_max_pri[i] > NEARZERO) {
                soil2gw[i] = 1;
            }
            basin_soil_moist_pri[0] += soil_moist_pri[i] * hru_perv_pri;
        }
        basin_soil_moist_pri[0] *= basin_area_inv_pri;
        last_soil_moist = basin_soil_moist_pri[0];

        k = -1;
    }

    @Override
    public void run() throws Exception {

    	 for (int ii = 0;ii < active_hrus_pri; ii++) {
        //getvalue
   //     int ii;
     //   ii = hru_route_order_pri[j];

        soil_moist_init_pri[ii] = this.soil_moist_init.getValue(ii);
        hru_ppt_pri = this.hru_ppt.getValue(ii);
        dprst_evap_hru_pri = this.dprst_evap_hru.getValue(ii);
        hru_percent_perv_pri = this.hru_percent_perv.getValue(ii);
        hru_impervevap_pri = this.hru_impervevap.getValue(ii);
        hru_intcpevap_pri = this.hru_intcpevap.getValue(ii);
        snow_evap_pri = this.snow_evap.getValue(ii);
        potet_pri = this.potet.getValue(ii);
        transp_on_pri = this.transp_on.getValue(ii);
        snowcov_area_pri = this.snowcov_area.getValue(ii);
        infil_pri = this.infil.getValue(ii);
        hru_perv_pri = this.hru_perv.getValue(ii);
        gmelt_to_soil_pri = this.gmelt_to_soil.getValue(ii);

        double perv_area, harea, soilin;
        double excs;

        if (ii == 0) {
            if (soil_rechr_pri == null) {
                init();
            }

            excs = 0.;

//        int mo = date.get(Calendar.MONTH);
//        int day = date.get(Calendar.DAY_OF_MONTH);
            mo = this.date_mo.getValue();
            day = this.date_day.getValue();

            double timestep = deltim_pri;
            td = timestep / 24.0;

            last_soil_moist = basin_soil_moist_pri[0];
            basin_actet_pri = 0.0;
            basin_soil_moist_pri[0] = 0.0;
            basin_soil_rechr_pri = 0.0;
            basin_perv_et_pri = 0.0;
            basin_lakeevap_pri = 0.0;
            basin_soil_to_gw_pri = 0.0;
            basin_gmelt2soil_pri = 0.0;
            basin_s2ss = 0.;
            basin_infil = 0.0;
        }
        /**
         * **************************nhru loop******************
         */
        //       for (i = 0; i < nhru; i++) {
        soil_to_gw_pri[ii] = 0.0;
        soil_to_ssr_pri[ii] = 0.0;
//        }
        /**
         * *************************nhru end**********************
         */
        /**
         * ******************** active_hrus loop******************
         */
        if (ii < this.active_hrus.getValue()) {
//        for (k = 0; k < active_hrus; k++) {
            int i = hru_route_order_pri[ii];
            harea = hru_area_pri[i];
            perv_area = hru_perv_pri;

            // soil_to_gw for whole hru
            // soil_to_ssr for whole hru
            soil_to_gw_pri[i] = 0.0;
            soil_to_ssr_pri[i] = 0.0;
            hru_actet_pri[i] = hru_impervevap_pri + hru_intcpevap_pri + snow_evap_pri + dprst_evap_hru_pri;
            if (frozen_pri[i] == 1) {
                basin_actet_pri += hru_actet_pri[i] * harea;
                basin_soil_moist_pri[0] += soil_moist_pri[i] * perv_area;
                //              continue;

            }

            if (frozen_pri[i] != 1) {
                // Hrutype can be 1 (land) or 3 (swale)
                if (hru_type_pri[i] != 2) {

                    //******add infiltration to soil and compute excess
                    //rsr, note perv_area has to be > 0.0
                    //infil for pervious, but gmelt_to_soil for whole hru??
                    soilin = infil_pri + gmelt_to_soil_pri;
                    basin_gmelt2soil_pri = basin_gmelt2soil_pri + gmelt_to_soil_pri * harea;
                    if (soilin > 0.0) {
                        soil_rechr_pri[i] = Math.min((soil_rechr_pri[i] + soilin), soil_rechr_max_pri[i]);
                        excs = soil_moist_pri[i] + soilin;
                        soil_moist_pri[i] = Math.min(excs, soil_moist_max_pri[i]);
                        excs = (excs - soil_moist_pri[i]) * hru_percent_perv_pri;
                        if (excs > 0.0) {
                            //note, soil_to_gw set to 0.0 outside hru loop
                            if (soil2gw[i] == 1) {
                                soil_to_gw_pri[i] = Math.min(soil2gw_max_pri[i] * td, excs);
                            }
                            soil_to_ssr_pri[i] = excs - soil_to_gw_pri[i];
                        }
                    }
                    double soil_lower = soil_moist_pri[i] - soil_rechr_pri[i];

                    //******compute actual evapotranspiration
                    avail_potet = potet_pri - hru_intcpevap_pri - snow_evap_pri - hru_impervevap_pri;
                    //rsr, sanity check
                    if (dprst_flag_pri > 0) {
                        avail_potet = avail_potet - dprst_evap_hru_pri;
                        if (avail_potet < 0.0) {
                            System.out.println("avail_potet<0 smbal " + i + " "
                                    + mo + " " + day + " " + avail_potet);
                            dprst_evap_hru_pri = dprst_evap_hru_pri + avail_potet;
                            if (dprst_evap_hru_pri < 0.0) {
                                dprst_evap_hru_pri = 0.0;
                            }
                            avail_potet = 0.0;
                        }
                    }
                    if (route_on_pri == 1) {
                        if (hru_ppt_pri > NEARZERO) {
                            avail_potet = 0.0;
                        }
                    }

                    compute_actet(i, perv_area, soil_moist_max_pri[i],
                            soil_rechr_max_pri[i], snowcov_area_pri,
                            transp_on_pri, cov_type_pri[i], soil_type_pri[i],
                            avail_potet);

                    hru_actet_pri[i] = hru_actet_pri[i] + perv_actet_pri[i] * hru_percent_perv_pri;
// ghl1299
// soil_moist & soil_rechr multiplied by perv_area instead of harea
                    basin_soil_to_gw_pri += soil_to_gw_pri[i] * harea;
                    basin_soil_rechr_pri += soil_rechr_pri[i] * perv_area;
                    basin_perv_et_pri += perv_actet_pri[i] * perv_area;
                    basin_soil_moist_pri[0] += soil_moist_pri[i] * perv_area;
                    basin_s2ss += soil_to_ssr_pri[i] * harea;
                    basin_infil += soilin * perv_area;
                } else {
                    avail_potet = 0.0;
                    hru_actet_pri[i] = potet_pri;
                    basin_lakeevap_pri = basin_lakeevap_pri + hru_actet_pri[i] * harea;
                }

                basin_actet_pri += hru_actet_pri[i] * harea;
                //       }
            }
        }

        /**
         * ****************************active hrus loop
         * end*********************
         */
        if (ii == this.active_hrus.getValue()) {
            basin_actet_pri *= basin_area_inv_pri;
            basin_perv_et_pri *= basin_area_inv_pri;
            basin_soil_rechr_pri *= basin_area_inv_pri;
            basin_soil_to_gw_pri *= basin_area_inv_pri;
            basin_soil_moist_pri[0] *= basin_area_inv_pri;
            basin_lakeevap_pri *= basin_area_inv_pri;
            basin_gmelt2soil_pri *= basin_area_inv_pri;

            if (log.isLoggable(Level.INFO)) {
                log.info("Smbal "
                        + basin_actet + " "
                        + basin_perv_et + " "
                        + basin_soil_rechr + " "
                        + basin_soil_to_gw + " "
                        + basin_soil_moist_pri[0] + " "
                        + basin_lakeevap + " "
                        + basin_gmelt2soil);
            }
            basin_s2ss *= basin_area_inv_pri;
            basin_infil *= basin_area_inv_pri;

//        if (prt_debug == 1) {
//            basin_s2ss = basin_s2ss * basin_area_inv;
//            basin_infil = basin_infil * basin_area_inv;
//            double bsmbal = last_soil_moist - this.basin_soil_moist + basin_infil -
//                    this.basin_perv_et - this.basin_soil_to_gw - basin_s2ss;
//            if (Math.abs(bsmbal) > 1.0e-4) {
//                System.out.println("possible water balance error");
//            } else if (Math.abs(bsmbal) > 5.0e-6) {
//                System.out.println("bsm rounding issue " + bsmbal);
//            }
//        
      
            this.basin_actet.setValue(basin_actet_pri);
            this.basin_soil_rechr.setValue(basin_soil_rechr_pri);
            this.basin_perv_et.setValue(basin_perv_et_pri);
            this.basin_soil_to_gw.setValue(basin_soil_to_gw_pri);
            this.basin_lakeevap.setValue(basin_lakeevap_pri);
            this.basin_gmelt2soil.setValue(basin_gmelt2soil_pri);

        }
        this.basin_soil_moist.setValue(ii, basin_soil_moist_pri[0]);
        this.hru_actet.setValue(ii, hru_actet_pri[ii]);
        this.soil_rechr.setValue(ii, soil_rechr_pri[ii]);
        this.soil_moist.setValue(ii, soil_moist_pri[ii]);
        this.soil_to_gw.setValue(ii, soil_to_gw_pri[ii]);
        this.soil_to_ssr.setValue(ii, soil_to_ssr_pri[ii]);
        this.perv_actet.setValue(ii, perv_actet_pri[ii]);

    }}

    @Override
    public void clear() throws Exception {
    }

    //***********************************************************************
    //     compute actual evapotranspiration
    //***********************************************************************
    private void compute_actet(int i, double perv_area, double soil_moist_max,
            double soil_rechr_max, double snowcov_area, int transp_on,
            int cov_type, int soil_type,
            double avail_potet) {

        int et_type = 0;
        double et, open_ground, pcts, pctr;
        double ets = 0.0;
        double etr = 0.0;

        open_ground = 1.0 - snowcov_area;

        //******determine if evaporation(et_type = 2) or transpiration plus
        //******evaporation(et_type = 3) are active.  if not, et_type = 1
        if (avail_potet < NEARZERO) {
            et_type = 1;
            avail_potet = 0.0;
        } else if (transp_on == 0) {
            if (open_ground < 0.01) {
                et_type = 1;
            } else {
                et_type = 2;
            }
        } else if (cov_type > 0) {
            et_type = 3;
        } else if (open_ground < 0.01) {
            et_type = 1;
        } else {
            et_type = 2;
        }

        if (et_type > 1) {
            pcts = soil_moist_pri[i] / soil_moist_max;
            pctr = soil_rechr_pri[i] / soil_rechr_max;
            ets = avail_potet;
            etr = avail_potet;

            //******sandy soil*/
            if (soil_type == 1) {
                if (pcts < 0.25) {
                    ets = 0.5 * pcts * avail_potet;
                }
                if (pctr < 0.25) {
                    etr = 0.5 * pctr * avail_potet;
                }
            } //******loam soil*/
            else if (soil_type == 2) {
                if (pcts < 0.5) {
                    ets = pcts * avail_potet;
                }
                if (pctr < 0.5) {
                    etr = pctr * avail_potet;
                }
            } //******clay soil*/
            else if (soil_type == 3) {
                if (pcts < TWOTHIRDS && pcts > ONETHIRD) {
                    ets = pcts * avail_potet;
                } else if (pcts <= ONETHIRD) {
                    ets = 0.5 * pcts * avail_potet;
                }
                if (pctr < TWOTHIRDS && pctr > ONETHIRD) {
                    etr = pctr * avail_potet;
                } else if (pctr <= ONETHIRD) {
                    etr = 0.5 * pctr * avail_potet;
                }
            }
            //******Soil moisture accounting*/
            if (et_type == 2) {
                etr = etr * open_ground;
            }
            if (etr > soil_rechr_pri[i]) {
                etr = soil_rechr_pri[i];
                soil_rechr_pri[i] = 0.0;
            } else {
                soil_rechr_pri[i] = soil_rechr_pri[i] - etr;
            }
            if (et_type == 2 || etr >= ets) {
                if (etr > soil_moist_pri[i]) {
                    etr = soil_moist_pri[i];
                    soil_moist_pri[i] = 0.0;
                } else {
                    soil_moist_pri[i] = soil_moist_pri[i] - etr;
                }
                et = etr;
            } else if (ets >= soil_moist_pri[i]) {
                et = soil_moist_pri[i];
                soil_moist_pri[i] = 0.0;
                soil_rechr_pri[i] = 0.0;
            } else {
                soil_moist_pri[i] = soil_moist_pri[i] - ets;
                et = ets;
            }
        } else {
            et = 0.0;
        }
        perv_actet_pri[i] = et;
    }
}
