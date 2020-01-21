package prms.process;

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
 ("Surface runoff." +
 "Computes surface runoff and infiltration for each HRU using" +
 "a non-linear variable-source-area method.")
 */
/*  Keywords
 ("Runoff, Surface")
 */
@ModuleMeta(moduleClass = "prms.process.SrunoffSmidx",
        name = "SrunoffSmidx",
        author = "hzfwhy@lzb.ac.cn",
        keyword = "Runoff, Surface",
        description = "Surface runoff."
        + "Computes surface runoff and infiltration for each HRU using"
        + "a non-linear variable-source-area method.")
public class SrunoffSmidx extends AbsComponent {

    private static final Logger log = Logger.getLogger("HOME.model." + SrunoffSmidx.class.getSimpleName());
    // private fields
    // return values for 'perv_sroff_smidx'
    private double[] perv_sroff_smidx_out;
    private double sri;
    private double srp;
    private double[] pkwater_last;
    private double NEARZERO = 1.0e-15;
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
     Description("Type of each HRU (0=inactive; 1=land; 2=lake; 3=swale)")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_type",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Type of each HRU (0=inactive; 1=land; 2=lake; 3=swale)")
    public PoolIntegerArray hru_type= new PoolIntegerArray();

    /* Role(PARAMETER)
     Description("HRU maximum impervious area retention storage Maximum impervious area retention storage for each HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "imperv_stor_max",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = " HRU maximum impervious area retention storage Maximum impervious area retention storage for each HRU")

    public PoolDoubleArray imperv_stor_max= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Coefficient in contributing area computations Coefficient in non-linear contributing area algorithm.  Equation used is: contributing area = smidx_coef *  10.**(smidx_exp*smidx) where smidx is soil_moist +  .5 * ppt_net")
     Unit("decimal fraction")
     Bound ("nhru")
     */
    @VariableMeta(name = "smidx_coef",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Coefficient in contributing area computations Coefficient in non-linear contributing area algorithm.  Equation used is: contributing area = smidx_coef *  10.**(smidx_exp*smidx) where smidx is soil_moist +  .5 * ppt_net\")"
            + "Unit(\"decimal fraction)")
    public PoolDoubleArray smidx_coef= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Exponent in contributing area computations Exponent in non-linear contributing area algorithm.  Equation used is: contributing area = smidx_coef *  10.**(smidx_exp*smidx) where smidx is soil_moist +  .5 * ppt_net")
     Unit("1/inch")
     Bound ("nhru")
     */
    @VariableMeta(name = "smidx_exp",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Exponent in contributing area computations Exponent in non-linear contributing area algorithm.  Equation used is: contributing area = smidx_coef *  10.**(smidx_exp*smidx) where smidx is soil_moist +  .5 * ppt_net")

    public PoolDoubleArray smidx_exp= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Maximum value of water for soil zone Maximum available water holding capacity of soil profile.  Soil profile is surface to bottom of rooting zone")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "soil_moist_max",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Maximum available water holding capacity of soil profile.  Soil profile is surface to bottom of rooting zone")

    public PoolDoubleArray soil_moist_max= new PoolDoubleArray();

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
     Description("Maximum contributing area Maximum possible area contributing to surface runoff  expressed as a portion of the HRU area")
     Unit("decimal fraction")
     Bound ("nhru")
     */
    @VariableMeta(name = "carea_max",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Maximum contributing area Maximum possible area contributing to surface runoff  expressed as a portion of the HRU area")
    public PoolDoubleArray carea_max= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Maximum snow infiltration per day Maximum snow infiltration per day")
     Unit("inches/day")
     Bound ("nhru")
     */
    @VariableMeta(name = "snowinfil_max",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Maximum snow infiltration per day Maximum snow infiltration per day")
    public PoolDoubleArray snowinfil_max= new PoolDoubleArray();
    //TODO feedback
    /* Description("Pseudo parameter. Soil moisture content for each HRU. [smbal]")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "soil_moist",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Current moisture content of soil profile to the depth  of the rooting zone of the major vegetation type on the  HRU")

    public PoolDoubleArray soil_moist= new PoolDoubleArray();//这里暂时没用！
    // Input Vars 

    /* Description("Date of the current time step")
     Unit("yyyy mm dd hh mm ss")
     */
    /**
     * ******************时间格式***********************
     */
    /**
     * public Calendar date;
     */
    @VariableMeta(name = "date_mo",
            dataType = DatatypeEnum.PoolInteger,
            description = "Calendar.MONTH)")
    public PoolInteger date_mo= new PoolInteger();
    @VariableMeta(name = "date_day",
            dataType = DatatypeEnum.PoolInteger,
            description = " Calendar.DAY_OF_MONTH)")
    public PoolInteger date_day= new PoolInteger();
    /* Description("Proportion of each HRU area that is impervious")
     Unit("decimal fraction")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_percent_impv",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Proportion of each HRU area that is impervious")
    public PoolDoubleArray hru_percent_impv= new PoolDoubleArray();

    /* Description("Indicator that a rain-snow mix event has occurred with no snowpack present on an HRU. [snow]")
     Bound ("nhru")
     */
    @VariableMeta(name = "pptmix_nopack",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Indicator that a rain-snow mix event has occurred with no snowpack present on an HRU. [snow]")
    public PoolIntegerArray pptmix_nopack= new PoolIntegerArray();

    /* Description("Rain on an HRU (hru_rain) minus interception. [intcp]")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "net_rain",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "hru_rain minus interception")
    public PoolDoubleArray net_rain= new PoolDoubleArray();

    /* Description("HRU impervious area. [basin]")
     Unit("acres")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_imperv",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Impervious area of each HRU")
    public PoolDoubleArray hru_imperv= new PoolDoubleArray();

    /* Description("Snowmelt from snowpack on an HRU. [snow]")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "snowmelt",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Snowmelt from snowpack on an HRU. [snow]")
    public PoolDoubleArray snowmelt= new PoolDoubleArray();

    /* Description("Snowpack water equivalent on an HRU. [snow]")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "pkwater_equiv",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = " Snowpack water equivalent on an HRU. [snow]")
    public PoolDoubleArray pkwater_equiv= new PoolDoubleArray();

    /* Description("HRU precipitation (rain and/or snow) with  interception removed")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "net_ppt",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "HRU precipitation (rain and/or snow) with  interception removed")
    public PoolDoubleArray net_ppt= new PoolDoubleArray();

    /* Description("Snow on an HRU (hru_snow) minus interception. [intcp]")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "net_snow",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "hru_snow minus interception")
    public PoolDoubleArray net_snow= new PoolDoubleArray();

    /*Description("HRU pervious area. [basin]")
     Unit("acres")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_perv",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Pervious area of each HRU")
    public PoolDoubleArray hru_perv= new PoolDoubleArray();

    /*Description("Precipitation on HRU, rain and snow. [precip]")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_ppt",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Precipitation on HRU, rain and snow. [precip]")
    public PoolDoubleArray hru_ppt= new PoolDoubleArray();

    /*Description("Evaporation and sublimation from snowpack on an HRU. [snow]")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "snow_evap",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Evaporation and sublimation from snowpack. [snow]")
    public PoolDoubleArray snow_evap= new PoolDoubleArray();

    /*Description("Potential evapotranspiration for each HRU. [potet]")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "potet",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Potential evapotranspiration for each HRU. [potet]")
    public PoolDoubleArray potet= new PoolDoubleArray();

    /*Description("Number of active HRUs")*/
    @VariableMeta(name = "active_hrus",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of active HRUs")
    public PoolInteger active_hrus= new PoolInteger();

    /*Description("Kinematic routing switch (0=daily; 1=storm period)")*/
    @VariableMeta(name = "route_on",
            dataType = DatatypeEnum.PoolInteger,
            description = "Kinematic routing switch (0=daily; 1=storm period)")
    public PoolInteger route_on= new PoolInteger();

    /*Description("Snow-covered area on an HRU, in decimal fraction of total HRU area. [snow]")
     Unit("decimal fraction")
     Bound ("nhru")
     */
    @VariableMeta(name = "snowcov_area",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Snow-covered area on an HRU, in decimal fraction of total HRU area. [snow]")
    public PoolDoubleArray snowcov_area= new PoolDoubleArray();

    /*Description("Evaporation from interception on each HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_intcpevap",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Evaporation from interception on each HR")
    public PoolDoubleArray hru_intcpevap= new PoolDoubleArray();

    /*Description("Routing order for HRUs")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_route_order",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Routing order for HRUs")
    public PoolIntegerArray hru_route_order= new PoolIntegerArray();

    /*Description("Inverse of total basin area as sum of HRU areas")
     Unit("1/acres")
     */
    @VariableMeta(name = "basin_area_inv",
            dataType = DatatypeEnum.PoolDouble,
            description = "Inverse of total basin area as sum of HRU areas")
    public PoolDouble basin_area_inv= new PoolDouble();
    // Output Vars 
    /*Description("Basin area-weighted average of surface runoff")
     Unit("inches")
     */
    
    @VariableMeta(name = "basin_sroff",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area-weighted average of surface runoff")
    public PoolDouble basin_sroff= new PoolDouble();

    /*Description("Total basin surface runoff for a storm timestep")
     Unit("inches")
     */
    @VariableMeta(name = "dt_sroff",
            dataType = DatatypeEnum.PoolDouble,
            description = "Total basin surface runoff for a storm timestep")
    public PoolDouble dt_sroff= new PoolDouble();

    /*Description("Basin area-weighted average for infiltration")
     Unit("inches")
     */
    @VariableMeta(name = "basin_infil",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area-weighted average for infiltration")
    public PoolDouble basin_infil= new PoolDouble();

    /*Description("Basin area-weighted average for evaporation from  impervious area")
     Unit("inches")
     */
    @VariableMeta(name = "basin_imperv_evap",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area-weighted average for evaporation from  impervious area")
    public PoolDouble basin_imperv_evap= new PoolDouble();

    /*Description("Basin area-weighted average for storage on  impervious area")
     Unit("inches")
     */
    @VariableMeta(name = "basin_imperv_stor",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area-weighted average for storage on  impervious area")
    public PoolDouble basin_imperv_stor= new PoolDouble();

    /*Description("Current storage on impervious area for each HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "imperv_stor",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Current storage on impervious area for each HRU")
    public PoolDoubleArray imperv_stor= new PoolDoubleArray();

    /*Description("Amount of water infiltrating the soil on each HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "infil",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Infiltration for each HRU. [sroff]")
    public PoolDoubleArray infil= new PoolDoubleArray();

    /*Description("Surface runoff to streams for each HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "sroff",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Infiltration for each HRU. [sroff]")
    public PoolDoubleArray sroff= new PoolDoubleArray();

    /*Description("Evaporation from impervious area")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "infil",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Infiltration for each HRU. [sroff]")
    public PoolDoubleArray imperv_evap= new PoolDoubleArray();

    /*Description("Storage on impervious area for each HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_impervstor",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Storage on impervious area for each HRU")
    public PoolDoubleArray hru_impervstor= new PoolDoubleArray();

    /*Description("Evaporation from impervious area for each HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_impervevap",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Evaporation from impervious area for each HRU")
    public PoolDoubleArray hru_impervevap= new PoolDoubleArray();

    /*Description("Evaporation from depression storage for each HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "dprst_evap_hru",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Evaporation from depression storage for each HRU")
    public PoolDoubleArray dprst_evap_hru = new PoolDoubleArray();

    /*Description("Hortonian surface runoff received from HRUs upslope")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "upslope_hortonian",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Hortonian surface runoff received from HRUs upslope")
    public PoolDoubleArray upslope_hortonian = new PoolDoubleArray();
    //private define 
    //xml io parameter
    private double[] imperv_stor_max_pri;
    private double[] smidx_coef_pri;
    private double[] smidx_exp_pri;
    private double[] soil_moist_max_pri;
    private double[] soil_moist_init_pri;
    private double[] carea_max_pri;
    private double[] snowinfil_max_pri;
    // private double[] soil_moist_pri;//特殊模块 smbl
    //input 
    private int nhru_pri;
    private double[] hru_area_pri;
    private int[] hru_type_pri;
    private double hru_percent_impv_pri;
    private int pptmix_nopack_pri;
    private double net_rain_pri;
    private double snowmelt_pri;
    private double pkwater_equiv_pri;
    private double net_ppt_pri;
    private double net_snow_pri;
    private double hru_perv_pri;
    private double hru_ppt_pri;
    private double snow_evap_pri;
    private double potet_pri;
    private int active_hrus_pri;
    private double snowcov_area_pri;
    private double hru_intcpevap_pri;
    private int[] hru_route_order_pri;
    private double basin_area_inv_pri;
    private int route_on_pri;
    private double hru_imperv_pri;
    private double[] soil_moist_pri;
    //out
    private double basin_sroff_pri;
    private double dt_sroff_pri;
    private double basin_infil_pri;
    private double basin_imperv_evap_pri;
    private double basin_imperv_stor_pri;
    private double[] imperv_stor_pri;
    private double[] infil_pri;
    private double[] sroff_pri;
    private double imperv_evap_pri;
    private double hru_impervstor_pri;
    private double hru_impervevap_pri;
    private double dprst_evap_hru_pri;
    private double upslope_hortonian_pri;
    //private loop define
    private int k;
    private int mo;
    private int day;

    @Override
    public void init() throws Exception {
        nhru_pri = this.nhru.getValue();
        imperv_stor_pri = new double[nhru_pri];
        infil_pri = new double[nhru_pri];
        sroff_pri = new double[nhru_pri];
        imperv_evap_pri = 0;
        hru_impervevap_pri = 0;
        hru_impervstor_pri = 0;
        dprst_evap_hru_pri = 0;
        upslope_hortonian_pri = 0;

        //     imperv_evap_pri= new double[nhru_pri];
        //     hru_impervstor_pri = new double[nhru_pri];
        //     hru_impervevap_pri = new double[nhru_pri];
        //    dprst_evap_hru_pri = new double[nhru_pri];
        //     upslope_hortonian_pri = new double[nhru_pri];
        perv_sroff_smidx_out = new double[2];
        pkwater_last = new double[nhru_pri];

        //parmater getvalue
        imperv_stor_max_pri = this.imperv_stor_max.getValue();
        smidx_coef_pri = this.smidx_coef.getValue();
        smidx_exp_pri = this.smidx_exp.getValue();
        soil_moist_max_pri = this.soil_moist_max.getValue();
        soil_moist_init_pri = this.soil_moist_init.getValue();
        carea_max_pri = this.carea_max.getValue();
        snowinfil_max_pri = this.snowinfil_max.getValue();

        hru_area_pri = this.hru_area.getValue();
        hru_type_pri = this.hru_type.getValue();
        route_on_pri = this.route_on.getValue();
        basin_area_inv_pri = this.basin_area_inv.getValue();
        hru_route_order_pri = this.hru_route_order.getValue();
        active_hrus_pri = this.active_hrus.getValue();

    
    }

    @Override
    public void run() throws Exception {

    	 for (int j = 0; j < active_hrus_pri; j++) {

        int ii = hru_route_order_pri[j];
        hru_percent_impv_pri = this.hru_percent_impv.getValue(ii);
        pptmix_nopack_pri = this.pptmix_nopack.getValue(ii);
        net_rain_pri = this.net_rain.getValue(ii);
        snowmelt_pri = this.snowmelt.getValue(ii);
        pkwater_equiv_pri = this.pkwater_equiv.getValue(ii);
        net_ppt_pri = this.net_ppt.getValue(ii);
        net_snow_pri = this.net_snow.getValue(ii);
        hru_perv_pri = this.hru_perv.getValue(ii);
        hru_ppt_pri = this.hru_ppt.getValue(ii);
        snow_evap_pri = this.snow_evap.getValue(ii);
        potet_pri = this.potet.getValue(ii);
        hru_imperv_pri = this.hru_imperv.getValue(ii);
        snowcov_area_pri = this.snowcov_area.getValue(ii);
        hru_percent_impv_pri = this.hru_percent_impv.getValue(ii);

        if (k == 0) {

            if (imperv_stor_pri == null) {
                init();
            }
            if (route_on_pri == 1) {
                return;
            }

            mo = this.date_mo.getValue();
            day = this.date_day.getValue();

            basin_sroff_pri = 0.0;
            dt_sroff_pri = 0.0;
            basin_infil_pri = 0.0;
            basin_imperv_evap_pri = 0.0;
            basin_imperv_stor_pri = 0.0;

            /*  int mo = date.get(Calendar.MONTH);
             int day = date.get(Calendar.DAY_OF_MONTH);
             */
        }

        /**
         * *********************************loop__********************************
         */
//        for (int k = 0; k < active_hrus; k++) {
        if (k < this.active_hrus.getValue()) {
            int i = hru_route_order_pri[k];
            double harea = hru_area_pri[i];

            if (hru_type_pri[i] != 2) {
                infil_pri[i] = 0.0;
                pkwater_last[i] = pkwater_equiv_pri;
                double sm = (soil_moist_pri == null) ? soil_moist_init_pri[i] : soil_moist_pri[i];
                double last_stor = imperv_stor_pri[i];
                double last_smstor = sm;
                double hperv = hru_perv_pri;
                double himperv = hru_imperv_pri;
                double runoff = 0.;
                srp = 0.;
                sri = 0.;
                double op_surf = 0.0;
                double cl_surf = 0.0;
                double last_hru_perv = hperv;

//******compute runoff for pervious, impervious, and depression storage area
                compute_infil(i, pptmix_nopack_pri, smidx_coef_pri[i],
                        smidx_exp_pri[i], sm,
                        soil_moist_max_pri[i], carea_max_pri[i],
                        net_rain_pri, net_ppt_pri,
                        himperv, imperv_stor_pri[i],
                        imperv_stor_max_pri[i], snowmelt_pri,
                        snowinfil_max_pri[i], net_snow_pri,
                        pkwater_equiv_pri);

                double avail_et = potet_pri - snow_evap_pri - hru_intcpevap_pri;

//******comuute runoff for pervious and impervious area
// there must be some pervious area for this hru
                runoff += srp * hperv + sri * himperv;

//******compute hru weighted average (to units of inches/dt)
                if (hru_type_pri[i] == 1) {
                    sroff_pri[i] = runoff / hru_area_pri[i];
                    basin_sroff_pri = basin_sroff_pri + sroff_pri[i] * harea;

                }
//******compute basin weighted average, lakes not included

                basin_infil_pri = basin_infil_pri + infil_pri[i] * hperv;

//******compute evaporation from impervious area
                if (hru_imperv_pri > NEARZERO) {
                    double tmp = avail_et / hru_percent_impv_pri;
                    imperv_et(i, tmp, snowcov_area_pri);

                    hru_impervevap_pri = imperv_evap_pri * hru_percent_impv_pri;
                    basin_imperv_evap_pri = basin_imperv_evap_pri + imperv_evap_pri * hru_imperv_pri;

                    hru_impervstor_pri = imperv_stor_pri[i] * hru_percent_impv_pri;
                    basin_imperv_stor_pri = basin_imperv_stor_pri + imperv_stor_pri[i] * hru_imperv_pri;

                    avail_et = avail_et - hru_impervevap_pri;
                    if (avail_et < 0.0) {
                        {
                            //rsr, sanity check
                            if (avail_et < -1.0e-5) {
                                System.out.println("avail_et<0 in srunoff imperv " + i
                                        + " " + mo + " " + day + " " + avail_et);
                            }
                            hru_impervevap_pri = hru_impervevap_pri + avail_et;
                            if (hru_impervevap_pri < 0.0) {
                                hru_impervevap_pri = 0.0;
                            }

                            avail_et = 0.0;

                        }

                    }
                }
            } else {
                // hru is a lake
                //rsr, eventually add code for lake area less than hru_area
                //     that includes soil_moist for percent of hru_area that is dry bank
                // sanity check
                if (infil_pri[i] + sroff_pri[i] + imperv_stor_pri[i] + imperv_evap_pri > 0.0) {
                    System.out.println("smidx lake error "
                            + infil_pri[i] + " " + sroff_pri[i] + " " + imperv_stor_pri[i] + " " + imperv_evap_pri);
                }
            }

//            if ( prt_debug == 1 && hru_type[i] != 2 ) {
//                 double basin_sroffp = basin_sroffp + srp*hru_perv[i];
//                 double basin_sroffi = basin_sroffi + sri*hru_imperv[i];
//
//                 double robal = snowmelt[i] - sroff[i] - infil[i]*hru_percent_perv[i] +
//                     (last_stor-imperv_stor[i]-imperv_evap[i]) * hru_percent_impv[i];
//                 if ( pptmix_nopack[i] == 1 || (pkwater_ante[i] < NEARZERO
//               && pkwater_equiv[i] < NEARZERO) ) robal = robal + net_rain[i];
//
//               double basin_robal = basin_robal + robal;
//               if ( Math.abs(robal) > 2.0e-5 ) {
//                 if ( Math.abs(robal) > 1.0e-4 ) {
//                     write (197, *) 'possible hru water balance error'
//                 } else {
//                     write (197, *) 'hru robal rounding issue'
//                 }
//               write (197, '(2i3,i4,13f9.5,i5)') mo, day, i, robal,
//     +             snowmelt[i], last_stor, infil[i], sroff[i],
//     +             imperv_stor[i], imperv_evap[i], net_ppt[i],
//     +             pkwater_ante[i], pkwater_equiv[i], snow_evap[i],
//     +             net_snow[i], net_rain[i], pptmix_nopack[i]
//               }
//            }
            //       }
        }
        /**
         * *********** end loop **************************************
         */
        //******compute basin weighted averages (to units of inches/dt)
        if (k + 1 == this.active_hrus.getValue()) {
            basin_sroff_pri *= basin_area_inv_pri;
            basin_imperv_evap_pri *= basin_area_inv_pri;
            basin_imperv_stor_pri *= basin_area_inv_pri;
            basin_infil_pri *= basin_area_inv_pri;

            dt_sroff_pri = basin_sroff_pri;

            if (log.isLoggable(Level.INFO)) {
                log.info("Srunoff " + basin_sroff_pri + " "
                        + basin_imperv_evap_pri + " "
                        + basin_imperv_stor_pri + " "
                        + basin_infil_pri);

            }

            this.basin_sroff.setValue(basin_sroff_pri);
            this.basin_imperv_evap.setValue(basin_imperv_evap_pri);
            this.basin_imperv_stor.setValue(basin_imperv_stor_pri);
            this.basin_infil.setValue(basin_infil_pri);
            this.dt_sroff.setValue(dt_sroff_pri);

         

        }

        this.infil.setValue(ii, infil_pri[ii]);
        this.imperv_stor.setValue(ii, imperv_stor_pri[ii]);
        this.imperv_evap.setValue(ii, imperv_evap_pri);///imperv_evap=====null
        this.hru_impervstor.setValue(ii, hru_impervstor_pri);
        this.hru_impervevap.setValue(ii, hru_impervevap_pri);
        this.dprst_evap_hru.setValue(ii, dprst_evap_hru_pri);
        this.upslope_hortonian.setValue(ii, upslope_hortonian_pri);
      //  this.sroff.setValue(ii, sroff_pri[ii]);
    	 }
    }

    @Override
    public void clear() throws Exception {
    }

    //***********************************************************************
    //      compute evaporation from impervious area
    //***********************************************************************
    private void imperv_et(int i, double snow_evap, double potet,
            double hru_intcpevap, double sca) {
        double avail_et;
        imperv_evap_pri = 0.0;
        if (sca < 1.0 && imperv_stor_pri[i] > NEARZERO) {
            avail_et = potet - snow_evap - hru_intcpevap;
            if (avail_et >= imperv_stor_pri[i]) {
                imperv_evap_pri = imperv_stor_pri[i] * (1.0 - sca);
            } else {
                imperv_evap_pri = avail_et * (1.0 - sca);
            }
            imperv_stor_pri[i] = imperv_stor_pri[i] - imperv_evap_pri;
        }
    }

    private void compute_infil(int i, int pptmix_nopack, double smidx_coef, double smidx_exp,
            double soil_moist, double soil_moist_max, double carea_max,
            double net_rain, double net_ppt,
            double hru_imperv, double imperv_stor, double imperv_stor_max,
            double snowmelt, double snowinfil_max, double net_snow,
            double pkwater_equiv) {
        double ppti, pptp, ptc;
        double snri;

//******if rain/snow event with no antecedent snowpack,
//******compute the runoff from the rain first and then proceed with the
//******snowmelt computations
        if (pptmix_nopack == 1) {
            ptc = net_rain;
            pptp = net_rain;
            ppti = net_rain;
            perv_imperv_comp(i, ptc, pptp, ppti, hru_imperv,
                    smidx_coef, smidx_exp, soil_moist,
                    carea_max, imperv_stor_max, imperv_stor);

        }

//******if precip on snowpack, all water available to the surface is
//******considered to be snowmelt, and the snowmelt infiltration
//******procedure is used.  if there is no snowpack and no precip,
//******then check for melt from last of snowpack.  if rain/snow mix
//******with no antecedent snowpack, compute snowmelt portion of runoff.
        if (snowmelt > 0.0) {

            if (pkwater_equiv > 0.0 || net_ppt <= net_snow) {

//******Pervious area computations
                infil_pri[i] = infil_pri[i] + snowmelt;
                if (pkwater_equiv > 0.0 && hru_type_pri[i] == 1) {
                    check_capacity(i, soil_moist_max, soil_moist, snowinfil_max);

                }
//******impervious area computations
                if (hru_imperv > NEARZERO) {
                    snri = imperv_sroff(i, imperv_stor_max, snowmelt);
                    sri = sri + snri;
                }
            } else {
//******snowmelt occurred and depleted the snowpack
                ptc = net_ppt;
                pptp = snowmelt;
                ppti = snowmelt;
                perv_imperv_comp(i, ptc, pptp, ppti, hru_imperv,
                        smidx_coef, smidx_exp, soil_moist,
                        carea_max, imperv_stor_max, imperv_stor);
            }
        } //******there was no snowmelt but a snowpack may exist.  if there is
        //******no snowpack then check for rain on a snowfree hru.
        else if (pkwater_equiv < NEARZERO) {

            //      if no snowmelt and no snowpack but there was net snow then
            //      snowpack was small and was lost to sublimation.
            if (net_snow < NEARZERO && net_rain > 0.0) {
// no snow, some rain
                ptc = net_rain;
                pptp = net_rain;
                //rsr, changed by george gl051501, assume no interception on impervious
                ppti = net_rain;

                perv_imperv_comp(i, ptc, pptp, ppti, hru_imperv,
                        smidx_coef, smidx_exp, soil_moist,
                        carea_max, imperv_stor_max, imperv_stor);
            }
        } //***** snowpack exists, check to see if infil exceeds maximum daily
        //***** snowmelt infiltration rate. infil results from rain snow mix
        //***** on a snowfree surface.
        // if soil is frozen, all infiltration is put in runoff
        else if (infil_pri[i] > 0.0 && hru_type_pri[i] == 1) {
            check_capacity(i, soil_moist_max, soil_moist, snowinfil_max);
        }
    }

    private void perv_imperv_comp(int i, double ptc, double pptp, double ppti,
            double hru_imperv,
            double smidx_coef, double smidx_exp, double soil_moist,
            double carea_max, double imperv_stor_max,
            double imperv_stor) {

        double inp = 0., snrp = 0., snri = 0.;
        //******pervious area computations
        //if (pptp > 0.0 && hru_perv > NEARZERO) {
        if (pptp > 0.0) {
            perv_sroff_smidx(i, perv_sroff_smidx_out, smidx_coef, smidx_exp, soil_moist,
                    carea_max, pptp, ptc);
            infil_pri[i] = infil_pri[i] + perv_sroff_smidx_out[1];
            srp = srp + perv_sroff_smidx_out[0];
//             srp = srp + snrp;
        }
        //******impervious area computations
        if (ppti > 0.0 && hru_imperv > NEARZERO) {
            snri = imperv_sroff(i, imperv_stor_max, ppti);
            sri = sri + snri;
        }
    }

    /**
     * Compute runoff from pervious area using non-linear contributing area
     * computations
     */
    private void perv_sroff_smidx(int i, double out[], double smidx_coef,
            double smidx_exp, double soil_moist, double carea_max, double pptp,
            double ptc) {

        /* infil gets updated in this method and srp is updated at the caller by the value returned */
        if (hru_type_pri[i] == 1) {
            double smidx = soil_moist + (.5 * ptc);
            double ca_percent;
            ca_percent = smidx_coef * Math.pow(10.0, (smidx_exp * smidx));
            if (ca_percent > carea_max) {
                ca_percent = carea_max;
            }
            out[0] = ca_percent * pptp;  // srp/////////////////////////这个地方有问题吗？
            out[1] = pptp - out[0];         // infil
//            snrp=ca_percent * pptp;
        } else {
            out[0] = 0.;
            out[1] = pptp;
//           snrp=out[0];
        }
    }

    /**
     * Compute runoff from impervious area
     *
     * @return sri
     */
    private double imperv_sroff(int i, double imperv_stor_max, double ppti) {
        double avail_stor = imperv_stor_max - imperv_stor_pri[i];
        if (ppti > avail_stor) {
            imperv_stor_pri[i] = imperv_stor_max;
            return ppti - avail_stor;
        } else {
            imperv_stor_pri[i] = imperv_stor_pri[i] + ppti;
            return 0.0;
        }
    }

    /**
     * Compute evaporation from impervious area
     */
    private void imperv_et(int index, double avail_et, double sca) {

        if (sca < 1.0 && imperv_stor_pri[index] > NEARZERO) {
            if (avail_et >= imperv_stor_pri[index]) {
                imperv_evap_pri = imperv_stor_pri[index] * (1.0 - sca);
            } else {
                imperv_evap_pri = avail_et * (1.0 - sca);
            }
            imperv_stor_pri[index] = imperv_stor_pri[index] - imperv_evap_pri;
        } else {
            imperv_evap_pri = 0.0;
        }
        //rsr, sanity check
        if (imperv_evap_pri > avail_et) {
            imperv_evap_pri = avail_et;
        }

    }

    //***********************************************************************
    // fill soil to soil_moist_max, if more than capacity restrict
    // infiltration by snowinfil_max, with excess added to runoff
    //***********************************************************************
    private void check_capacity(int i, double soil_moist_max, double soil_moist,
            double snowinfil_max) {

        double capacity = soil_moist_max - soil_moist;
        double excess = infil_pri[i] - capacity;
        if (excess > snowinfil_max) {
            srp = srp + excess - snowinfil_max;
            infil_pri[i] = snowinfil_max + capacity;
        }
    }
}
