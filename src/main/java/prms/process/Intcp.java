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
 ("Interception calculation." +
 "Computes amount of intercepted rain and snow, evaporation" +
 "from intercepted rain and snow, and net rain and snow that" +
 "reaches the soil or snowpack.")
 */

/*   Keywords
 ("Interception")
 */
@ModuleMeta(moduleClass = "prms.process.Intcp",
        name = "Intcp",
        author = "hzfwhy@lzb.ac.cn",
        keyword = "Interception",
        description = "Interception calculation."
        + "Computes amount of intercepted rain and snow, evaporation"
        + "from intercepted rain and snow, and net rain and snow that reaches the soil or snowpack.")
public class Intcp extends AbsComponent {

    private static final Logger log = Logger.getLogger("HOME.model."
            + Intcp.class.getSimpleName());
    // private fields
    private double stor_last[];
    private static double NEARZERO = 1.0e-15;   //TODO what is near?
    private int[] intcp_transp_on;
    // Input params 
    /* Role(PARAMETER)
     Description("Number of HRUs.")
     */
    @VariableMeta(name = "nhru",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of HRUs.")
    public PoolInteger nhru = new PoolInteger();
    /* Role(PARAMETER)
     Description("Number of evaporation pan stations.")
     */
    @VariableMeta(name = "nevap",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of evaporation pan stations.")
    public PoolInteger nevap= new PoolInteger();

    /* Role(PARAMETER)
     Description("Evaporation pan coefficient Evaporation pan coefficient")
     Bound ("nmonths")
     */
    @VariableMeta(name = "epan_coef",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Evaporation pan coefficient Evaporation pan coefficient")
    public PoolDoubleArray epan_coef= new PoolDoubleArray();

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
     Description("Snow interception storage capacity for the major vegetation type in each HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "snow_intcp",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Snow interception storage capacity for the major vegetation type in each HRU")
    public PoolDoubleArray snow_intcp= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Summer rain interception storage capacity for the major vegetation type in each HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "srain_intcp",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Winter rain interception storage capacity for the major vegetation type in the HRU")
    public PoolDoubleArray srain_intcp= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Winter rain interception storage capacity for the major vegetation type in the HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "wrain_intcp",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Winter rain interception storage capacity for the major vegetation type in the HRU")
    public PoolDoubleArray wrain_intcp= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Vegetation cover type designation for each HRU (0=bare soil; 1=grasses; 2=shrubs; 3=trees)")
     Bound ("nhru")
     */
    @VariableMeta(name = "cov_type",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Vegetation cover type designation for each HRU (0=bare soil; 1=grasses; 2=shrubs; 3=trees)")
    public PoolIntegerArray cov_type= new PoolIntegerArray();

    /* Role(PARAMETER)
     Description("Summer vegetation cover density for the major vegetation type on each HRU")
     Unit("decimal fraction")
     Bound ("nhru")
     */
    @VariableMeta(name = "covden_sum",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Summer vegetation cover density for the major vegetation type on each HRU")
    public PoolDoubleArray covden_sum= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Winter vegetation cover density for the major vegetation type on each HRU")
     Unit("decimal fraction")
     Bound ("nhru")
     */
    @VariableMeta(name = "covden_win",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Winter vegetation cover density for the major vegetation type on each HRU")
    public PoolDoubleArray covden_win= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Proportion of potential ET that is sublimated from snow surface")
     Unit("decimal fraction")
     */
    @VariableMeta(name = "potet_sublim",
            dataType = DatatypeEnum.PoolDouble,
            description = "Proportion of potential ET that is sublimated from snow surface")
    public PoolDouble potet_sublim= new PoolDouble();

    /* Role(PARAMETER)
     Description("Type of each HRU (0=inactive; 1=land; 2=lake; 3=swale)")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_type",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Type of each HRU (0=inactive; 1=land; 2=lake; 3=swale)")
    public PoolIntegerArray hru_type= new PoolIntegerArray();

    /* Description("Psuedo parameter, snow pack water equivalent from previous time step.")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "pkwater_equiv",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Psuedo parameter, snow pack water equivalent from previous time step")
    public PoolDoubleArray pkwater_equiv= new PoolDoubleArray();  //TODO feedback
    // Input vars 
    /* Description("Rain on HRU. [precip]")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_rain",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Rain on HRU. [precip]")
    public PoolDoubleArray hru_rain= new PoolDoubleArray();

    /* Description("Snow on HRU. [precip]")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_snow",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Snow on HRU. [precip]")
    public PoolDoubleArray hru_snow= new PoolDoubleArray();

    /* Description("Precipitation on HRU, rain and snow. [precip]")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_ppt",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Precipitation on HRU, rain and snow. [precip]")
    public PoolDoubleArray hru_ppt= new PoolDoubleArray();

    @VariableMeta(name = "basin_ppt",
            dataType = DatatypeEnum.PoolDouble,
            description = "basin_ppt")
    public PoolDouble basin_ppt= new PoolDouble();

    /* Description("Indicator for whether transpiration is occurring, 0=no, 1=yes. [potet]")
     Bound ("nhru")
     */
    @VariableMeta(name = "transp_on",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Indicator for whether transpiration is occurring, 0=no, 1=yes. [potet]")
    public PoolIntegerArray transp_on= new PoolIntegerArray();

    /* Description("Maximum HRU temperature. [temp]")
     Unit("deg F")
     Bound ("nhru")
     */
    @VariableMeta(name = "tmaxf",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Maximum HRU temperature. [temp]")
    public PoolDoubleArray tmaxf= new PoolDoubleArray();

    /* Description("Average HRU temperature. [temp]")
     Unit("deg C")
     Bound ("nhru")
     */
    @VariableMeta(name = "tavgc",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Average HRU temperature. [temp]")
    public PoolDoubleArray tavgc= new PoolDoubleArray();

    /* Description("The computed solar radiation for each HRU [solrad]")
     Unit("calories/cm2")
     Bound ("nhru")
     */
     @VariableMeta(name = "swrad",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "The computed solar radiation for each HRU [solrad]")
    public PoolDoubleArray swrad= new PoolDoubleArray();

    /* Description("Measured pan evaporation. [obs]")
     Unit("inches")
     Bound ("nevap")
     */
    @VariableMeta(name = "pan_evap",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Measured pan evaporation. [obs]")
    public PoolDoubleArray pan_evap= new PoolDoubleArray();//观测的蒸发值obs模块

    /* Description("Potential evapotranspiration for each HRU. [potet]")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "potet",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Potential evapotranspiration for each HRU. [potet]")
    public PoolDoubleArray potet= new PoolDoubleArray();

    /* Description("Kinematic routing switch (0=daily; 1=storm period)")*/
    @VariableMeta(name = "route_on",
            dataType = DatatypeEnum.PoolInteger,
            description = "Kinematic routing switch (0=daily; 1=storm period)")
    public PoolInteger route_on= new PoolInteger();

    /* Description("Number of active HRUs")*/
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
     * ************时间格式定义***********************************
     */
    /* public Calendar date;*/
    @VariableMeta(name = "date_mo",
            dataType = DatatypeEnum.PoolInteger,
            description = "public Calendar date")

    public PoolInteger date_mo= new PoolInteger();
    /* Description("Length of the time step")
     Unit("hours")
     */
    @VariableMeta(name = "deltim",
            dataType = DatatypeEnum.PoolDouble,
            description = "Length of the time step")
    public PoolDouble deltim= new PoolDouble();

    /* Description("Inverse of total basin area as sum of HRU areas")
     Unit("1/acres")
     */
    @VariableMeta(name = "basin_area_inv",
            dataType = DatatypeEnum.PoolDouble,
            description = "Inverse of total basin area as sum of HRU areas")
    public PoolDouble basin_area_inv= new PoolDouble();
    // Output vars 
    /* Description("Evaporation from interception on each HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_intcpevap",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Evaporation from interception on each HR")
    public PoolDoubleArray hru_intcpevap= new PoolDoubleArray();

    /* Description("Storage in canopy on each HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_intcpstor",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Storage in canopy on each HRU")
    public PoolDoubleArray hru_intcpstor= new PoolDoubleArray();

    /* Description("Basin area-weighted average changeover interception")
     Unit("inches")
     */
    @VariableMeta(name = "last_intcp_stor",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area-weighted average changeover interception")
    public PoolDouble last_intcp_stor= new PoolDouble();

    /* Description("hru_rain minus interception")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "net_rain",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "hru_rain minus interception")
    public PoolDoubleArray net_rain= new PoolDoubleArray();

    /* Description("hru_snow minus interception")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "net_snow",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "hru_snow minus interception")
    public PoolDoubleArray net_snow= new PoolDoubleArray();

    /* Description("HRU precipitation (rain and/or snow) with  interception removed")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "net_ppt",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "HRU precipitation (rain and/or snow) with  interception removed")
    public PoolDoubleArray net_ppt= new PoolDoubleArray();

    /* Description("Basin area-weighted average net_ppt")
     Unit("inches")
     */
    @VariableMeta(name = "basin_net_ppt",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area-weighted average net_ppt")
    public PoolDouble basin_net_ppt= new PoolDouble();

    /* Description("Current interception storage on each HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "intcp_stor",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Current interception storage on each HRU")
    public PoolDoubleArray intcp_stor= new PoolDoubleArray();

    /* Description("Basin area-weighted average interception storage")
     Unit("inches")
     */
    @VariableMeta(name = "basin_intcp_stor",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area-weighted average interception storage")
    public PoolDouble basin_intcp_stor= new PoolDouble();

    /* Description("Evaporation from interception on canopy of each HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "intcp_evap",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Evaporation from interception on canopy of each HRU")
    public PoolDoubleArray intcp_evap= new PoolDoubleArray();

    /* Description("Basin area-weighted evaporation from interception")
     Unit("inches")
     */
    @VariableMeta(name = "basin_intcp_evap",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area-weighted evaporation from interception")
    public PoolDouble basin_intcp_evap= new PoolDouble();

    /* Description("Form (rain or snow) of interception (0=rain; 1=snow)")
     Bound ("nhru")
     */
    @VariableMeta(name = "intcp_form",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Form (rain or snow) of interception (0=rain; 1=snow)")
    public PoolIntegerArray intcp_form= new PoolIntegerArray();

    /* Description("Whether there is interception in the canopy (0=no; 1=yes)")
     Bound ("nhru")
     */
    @VariableMeta(name = "intcp_on",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Whether there is interception in the canopy (0=no; 1=yes)")
    public PoolIntegerArray intcp_on= new PoolIntegerArray();
    // In Out variables

    /* Description("New snow on HRU (0=no; 1=yes)")
     Bound ("nhru")
     */
    @VariableMeta(name = "newsnow",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "New snow on HRU (0=no; 1=yes)")
    public PoolIntegerArray newsnow= new PoolIntegerArray();      // altering

    /* Description("Precipitation is mixture of rain and snow (0=no; 1=yes)")
     Bound ("nhru")
     */
    @VariableMeta(name = "pptmix",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Precipitation is mixture of rain and snow (0=no; 1=yes)")
    public PoolIntegerArray pptmix= new PoolIntegerArray();       // altering
    //private define
    //xml IO parameter
    private double[] pkwater_equiv_pri;
    private int nevap_pri;
    private double[] epan_coef_pri;
    private double[] snow_intcp_pri;
    private double[] srain_intcp_pri;
    private double[] wrain_intcp_pri;
    private double[] covden_sum_pri;
    private double[] covden_win_pri;
    private double potet_sublim_pri;
    //input
    private int nhru_pri;
    private double[] hru_area_pri;
    private int[] cov_type_pri;
    private int[] hru_type_pri;
    private double hru_rain_pri;
    private double hru_snow_pri;
    private double hru_ppt_pri;
    private double basin_ppt_pri;
    private int[] transp_on_pri;
    private double tmaxf_pri;
    private double tavgc_pri;
    private double swrad_pri;
    private double[] pan_evap_pri;
    private double potet_pri;
    private int route_on_pri;
    private int active_hrus_pri;
    private int[] hru_route_order_pri;
    private double deltim_pri;
    private double basin_area_inv_pri;
    //out
    private double[] hru_intcpevap_pri;
    private double[] hru_intcpstor_pri;
    private double last_intcp_stor_pri;
    private double[] net_rain_pri;
    private double[] net_snow_pri;
    private double[] net_ppt_pri;
    private double basin_net_ppt_pri;
    private double[] intcp_stor_pri;
    private double basin_intcp_stor_pri;
    private double[] intcp_evap_pri;
    private double basin_intcp_evap_pri;
    private int[] intcp_form_pri;
    private int[] intcp_on_pri;
    //time series io
    private int date_mo_pri;
    //out input
    private int newsnow_pri;
    private int pptmix_pri;
    //define loop
    public int j;
    private int mo;
    private double last_stor;

    @Override
    public void init() throws Exception {
        nhru_pri = this.nhru.getValue();
        stor_last = new double[nhru_pri];

        net_ppt_pri = new double[nhru_pri];
        net_rain_pri = new double[nhru_pri];
        net_snow_pri = new double[nhru_pri];
        intcp_stor_pri = new double[nhru_pri];
        intcp_on_pri = new int[nhru_pri];
        intcp_form_pri = new int[nhru_pri];
        intcp_evap_pri = new double[nhru_pri];
        intcp_transp_on = new int[nhru_pri];
        hru_intcpevap_pri = new double[nhru_pri];
        hru_intcpstor_pri = new double[nhru_pri];

        //getvalue 
        pkwater_equiv_pri = this.pkwater_equiv.getValue();
        nevap_pri = this.nevap.getValue();
        epan_coef_pri = this.epan_coef.getValue();
        snow_intcp_pri = this.snow_intcp.getValue();
        srain_intcp_pri = this.srain_intcp.getValue();
        wrain_intcp_pri = this.wrain_intcp.getValue();
        covden_sum_pri = this.covden_sum.getValue();
        covden_win_pri = this.covden_win.getValue();
        potet_sublim_pri = this.potet_sublim.getValue();

        hru_area_pri = this.hru_area.getValue();
        cov_type_pri = this.cov_type.getValue();
        hru_type_pri = this.hru_type.getValue();
        hru_route_order_pri = this.hru_route_order.getValue();
        basin_area_inv_pri = this.basin_area_inv.getValue();
        deltim_pri = this.deltim.getValue();
        active_hrus_pri = this.active_hrus.getValue();
        route_on_pri = this.route_on.getValue();
        transp_on_pri = this.transp_on.getValue();
        if (nevap_pri == 0) {
            pan_evap_pri = new double[1];
        } else if (nevap_pri > 0) {
            pan_evap_pri = new double[nevap_pri];
        }

        for (int i = 0; i < nhru_pri; i++) {
            intcp_transp_on[i] = transp_on_pri[i];
            if (covden_win_pri[i] > covden_sum_pri[i]) {
                System.out.println("warning, covden_win>covden_sum, hru: " + i);
            }
            if (cov_type_pri[i] == 0 && (covden_win_pri[i] > 0.0 || covden_sum_pri[i] > 0.0)) {
                System.out.println("warning, cov_type=0 & cov_den not 0. hru: " + i + " winter: " + covden_win_pri[i] + " summer: " + covden_sum_pri[i]);
            }
            if (cov_type_pri[i] != 0 && hru_type_pri[i] == 2) {
                System.out.println("warning, cov_type must be 0 for lakes, reset from: " + cov_type_pri[i] + " to 0 for hru:" + i);
                cov_type_pri[i] = 0;
                covden_sum_pri[i] = 0.0;
                covden_win_pri[i] = 0.0;
            }
        }

        j = -1;
    }

    @Override
    public void run() throws Exception {

        double stor, cov, evrn, evsn, z, d;
        double diff, avail_et;

        for (int j = 0; j < this.active_hrus.getValue(); j++) {
        int i = hru_route_order_pri[j];
        newsnow_pri = this.newsnow.getValue(i);
        pptmix_pri = this.pptmix.getValue(i);
        hru_rain_pri = this.hru_rain.getValue(i);
        hru_snow_pri = this.hru_snow.getValue(i);
        hru_ppt_pri = this.hru_ppt.getValue(i);
        tmaxf_pri = this.tmaxf.getValue(i);
        tavgc_pri = this.tavgc.getValue(i);
        swrad_pri = 1;//this.swrad.getValue(i);

        potet_pri = this.potet.getValue(i);
        transp_on_pri[i] = this.transp_on.getValue(i);

        if (j == 0) {
            if (stor_last == null) {
                init();
            }

//        int mo = date.get(java.util.Calendar.MONTH);
            mo = this.date_mo.getValue();
            last_stor = basin_intcp_stor_pri;

            basin_net_ppt_pri = 0.0;
            basin_intcp_evap_pri = 0.0;
            basin_intcp_stor_pri = 0.0;
            last_intcp_stor_pri = 0.0;
            basin_ppt_pri = this.basin_ppt.getValue();

        }

        /**
         * *************loop****************************************************
         */
              
      //  if (j < this.active_hrus.getValue()) {

            double harea = hru_area_pri[i];

            if (hru_type_pri[i] == 2) {   // lake hrus
                net_rain_pri[i] = hru_rain_pri;
                net_snow_pri[i] = hru_snow_pri;
                net_ppt_pri[i] = hru_ppt_pri;
                basin_net_ppt_pri += net_ppt_pri[i] * harea;
                //               continue;
//                  j=j+1;
            }

            // Adjust interception amounts for changes in summer/winter cover density
            //!rsr 1/12/05 int_snow is not used currently, to be implemented later
            if (hru_type_pri[i] != 2) {
                if (transp_on_pri[i] == 1) {
                    cov = covden_sum_pri[i];
                } else {
                    cov = covden_win_pri[i];
                }
                double intcpstor = intcp_stor_pri[i];
                double intcpevap = 0.0;
                double evap_changeover = 0.0;

                //*****Determine the amount of interception from rain
                if (cov_type_pri[i] == 0) {
                    net_rain_pri[i] = hru_rain_pri;
                    net_snow_pri[i] = hru_snow_pri;
                    net_ppt_pri[i] = hru_ppt_pri;
                } else {
                    //            System.out.println(i + " r " + hru_rain[i]);
                    // *** go from summer to winter cover density
                    //rsr, evap_changeover and int_last are volumes
                    if (transp_on_pri[i] == 0 && intcp_transp_on[i] == 1) {
                        intcp_transp_on[i] = 0;

                        if (intcpstor > 0.0) {
                            diff = covden_sum_pri[i] - cov;
                            evap_changeover = intcpstor * diff;
                            if (evap_changeover < 0.0) {
                                System.out.println("intercept water loss hru: " + i + " "
                                        + evap_changeover);
                                evap_changeover = 0.0;
                            }
                            if (evap_changeover > potet_pri) {
                                evap_changeover = potet_pri;
                            }

                            if (cov > NEARZERO) {
                                intcpstor = (intcpstor * covden_sum_pri[i] - evap_changeover) / cov;
                            } else {
                                System.out.println("covden_win=0.0 at winter changeover with"
                                        + " canopy storage, increased potet " + intcpstor);
                                potet_pri = potet_pri + stor_last[i];
                                evap_changeover = 0.0;
                                double tmp = stor_last[i] * harea * basin_area_inv_pri;
                                last_intcp_stor_pri = last_intcp_stor_pri + tmp;
                                last_stor = last_stor - tmp;
                                stor_last[i] = 0.0;
                                intcpstor = 0.0;
                            }
                        } else {
                            intcpstor = 0.0;
                        }
                    } // ***  go from winter to summer cover density
                    else if (transp_on_pri[i] == 1 && intcp_transp_on[i] == 0) {
                        intcp_transp_on[i] = 1;
                        if (intcpstor > 0.0) {
                            diff = covden_win_pri[i] - cov;
                            if (Math.abs(intcpstor * diff) > NEARZERO) {
                                if (cov > NEARZERO) {
                                    intcpstor = intcpstor * covden_win_pri[i] / cov;
                                } else {
                                    //*** if storage on winter canopy, with
                                    //    summer cover = 0, evap all storage up to potet
                                    evap_changeover = intcpstor * covden_win_pri[i];
                                    if (evap_changeover > potet_pri) {
                                        evap_changeover = potet_pri;
                                    }
                                    intcpstor = (intcpstor * covden_win_pri[i] - evap_changeover) / cov;
                                }
                            }
                        } else {
                            intcpstor = 0.;
                        }
                    }
                    avail_et = potet_pri - evap_changeover;

                    // Determine the amount of interception from rain
                    // System.out.println(i + " r " + hru_rain[i]);
                    net_rain_pri[i] = hru_rain_pri;
                    if (hru_rain_pri > 0. && cov > NEARZERO) {
                        intcp_form_pri[i] = 0;
                        if (transp_on_pri[i] == 1) {
                            stor = srain_intcp_pri[i];
                        } else {
                            stor = wrain_intcp_pri[i];
                        }
                        if (cov_type_pri[i] > 1) {
                            intcpstor = intercept(1, i, hru_rain_pri, stor, cov, intcpstor);
                        } else if (cov_type_pri[i] == 1) {
                            if (pkwater_equiv_pri != null && pkwater_equiv_pri[i] <= 0 && hru_snow_pri <= 0) {
//                        if (pkwater_equiv_intcp[i] <= 0 && hru_snow[i] <= 0) {
                                //rsr             when not a mixed event
                                //rsr, 03/24/2008 intercept rain on snow-free grass,
                                intcpstor = intercept(1, i, hru_rain_pri, stor, cov, intcpstor);
                                //rsr 03/24/2008
                                //it was decided to leave the water in intcpstor rather
                                //than put the water in the snowpack, as doing so for a
                                //mixed event on grass with snow-free surface produces a
                                //divide by zero in snowcomp_prms. storage on grass will
                                //eventually evaporate
                            }
                        }
                        //    System.out.println("inst_r, i" + i + " " + intcpstor );
                    }
                    //Determine amount of interception from snow
                    net_snow_pri[i] = hru_snow_pri;
                    if (hru_snow_pri > 0. && cov > NEARZERO) {
                        intcp_form_pri[i] = 1;
                        if (cov_type_pri[i] > 1) {
                            intcpstor = intercept(2, i, hru_snow_pri, snow_intcp_pri[i], cov, intcpstor);
                            if (net_snow_pri[i] < NEARZERO) {   //rsr, added 3/9/2006
                                newsnow_pri = 0;
                                pptmix_pri = 0;    // reset to be sure it is zero
                                //iputnsflg = 1;
                            }
                        }
                    }
                    //   System.out.println("inst_s, i" + i + " " + intcpstor );
                    net_ppt_pri[i] = net_rain_pri[i] + net_snow_pri[i];
                    //      System.out.println(i + " r " + net_rain[i] + " s " + net_snow[i]);
                    //******compute evaporation or sublimation of interception
                    if (intcp_on_pri[i] == 1) {
                        if (route_on_pri == 0 || hru_ppt_pri < NEARZERO) {
                            evrn = avail_et / epan_coef_pri[mo];
                            evsn = potet_sublim_pri * avail_et;
                            if (nevap_pri > 0) {
                                pan_evap_pri[i] = this.pan_evap.getValue(i);
                                if (pan_evap_pri[0] > -998.99) {
                                    evrn = pan_evap_pri[0];
                                }
                            }
                            //******compute snow interception loss
                            if (intcp_form_pri[i] == 1) {
                                if (basin_ppt_pri < NEARZERO) {
                                    z = intcpstor - evsn;
                                    if (z > 0.0) {
                                        intcp_on_pri[i] = 1;
                                        intcpstor = z;
                                        intcpevap = evsn;
                                    } else {
                                        intcpevap = intcpstor;
                                        intcpstor = 0.;
                                        intcp_on_pri[i] = 0;
                                    }
                                }
                            } else if (intcp_form_pri[i] == 0) {
                                d = intcpstor - evrn;
                                if (d > 0.) {
                                    intcpstor = d;
                                    intcpevap = evrn;
                                    intcp_on_pri[i] = 1;
                                } else {
                                    intcpevap = intcpstor;
                                    intcpstor = 0.0;
                                    intcp_on_pri[i] = 0;
                                }
                            }
                        }
                    }
                }
                if (cov > 0.0 && evap_changeover > 0.0) {
                    intcp_evap_pri[i] = intcpevap + evap_changeover / cov;
                } else {
                    intcp_evap_pri[i] = intcpevap;
                }
                hru_intcpevap_pri[i] = intcp_evap_pri[i] * cov;
                intcp_stor_pri[i] = intcpstor;
                hru_intcpstor_pri[i] = intcpstor * cov;
//                System.out.println("hrn, hsn, intev, intst " + mo + " " + day +
//                        " " + i + " " + hru_rain[i] + " " + hru_snow[i] + " " +
//                        intcp_evap[i] + " " + intcp_stor[i]);
//            
                //rsr, question about depression storage for basin_net_ppt???
                //my assumption is that cover density is for the whole hru
                basin_net_ppt_pri = basin_net_ppt_pri + (net_ppt_pri[i] * harea);
                basin_intcp_stor_pri += hru_intcpstor_pri[i] * harea;
                basin_intcp_evap_pri += hru_intcpevap_pri[i] * harea;
                //      }
            }

            this.last_intcp_stor.setValue(last_intcp_stor_pri);
            this.hru_intcpevap.setValue(i, hru_intcpevap_pri[i]);
            this.hru_intcpstor.setValue(i, hru_intcpstor_pri[i]);
            this.net_rain.setValue(i, net_rain_pri[i]);
            this.net_snow.setValue(i, net_snow_pri[i]);
            this.net_ppt.setValue(i, net_ppt_pri[i]);
            this.intcp_stor.setValue(i, intcp_stor_pri[i]);
            this.intcp_evap.setValue(i, intcp_evap_pri[i]);
            this.intcp_form.setValue(i, intcp_form_pri[i]);
            this.intcp_on.setValue(i, intcp_on_pri[i]);
            this.newsnow.setValue(i, newsnow_pri);
            this.pptmix.setValue(i, pptmix_pri);

        }
        //*************************end loop*************************************/ 
        if (j + 1 == this.active_hrus.getValue()) {
            basin_net_ppt_pri *= basin_area_inv_pri;
            basin_intcp_stor_pri *= basin_area_inv_pri;
            basin_intcp_evap_pri *= basin_area_inv_pri;

            if (log.isLoggable(Level.INFO)) {
                log.info("Intcp " + basin_net_ppt_pri + " "
                        + basin_intcp_stor_pri + " "
                        + basin_intcp_evap_pri);
            }

            this.basin_net_ppt.setValue(basin_net_ppt_pri);
            this.basin_intcp_stor.setValue(basin_intcp_stor_pri);
            this.basin_intcp_evap.setValue(basin_intcp_evap_pri);

           
        }

    }

    @Override
    public void clear() throws Exception {
    }

    private double intercept(int id, int index, double precip, double stor_max, double cov, double intcp_stor) {
        double thrufall;
        intcp_on_pri[index] = 1;
        //rsr note: avail_stor can be negative when wrain_intcp < snow_intcp
        //for mixed precipitation event

        // System.out.println("inst_r, i" + index + " " + intcpstor );
        double avail_stor = stor_max - intcp_stor;
        if (avail_stor < NEARZERO) {
            thrufall = precip;
        } else if (precip > avail_stor) {
            intcp_stor = stor_max;
            thrufall = precip - avail_stor;
        } else {
            intcp_stor = intcp_stor + precip;
            thrufall = 0.0;
        }
        if (id == 1) {
            net_rain_pri[index] = ((precip * (1. - cov)) + (thrufall * cov));
            //*** allow intcp_stor to exceed stor_max with small amounts of precip
            if (net_rain_pri[index] < 0.000001) {
                intcp_stor = intcp_stor + net_rain_pri[index] / cov;
                net_rain_pri[index] = 0.0;
            }
        } else {
            net_snow_pri[index] = ((precip * (1. - cov)) + (thrufall * cov));
            //*** allow intcp_stor to exceed stor_max with small amounts of precip
            if (net_snow_pri[index] < 0.000001) {
                intcp_stor = intcp_stor + net_snow_pri[index] / cov;
                net_snow_pri[index] = 0.0;
            }
        }
        return intcp_stor;
    }
}
